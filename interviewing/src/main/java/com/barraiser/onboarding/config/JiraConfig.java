package com.barraiser.onboarding.config;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import feign.RequestInterceptor;
import java.util.Base64;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;

@Log4j2
@AllArgsConstructor
public class JiraConfig {
  public static final String JIRA_AUTOMATION_ACCOUNT = "automation@barraiser.com";
  private final AWSSecretsManager awsSecretsManager;

  @SneakyThrows
  @Bean("JiraServerAPIToken")
  public String getApiToken() {

    return this.awsSecretsManager
        .getSecretValue(new GetSecretValueRequest().withSecretId("JiraAutomation"))
        .getSecretString();
  }

  @Bean
  public RequestInterceptor requestInterceptor() {
    return requestTemplate -> {
      final Base64.Encoder encoder = Base64.getEncoder();
      final String authString = String.format("%s:%s", JIRA_AUTOMATION_ACCOUNT, this.getApiToken());
      final String base64EncodedAuthString = encoder.encodeToString(authString.getBytes());

      requestTemplate.header("Content-Type", "application/json");
      requestTemplate.header("Authorization", "Basic " + base64EncodedAuthString);
    };
  }
}
