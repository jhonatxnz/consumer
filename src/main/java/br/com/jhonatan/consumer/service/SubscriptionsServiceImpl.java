package br.com.jhonatan.consumer.service;

import br.com.jhonatan.consumer.dto.StatusResponse;
import br.com.jhonatan.consumer.dto.ContactRequest;
import br.com.jhonatan.consumer.dto.SubscriptionDetails;
import br.com.jhonatan.consumer.enums.SubscriptionStatus;
import br.com.jhonatan.consumer.exception.*;
import br.com.jhonatan.consumer.model.Subscriptions;
import br.com.jhonatan.consumer.model.UserSubscriptions;
import br.com.jhonatan.consumer.model.Users;
import br.com.jhonatan.consumer.repository.SubscriptionsRepository;
import br.com.jhonatan.consumer.repository.UserSubscriptionsRepository;
import br.com.jhonatan.consumer.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Log4j2
@Service
@RequiredArgsConstructor
public class SubscriptionsServiceImpl implements SubscriptionsService {

    private final UsersRepository usersRepository;
    private final SubscriptionsRepository subscriptionsRepository;
    private final UserSubscriptionsRepository userSubscriptionsRepository;

    @Override
    public List<SubscriptionDetails> list(String document) {
        Users user = usersRepository.findByDocument(document).orElseThrow(UserNotFoundException::new);

        List<UserSubscriptions> userSubscriptions = userSubscriptionsRepository.findByUserId(user.getId());

        return userSubscriptions.stream().map(subscription -> {
            Subscriptions subscriptionDetails = subscriptionsRepository.findById(subscription.getSubscriptionId())
                    .orElseThrow(SubscriptionNotFoundException::new);

            return SubscriptionDetails.builder()
                    .subscription(subscriptionDetails.getName())
                    .email(userSubscriptions.getFirst().getEmail())
                    .phone(userSubscriptions.getFirst().getPhone())
                    .partner(subscriptionDetails.getPartner())
                    .status(userSubscriptions.getFirst().getStatus())
                    .createdAt(userSubscriptions.getFirst().getCreatedAt())
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

        List<UserSubscriptions> userSubscriptions = userSubscriptionsRepository.findByUserId(user.getId())
                .stream()
                .filter(userSubscription -> userSubscription.getSubscriptionId().equals(subscription.getId()))
                .toList();

        if (!userSubscriptions.isEmpty() && Objects.equals(userSubscriptions.getFirst().getStatus(), SubscriptionStatus.ACTIVE.value())) {
            log.info("User already has subscription");
            throw new UserAlreadyHasSubscription();
        } else if (!userSubscriptions.isEmpty() && Objects.equals(userSubscriptions.getFirst().getStatus(), SubscriptionStatus.INACTIVE.value())) {
            log.info("Reactivating {} subscription for user {}", subscriptionCode, document);

            UserSubscriptions existingSubscription = userSubscriptions.getFirst();

            existingSubscription.setStatus(SubscriptionStatus.ACTIVE.value());
            existingSubscription.setUpdatedAt(java.time.LocalDateTime.now());
            existingSubscription.setCanceledAt(null);
            existingSubscription.setEmail(user.getEmail());
            existingSubscription.setPhone(user.getPhone());

            userSubscriptionsRepository.save(existingSubscription);

            log.info("{} subscription successfully reactivated for user {}", subscriptionCode, document);

            return StatusResponse.builder()
                    .subscription(subscriptionCode)
                    .partner(subscription.getPartner())
                    .message("Reactivated successfully")
                    .build();
        }
        else {
            log.info("Activating {} subscription for user {}", subscriptionCode, document);

            UserSubscriptions userSubscriptionsActive = UserSubscriptions.builder()
                    .subscriptionId(subscription.getId())
                    .userId(user.getId())
                    .createdAt(java.time.LocalDateTime.now())
                    .updatedAt(java.time.LocalDateTime.now())
                    .status(SubscriptionStatus.ACTIVE.value())
                    .email(request.getEmail())
                    .phone(request.getPhone())
                    .build();

            userSubscriptionsRepository.save(userSubscriptionsActive);

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

        UserSubscriptions subscriptionToCancel = userSubscriptionsRepository.findByUserId(user.getId())
                .stream()
                .filter(userSubscription -> userSubscription.getSubscriptionId().equals(subscription.getId()))
                .findFirst()
                .orElseThrow(() -> new SubscriptionNotFoundException("Subscription not found for the customer"));

        if (subscriptionToCancel.getStatus().equals(SubscriptionStatus.INACTIVE.value())) {
            log.info("Subscription already canceled for the customer");
            throw new SubscriptionAlreadyCanceled();
        }

        subscriptionToCancel.setStatus(SubscriptionStatus.INACTIVE.value());
        subscriptionToCancel.setCanceledAt(java.time.LocalDateTime.now());
        subscriptionToCancel.setUpdatedAt(java.time.LocalDateTime.now());

        userSubscriptionsRepository.save(subscriptionToCancel);

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

        UserSubscriptions subscriptionToBlock = userSubscriptionsRepository.findByUserId(user.getId())
                .stream()
                .filter(userSubscription -> userSubscription.getSubscriptionId().equals(subscription.getId()))
                .findFirst()
                .orElseThrow(() -> new SubscriptionNotFoundException("Subscription not found for the customer"));

        if (subscriptionToBlock.getStatus().equals(SubscriptionStatus.BLOCKED.value())) {
            log.info("Subscription already blocked for the customer");
            throw new SubscriptionAlreadyBlocked();
        }

        subscriptionToBlock.setStatus(SubscriptionStatus.BLOCKED.value());
        subscriptionToBlock.setUpdatedAt(java.time.LocalDateTime.now());

        userSubscriptionsRepository.save(subscriptionToBlock);

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

        UserSubscriptions subscriptionToBlock = userSubscriptionsRepository.findByUserId(user.getId())
                .stream()
                .filter(userSubscription -> userSubscription.getSubscriptionId().equals(subscription.getId()))
                .findFirst()
                .orElseThrow(() -> new SubscriptionNotFoundException("Subscription not found for the customer"));

        if (subscriptionToBlock.getStatus().equals(SubscriptionStatus.ACTIVE.value())) {
            log.info("Subscription already unblocked for the customer");
            throw new SubscriptionAlreadyUnblocked();
        }

        subscriptionToBlock.setStatus(SubscriptionStatus.ACTIVE.value());
        subscriptionToBlock.setUpdatedAt(java.time.LocalDateTime.now());

        userSubscriptionsRepository.save(subscriptionToBlock);

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

        List<UserSubscriptions> userSubscriptions = userSubscriptionsRepository.findByUserId(user.getId())
                .stream()
                .filter(userSubscription -> userSubscription.getSubscriptionId().equals(subscription.getId()))
                .toList();

        for (UserSubscriptions subscriptions : userSubscriptions) {
            subscriptions.setEmail(request.getEmail());
            subscriptions.setPhone(request.getPhone());
            userSubscriptionsRepository.save(subscriptions);
        }

        return StatusResponse.builder()
                .subscription(subscriptionCode)
                .partner(subscription.getPartner())
                .message("User contacts successfully updated")
                .build();

    }
}
