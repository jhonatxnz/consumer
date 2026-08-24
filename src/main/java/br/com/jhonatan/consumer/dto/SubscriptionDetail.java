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
public class SubscriptionDetail {

    private String subscription;
    private LocalDateTime createdAt;
    private String status;
    private String partner;
    private String email;
    private String phone;
}
