package br.com.jhonatan.consumer.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderTokenResponse {

    private String accessToken;
    private String tokenType;
    private long expiresIn;
}
