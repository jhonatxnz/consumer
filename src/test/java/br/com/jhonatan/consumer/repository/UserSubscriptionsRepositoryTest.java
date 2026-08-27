package br.com.jhonatan.consumer.repository;

import br.com.jhonatan.consumer.enums.SubscriptionStatus;
import br.com.jhonatan.consumer.model.Subscriptions;
import br.com.jhonatan.consumer.model.UserSubscriptions;
import br.com.jhonatan.consumer.model.Users;
import br.com.jhonatan.consumer.util.SubscriptionCreator;
import br.com.jhonatan.consumer.util.UserCreator;
import br.com.jhonatan.consumer.util.UserSubscriptionCreator;
import jakarta.validation.ConstraintViolationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@DisplayName("Tests for UserSubscriptionsRepository")
class UserSubscriptionsRepositoryTest {

    @Autowired
    UserSubscriptionsRepository userSubscriptionsRepository;

    @Test
    @DisplayName("Save creates user subscription when successful")
    void save_CreatesUserSubscription_WhenSuccessful(){
        Users user = UserCreator.createValidUser();

        Subscriptions subscription = SubscriptionCreator.createValidSubscription();

        UserSubscriptions userSubscriptionToBeSaved = UserSubscriptionCreator.createUserSubscriptionToBeSaved(user, subscription);

        UserSubscriptions savedUserSubscription = this.userSubscriptionsRepository.save(userSubscriptionToBeSaved);

        Assertions.assertThat(savedUserSubscription).isNotNull();

        Assertions.assertThat(savedUserSubscription.getId()).isNotNull();

        Assertions.assertThat(savedUserSubscription.getUserId()).isEqualTo(user.getId());

        Assertions.assertThat(savedUserSubscription.getSubscriptionId()).isEqualTo(subscription.getId());
    }

