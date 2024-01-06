package com.barraiser.onboarding.interviewing.interviewpad.codejudge;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.barraiser.common.monitoring.Profiled;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class CodeJudgeService {
  private final AWSSecretsManager awsSecretsManager;
  private final CodeJudgeClient codeJudgeClient;

  @Profiled(name = "codejudge")
  public CodeJudgeBulkCreateResponse generateNewPad() {
    final String apiKey =
        this.awsSecretsManager
            .getSecretValue(new GetSecretValueRequest().withSecretId("CodeJudgeApiKey"))
            .getSecretString();
    final String entHeader =
        String.format("{\"apiKey\": \"%s\", \"email\": \"admin@barraiser.com\"}", apiKey);
    final ResponseEntity<CodeJudgeBulkCreateResponse> response =
        this.codeJudgeClient.bulkGeneratePads(
            entHeader,
            CodeJudgeBulkCreateRequest.builder()
                .numPads(1)
                .infiniteDuration(true)
                .isAvReq(false)
                .showChat(false)
                .hideQuesPanel(true)
                .hideScorecard(true)
                .hideWaitingRoom(true)
                .build());

    if (response.getStatusCode() == HttpStatus.CREATED) {
      log.info("Successfully created a code pad");
      return response.getBody();
    } else {
      log.error("Bad response from code judge {}, {}", response.getStatusCode(), response);
      throw new RuntimeException("Code judge interview pad creation failing");
    }
  }
}
