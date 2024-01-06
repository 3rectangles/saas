package com.barraiser.common.graphql.input;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class GetEvaluationInput {
    private String id;
    private String jira;
    private String userId;
    private String agv; // algo version for scoring.
    private Boolean scoresNeedNotBeGenerated;
}
