package br.com.jhonatan.consumer.exception;

public class SubscriptionAlreadyUnblocked extends RuntimeException {

    public SubscriptionAlreadyUnblocked() {
        super("Subscription already unblocked for user");
    }

    public SubscriptionAlreadyUnblocked(String message) {
        super(message);
    }
}
