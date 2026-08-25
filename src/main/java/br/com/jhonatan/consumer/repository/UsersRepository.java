package br.com.jhonatan.consumer.repository;

import br.com.jhonatan.consumer.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {

    Optional<Users> findByDocument(String document);
}
