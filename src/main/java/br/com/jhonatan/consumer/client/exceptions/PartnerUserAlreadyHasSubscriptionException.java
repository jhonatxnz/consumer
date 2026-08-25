package br.com.jhonatan.consumer.client.exceptions;

public class PartnerUserAlreadyHasSubscriptionException extends RuntimeException {

    public PartnerUserAlreadyHasSubscriptionException(String message) {
        super(message);
    }
}
