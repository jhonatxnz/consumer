package br.com.jhonatan.consumer.service;

import br.com.jhonatan.consumer.dto.ActivationResult;
import br.com.jhonatan.consumer.dto.ContactRequest;
import br.com.jhonatan.consumer.dto.StatusResponse;
import br.com.jhonatan.consumer.dto.SubscriptionDetail;

import java.util.List;

public interface SubscriptionsService {

    List<SubscriptionDetail> list(String document);

    ActivationResult activate(String document, String subscription, ContactRequest request);

    StatusResponse cancel(String document, String subscription);

    StatusResponse reactivate(String document, String subscription, ContactRequest request);

    StatusResponse block(String document, String subscription);

    StatusResponse unblock(String document, String subscription);

    StatusResponse updateContact(String document, String subscription, ContactRequest request);
}
