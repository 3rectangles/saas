/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.status.graphql;

import com.barraiser.onboarding.auth.Authorizer;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.interview.evaluation.auth.EvaluationPartnerStatusAuthorizer;
import com.barraiser.onboarding.interview.status.EvaluationStatusManager;
import com.barraiser.onboarding.interview.status.graphql.input.PartnerStatusInput;
import graphql.schema.DataFetchingEnvironment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EvaluationPartnerStatusMutationTest {
	@InjectMocks
	private EvaluationPartnerStatusMutation evaluationPartnerStatusMutation;

	@Mock
	private GraphQLUtil graphQLUtil;
	@Mock
	private Authorizer authorizer;
	@Mock
	private EvaluationStatusManager evaluationStatusManager;
	@Mock
	private DataFetchingEnvironment dataFetchingEnvironment;

	@Test
	public void shouldAuthenticateUser() throws Exception {
		// WHEN
		this.evaluationPartnerStatusMutation.get(dataFetchingEnvironment);

		// THEN
		verify(this.graphQLUtil).getLoggedInUser(dataFetchingEnvironment);
	}

	@Test
	public void shouldAuthorizeUser() throws Exception {
		// WHEN
		this.evaluationPartnerStatusMutation.get(dataFetchingEnvironment);

		// THEN
		verify(this.authorizer).can(
				argThat(arg -> arg.getUserName().equals("userId")),
				eq(EvaluationPartnerStatusAuthorizer.ACTION_WRITE),
				argThat(arg -> arg.getType().equals(EvaluationPartnerStatusAuthorizer.RESOURCE_TYPE) &&
						((Map<String, String>) arg.getResource()).get("evaluationId").equals("eid") &&
						((Map<String, String>) arg.getResource()).get("partnerStatusId").equals("1")));
	}

	@Test
	public void shouldTransitionStatus() throws Exception {
		// WHEN
		this.evaluationPartnerStatusMutation.get(dataFetchingEnvironment);

		// THEN
		verify(this.evaluationStatusManager).transitionPartnerStatus("eid", "1", "userId");
	}

	@Before
	public void setup() {
		when(this.graphQLUtil.getArgument(any(), any(), any())).thenReturn(
				PartnerStatusInput.builder().statusId("1").evaluationId("eid").build());
		when(this.graphQLUtil.getLoggedInUser(any()))
				.thenReturn(AuthenticatedUser.builder().userName("userId").build());
	}
}
