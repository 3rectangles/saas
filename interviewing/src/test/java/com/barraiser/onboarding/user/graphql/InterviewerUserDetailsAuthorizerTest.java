/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.user.graphql;

import com.barraiser.common.graphql.types.Interviewer;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.commons.auth.UserRole;
import com.barraiser.onboarding.auth.AuthorizationException;
import com.barraiser.onboarding.graphql.AuthorizationResult;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import graphql.schema.DataFetchingEnvironment;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InterviewerUserDetailsAuthorizerTest {
	@Mock
	private GraphQLUtil graphQLUtil;
	@Mock
	private DataFetchingEnvironment environment;

	@InjectMocks
	private InterviewerUserDetailsAuthorizer interviewerUserDetailsAuthorizer;

	private static final List<String> ALL_FIELDS = List.of(
			"id",
			"email",
			"firstName",
			"isdCode",
			"phone",
			"roles",
			"role",
			"lastName",
			"almaMater",
			"currentCompanyName",
			"workExperienceInMonths",
			"lastCompanies",
			"category");
	private static final List<String> NON_PI_FIELDS = List.of(
			"roles",
			"role",
			"almaMater",
			"currentCompanyName",
			"workExperienceInMonths",
			"lastCompanies",
			"category");

	@Test
	public void testAuthorizationForAdminUsers() {
		// GIVEN
		final String partnerId = "a partner id";
		final Interviewer interviewer = Interviewer.builder()
				.build();

		final AuthenticatedUser authenticatedUser = AuthenticatedUser.builder()
				.partnerId(partnerId)
				.roles(List.of(UserRole.ADMIN))
				.build();
		when(environment.getSource()).thenReturn(interviewer);
		when(graphQLUtil.getLoggedInUser(environment)).thenReturn(authenticatedUser);

		// WHEN
		final AuthorizationResult result = interviewerUserDetailsAuthorizer.authorize(environment);

		// THEN
		final List<String> readableFields = result.getReadableFields();
		assertEquals(ALL_FIELDS.size(), CollectionUtils.intersection(readableFields, ALL_FIELDS).size());

	}

	@Test
	public void testAuthorizationForOpsUsers() {
		// GIVEN
		final String partnerId = "a partner id";
		final Interviewer interviewer = Interviewer.builder()
				.build();

		final AuthenticatedUser authenticatedUser = AuthenticatedUser.builder()
				.partnerId(partnerId)
				.roles(List.of(UserRole.OPS))
				.build();
		when(environment.getSource()).thenReturn(interviewer);
		when(graphQLUtil.getLoggedInUser(environment)).thenReturn(authenticatedUser);

		// WHEN
		final AuthorizationResult result = interviewerUserDetailsAuthorizer.authorize(environment);

		// THEN
		final List<String> readableFields = result.getReadableFields();
		assertEquals(NON_PI_FIELDS.size(), readableFields.size());
		assertEquals(NON_PI_FIELDS.size(), CollectionUtils.intersection(readableFields, NON_PI_FIELDS).size());
	}

	@Test
	public void testAuthorizationForPartnerUsersWithSameTenantId() {
		// GIVEN
		final String partnerId = "a partner id";
		final Interviewer interviewer = Interviewer.builder()
				.tenantId(partnerId)
				.build();

		final AuthenticatedUser authenticatedUser = AuthenticatedUser.builder()
				.partnerId(partnerId)
				.roles(List.of(UserRole.PARTNER))
				.build();
		when(environment.getSource()).thenReturn(interviewer);
		when(graphQLUtil.getLoggedInUser(environment)).thenReturn(authenticatedUser);

		// WHEN
		final AuthorizationResult result = interviewerUserDetailsAuthorizer.authorize(environment);

		// THEN
		final List<String> readableFields = result.getReadableFields();
		assertEquals(ALL_FIELDS.size(), CollectionUtils.intersection(readableFields, ALL_FIELDS).size());
	}

	@Test
	public void testAuthorizationForPartnerEmployeeUsersWithSameTenantId() {
		// GIVEN
		final String partnerId = "a partner id";
		final Interviewer interviewer = Interviewer.builder()
				.tenantId(partnerId)
				.build();

		final AuthenticatedUser authenticatedUser = AuthenticatedUser.builder()
				.partnerId(partnerId)
				.roles(List.of(UserRole.PARTNER))
				.build();
		when(environment.getSource()).thenReturn(interviewer);
		when(graphQLUtil.getLoggedInUser(environment)).thenReturn(authenticatedUser);

		// WHEN
		final AuthorizationResult result = interviewerUserDetailsAuthorizer.authorize(environment);

		// THEN
		final List<String> readableFields = result.getReadableFields();
		assertEquals(ALL_FIELDS.size(), CollectionUtils.intersection(readableFields, ALL_FIELDS).size());
	}

	@Test
	public void shouldReturnAllIfExpertIsTheLoggedInUser() {
		// GIVEN
		final String interviewerId = "an interviewer id";
		final Interviewer interviewer = Interviewer.builder()
				.id(interviewerId)
				.build();

		final AuthenticatedUser authenticatedUser = AuthenticatedUser.builder()
				.userName(interviewerId)
				.roles(List.of())
				.build();
		when(environment.getSource()).thenReturn(interviewer);
		when(graphQLUtil.getLoggedInUser(environment)).thenReturn(authenticatedUser);

		// WHEN
		final AuthorizationResult result = interviewerUserDetailsAuthorizer.authorize(environment);

		// THEN
		final List<String> readableFields = result.getReadableFields();
		assertEquals(ALL_FIELDS.size(), CollectionUtils.intersection(readableFields, ALL_FIELDS).size());
	}

	@Test(expected = AuthorizationException.class)
	public void testAuthorizationForPartnerEmployeeUsersWithDifferentTenantId() {
		// GIVEN
		final String partnerId = "a partner id";
		final Interviewer interviewer = Interviewer.builder()
				.tenantId("a different tenant id")
				.build();

		final AuthenticatedUser authenticatedUser = AuthenticatedUser.builder()
				.partnerId(partnerId)
				.roles(List.of(UserRole.PARTNER))
				.build();
		when(environment.getSource()).thenReturn(interviewer);
		when(graphQLUtil.getLoggedInUser(environment)).thenReturn(authenticatedUser);

		// WHEN
		final AuthorizationResult result = interviewerUserDetailsAuthorizer.authorize(environment);

		// THEN
	}

	@Test(expected = AuthorizationException.class)
	public void testAuthorizationForCandidate() {
		// GIVEN
		final String partnerId = "a partner id";
		final Interviewer interviewer = Interviewer.builder()
				.tenantId("a different tenant id")
				.build();

		final AuthenticatedUser authenticatedUser = AuthenticatedUser.builder()
				.partnerId(partnerId)
				.roles(List.of(UserRole.CANDIDATE))
				.build();
		when(environment.getSource()).thenReturn(interviewer);
		when(graphQLUtil.getLoggedInUser(environment)).thenReturn(authenticatedUser);

		// WHEN
		final AuthorizationResult result = interviewerUserDetailsAuthorizer.authorize(environment);

		// THEN
	}

}
