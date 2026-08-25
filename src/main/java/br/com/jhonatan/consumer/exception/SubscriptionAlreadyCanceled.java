package br.com.jhonatan.consumer.exception;

public class SubscriptionAlreadyCanceled extends RuntimeException {

    public SubscriptionAlreadyCanceled() {
        super("Subscription already canceled for user");
    }

    public SubscriptionAlreadyCanceled(String message) {
        super(message);
    }
}
