package com.barraiser.onboarding.interview.jira;

public class JiraTransitionNotFoundException extends RuntimeException {
    public JiraTransitionNotFoundException(final String message) {
        super(message);
    }
}
