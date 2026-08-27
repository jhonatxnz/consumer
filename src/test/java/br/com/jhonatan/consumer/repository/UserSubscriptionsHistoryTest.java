package br.com.jhonatan.consumer.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

@DataJpaTest
@DisplayName("Tests for UserSubscriptionsHistoryTest")
class UserSubscriptionsHistoryTest {

    @Autowired
    UserSubscriptionsHistoryRepository userSubscriptionsHistoryRepository;

    @Test
    @DisplayName("Save creates user subscription history when successful")
    void save_CreatesUserSubscriptionHistory_WhenSuccessful(){

    }

    @Test
    @DisplayName("Save updates user subscription history when successful")
    void save_UpdatesUserSubscriptionHistory_WhenSuccessful(){

    }

    @Test
    @DisplayName("Delete removes user subscription history when successful")
    void save_RemovesUserSubscriptionHistory_WhenSuccessful(){

    }

}