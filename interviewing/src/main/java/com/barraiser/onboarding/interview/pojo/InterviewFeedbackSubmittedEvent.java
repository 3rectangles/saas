package com.barraiser.onboarding.interview.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
public class InterviewFeedbackSubmittedEvent {
    private String interviewId;
    private Boolean saveSentiment;
}
