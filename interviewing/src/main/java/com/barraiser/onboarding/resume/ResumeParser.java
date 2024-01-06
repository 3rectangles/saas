package com.barraiser.onboarding.resume;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.barraiser.common.utilities.UrlUtil;
import com.barraiser.onboarding.resume.dto.ParseResumeRequestDTO;
import com.barraiser.onboarding.resume.dto.ParsedResumeDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Map;

@Log4j2
@Component
@AllArgsConstructor
public class ResumeParser {
    private final RChilliParserClient rChilliParserClient;
    private final AWSSecretsManager awsSecretsManager;
    private final ObjectMapper objectMapper;

    public ParsedResumeDTO parseResume(final String resumeUrl) {
        return this.rChilliParserClient.parseResume(this.buildParseResumeRequest(resumeUrl))
            .getResumeParserData();
    }

    public String parseResumeToJSONString(final String resumeUrl) {
        return this.rChilliParserClient.parseResumeToJsonString(this.buildParseResumeRequest(resumeUrl));
    }

    private ParseResumeRequestDTO buildParseResumeRequest(final String resumeUrl) {
        final Map<String, String> rChilliApiKeySecret = this.getRChilliSecret();

        return ParseResumeRequestDTO.builder()
            .url(UrlUtil.encodeUrl(resumeUrl))
            .subUserId(rChilliApiKeySecret.get("rchilli_sub_user_id"))
            .version("8.0.0")
            .userKey(rChilliApiKeySecret.get("rchilli_user_key"))
            .build();
    }

    @SneakyThrows
    private Map<String, String> getRChilliSecret() {
        final String rChilliSecretString = this.awsSecretsManager.getSecretValue(
            new GetSecretValueRequest().withSecretId("rchilli")
        ).getSecretString();

        return this.objectMapper.readValue(rChilliSecretString, Map.class);
    }
}
