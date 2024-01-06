/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.onboarding.availability.AvailabilityManager;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.common.graphql.input.GetAvailabilityInput;
import graphql.schema.DataFetchingEnvironment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GetAvailabilityAuthorizedGraphQLQueryTest {
	@Mock
	private AvailabilityManager availabilityManager;
	@Mock
	private GraphQLUtil graphQLUtil;
	@Mock
	private DataFetchingEnvironment environment;

	@Test
	public void testGetQuery() {
		// GIVEN

		when(this.graphQLUtil.getArgument(any(), any(), any())).thenReturn(GetAvailabilityInput.builder()
				.userId("1")
				.startDate(0)
				.endDate(1)
				.build());

		// WHEN
		// final GetAvailability query = new GetAvailability(this.availabilityManager,
		// this.graphQLUtil);

		// THEN
		// final Object result = query.get(this.environment);

		// assertEquals("1", result.get(0).getUserId());
	}
}
