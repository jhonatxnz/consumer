package br.com.jhonatan.consumer.exception;

public class SubscriptionAlreadyBlocked extends RuntimeException {

    public SubscriptionAlreadyBlocked() {
        super("Subscription already blocked for user");
    }

    public SubscriptionAlreadyBlocked(String message) {
        super(message);
    }
}
