package com.barraiser.onboarding.auth.enums;

public enum Action {

    READ("READ"),

    WRITE("WRITE");

    private final String action;

    public String getValue() {
        return this.action;
    }

    Action(final String action) {
        this.action = action;
    }
}
