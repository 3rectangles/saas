/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.interviewPad;

import com.barraiser.onboarding.graphql.AuthorizationResult;
import com.barraiser.onboarding.graphql.GraphQLAbacAuthorizer;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Component
public class InterviewPadAuthorizer implements GraphQLAbacAuthorizer {
	private static final List<String> ALL_FIELDS = List.of(
			"intervieweePad",
			"interviewerPad");

	@Override
	public AuthorizationResult authorize(final DataFetchingEnvironment environment) {

		return AuthorizationResult.builder()
				.readableFields(ALL_FIELDS)
				.build();
	}
}
