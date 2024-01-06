package com.barraiser.onboarding.auth.sso.microsoft;

import com.auth0.jwt.JWT;
import com.barraiser.onboarding.auth.sso.EmailExtractorFromIdToken;
import com.barraiser.onboarding.auth.sso.SSOProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

@Component
@Log
@RequiredArgsConstructor
public class MicrosoftEmailExtractorFromIdToken implements EmailExtractorFromIdToken {
    @Override
    public String source() {
        return SSOProvider.MICROSOFT;
    }

    @Override
    public String getEmailFromIdToken(String idToken) {
        return JWT.decode(idToken).getClaims().get("email").asString();
    }
}