    @Test
    @DisplayName("Save throws DataIntegrityViolationException when customer is null")
    void save_ThrowsDataIntegrityViolationException_WhenCustomerIsNull() {
        Subscriptions subscriptionToBeSaved = SubscriptionCreator.createSubscriptionToBeSaved();
        
        UserSubscriptions userSubscription = UserSubscriptions.builder()
                .userId(null)
                .subscriptionId(subscriptionToBeSaved.getId())
                .status(SubscriptionStatus.ACTIVE.value())
                .build();

        Assertions.assertThatThrownBy(() -> {
            userSubscriptionsRepository.save(userSubscription);
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Save throws DataIntegrityViolationException when subscription is null")
    void save_ThrowsDataIntegrityViolationException_WhenSubscriptionIsNull() {
        Users user = UserCreator.createUserToBeSaved();

        UserSubscriptions userSubscription = UserSubscriptions.builder()
                .userId(user.getId())
                .subscriptionId(null)
                .status(SubscriptionStatus.ACTIVE.value())
                .build();

        Assertions.assertThatThrownBy(() -> {
            userSubscriptionsRepository.save(userSubscription);
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Save throws DataIntegrityViolationException when customer not exists")
    void save_ThrowsConstraintViolationException_WhenCustomerNotExists() {
        Subscriptions subscription = SubscriptionCreator.createSubscriptionToBeSaved();

        UserSubscriptions userSubscription = UserSubscriptions.builder()
                .userId(2L)
                .subscriptionId(subscription.getId())
                .status(SubscriptionStatus.ACTIVE.value())
                .build();

        Assertions.assertThatThrownBy(() -> {
            userSubscriptionsRepository.save(userSubscription);
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Save throws DataIntegrityViolationException when subscription not exists")
    void save_ThrowsConstraintViolationException_WhenSubscriptionNotExists() {
        Users user = UserCreator.createUserToBeSaved();

        UserSubscriptions userSubscription = UserSubscriptions.builder()
                .userId(user.getId())
                .subscriptionId(2L)
                .status(SubscriptionStatus.ACTIVE.value())
                .build();

        Assertions.assertThatThrownBy(() -> {
            userSubscriptionsRepository.save(userSubscription);
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Save updates user subscription when successful")
    void save_UpdatesUserSubscription_WhenSuccessful(){
        Users validUser = UserCreator.createValidUser();

        Subscriptions validSubscription = SubscriptionCreator.createValidSubscription();

        UserSubscriptions userSubscriptionToBeSaved = UserSubscriptionCreator.createUserSubscriptionToBeSaved(validUser, validSubscription);

        UserSubscriptions savedUserSubscription = userSubscriptionsRepository.save(userSubscriptionToBeSaved);

        savedUserSubscription.setStatus(SubscriptionStatus.INACTIVE.value());

        UserSubscriptions updateUserSubscription = userSubscriptionsRepository.save(userSubscriptionToBeSaved);

        Assertions.assertThat(updateUserSubscription).isNotNull();

        Assertions.assertThat(updateUserSubscription.getId()).isNotNull();

        Assertions.assertThat(updateUserSubscription.getStatus()).isEqualTo(savedUserSubscription.getStatus());
    }

    @Test
    @DisplayName("Delete removes user subscription when successful")
    void delete_RemovesUserSubscription_WhenSuccessful(){
        Users validUser = UserCreator.createValidUser();

        Subscriptions validSubscription = SubscriptionCreator.createValidSubscription();

        UserSubscriptions userSubscriptionToBeSaved = UserSubscriptionCreator.createUserSubscriptionToBeSaved(validUser, validSubscription);

        UserSubscriptions savedUserSubscription = userSubscriptionsRepository.save(userSubscriptionToBeSaved);

        this.userSubscriptionsRepository.delete(savedUserSubscription);

        Optional<UserSubscriptions> notFoundedUserSubscription = userSubscriptionsRepository.findById(savedUserSubscription.getSubscriptionId());

        Assertions.assertThat(notFoundedUserSubscription).isEmpty();
    }

    @Test
    @DisplayName("Find by user id returns user subscriptions when successful")
    void findByUserId_ReturnsUserSubscriptions_WhenSuccessful(){
        Users validUser = UserCreator.createValidUser();

        Subscriptions validSubscription = SubscriptionCreator.createValidSubscription();

        UserSubscriptions userSubscriptionToBeSaved = UserSubscriptionCreator.createUserSubscriptionToBeSaved(validUser, validSubscription);

        this.userSubscriptionsRepository.save(userSubscriptionToBeSaved);

        Assertions.assertThat(this.userSubscriptionsRepository.findByUserId(validUser.getId())).isNotEmpty();
    }

    @Test
    @DisplayName("Find by user id returns empty when user subscriptions is not found")
    void findByUserId_ReturnsEmpty_WhenSuccessful(){
        Users validUser = UserCreator.createValidUser();

        Assertions.assertThat(this.userSubscriptionsRepository.findByUserId(validUser.getId())).isEmpty();
    }

    @Test
    @DisplayName("Find by user id and subscription id returns user subscription when successful")
    void findByUserIdAndSubscription_ReturnsUserSubscriptions_WhenSuccessful(){
        Users validUser = UserCreator.createValidUser();

        Subscriptions validSubscription = SubscriptionCreator.createValidSubscription();

        UserSubscriptions userSubscriptionToBeSaved = UserSubscriptionCreator.createUserSubscriptionToBeSaved(validUser, validSubscription);

        this.userSubscriptionsRepository.save(userSubscriptionToBeSaved);

        Assertions.assertThat(this.userSubscriptionsRepository.findByUserIdAndSubscriptionId(validUser.getId(), validSubscription.getId())).isNotEmpty();
    }

    @Test
    @DisplayName("Find by user id and subscription id returns empty when user subscription is not found")
    void findByUserIdAndSubscription_ReturnsEmpty_WhenSuccessful(){
        Users validUser = UserCreator.createValidUser();

        Subscriptions validSubscription = SubscriptionCreator.createValidSubscription();

        Assertions.assertThat(this.userSubscriptionsRepository.findByUserIdAndSubscriptionId(validUser.getId(), validSubscription.getId())).isEmpty();
    }

    @Test
    @DisplayName("Find by user Id and status returns user subscription when successful")
    void findByUserIdAndStatus_ReturnsUserSubscriptions_WhenSuccessful(){

        Users validUser = UserCreator.createValidUser();

        Subscriptions validSubscription = SubscriptionCreator.createValidSubscription();

        UserSubscriptions userSubscriptionToBeSaved = UserSubscriptionCreator.createUserSubscriptionToBeSaved(validUser, validSubscription);

        this.userSubscriptionsRepository.save(userSubscriptionToBeSaved);

        Assertions.assertThat(this.userSubscriptionsRepository.findByUserIdAndStatus(validUser.getId(), validSubscription.getStatus())).isNotEmpty();
    }

    @Test
    @DisplayName("Find by user Id and status returns empty when user subscription is not found")
    void findByUserIdAndStatus_ReturnsEmpty_WhenSuccessful(){
        Users validUser = UserCreator.createValidUser();

        Subscriptions validSubscription = SubscriptionCreator.createValidSubscription();

        Assertions.assertThat(this.userSubscriptionsRepository.findByUserIdAndStatus(validUser.getId(), validSubscription.getStatus())).isEmpty();
    }
}