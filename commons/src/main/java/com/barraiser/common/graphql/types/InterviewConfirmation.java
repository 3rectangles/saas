package com.barraiser.common.graphql.types;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class InterviewConfirmation {
    private String id;
    private String interviewId;
    private Boolean candidateConfirmation;
    private Boolean interviewerConfirmation;
}
