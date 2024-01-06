package com.barraiser.onboarding.config;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Log4j2
public class SlackConfig {

    @Bean
    public MethodsClient getMethodClient(final AWSSecretsManager awsSecretsManager) {
        final String token =
                awsSecretsManager
                        .getSecretValue(
                                new GetSecretValueRequest().withSecretId("MonitoringSlackBotToken"))
                        .getSecretString();

        final Slack slack = Slack.getInstance();
        return slack.methods(token);
    }
}
