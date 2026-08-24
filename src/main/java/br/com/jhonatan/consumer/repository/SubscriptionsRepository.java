package br.com.jhonatan.consumer.repository;

import br.com.jhonatan.consumer.model.Subscriptions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriptionsRepository extends JpaRepository<Subscriptions, Long> {

    Optional<Subscriptions> findByCode(String code);
}
