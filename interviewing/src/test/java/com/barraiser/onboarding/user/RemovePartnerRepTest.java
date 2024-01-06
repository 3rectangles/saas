/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.user;

import com.barraiser.onboarding.auth.AuthenticationException;
import com.barraiser.onboarding.auth.AuthorizationException;
import com.barraiser.onboarding.auth.Authorizer;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.common.graphql.input.PartnerAccessInput;
import com.barraiser.onboarding.interview.jira.expert.ExpertElasticSearchManager;
import com.barraiser.onboarding.partner.auth.PartnerPortalAuthorizer;
import com.barraiser.onboarding.user.expert.ExpertDBManager;
import com.barraiser.onboarding.user.expert.dto.ExpertDetails;
import com.barraiser.onboarding.user.expert.mapper.ExpertMapper;
import graphql.schema.DataFetchingEnvironment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RemovePartnerRepTest {
	@InjectMocks
	private RemovePartnerRep removePartnerRep;

	@Mock
	private GraphQLUtil graphQLUtil;
	@Mock
	private UserInformationManagementHelper userManagement;
	@Mock
	private Authorizer authorizer;
	@Mock
	private PartnerRepsRepository partnerRepsRepository;
	@Mock
	private ExpertDBManager expertDBManager;
	@Mock
	private ExpertElasticSearchManager expertElasticSearchManager;
	@Mock
	private ExpertMapper expertMapper;
	@Mock
	private PartnerCompanyRepository partnerCompanyRepository;
	@Mock
	private DataFetchingEnvironment dataFetchingEnvironment;

	@Test(expected = AuthenticationException.class)
	public void shouldNotAllowUnAuthenticatedUser() throws Exception {
		// GIVEN
		when(this.graphQLUtil.getLoggedInUser(any()))
				.thenThrow(new AuthenticationException(""));
		// WHEN
		this.removePartnerRep.get(this.dataFetchingEnvironment);
	}

	@Test(expected = AuthorizationException.class)
	public void shouldNotAllowUnAuthorizedUser() throws Exception {
		// GIVEN
		final AuthenticatedUser user = AuthenticatedUser.builder().build();
		when(this.graphQLUtil.getLoggedInUser(this.dataFetchingEnvironment))
				.thenReturn(user);
		when(this.graphQLUtil.getArgument(any(), any(), any()))
				.thenReturn(PartnerAccessInput.builder().build());
		doThrow(new AuthorizationException()).when(this.authorizer).can(any(), any(), any());

		// WHEN
		this.removePartnerRep.get(this.dataFetchingEnvironment);
	}

	@Test
	public void shouldCheckForAuthorization() throws Exception {
		// WHEN
		this.removePartnerRep.get(this.dataFetchingEnvironment);

		// THEN
		verify(this.authorizer).can(
				argThat(arg -> arg.getUserName().equals("logged_in_user_id")),
				eq(PartnerPortalAuthorizer.ACTION_READ_AND_WRITE),
				argThat(arg -> arg.getType().equals(PartnerPortalAuthorizer.RESOURCE_TYPE) &&
						arg.getResource().equals("test_partner_id")));
	}

	@Test
	public void shouldDeleteFromPartnerReps() throws Exception {
		// WHEN
		this.removePartnerRep.get(this.dataFetchingEnvironment);

		// THEN
		verify(this.partnerRepsRepository).deleteById("test_user_id");
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAllowUserRevokeHisOwnAccess() throws Exception {
		// GIVEN
		when(this.userManagement.findUserByEmail(any())).thenReturn(Optional.of("logged_in_user_id"));

		// WHEN
		this.removePartnerRep.get(this.dataFetchingEnvironment);
	}

	@Before
	public void setup() {
		when(this.graphQLUtil.getLoggedInUser(this.dataFetchingEnvironment))
				.thenReturn(AuthenticatedUser.builder().userName("logged_in_user_id").build());
		when(this.graphQLUtil.getArgument(any(), any(), any()))
				.thenReturn(PartnerAccessInput.builder()
						.email("test_user_email@gmail.com")
						.partnerId("test_partner_id")
						.build());
		doNothing().when(this.authorizer).can(any(), any(), any());
		when(this.userManagement.findUserByEmail(any())).thenReturn(Optional.of("test_user_id"));
		when(this.partnerRepsRepository.findByPartnerRepIdAndPartnerId(any(), any()))
				.thenReturn(Optional.of(PartnerRepsDAO.builder()
						.id("test_user_id")
						.partnerId("test_partner_id")
						.build()));
		when(this.expertDBManager.getExpert(any())).thenReturn(ExpertDAO.builder().id("test_added_user_id").build());
		when(this.expertMapper.toExpertDetails(any())).thenReturn(ExpertDetails.builder().build());
		when(this.expertMapper.toExpertDAO(any())).thenReturn(ExpertDAO.builder().build());
		when(this.partnerCompanyRepository.findById(any()))
				.thenReturn(Optional.of(PartnerCompanyDAO.builder().companyId("test_company_id").build()));

	}
}
