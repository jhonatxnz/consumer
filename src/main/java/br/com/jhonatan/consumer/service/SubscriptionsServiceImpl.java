package br.com.jhonatan.consumer.service;

import br.com.jhonatan.consumer.dto.ActivationResult;
import br.com.jhonatan.consumer.dto.ContactRequest;
import br.com.jhonatan.consumer.dto.StatusResponse;
import br.com.jhonatan.consumer.dto.SubscriptionDetail;
import br.com.jhonatan.consumer.repository.SubscriptionsRepository;
import br.com.jhonatan.consumer.repository.UserSubscriptionsRepository;
import br.com.jhonatan.consumer.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionsServiceImpl implements SubscriptionsService {

    private final UsersRepository usersRepository;
    private final SubscriptionsRepository subscriptionsRepository;
    private final UserSubscriptionsRepository userSubscriptionsRepository;

    @Override
    public List<SubscriptionDetail> list(String document) {
        // TODO: list the customer's subscriptions (subscription, createdAt, status, partner, email, phone)
        throw new UnsupportedOperationException("TODO: SubscriptionsServiceImpl.list");
    }

    @Override
    public ActivationResult activate(String document, String subscription, ContactRequest request) {
        // TODO: activate the subscription for the customer.
        // This service consumes the provider API - you'll likely need to call it here
        // (e.g. to validate the customer/subscription) before writing to users_subscriptions.
        throw new UnsupportedOperationException("TODO: SubscriptionsServiceImpl.activate");
    }

    @Override
    public StatusResponse cancel(String document, String subscription) {
        // TODO: cancel the subscription
        throw new UnsupportedOperationException("TODO: SubscriptionsServiceImpl.cancel");
    }

    @Override
    public StatusResponse reactivate(String document, String subscription, ContactRequest request) {
        // TODO: reactivate a previously canceled/blocked subscription
        throw new UnsupportedOperationException("TODO: SubscriptionsServiceImpl.reactivate");
    }

    @Override
    public StatusResponse block(String document, String subscription) {
        // TODO: block the subscription
        throw new UnsupportedOperationException("TODO: SubscriptionsServiceImpl.block");
    }

    @Override
    public StatusResponse unblock(String document, String subscription) {
        // TODO: unblock the subscription
        throw new UnsupportedOperationException("TODO: SubscriptionsServiceImpl.unblock");
    }

    @Override
    public StatusResponse updateContact(String document, String subscription, ContactRequest request) {
        // TODO: update the email/phone tied to this subscription
        throw new UnsupportedOperationException("TODO: SubscriptionsServiceImpl.updateContact");
    }
}
