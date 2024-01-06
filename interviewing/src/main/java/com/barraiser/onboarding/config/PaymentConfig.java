package com.barraiser.onboarding.config;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.barraiser.onboarding.common.StaticAppConfigValues;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class PaymentConfig {
    private final StaticAppConfigValues staticAppConfigValues;

    @Bean(name = "RazorPayWebhookSecret")
    public String getRazorPayWebhookSecret(final AWSSecretsManager awsSecretsManager) {
        return awsSecretsManager.getSecretValue(
            new GetSecretValueRequest().withSecretId("RazorPayWebhookSecret"))
            .getSecretString();
    }

    @Bean
    public RazorpayClient getRazorpayClient(final ObjectMapper objectMapper, final AWSSecretsManager awsSecretsManager)
            throws RazorpayException, JsonProcessingException {

        final String razorPaySecretValue = awsSecretsManager.getSecretValue(
                new GetSecretValueRequest().withSecretId(this.staticAppConfigValues.getRazorPaySecretKey()))
                .getSecretString();

        return new RazorpayClient(this.staticAppConfigValues.getRazorPayApiKeyId(), razorPaySecretValue);
    }
}
