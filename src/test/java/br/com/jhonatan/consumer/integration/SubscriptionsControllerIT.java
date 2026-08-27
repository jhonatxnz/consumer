package br.com.jhonatan.consumer.integration;

import br.com.jhonatan.consumer.ConsumerApplication;
import br.com.jhonatan.consumer.repository.SubscriptionsRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

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

    @AfterEach
    void tearDown() {
        subscriptionsRepository.deleteAll();
    }

    @Test
    @DisplayName("list returns customer by username when successful")
    void list() {

    }

    @Test
    @DisplayName("list returns customer by username when successful")
    void activate() {

    }

    @Test
    @DisplayName("list returns customer by username when successful")
    void cancel() {

    }

    @Test
    @DisplayName("list returns customer by username when successful")
    void block() {

    }

    @Test
    @DisplayName("list returns customer by username when successful")
    void unblock() {

    }

    @Test
    @DisplayName("list returns customer by username when successful")
    void updateContact() {

    }

}
