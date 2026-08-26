package br.com.jhonatan.consumer.client.exceptions;

import org.springframework.web.client.RestClientException;

public class PartnerIntegrationException extends RuntimeException {

    public PartnerIntegrationException(String message, RestClientException cause) {
        super(message + ": " + cause.getMessage(), cause);
    }

    public PartnerIntegrationException(String message) {
        super(message);
    }
}
