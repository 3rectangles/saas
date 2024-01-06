package com.barraiser.onboarding.auth.sso;

public interface EmailExtractorFromIdToken {
    String source();
    String getEmailFromIdToken(final String idToken);
}
