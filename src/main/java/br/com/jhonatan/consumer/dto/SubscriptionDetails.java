package br.com.jhonatan.consumer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionDetails {

    private String subscription;
    private String email;
    private String phone;
    private String partner;
    private String status;
    private LocalDateTime createdAt;
}
