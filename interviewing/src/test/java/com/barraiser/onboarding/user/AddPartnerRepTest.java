/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.user;

import com.barraiser.common.utilities.PhoneParser;
import com.barraiser.commons.auth.UserRole;
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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AddPartnerRepTest {
	@InjectMocks
	private AddPartnerRep addPartnerRep;
	@Mock
	private GraphQLUtil graphQLUtil;
	@Mock
	private UserInformationManagementHelper userManagement;
	@Mock
	private ExpertDBManager expertDBManager;
	@Mock
	private ExpertElasticSearchManager expertElasticSearchManager;
	@Mock
	private ExpertMapper expertMapper;
	@Mock
	private PartnerRepsRepository partnerRepsRepository;
	@Mock
	private PartnerCompanyRepository partnerCompanyRepository;
	@Mock
	private Authorizer authorizer;
	@Mock
	private PhoneParser phoneParser;
	@Mock
	private DataFetchingEnvironment dataFetchingEnvironment;

	@Test(expected = AuthenticationException.class)
	public void shouldNotAllowUnAuthenticatedUser() throws Exception {
		// GIVEN
		when(this.graphQLUtil.getLoggedInUser(any()))
				.thenThrow(new AuthenticationException(""));
		// WHEN
		this.addPartnerRep.get(this.dataFetchingEnvironment);
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
		this.addPartnerRep.get(this.dataFetchingEnvironment);
	}

	@Test
	public void shouldCheckForAuthorization() throws Exception {
		// GIVEN
		this.executeGivenSectionForGet();

		// WHEN
		this.addPartnerRep.get(this.dataFetchingEnvironment);

		// THEN
		verify(this.authorizer).can(
				argThat(arg -> arg.getUserName().equals("test_user_id")),
				eq(PartnerPortalAuthorizer.ACTION_READ_AND_WRITE),
				argThat(arg -> arg.getType().equals(PartnerPortalAuthorizer.RESOURCE_TYPE) &&
						arg.getResource().equals("test_partner_id")));

	}

	@Test
	public void shouldGetUserBeingAdded() throws Exception {
		// GIVEN
		this.executeGivenSectionForGet();

		// WHEN
		this.addPartnerRep.get(this.dataFetchingEnvironment);

		// THEN
		verify(this.userManagement).getOrCreateUserByEmail("test_user_email@gmail.com");
	}

	@Test
	public void shouldUpdateUserDetails() throws Exception {
		// GIVEN
		this.executeGivenSectionForGet();

		// WHEN
		this.addPartnerRep.get(this.dataFetchingEnvironment);

		// THEN
		verify(this.userManagement).updateUserDetailsFromDAO(argThat(
				arg -> arg.getEmail().equals("test_user_email@gmail.com") &&
						arg.getPhone().equals("+918903265604") &&
						arg.getFirstName().equals("Test") &&
						arg.getLastName().equals("User")));
	}

	@Test
	public void shouldUpdateRole() throws Exception {
		// GIVEN
		this.executeGivenSectionForGet();

		// WHEN
		this.addPartnerRep.get(this.dataFetchingEnvironment);

		// THEN
		verify(this.userManagement).addUserRole("test_added_user_id", UserRole.PARTNER);
	}

	@Test
	public void shouldNotUpdatePartnerRepsIfAlreadyInPartnerReps() throws Exception {
		// GIVEN
		this.executeGivenSectionForGet();
		when(this.partnerRepsRepository.findByPartnerRepIdAndPartnerId(any(), any()))
				.thenReturn(Optional.of(PartnerRepsDAO.builder()
						.id("test_added_user_id")
						.partnerId("test_partner_id")
						.build()));

		// WHEN
		this.addPartnerRep.get(this.dataFetchingEnvironment);

		// THEN
		verify(this.partnerRepsRepository, never()).save(any());
	}

	@Test
	public void shouldUpdatePartnerRepsIfNotInPartnerReps() throws Exception {
		// GIVEN
		this.executeGivenSectionForGet();
		when(this.partnerRepsRepository.findByPartnerRepIdAndPartnerId(any(), any())).thenReturn(Optional.empty());

		// WHEN
		this.addPartnerRep.get(this.dataFetchingEnvironment);

		// THEN
		verify(this.partnerRepsRepository).save(argThat(
				arg -> arg.getPartnerId().equals("test_partner_id") &&
						arg.getPartnerRepId().equals("test_added_user_id")));
	}

	private void executeGivenSectionForGet() {
		when(this.graphQLUtil.getLoggedInUser(this.dataFetchingEnvironment))
				.thenReturn(AuthenticatedUser.builder().userName("test_user_id").build());
		when(this.graphQLUtil.getArgument(any(), any(), any()))
				.thenReturn(PartnerAccessInput.builder()
						.partnerId("test_partner_id")
						.email("test_user_email@gmail.com")
						.phone("8903265604")
						.firstName("Test")
						.lastName("User")
						.build());
		doNothing().when(this.authorizer).can(any(), any(), any());
		when(this.userManagement.getOrCreateUserByEmail(any())).thenReturn(UserDetailsDAO.builder()
				.id("test_added_user_id")
				.email("test_user_email@gmail.com")
				.build());
		when(this.expertDBManager.getOrCreateExpertById(any()))
				.thenReturn(ExpertDAO.builder().id("test_added_user_id").build());
		when(this.expertMapper.toExpertDetails(any())).thenReturn(ExpertDetails.builder().build());
		when(this.expertMapper.toExpertDAO(any())).thenReturn(ExpertDAO.builder().build());

		when(this.partnerCompanyRepository.findById(any()))
				.thenReturn(Optional.of(PartnerCompanyDAO.builder().companyId("test_company_id").build()));
		when(this.phoneParser.getFormattedPhone(any())).thenReturn("+918903265604");
	}
}
