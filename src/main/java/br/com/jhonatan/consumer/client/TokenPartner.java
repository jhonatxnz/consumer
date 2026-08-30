package br.com.jhonatan.consumer.client;

import br.com.jhonatan.consumer.client.dto.ProviderTokenRequest;
import br.com.jhonatan.consumer.client.dto.ProviderTokenResponse;
import br.com.jhonatan.consumer.client.exceptions.PartnerIntegrationException;
import br.com.jhonatan.consumer.client.exceptions.PartnerInvalidDataException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class TokenPartner {
    private final RestClient providerRestClient;

    @Value("${provider.api.client-id}") String clientId;

    @Value("${provider.api.client-secret}") String clientSecret;

    public ProviderTokenResponse generateToken() {

        ProviderTokenRequest providerTokenRequest = ProviderTokenRequest.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build();

        return execute("generate token", () ->
                providerRestClient.post()
                        .uri("/api/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(providerTokenRequest)
                        .retrieve()
                        .body(ProviderTokenResponse.class));
    }

    private <T> T execute(String action, Supplier<T> call) {
        try {
            return call.get();
        } catch (HttpClientErrorException.BadRequest e) {
            throw new PartnerInvalidDataException("Invalid data to " + action + ": " + e.getMessage());
        } catch (RestClientException e) {
            throw new PartnerIntegrationException("Failed to " + action, e);
        }
    }
}
