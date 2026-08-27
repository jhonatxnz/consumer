package br.com.jhonatan.consumer.util;

import br.com.jhonatan.consumer.model.Subscriptions;

import java.math.BigDecimal;

public class SubscriptionCreator {
    public static Subscriptions createSubscriptionToBeSaved (){
        return Subscriptions.builder()
                .name("Subscription test")
                .code("SUBSCRIPT10N T3ST")
                .description("Subscription description test")
                .status("1")
                .price(BigDecimal.valueOf(9.99))
                .category("A")
                .partner("Provider API")
                .build();
    }

    public static Subscriptions createValidSubscription (){
        return Subscriptions.builder()
                .id(1L)
                .name("Subscription test")
                .code("SUBSCRIPT10N T3ST")
                .description("Subscription description test")
                .status("1")
                .price(BigDecimal.valueOf(9.99))
                .category("A")
                .partner("Provider API")
                .build();
    }
}
