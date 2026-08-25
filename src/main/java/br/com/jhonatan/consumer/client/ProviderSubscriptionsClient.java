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

@Component
@RequiredArgsConstructor
public class ProviderSubscriptionsClient {

    private final RestClient providerRestClient;

    public ProviderUserResponse findUser(String document) {
        try {
            return providerRestClient.get()
                    .uri("api/customers/document/{document}", document)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (HttpClientErrorException.NotFound e) {
            throw new PartnerUserNotFoundException("User " + document +  " not found" );
        } catch (RestClientException e) {
            throw new PartnerIntegrationException("Failed to find user " + document + " ", e);
        }
    }

    public void createUser(ProviderUserRequest request) {
        try {

            ProviderUserRequest providerUserRequest = ProviderUserRequest.builder()
                    .name(request.getName())
                    .document(request.getDocument())
                    .email(request.getEmail())
                    .phone(request.getPhone())
                    .build();

            providerRestClient.post()
                    .uri("api/customers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(providerUserRequest)
                    .retrieve()
                    .toBodilessEntity();
        }
        catch (HttpClientErrorException.BadRequest e) {
            throw new PartnerInvalidDataException("Invalid data for user creation: " + e.getMessage());
        } catch (HttpClientErrorException.Conflict e) {
            throw new PartnerUserAlreadyExistsException("User " + request.getDocument() +  " already has exists");
        }  catch (RestClientException e) {
            throw new PartnerIntegrationException("Failed to create user " + request.getDocument(), e);
        }
    }

    public ProviderUserResponse updateUser(String document, ProviderUpdateUserRequest request) {
        try {
            ProviderUpdateUserRequest providerUpdateUserRequest = ProviderUpdateUserRequest.builder()
                    .name(request.getName())
                    .email(request.getEmail())
                    .phone(request.getPhone())
                    .build();

            return providerRestClient.put()
                    .uri("api/customers/{document}", document)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(providerUpdateUserRequest)
                    .retrieve()
                    .body(ProviderUserResponse.class);
        }
        catch (HttpClientErrorException.BadRequest e) {
            throw new PartnerInvalidDataException("Invalid data for user update: " + e.getMessage());
        } catch (HttpClientErrorException.NotFound e) {
            throw new PartnerUserNotFoundException("User not found in partner");
        } catch (RestClientException e) {
            throw new PartnerIntegrationException("Failed to update user " + document + " ", e);
        }
    }

    public List<ProviderSubscriptionResponse> listSubscriptions(String document) {
        try {
            return providerRestClient.get()
                    .uri("api/customers/{document}/subscriptions", document)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        }
        catch (HttpClientErrorException.BadRequest e) {
            throw new PartnerInvalidDataException("Invalid data for listing subscriptions: " + e.getMessage());
        } catch (HttpClientErrorException.NotFound e) {
            throw new PartnerUserNotFoundException("User not found in partner");
        } catch (RestClientException e) {
            throw new PartnerIntegrationException("Failed to list subscriptions for " + document, e);
        }
    }

    public void createSubscription(String document, String code) {
        try {
            providerRestClient.post()
                    .uri("api/customers/{document}/subscriptions", document)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ProviderSubscriptionRequest(code))
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException.BadRequest e) {
            throw new PartnerInvalidDataException("Invalid data for subscription creation: " + e.getMessage());
        } catch (HttpClientErrorException.Conflict e) {
            throw new PartnerUserAlreadyHasSubscriptionException("User " + document +  "already has subscription " + code);
        } catch (HttpClientErrorException.NotFound e) {
            throw new PartnerUserNotFoundException("User not found in partner");
        } catch (RestClientException e) {
            throw new PartnerIntegrationException("Failed to create subscription for " + document, e);
        }
    }

    public void cancelSubscription(String document, String code) {
        try {
             providerRestClient.delete()
                    .uri("api/customers/{document}/subscriptions/{subscription}", document, code)
                    .retrieve()
                    .toBodilessEntity();
        }
        catch (HttpClientErrorException.BadRequest e) {
            throw new PartnerInvalidDataException("Invalid data for delete subscription: " + e.getMessage());
        } catch (HttpClientErrorException.NotFound e) {
            throw new PartnerUserNotFoundException(e.getMessage());
        } catch (RestClientException e) {
            throw new PartnerIntegrationException("Failed to delete subscription for " + document, e);
        }
    }
}
