package br.com.jhonatan.consumer.controller;

import br.com.jhonatan.consumer.service.SubscriptionsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionsControllerTest {

    @InjectMocks
    SubscriptionsController subscriptionsController;

    @Mock
    SubscriptionsService subscriptionsServiceMock;

    @Test
    @DisplayName("listSubscriptions returns list of subscriptions by customer document when successful")
    void listSubscriptions(){

    }

    @Test
    @DisplayName("listSubscriptions returns list of subscriptions by customer document when successful")
    void listSubscriptionsEmpty(){

    }

    @Test
    @DisplayName("listSubscriptions returns list of subscriptions by customer document when successful")
    void activate(){

    }

    @Test
    @DisplayName("listSubscriptions returns list of subscriptions by customer document when successful")
    void cancel(){

    }

    @Test
    @DisplayName("listSubscriptions returns list of subscriptions by customer document when successful")
    void block(){

    }

    @Test
    @DisplayName("listSubscriptions returns list of subscriptions by customer document when successful")
    void unblock(){

    }

    @Test
    @DisplayName("listSubscriptions returns list of subscriptions by customer document when successful")
    void updateContact(){

    }

}