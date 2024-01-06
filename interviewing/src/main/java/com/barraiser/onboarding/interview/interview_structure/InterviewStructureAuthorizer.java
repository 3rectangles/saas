/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.interview_structure;

import com.barraiser.onboarding.graphql.AuthorizationResult;
import com.barraiser.onboarding.graphql.GraphQLAbacAuthorizer;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class InterviewStructureAuthorizer implements GraphQLAbacAuthorizer {

	@Override
	public AuthorizationResult authorize(final DataFetchingEnvironment environment) {
		return AuthorizationResult.builder()
				.readableFields(List.of("*"))
				.build();
	}
}
