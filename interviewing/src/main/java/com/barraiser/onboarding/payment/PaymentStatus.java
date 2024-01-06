package com.barraiser.onboarding.payment;

import lombok.Getter;

public enum PaymentStatus {
    ORDER_CREATED("order_created"),
    AUTHORIZED("authorized"),
    CAPTURED("captured"),
    CANCELED("canceled"),
    FAILED("failed"),
    PAID("paid");

    @Getter
    private String val;

    PaymentStatus(final String val) {
        this.val = val;
    }

    public static PaymentStatus fromString(final String str) {
        switch (str) {
            case "order_created":
                return ORDER_CREATED;
            case "authorized":
                return AUTHORIZED;
            case "captured":
                return CAPTURED;
            case "canceled":
                return CANCELED;
            case "failed":
                return FAILED;
            case "paid":
                return PAID;
            default:
                return null;
        }
    }
}
