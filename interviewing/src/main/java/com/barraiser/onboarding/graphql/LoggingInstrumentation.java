/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.graphql;

import graphql.ExecutionResult;
import graphql.execution.instrumentation.InstrumentationContext;
import graphql.execution.instrumentation.SimpleInstrumentation;
import graphql.execution.instrumentation.SimpleInstrumentationContext;
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@AllArgsConstructor
public class LoggingInstrumentation extends SimpleInstrumentation {

	@Override
	public InstrumentationContext<ExecutionResult> beginExecution(InstrumentationExecutionParameters parameters) {
		var startTime = System.currentTimeMillis();

		var executionId = parameters.getExecutionInput().getExecutionId();

		log.info("GraphQL query received: {}", GraphQLLog.builder()
				.executionId(executionId)
				.startTime(startTime)
				.query(parameters.getQuery())
				.variables(parameters.getVariables())
				.build().toString());

		return SimpleInstrumentationContext.whenCompleted((executionResult, throwable) -> {
			if (throwable == null) {
				// log.info("Completed Successfully");
				var endTime = System.currentTimeMillis();
				var durationInMilliSeconds = endTime - startTime;
				GraphQLLog graphQLLog = GraphQLLog.builder()
						.executionId(executionId)
						.startTime(startTime)
						.query(parameters.getQuery())
						.variables(parameters.getVariables())
						.durationInMilliSeconds(durationInMilliSeconds)
						.build();
				log.info("Instrumentation Output: {}", graphQLLog.toString());

			} else {
				log.warn("{} Failed", executionId, throwable);
			}
		});
	}
}
