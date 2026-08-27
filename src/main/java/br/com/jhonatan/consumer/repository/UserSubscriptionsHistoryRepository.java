package br.com.jhonatan.consumer.repository;

import br.com.jhonatan.consumer.model.UserSubscriptionsHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSubscriptionsHistoryRepository extends JpaRepository<UserSubscriptionsHistory, Long> {
}
