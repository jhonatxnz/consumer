package br.com.jhonatan.consumer.integration;

import br.com.jhonatan.consumer.ConsumerApplication;
import br.com.jhonatan.consumer.client.ProviderSubscriptionsClient;
import br.com.jhonatan.consumer.controller.RestControllerUrlBase;
import br.com.jhonatan.consumer.dto.ContactRequest;
import br.com.jhonatan.consumer.dto.StatusResponse;
import br.com.jhonatan.consumer.dto.SubscriptionDetails;
import br.com.jhonatan.consumer.enums.SubscriptionStatus;
import br.com.jhonatan.consumer.model.Subscriptions;
import br.com.jhonatan.consumer.model.UserSubscriptions;
import br.com.jhonatan.consumer.model.Users;
import br.com.jhonatan.consumer.repository.SubscriptionsRepository;
import br.com.jhonatan.consumer.repository.UserSubscriptionsRepository;
import br.com.jhonatan.consumer.repository.UsersRepository;
import br.com.jhonatan.consumer.util.SubscriptionCreator;
import br.com.jhonatan.consumer.util.UserCreator;
import br.com.jhonatan.consumer.util.UserSubscriptionCreator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

@SpringBootTest(classes = ConsumerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@AutoConfigureTestDatabase
public class SubscriptionsControllerIT {


    @Autowired
    private TestRestTemplate testRestTemplate;

    @LocalServerPort
    private int port;

    @Autowired
    private SubscriptionsRepository subscriptionsRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private UserSubscriptionsRepository userSubscriptionsRepository;

    @MockitoBean
    private ProviderSubscriptionsClient providerSubscriptionsClient;

    @AfterEach
    void tearDown() {
        subscriptionsRepository.deleteAll();
        usersRepository.deleteAll();
        userSubscriptionsRepository.deleteAll();
    }

    @Test
    @DisplayName("list returns list of subscriptions by customer document when successful")
    void list_ReturnsListOfSubscriptionsByCustomerDocument_WhenSuccessful() {
        Users savedUser = usersRepository.save(UserCreator.createUserToBeSaved());

        Subscriptions savedSubscription = subscriptionsRepository.save(SubscriptionCreator.createSubscriptionToBeSaved());

        userSubscriptionsRepository.save(UserSubscriptionCreator.createUserSubscriptionToBeSaved(savedUser, savedSubscription));

        List<SubscriptionDetails> response = testRestTemplate.exchange(
                RestControllerUrlBase.BASE_URL + "/users/{document}/subscriptions",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<SubscriptionDetails>>() {},
                savedUser.getDocument()
        ).getBody();

        Assertions.assertThat(response).isNotNull();

        Assertions.assertThat(response)
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(response.getFirst().getSubscription()).isEqualTo(savedSubscription.getName());
    }

    @Test
    @DisplayName("activate activates subscription when successful")
    void activate_ActivesSubscription_WhenSuccessful() {
        Users savedUser = usersRepository.save(UserCreator.createUserToBeSaved());

        Subscriptions savedSubscription = subscriptionsRepository.save(SubscriptionCreator.createSubscriptionToBeSaved());


        ContactRequest contactRequest = ContactRequest.builder()
                .email(savedUser.getEmail())
                .phone(savedUser.getPhone())
                .build();

        StatusResponse response = testRestTemplate.exchange(
                RestControllerUrlBase.BASE_URL + "/users/{document}/subscriptions/{code}/activate",
                HttpMethod.POST,
                new HttpEntity<>(contactRequest),
                new ParameterizedTypeReference<StatusResponse>() {},
                savedUser.getDocument(),
                savedSubscription.getCode()
        ).getBody();

        Assertions.assertThat(response).isNotNull();

        Assertions.assertThat(response.getSubscription()).isEqualTo(savedSubscription.getCode());

        Assertions.assertThat(response.getMessage()).isEqualTo("Subscription successfully activated");

        BDDMockito.then(providerSubscriptionsClient).should()
                .createUser(BDDMockito.any());
        BDDMockito.then(providerSubscriptionsClient).should()
                .createSubscription(savedUser.getDocument(), savedSubscription.getCode());
    }

    @Test
    @DisplayName("activate reactivates subscription when successful")
    void activate_ReactivatesSubscription_WhenSuccessful() {
        Users savedUser = usersRepository.save(UserCreator.createUserToBeSaved());

        Subscriptions savedSubscription = subscriptionsRepository.save(SubscriptionCreator.createSubscriptionToBeSaved());

        UserSubscriptions inactiveSubscription = UserSubscriptionCreator.createUserSubscriptionToBeSaved(savedUser, savedSubscription);

        inactiveSubscription.setStatus(SubscriptionStatus.INACTIVE.value());

        userSubscriptionsRepository.save(inactiveSubscription);

        ContactRequest contactRequest = ContactRequest.builder()
                .email(savedUser.getEmail())
                .phone(savedUser.getPhone())
                .build();

        StatusResponse response = testRestTemplate.exchange(
                RestControllerUrlBase.BASE_URL + "/users/{document}/subscriptions/{code}/activate",
                HttpMethod.POST,
                new HttpEntity<>(contactRequest),
                new ParameterizedTypeReference<StatusResponse>() {},
                savedUser.getDocument(),
                savedSubscription.getCode()
        ).getBody();

        Assertions.assertThat(response).isNotNull();

        Assertions.assertThat(response.getSubscription()).isEqualTo(savedSubscription.getCode());

        Assertions.assertThat(response.getMessage()).isEqualTo("Subscription successfully reactivated");

        BDDMockito.then(providerSubscriptionsClient).should()
                .createSubscription(savedUser.getDocument(), savedSubscription.getCode());
        BDDMockito.then(providerSubscriptionsClient).should(BDDMockito.never())
                .createUser(BDDMockito.any());
    }

    @Test
    @DisplayName("cancel cancels subscription when successful")
    void cancel_CancelsSubscription_WhenSuccessful() {
        Users savedUser = usersRepository.save(UserCreator.createUserToBeSaved());

        Subscriptions savedSubscription = subscriptionsRepository.save(SubscriptionCreator.createSubscriptionToBeSaved());

        userSubscriptionsRepository.save(UserSubscriptionCreator.createUserSubscriptionToBeSaved(savedUser, savedSubscription));

        StatusResponse response = testRestTemplate.exchange(
                RestControllerUrlBase.BASE_URL + "/users/{document}/subscriptions/{code}/cancellation",
                HttpMethod.DELETE,
                null,
                new ParameterizedTypeReference<StatusResponse>() {},
                savedUser.getDocument(),
                savedSubscription.getCode()
        ).getBody();

        Assertions.assertThat(response).isNotNull();

        Assertions.assertThat(response.getSubscription()).isEqualTo(savedSubscription.getCode());

        Assertions.assertThat(response.getMessage()).isEqualTo("Subscription successfully canceled");

        BDDMockito.then(providerSubscriptionsClient).should()
                .cancelSubscription(savedUser.getDocument(), savedSubscription.getCode());
    }

    @Test
    @DisplayName("block blocks subscription when successful")
    void block_BlocksSubscription_WhenSuccessful() {
        Users savedUser = usersRepository.save(UserCreator.createUserToBeSaved());

        Subscriptions savedSubscription = subscriptionsRepository.save(SubscriptionCreator.createSubscriptionToBeSaved());

        userSubscriptionsRepository.save(UserSubscriptionCreator.createUserSubscriptionToBeSaved(savedUser, savedSubscription));

        StatusResponse response = testRestTemplate.exchange(
                RestControllerUrlBase.BASE_URL + "/users/{document}/subscriptions/{code}/block",
                HttpMethod.POST,
                null,
                new ParameterizedTypeReference<StatusResponse>() {},
                savedUser.getDocument(),
                savedSubscription.getCode()
        ).getBody();

        Assertions.assertThat(response).isNotNull();

        Assertions.assertThat(response.getSubscription()).isEqualTo(savedSubscription.getCode());

        Assertions.assertThat(response.getMessage()).isEqualTo("Subscription successfully blocked");

        BDDMockito.then(providerSubscriptionsClient).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("unblock unblocks subscription when successful")
    void unblock_UnblocksSubscription_WhenSuccessful() {
        Users savedUser = usersRepository.save(UserCreator.createUserToBeSaved());

        Subscriptions savedSubscription = subscriptionsRepository.save(SubscriptionCreator.createSubscriptionToBeSaved());

        UserSubscriptions blockedSubscription = UserSubscriptionCreator.createUserSubscriptionToBeSaved(savedUser, savedSubscription);
        blockedSubscription.setStatus(SubscriptionStatus.BLOCKED.value());
        userSubscriptionsRepository.save(blockedSubscription);

        StatusResponse response = testRestTemplate.exchange(
                RestControllerUrlBase.BASE_URL + "/users/{document}/subscriptions/{code}/unblock",
                HttpMethod.POST,
                null,
                new ParameterizedTypeReference<StatusResponse>() {},
                savedUser.getDocument(),
                savedSubscription.getCode()
        ).getBody();

        Assertions.assertThat(response).isNotNull();

        Assertions.assertThat(response.getSubscription()).isEqualTo(savedSubscription.getCode());

        Assertions.assertThat(response.getMessage()).isEqualTo("Subscription successfully unblocked");

        BDDMockito.then(providerSubscriptionsClient).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("updateContact updates contact user information when successful")
    void updateContact_UpdatesContactUserInformation_WhenSuccessful() {
        Users savedUser = usersRepository.save(UserCreator.createUserToBeSaved());

        Subscriptions savedSubscription = subscriptionsRepository.save(SubscriptionCreator.createSubscriptionToBeSaved());

        userSubscriptionsRepository.save(UserSubscriptionCreator.createUserSubscriptionToBeSaved(savedUser, savedSubscription));

        ContactRequest contactRequest = ContactRequest.builder()
                .email(savedUser.getEmail())
                .phone(savedUser.getPhone())
                .build();

        StatusResponse response = testRestTemplate.exchange(
                RestControllerUrlBase.BASE_URL + "/users/{document}/subscriptions/{code}",
                HttpMethod.PUT,
                new HttpEntity<>(contactRequest),
                new ParameterizedTypeReference<StatusResponse>() {},
                savedUser.getDocument(),
                savedSubscription.getCode()
        ).getBody();

        Assertions.assertThat(response).isNotNull();

        Assertions.assertThat(response.getSubscription()).isEqualTo(savedSubscription.getCode());

        Assertions.assertThat(response.getMessage()).isEqualTo("User contact successfully updated");

        BDDMockito.then(providerSubscriptionsClient).should()
                .updateUser(BDDMockito.eq(savedUser.getDocument()), BDDMockito.any());
    }
}