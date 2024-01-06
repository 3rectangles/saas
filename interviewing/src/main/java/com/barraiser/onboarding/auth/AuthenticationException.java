package com.barraiser.onboarding.auth;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

@Data
@Log4j2
public class AuthenticationException extends RuntimeException {
    public static final String USER_NOT_FOUND = "USER_NOT_FOUND";
    public static final String USER_NOT_AUTHENTICATED = "USER_NOT_AUTHENTICATED";

    private final String type;

    public AuthenticationException(final String message) {
        super(message);
        this.type = USER_NOT_AUTHENTICATED;
    }

    public AuthenticationException(final String type, final String message) {
        super(message);
        this.type = type;
    }
}
