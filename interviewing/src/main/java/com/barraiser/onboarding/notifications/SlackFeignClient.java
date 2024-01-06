package com.barraiser.onboarding.notifications;

import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "slack-feign-client",
        url = "https://slack.com/api",
        configuration = SlackFeignClient.Configuration.class)
public interface SlackFeignClient {
    @PostMapping(value = "/oauth.v2.access", consumes = "application/x-www-form-urlencoded")
    String getAccessToken(@RequestBody SlackBody slackbody);

    class Configuration {
        @Bean
        Encoder feignFormEncoder(final ObjectFactory<HttpMessageConverters> converters) {
            return new SpringFormEncoder(new SpringEncoder(converters));
        }
    }
}
