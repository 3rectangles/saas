package com.barraiser.onboarding.config;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@Log4j2
@AllArgsConstructor
public class SlackClientConfig {

    private final AWSSecretsManager awsSecretsManager;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    public Map<String, String> getSlackSecret() {
        final String slackClientSecretString = this.awsSecretsManager.getSecretValue(
            new GetSecretValueRequest().withSecretId("slackClient")
        ).getSecretString();

        return this.objectMapper.readValue(slackClientSecretString, Map.class);
    }
}
