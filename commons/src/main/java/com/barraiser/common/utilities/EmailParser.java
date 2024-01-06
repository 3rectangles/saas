package com.barraiser.common.utilities;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class EmailParser {
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";

    private final static Pattern pattern = Pattern.compile(EMAIL_REGEX);

    public static void validateEmail(final String email) {
        final Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Email not following proper format: " + email);
        }
    }

    public static String getDomainFromEmail(final String email) {
        return email.substring(email.indexOf("@") + 1);
    }
}
