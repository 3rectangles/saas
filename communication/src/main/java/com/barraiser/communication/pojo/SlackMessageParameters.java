package com.barraiser.communication.pojo;

import lombok.*;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SlackMessageParameters {
    private String eventType;
    private String partnerId;
    private String userName;
    private String evaluationId;
    private Integer interviewRound;
    private String cancellationReason;
    private String jobRole;
    private String cancellationType;
    private String domain;
}
