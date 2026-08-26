package br.com.jhonatan.consumer.client.exceptions;

public class PartnerSubscriptionAlreadyCanceledException extends RuntimeException {

    public PartnerSubscriptionAlreadyCanceledException(String message) {
        super(message);
    }
}
