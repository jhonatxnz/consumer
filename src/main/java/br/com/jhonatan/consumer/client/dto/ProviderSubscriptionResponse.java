package br.com.jhonatan.consumer.client.dto;

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
public class ProviderSubscriptionResponse {

    private String name;

    private String code;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String status;
}