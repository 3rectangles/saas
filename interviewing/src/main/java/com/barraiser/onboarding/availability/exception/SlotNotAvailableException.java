package com.barraiser.onboarding.availability.exception;

public class SlotNotAvailableException extends IllegalArgumentException {
    public SlotNotAvailableException(final String s) {
        super(s);
    }
}
