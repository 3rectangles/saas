package com.barraiser.onboarding.user;

public class UserSignUpFailedException extends IllegalArgumentException {
    public UserSignUpFailedException(final String message) {
        super(message);
    }
}
