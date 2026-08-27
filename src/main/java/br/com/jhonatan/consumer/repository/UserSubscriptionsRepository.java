package br.com.jhonatan.consumer.repository;

import br.com.jhonatan.consumer.model.UserSubscriptions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserSubscriptionsRepository extends JpaRepository<UserSubscriptions, Long> {

    List<UserSubscriptions> findByUserId(Long userId);

    Optional<UserSubscriptions> findByUserIdAndSubscriptionId(Long userId, Long subscriptionId);

    List<UserSubscriptions> findByUserIdAndStatus(Long customerId, String status);
}
