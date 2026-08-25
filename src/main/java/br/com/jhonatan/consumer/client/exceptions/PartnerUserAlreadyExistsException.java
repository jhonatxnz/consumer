package br.com.jhonatan.consumer.client.exceptions;

public class PartnerUserAlreadyExistsException extends RuntimeException {
    public PartnerUserAlreadyExistsException(String message) {
        super(message);
    }
}
