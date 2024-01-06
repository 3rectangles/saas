package com.barraiser.onboarding.interviewing.interviewpad;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InterviewPad {
    private String interviewerPad;
    private String intervieweePad;
}
