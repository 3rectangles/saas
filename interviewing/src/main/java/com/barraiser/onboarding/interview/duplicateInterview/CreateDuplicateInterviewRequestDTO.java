package com.barraiser.onboarding.interview.duplicateInterview;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateDuplicateInterviewRequestDTO {
    private String interviewId;
    private String expertId;
    private String testCompanyId;
    private String testCandidateId;
    private String duplicateReason;
}
