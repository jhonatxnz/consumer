package br.com.jhonatan.consumer.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public enum SubscriptionStatus {

    INACTIVE("0"),
    ACTIVE("1"),
    BLOCKED("2");

    private final String id;

    public String value() { return id; }
}