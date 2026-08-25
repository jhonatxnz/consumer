package br.com.jhonatan.consumer.client.exceptions;

import org.springframework.web.client.RestClientException;

public class PartnerIntegrationException extends RuntimeException {

    public PartnerIntegrationException(String document, RestClientException message) {
        super(document + message);
    }

    public PartnerIntegrationException(String message) {
        super(message);
    }
}
