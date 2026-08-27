package br.com.jhonatan.consumer.repository;

import br.com.jhonatan.consumer.model.Subscriptions;
import br.com.jhonatan.consumer.util.SubscriptionCreator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import java.util.Optional;

@DataJpaTest
@DisplayName("Tests for SubscriptionsRepository")
class SubscriptionsRepositoryTest {

    @Autowired
    SubscriptionsRepository subscriptionsRepository;

    @Test
    @DisplayName("Save creates subscription when successful")
    void save_PersistSubscription_WhenSuccessful(){
        Subscriptions subscriptionToBeSaved = SubscriptionCreator.createSubscriptionToBeSaved();

        Subscriptions savedSubscription = this.subscriptionsRepository.save(subscriptionToBeSaved);

        Assertions.assertThat(savedSubscription).isNotNull();

        Assertions.assertThat(savedSubscription.getId()).isNotNull();

        Assertions.assertThat(savedSubscription.getName()).isEqualTo(subscriptionToBeSaved.getName());
    }

    @Test
    @DisplayName("Save updates subscription when successful")
    void save_UpdatesSubscription_WhenSuccessful(){
        Subscriptions subscriptionToBeSaved = SubscriptionCreator.createSubscriptionToBeSaved();

        Subscriptions savedSubscription = this.subscriptionsRepository.save(subscriptionToBeSaved);

        savedSubscription.setName("Subscription updated test");

        Subscriptions updatedSubscription = this.subscriptionsRepository.save(savedSubscription);

        Assertions.assertThat(savedSubscription).isNotNull();

        Assertions.assertThat(savedSubscription.getId()).isNotNull();

        Assertions.assertThat(savedSubscription.getName()).isEqualTo(updatedSubscription.getName());
    }

    @Test
    @DisplayName("Delete removes subscription when successful")
    void delete_DeleteSubscription_WhenSuccessful(){
        Subscriptions subscriptionToBeSaved = SubscriptionCreator.createSubscriptionToBeSaved();

        Subscriptions savedSubscription = this.subscriptionsRepository.save(subscriptionToBeSaved);

        this.subscriptionsRepository.delete(savedSubscription);

        Optional<Subscriptions> deletedSubscription = subscriptionsRepository.findById(savedSubscription.getId());

        Assertions.assertThat(deletedSubscription).isEmpty();
    }

    @Test
    @DisplayName("Find by code returns subscription when successful")
    void find_ReturnsSubscription_WhenSuccessful(){
        Subscriptions subscriptionToBeSaved = SubscriptionCreator.createSubscriptionToBeSaved();

        Subscriptions savedSubscription = this.subscriptionsRepository.save(subscriptionToBeSaved);

        Optional<Subscriptions> subscriptionToBeFound = subscriptionsRepository.findByCode(savedSubscription.getCode());

        Assertions.assertThat(subscriptionToBeFound).isPresent().isNotNull();

        Assertions.assertThat(subscriptionToBeFound.get().getCode()).isEqualTo(subscriptionToBeSaved.getCode());
    }

    @Test
    @DisplayName("Find by code returns empty when no subscription is found")
    void find_ReturnsEmpty_WhenSuccessful(){
        Optional<Subscriptions> subscriptions = this.subscriptionsRepository.findByCode("NotExistingCode");

        Assertions.assertThat(subscriptions).isEmpty();

    }
}