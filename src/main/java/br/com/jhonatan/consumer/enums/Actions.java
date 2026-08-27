package br.com.jhonatan.consumer.enums;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum Actions {

    INACTIVATE("INACTIVATE"),
    ACTIVATE("ACTIVATE"),
    REACTIVATE("REACTIVATE"),
    BLOCKED("BLOCKED"),
    UNBLOCKED("UNBLOCKED");

    private final String action;

    Actions(String action) {
        this.action = action;
    }

    public String value() {
        return action;
    }
}