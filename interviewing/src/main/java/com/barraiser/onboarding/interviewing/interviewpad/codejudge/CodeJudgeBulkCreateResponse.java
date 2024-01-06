package com.barraiser.onboarding.interviewing.interviewpad.codejudge;

import lombok.*;

import java.util.List;

@Builder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CodeJudgeBulkCreateResponse {
    private List<CodePad> response;

    @Data
    public static class CodePad {
        private String candidate;
        private String interviewer_1;
    }
}
