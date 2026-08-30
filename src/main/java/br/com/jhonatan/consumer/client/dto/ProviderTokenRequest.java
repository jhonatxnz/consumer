package br.com.jhonatan.consumer.client.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProviderTokenRequest {
    @NotBlank
    private String clientId;

    @NotBlank
    private String clientSecret;
}
