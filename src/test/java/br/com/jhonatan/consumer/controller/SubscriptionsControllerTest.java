package br.com.jhonatan.consumer.controller;

import br.com.jhonatan.consumer.dto.ContactRequest;
import br.com.jhonatan.consumer.dto.StatusResponse;
import br.com.jhonatan.consumer.dto.SubscriptionDetails;
import br.com.jhonatan.consumer.enums.SubscriptionStatus;
import br.com.jhonatan.consumer.model.Subscriptions;
import br.com.jhonatan.consumer.model.UserSubscriptions;
import br.com.jhonatan.consumer.model.Users;
import br.com.jhonatan.consumer.service.SubscriptionsService;
import br.com.jhonatan.consumer.util.SubscriptionCreator;
import br.com.jhonatan.consumer.util.UserCreator;
import br.com.jhonatan.consumer.util.UserSubscriptionCreator;
import org.apache.kafka.common.metrics.Stat;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionsControllerTest {

    @InjectMocks
    SubscriptionsController subscriptionsController;

    @Mock
    SubscriptionsService subscriptionsServiceMock;

    @Test
    @DisplayName("listSubscriptions returns list of subscriptions by customer document when successful")
    void list_ReturnsListOfSubscriptions_WhenSuccessful(){
        Users expectedUser = UserCreator.createValidUser();

        Subscriptions subscription = SubscriptionCreator.createValidSubscription();

        UserSubscriptions userSubscription = UserSubscriptionCreator.createUserSubscriptionToBeSaved(expectedUser, subscription);

       SubscriptionDetails subscriptionDetails = SubscriptionDetails.builder()
                .subscription(subscription.getName())
                .email(userSubscription.getEmail())
                .phone(userSubscription.getPhone())
                .partner(subscription.getPartner())
                .status(subscription.getStatus())
                .createdAt(userSubscription.getCreatedAt())
                .build();


        BDDMockito.when(subscriptionsServiceMock.list(expectedUser.getDocument()))
                .thenReturn(List.of(subscriptionDetails));

        subscriptionsController.listSubscriptions(expectedUser.getDocument());

        List<SubscriptionDetails> subscriptionDetailsList = subscriptionsController.listSubscriptions(expectedUser.getDocument());


        Assertions.assertThat(subscriptionDetailsList)
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(subscriptionDetailsList.getFirst().getSubscription())
                .isEqualTo(subscription.getName());

    }

    @Test
    @DisplayName("listSubscriptions returns empty list when successful")
    void list_ReturnsEmptyList_WhenSuccessful(){
        Users expectedUser = UserCreator.createValidUser();

        BDDMockito.when(subscriptionsServiceMock.list(expectedUser.getDocument()))
                .thenReturn(List.of());

        List<SubscriptionDetails> subscriptionDetailsList = subscriptionsController.listSubscriptions(expectedUser.getDocument());

        Assertions.assertThat(subscriptionDetailsList)
                .isEmpty();
    }

    @Test
    @DisplayName("activate saves subscription when successful")
    void activate_SavesSubscription_WhenSuccessful(){
        Users expectedUser = UserCreator.createValidUser();

        Subscriptions subscription = SubscriptionCreator.createValidSubscription();

        ContactRequest contactRequest = ContactRequest.builder()
                .email(expectedUser.getEmail())
                .phone(expectedUser.getPhone())
                .build();

        BDDMockito.when(subscriptionsServiceMock.activate(expectedUser.getDocument(), subscription.getCode(), contactRequest))
                .thenReturn(StatusResponse.builder()
                        .subscription(subscription.getCode())
                        .partner(subscription.getPartner())
                        .status(SubscriptionStatus.ACTIVE.getId())
                        .message("Subscription activated successfully")
                        .build());

        StatusResponse statusResponse = subscriptionsController.activate(expectedUser.getDocument(), subscription.getCode(), contactRequest);

        Assertions.assertThat(statusResponse).isNotNull();

        Assertions.assertThat(statusResponse.getSubscription()).isEqualTo(subscription.getCode());

        Assertions.assertThat(statusResponse.getMessage()).isEqualTo("Subscription activated successfully");
    }

    @Test
    @DisplayName("activate reactivates subscription when successful")
    void activate_ReactivatesSubscription_WhenSuccessful(){
        Users expectedUser = UserCreator.createValidUser();

        Subscriptions subscription = SubscriptionCreator.createValidSubscription();

        ContactRequest contactRequest = ContactRequest.builder()
                .email(expectedUser.getEmail())
                .phone(expectedUser.getPhone())
                .build();

        BDDMockito.when(subscriptionsServiceMock.activate(expectedUser.getDocument(), subscription.getCode(), contactRequest))
                .thenReturn(StatusResponse.builder()
                        .subscription(subscription.getCode())
                        .partner(subscription.getPartner())
                        .status(SubscriptionStatus.ACTIVE.getId())
                        .message("Subscription reactivated successfully")
                        .build());

        StatusResponse statusResponse = subscriptionsController.activate(expectedUser.getDocument(), subscription.getCode(), contactRequest);

        Assertions.assertThat(statusResponse).isNotNull();

        Assertions.assertThat(statusResponse.getSubscription()).isEqualTo(subscription.getCode());

        Assertions.assertThat(statusResponse.getMessage()).isEqualTo("Subscription reactivated successfully");
    }

    @Test
    @DisplayName("cancel deletes subscription when successful")
    void cancel_DeletesSubscription_WhenSuccessful(){
        Users expectedUser = UserCreator.createValidUser();

        Subscriptions subscription = SubscriptionCreator.createValidSubscription();

        BDDMockito.when(subscriptionsServiceMock.cancel(expectedUser.getDocument(), subscription.getCode()))
                .thenReturn(StatusResponse.builder()
                        .subscription(subscription.getCode())
                        .partner(subscription.getPartner())
                        .status(SubscriptionStatus.ACTIVE.getId())
                        .message("Subscription successfully canceled")
                        .build());

        StatusResponse statusResponse = subscriptionsController.cancel(expectedUser.getDocument(), subscription.getCode());

        Assertions.assertThat(statusResponse).isNotNull();

        Assertions.assertThat(statusResponse.getSubscription()).isEqualTo(subscription.getCode());

        Assertions.assertThat(statusResponse.getMessage()).isEqualTo("Subscription successfully canceled");
    }

    @Test
    @DisplayName("block blocks subscription when successful")
    void block_BlocksSubscription_WhenSuccessful(){
        Users expectedUser = UserCreator.createValidUser();

        Subscriptions subscription = SubscriptionCreator.createValidSubscription();

        BDDMockito.when(subscriptionsServiceMock.block(expectedUser.getDocument(), subscription.getCode()))
                .thenReturn(StatusResponse.builder()
                        .subscription(subscription.getCode())
                        .partner(subscription.getPartner())
                        .status(SubscriptionStatus.ACTIVE.getId())
                        .message("Subscription successfully blocked")
                        .build());

        StatusResponse statusResponse = subscriptionsController.block(expectedUser.getDocument(), subscription.getCode());

        Assertions.assertThat(statusResponse).isNotNull();

        Assertions.assertThat(statusResponse.getSubscription()).isEqualTo(subscription.getCode());

        Assertions.assertThat(statusResponse.getMessage()).isEqualTo("Subscription successfully blocked");
    }

    @Test
    @DisplayName("unblock unblocks subscription when successful")
    void unblock_UnblocksSubscription_WhenSuccessful(){
        Users expectedUser = UserCreator.createValidUser();

        Subscriptions subscription = SubscriptionCreator.createValidSubscription();

        BDDMockito.when(subscriptionsServiceMock.unblock(expectedUser.getDocument(), subscription.getCode()))
                .thenReturn(StatusResponse.builder()
                        .subscription(subscription.getCode())
                        .partner(subscription.getPartner())
                        .status(SubscriptionStatus.ACTIVE.getId())
                        .message("Subscription successfully unblocked")
                        .build());

        StatusResponse statusResponse = subscriptionsController.unblock(expectedUser.getDocument(), subscription.getCode());

        Assertions.assertThat(statusResponse).isNotNull();

        Assertions.assertThat(statusResponse.getSubscription()).isEqualTo(subscription.getCode());

        Assertions.assertThat(statusResponse.getMessage()).isEqualTo("Subscription successfully unblocked");
    }

    @Test
    @DisplayName("updateContact updates contact information when successful")
    void update_UpdatesContact_WhenSuccessful(){
        Users expectedUser = UserCreator.createValidUser();

        Subscriptions subscription = SubscriptionCreator.createValidSubscription();

        ContactRequest contactRequest = ContactRequest.builder()
                .email(expectedUser.getEmail())
                .phone(expectedUser.getPhone())
                .build();

        BDDMockito.when(subscriptionsServiceMock.updateContact(expectedUser.getDocument(), subscription.getCode(), contactRequest))
                .thenReturn(StatusResponse.builder()
                        .subscription(subscription.getCode())
                        .partner(subscription.getPartner())
                        .status(SubscriptionStatus.ACTIVE.getId())
                        .message("User contact successfully updated")
                        .build());

        StatusResponse statusResponse = subscriptionsController.updateContact(expectedUser.getDocument(), subscription.getCode(), contactRequest);

        Assertions.assertThat(statusResponse).isNotNull();

        Assertions.assertThat(statusResponse.getSubscription()).isEqualTo(subscription.getCode());

        Assertions.assertThat(statusResponse.getMessage()).isEqualTo("User contact successfully updated");
    }
}