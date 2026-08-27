package br.com.jhonatan.consumer.service;

import br.com.jhonatan.consumer.client.ProviderSubscriptionsClient;
import br.com.jhonatan.consumer.client.dto.ProviderUpdateUserRequest;
import br.com.jhonatan.consumer.client.dto.ProviderUserRequest;
import br.com.jhonatan.consumer.dto.StatusResponse;
import br.com.jhonatan.consumer.dto.ContactRequest;
import br.com.jhonatan.consumer.dto.SubscriptionDetails;
import br.com.jhonatan.consumer.enums.Actions;
import br.com.jhonatan.consumer.enums.SubscriptionStatus;
import br.com.jhonatan.consumer.exception.*;
import br.com.jhonatan.consumer.model.Subscriptions;
import br.com.jhonatan.consumer.model.UserSubscriptions;
import br.com.jhonatan.consumer.model.UserSubscriptionsHistory;
import br.com.jhonatan.consumer.model.Users;
import br.com.jhonatan.consumer.repository.SubscriptionsRepository;
import br.com.jhonatan.consumer.repository.UserSubscriptionsHistoryRepository;
import br.com.jhonatan.consumer.repository.UserSubscriptionsRepository;
import br.com.jhonatan.consumer.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class SubscriptionsServiceImpl implements SubscriptionsService {

    private final UsersRepository usersRepository;
    private final SubscriptionsRepository subscriptionsRepository;
    private final UserSubscriptionsRepository userSubscriptionsRepository;
    private final UserSubscriptionsHistoryRepository userSubscriptionsHistoryRepository;

    private final ProviderSubscriptionsClient providerSubscriptionsClient;

    @Override
    public List<SubscriptionDetails> list(String document) {

        Users user = usersRepository.findByDocument(document).orElseThrow(UserNotFoundException::new);

        List<UserSubscriptions> userSubscriptions = userSubscriptionsRepository.findByUserId(user.getId());

        return userSubscriptions.stream().map(subscription -> {
            Subscriptions subscriptionDetails = subscriptionsRepository.findById(subscription.getSubscriptionId())
                    .orElseThrow(SubscriptionNotFoundException::new);

            return SubscriptionDetails.builder()
                    .subscription(subscriptionDetails.getName())
                    .email(subscription.getEmail())
                    .phone(subscription.getPhone())
                    .partner(subscriptionDetails.getPartner())
                    .status(subscription.getStatus())
                    .createdAt(subscription.getCreatedAt())
                    .build();
            }
        ).toList();

    }

    @Override
    public StatusResponse activate(String document, String subscriptionCode, ContactRequest request) {
        log.info("Starting activation process - Consumer API");

        Users user = usersRepository.findByDocument(document)
                .orElseThrow(UserNotFoundException::new);

        Subscriptions subscription = subscriptionsRepository.findByCode(subscriptionCode)
                .orElseThrow(SubscriptionNotFoundException::new);

        Optional<UserSubscriptions> existingSubscription = userSubscriptionsRepository
                .findByUserIdAndSubscriptionId(user.getId(), subscription.getId());

        if (existingSubscription.isPresent() && existingSubscription.get().getStatus().equals(SubscriptionStatus.ACTIVE.value())) {
            log.info("User already has subscription");
            throw new UserAlreadyHasSubscription();
        } else if (existingSubscription.isPresent() && existingSubscription.get().getStatus().equals(SubscriptionStatus.INACTIVE.value())) {
            log.info("Reactivating {} subscription for user {}", subscriptionCode, document);

            UserSubscriptions subscriptionToReactivate = existingSubscription.get();

            subscriptionToReactivate.setStatus(SubscriptionStatus.ACTIVE.value());
            subscriptionToReactivate.setUpdatedAt(LocalDateTime.now());
            subscriptionToReactivate.setCanceledAt(null);
            subscriptionToReactivate.setEmail(request.getEmail());
            subscriptionToReactivate.setPhone(request.getPhone());

            providerSubscriptionsClient.createSubscription(user.getDocument(), subscription.getCode());
            //rescue outliers -> outlierException from provider

            userSubscriptionsRepository.save(subscriptionToReactivate);

            updateUserSubscriptionHistory(subscriptionToReactivate, subscription.getPartner(), Actions.REACTIVATE);

            log.info("{} subscription successfully reactivated for user {}", subscriptionCode, document);

            return StatusResponse.builder()
                    .subscription(subscriptionCode)
                    .partner(subscription.getPartner())
                    .message("Subscription successfully reactivated")
                    .build();
        }
        else {
            log.info("Activating {} subscription for user {}", subscriptionCode, document);

            ProviderUserRequest providerUserRequest = ProviderUserRequest.builder()
                    .name(user.getName())
                    .document(document)
                    .email(request.getEmail())
                    .phone(request.getPhone())
                    .build();

            providerSubscriptionsClient.createUser(providerUserRequest);
            //rescue outliers -> outlierException from provider

            UserSubscriptions subscriptionsToActive = UserSubscriptions.builder()
                    .subscriptionId(subscription.getId())
                    .userId(user.getId())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .status(SubscriptionStatus.ACTIVE.value())
                    .email(request.getEmail())
                    .phone(request.getPhone())
                    .build();

            providerSubscriptionsClient.createSubscription(user.getDocument(), subscription.getCode());

            userSubscriptionsRepository.save(subscriptionsToActive);

            updateUserSubscriptionHistory(subscriptionsToActive, subscription.getPartner(), Actions.ACTIVATE);

            log.info("{} subscription successfully activated for user {}", subscriptionCode, document);

            return StatusResponse.builder()
                    .subscription(subscriptionCode)
                    .partner(subscription.getPartner())
                    .message("Subscription successfully activated")
                    .build();
        }
    }

    @Override
    public StatusResponse cancel(String document, String subscriptionCode) {
        log.info("Starting cancellation process - Consumer API");

        Users user = usersRepository.findByDocument(document)
                .orElseThrow(UserNotFoundException::new);

        Subscriptions subscription = subscriptionsRepository.findByCode(subscriptionCode)
                .orElseThrow(SubscriptionNotFoundException::new);

        UserSubscriptions subscriptionToCancel = userSubscriptionsRepository
                .findByUserIdAndSubscriptionId(user.getId(), subscription.getId())
                .orElseThrow(() -> new SubscriptionNotFoundException("Subscription not found for the customer"));

        if (subscriptionToCancel.getStatus().equals(SubscriptionStatus.INACTIVE.value())) {
            log.info("Subscription already canceled for the customer");
            throw new SubscriptionAlreadyCanceled();
        }

        subscriptionToCancel.setStatus(SubscriptionStatus.INACTIVE.value());
        subscriptionToCancel.setCanceledAt(LocalDateTime.now());
        subscriptionToCancel.setUpdatedAt(LocalDateTime.now());

        providerSubscriptionsClient.cancelSubscription(document, subscriptionCode);

        userSubscriptionsRepository.save(subscriptionToCancel);

        updateUserSubscriptionHistory(subscriptionToCancel, subscription.getPartner(), Actions.INACTIVATE);

        log.info("Subscription successfully canceled for the customer {}", document);

        return StatusResponse.builder()
                .subscription(subscriptionCode)
                .partner(subscription.getPartner())
                .message("Subscription successfully canceled")
                .build();
    }

    @Override
    public StatusResponse block(String document, String subscriptionCode) {
        log.info("Starting blocking process - Consumer API");

        Users user = usersRepository.findByDocument(document)
                .orElseThrow(UserNotFoundException::new);

        Subscriptions subscription = subscriptionsRepository.findByCode(subscriptionCode)
                .orElseThrow(SubscriptionNotFoundException::new);

        UserSubscriptions subscriptionToBlock = userSubscriptionsRepository
                .findByUserIdAndSubscriptionId(user.getId(), subscription.getId())
                .orElseThrow(() -> new SubscriptionNotFoundException("Subscription not found for the customer"));

        if (subscriptionToBlock.getStatus().equals(SubscriptionStatus.BLOCKED.value())) {
            log.info("Subscription already blocked for the customer");
            throw new SubscriptionAlreadyBlocked();
        }

        subscriptionToBlock.setStatus(SubscriptionStatus.BLOCKED.value());
        subscriptionToBlock.setUpdatedAt(LocalDateTime.now());

        userSubscriptionsRepository.save(subscriptionToBlock);

        updateUserSubscriptionHistory(subscriptionToBlock, subscription.getPartner(), Actions.BLOCKED);


        log.info("Subscription successfully blocked for the customer {}", document);

        return StatusResponse.builder()
                .subscription(subscriptionCode)
                .partner(subscription.getPartner())
                .message("Subscription successfully blocked")
                .build();
    }

    @Override
    public StatusResponse unblock(String document, String subscriptionCode) {
        log.info("Starting unblocking process - Consumer API");

        Users user = usersRepository.findByDocument(document)
                .orElseThrow(UserNotFoundException::new);

        Subscriptions subscription = subscriptionsRepository.findByCode(subscriptionCode)
                .orElseThrow(SubscriptionNotFoundException::new);

        UserSubscriptions subscriptionToUnblock = userSubscriptionsRepository
                .findByUserIdAndSubscriptionId(user.getId(), subscription.getId())
                .orElseThrow(() -> new SubscriptionNotFoundException("Subscription not found for the customer"));

        if (subscriptionToUnblock.getStatus().equals(SubscriptionStatus.ACTIVE.value())) {
            log.info("Subscription already unblocked for the customer");
            throw new SubscriptionAlreadyUnblocked();
        }

        subscriptionToUnblock.setStatus(SubscriptionStatus.ACTIVE.value());
        subscriptionToUnblock.setUpdatedAt(LocalDateTime.now());

        userSubscriptionsRepository.save(subscriptionToUnblock);

        updateUserSubscriptionHistory(subscriptionToUnblock, subscription.getPartner(), Actions.UNBLOCKED);


        log.info("Subscription successfully unblocked for the customer {}", document);

        return StatusResponse.builder()
                .subscription(subscriptionCode)
                .partner(subscription.getPartner())
                .message("Subscription successfully unblocked")
                .build();
    }

    @Override
    public StatusResponse updateContact(String document, String subscriptionCode, ContactRequest request) {
        log.info("Updating contact - Consumer API");

        Users user = usersRepository.findByDocument(document)
                .orElseThrow(UserNotFoundException::new);

        Subscriptions subscription = subscriptionsRepository.findByCode(subscriptionCode)
                .orElseThrow(SubscriptionNotFoundException::new);

        UserSubscriptions userSubscription = userSubscriptionsRepository
                .findByUserIdAndSubscriptionId(user.getId(), subscription.getId())
                .orElseThrow(() -> new SubscriptionNotFoundException("Subscription not found for the customer"));

        ProviderUpdateUserRequest providerUpdateUserRequest = ProviderUpdateUserRequest.builder()
                .name(user.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .build();

        providerSubscriptionsClient.updateUser(document, providerUpdateUserRequest);

        userSubscription.setEmail(request.getEmail());
        userSubscription.setPhone(request.getPhone());

        userSubscriptionsRepository.save(userSubscription);

        List<UserSubscriptions> customerSubscriptions = userSubscriptionsRepository.findByUserIdAndStatus(user.getId(), SubscriptionStatus.ACTIVE.value());

        for (UserSubscriptions activeSubscription  : customerSubscriptions){
            activeSubscription.setEmail(request.getEmail());
            activeSubscription.setPhone(request.getPhone());
            activeSubscription.setUpdatedAt(java.time.LocalDateTime.now());

            userSubscriptionsRepository.save(activeSubscription);
        }

        return StatusResponse.builder()
                .subscription(subscriptionCode)
                .partner(subscription.getPartner())
                .message("User contact successfully updated")
                .build();

    }

    public void updateUserSubscriptionHistory(UserSubscriptions userSubscriptions, String partner, Actions action){

        UserSubscriptionsHistory userSubscriptionsHistory = UserSubscriptionsHistory.builder()
                .subscriptionId(userSubscriptions.getSubscriptionId())
                .userId(userSubscriptions.getUserId())
                .action(action.value())
                .date(LocalDateTime.now())
                .partner(partner)
                .build();

        userSubscriptionsHistoryRepository.save(userSubscriptionsHistory);
    }
}
