package com.barraiser.onboarding.auth.apikey;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ApiKeyScope {
    PARTNER("partner"),
    ADMIN("admin");

    @Getter
    private String value;
}
