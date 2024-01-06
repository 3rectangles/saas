package com.barraiser.onboarding.resume.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ParseResumeRequestDTO {
    private String url;

    @JsonProperty("userkey")
    private String userKey;

    private String version;

    @JsonProperty("subuserid")
    private String subUserId;
}
