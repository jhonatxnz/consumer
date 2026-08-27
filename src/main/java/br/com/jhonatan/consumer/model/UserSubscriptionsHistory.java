package br.com.jhonatan.consumer.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "user_subscriptions_history")
public class UserSubscriptionsHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subscription_id", nullable = false)
    private Long subscriptionId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "action", nullable = false)
    private String action;

    @Column(name = "date", nullable = false)
    private LocalDateTime date;

    @Column(name = "partner", nullable = false)
    private String partner;

}
