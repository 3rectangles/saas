package com.barraiser.common.graphql.types;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class InterviewChangeHistory {

    private String interviewId;

    private String fieldName;

    private Long createdOn;

    private UserDetails createdByUser;

    private String displayValue;

    private String displayReason;

    private Long scheduledTime;

    private Long rescheduledTime;
}
