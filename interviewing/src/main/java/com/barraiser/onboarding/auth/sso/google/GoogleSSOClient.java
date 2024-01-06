package com.barraiser.onboarding.auth.sso.google;

import com.barraiser.onboarding.auth.sso.google.dto.GoogleOpenIdConfigDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "google-sso-client", url = "https://accounts.google.com")
public interface GoogleSSOClient {

    @GetMapping("/.well-known/openid-configuration")
    GoogleOpenIdConfigDTO getOpenIdConfig();
}
