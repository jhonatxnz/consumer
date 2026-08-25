package br.com.jhonatan.consumer.client.exceptions;

public class PartnerUserNotFoundException extends RuntimeException {

    public PartnerUserNotFoundException(String message) {
        super(message);
    }
}
