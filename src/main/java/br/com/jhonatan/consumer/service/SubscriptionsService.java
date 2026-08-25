package br.com.jhonatan.consumer.service;

import br.com.jhonatan.consumer.dto.StatusResponse;
import br.com.jhonatan.consumer.dto.ContactRequest;
import br.com.jhonatan.consumer.dto.SubscriptionDetails;

import java.util.List;

public interface SubscriptionsService {

    List<SubscriptionDetails> list(String document);

    StatusResponse activate(String document, String subscriptionCode, ContactRequest request);

    StatusResponse cancel(String document, String subscriptionCode);

    StatusResponse block(String document, String subscriptionCode);

    StatusResponse unblock(String document, String subscriptionCode);

    StatusResponse updateContact(String document, String subscriptionCode, ContactRequest request);
}
