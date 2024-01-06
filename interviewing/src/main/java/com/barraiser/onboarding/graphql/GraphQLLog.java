package com.barraiser.onboarding.graphql;


import graphql.execution.ExecutionId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.log4j.Log4j2;

import java.util.Map;

@SuperBuilder(toBuilder = true)
@Log4j2
@Getter
@Setter
@NoArgsConstructor
@ToString
public class GraphQLLog {
    private ExecutionId executionId;
    private String query;
    private Map<String, Object> variables;
    private long startTime;
    private double durationInMilliSeconds;
}
