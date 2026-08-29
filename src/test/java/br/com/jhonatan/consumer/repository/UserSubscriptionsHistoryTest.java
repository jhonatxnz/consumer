package br.com.jhonatan.consumer.repository;

import br.com.jhonatan.consumer.model.UserSubscriptionsHistory;
import br.com.jhonatan.consumer.util.UserSubscriptionsHistoryCreator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

@DataJpaTest
@DisplayName("Tests for UserSubscriptionsHistoryRepository")
class UserSubscriptionsHistoryTest {

    @Autowired
    UserSubscriptionsHistoryRepository userSubscriptionsHistoryRepository;


    @Test
    @DisplayName("Save creates user subscription history when successful")
    void save_CreatesUserSubscriptionHistory_WhenSuccessful() {
        UserSubscriptionsHistory historyToBeSaved = UserSubscriptionsHistoryCreator.createHistoryToBeSaved();

        UserSubscriptionsHistory savedHistory = userSubscriptionsHistoryRepository.save(historyToBeSaved);

        Assertions.assertThat(savedHistory).isNotNull();

        Assertions.assertThat(savedHistory.getId()).isNotNull();

        Assertions.assertThat(savedHistory.getAction()).isEqualTo(historyToBeSaved.getAction());

        Assertions.assertThat(savedHistory.getSubscriptionId()).isEqualTo(historyToBeSaved.getSubscriptionId());

        Assertions.assertThat(savedHistory.getUserId()).isEqualTo(historyToBeSaved.getUserId());
    }

    @Test
    @DisplayName("Save updates user subscription history when successful")
    void save_UpdatesUserSubscriptionHistory_WhenSuccessful() {
        UserSubscriptionsHistory savedHistory = userSubscriptionsHistoryRepository.save(UserSubscriptionsHistoryCreator.createHistoryToBeSaved());

        savedHistory.setAction("CANCEL");

        UserSubscriptionsHistory updatedHistory = userSubscriptionsHistoryRepository.save(savedHistory);

        Assertions.assertThat(updatedHistory).isNotNull();

        Assertions.assertThat(updatedHistory.getId()).isEqualTo(savedHistory.getId());

        Assertions.assertThat(updatedHistory.getAction()).isEqualTo("CANCEL");
    }

    @Test
    @DisplayName("Delete removes user subscription history when successful")
    void save_RemovesUserSubscriptionHistory_WhenSuccessful() {
        UserSubscriptionsHistory savedHistory = userSubscriptionsHistoryRepository.save(UserSubscriptionsHistoryCreator.createHistoryToBeSaved());

        userSubscriptionsHistoryRepository.delete(savedHistory);

        Optional<UserSubscriptionsHistory> historyOptional =
                userSubscriptionsHistoryRepository.findById(savedHistory.getId());

        Assertions.assertThat(historyOptional).isEmpty();
    }
}