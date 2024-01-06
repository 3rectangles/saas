package com.barraiser.onboarding.auth;

public class AuthorizationException extends RuntimeException {
    public AuthorizationException(final String message) {
        super(message);
    }
    public AuthorizationException() {
        super("User is not authorized to perform this operation");
    }
}
