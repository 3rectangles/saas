package com.barraiser.onboarding.resume;

import feign.RequestInterceptor;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;

@Log4j2
@AllArgsConstructor
public class RChilliConfig {
    @Bean("RChilliRequestInterceptor")
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("Content-Type", "application/json");
            requestTemplate.header("Accept", "application/json");
        };
    }
}
