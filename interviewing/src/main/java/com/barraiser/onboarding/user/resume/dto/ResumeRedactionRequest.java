package com.barraiser.onboarding.user.resume.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ResumeRedactionRequest {
    private String resumeLink;
    private String intervieweeId;
}
