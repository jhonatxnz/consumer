package br.com.jhonatan.consumer.exception;

public class SubscriptionNotFoundException extends RuntimeException {

    public SubscriptionNotFoundException() {
        super("Subscription not found");
    }

    public SubscriptionNotFoundException(String message) {
        super(message);
    }
}
