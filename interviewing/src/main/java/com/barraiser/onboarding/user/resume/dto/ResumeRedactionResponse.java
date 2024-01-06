package com.barraiser.onboarding.user.resume.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ResumeRedactionResponse {

    @JsonProperty("resumeRedacted")
    private Boolean isResumeRedacted;

    @JsonProperty("resumeLink")
    private String resumeLink;
}
