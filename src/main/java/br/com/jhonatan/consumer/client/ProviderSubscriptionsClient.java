package br.com.jhonatan.consumer.client;

import br.com.jhonatan.consumer.client.dto.*;
import br.com.jhonatan.consumer.client.exceptions.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class ProviderSubscriptionsClient {

    private final RestClient providerRestClient;

    public ProviderUserResponse findUser(String document) {
        return execute("find user " + document, () ->
                providerRestClient.get()
                        .uri("/api/customers/document/{document}", document)
                        .retrieve()
                        .body(ProviderUserResponse.class));
    }

    public void createUser(ProviderUserRequest request) {
        try {
            executeVoid("create user " + request.getDocument(), () ->
                    providerRestClient.post()
                            .uri("/api/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(request)
                            .retrieve()
                            .toBodilessEntity());
        } catch (HttpClientErrorException.Conflict e) {
            throw new PartnerUserAlreadyExistsException("User " + request.getDocument() + " already exists in the partner");
        }
    }

    public ProviderUserResponse updateUser(String document, ProviderUpdateUserRequest request) {
        return execute("update user " + document, () ->
                providerRestClient.put()
                        .uri("/api/customers/{document}", document)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(request)
                        .retrieve()
                        .body(ProviderUserResponse.class));
    }

    public List<ProviderSubscriptionResponse> listSubscriptions(String document) {
        return this.<List<ProviderSubscriptionResponse>>execute("list subscriptions for " + document, () ->
                providerRestClient.get()
                        .uri("/api/customers/{document}/subscriptions", document)
                        .retrieve()
                        .body(new ParameterizedTypeReference<List<ProviderSubscriptionResponse>>() {}));
    }

    public void createSubscription(String document, String code) {
        try {
            executeVoid("create subscription " + code + " for " + document, () ->
                    providerRestClient.post()
                            .uri("/api/customers/{document}/subscriptions", document)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(new ProviderSubscriptionRequest(code))
                            .retrieve()
                            .toBodilessEntity());
        } catch (HttpClientErrorException.Conflict e) {
            throw new PartnerUserAlreadyHasSubscriptionException("User " + document + " already has subscription " + code);
        }
    }

    public void cancelSubscription(String document, String code) {
        try {
            executeVoid("cancel subscription " + code + " for " + document, () ->
                    providerRestClient.delete()
                            .uri("/api/customers/{document}/subscriptions/{subscription}", document, code)
                            .retrieve()
                            .toBodilessEntity());
        } catch (HttpClientErrorException.Conflict e) {
            throw new PartnerSubscriptionAlreadyCanceledException("Subscription " + code + " already canceled in the partner for user " + document);
        }
    }

    private <T> T execute(String action, Supplier<T> call) {
        try {
            return call.get();
        } catch (HttpClientErrorException.BadRequest e) {
            throw new PartnerInvalidDataException("Invalid data to " + action + ": " + e.getMessage());
        } catch (HttpClientErrorException.NotFound e) {
            throw new PartnerUserNotFoundException("Not found in the partner while trying to " + action);
        } catch (HttpClientErrorException.Conflict e) {
            throw e;
        } catch (RestClientException e) {
            throw new PartnerIntegrationException("Failed to " + action, e);
        }
    }

    private void executeVoid(String action, Runnable call) {
        execute(action, () -> {
            call.run();
            return null;
        });
    }
}
