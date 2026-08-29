package br.com.jhonatan.consumer.util;

import br.com.jhonatan.consumer.model.UserSubscriptionsHistory;

import java.time.LocalDateTime;

public class UserSubscriptionsHistoryCreator {
    public static UserSubscriptionsHistory createHistoryToBeSaved() {
        return UserSubscriptionsHistory.builder()
                .subscriptionId(1L)
                .userId(1L)
                .action("ACTIVATE")
                .partner("Provider API")
                .date(LocalDateTime.now())
                .build();
    }
}
