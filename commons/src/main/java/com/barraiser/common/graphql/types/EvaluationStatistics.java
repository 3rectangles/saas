package com.barraiser.common.graphql.types;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class EvaluationStatistics {
    private Long total;
    private Long active;
    private Long requiresAction;
}
