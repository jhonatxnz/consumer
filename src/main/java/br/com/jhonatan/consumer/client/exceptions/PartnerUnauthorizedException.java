package br.com.jhonatan.consumer.client.exceptions;

public class PartnerUnauthorizedException extends RuntimeException {

    public PartnerUnauthorizedException(String message) {
        super(message);
    }
}
