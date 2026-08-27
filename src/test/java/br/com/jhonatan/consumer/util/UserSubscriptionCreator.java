package br.com.jhonatan.consumer.util;

import br.com.jhonatan.consumer.enums.SubscriptionStatus;
import br.com.jhonatan.consumer.model.Subscriptions;
import br.com.jhonatan.consumer.model.UserSubscriptions;
import br.com.jhonatan.consumer.model.Users;

import java.time.LocalDateTime;

public class UserSubscriptionCreator {
    public static UserSubscriptions createUserSubscriptionToBeSaved(Users user, Subscriptions subscription){
        return UserSubscriptions.builder()
                .userId(user.getId())
                .subscriptionId(subscription.getId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .canceledAt(null)
                .status(SubscriptionStatus.ACTIVE.value())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build();
    }
}
