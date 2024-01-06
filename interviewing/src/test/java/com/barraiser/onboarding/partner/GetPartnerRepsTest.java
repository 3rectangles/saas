/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.partner;

import com.barraiser.onboarding.auth.AuthenticationException;
import com.barraiser.onboarding.auth.AuthorizationException;
import com.barraiser.onboarding.auth.Authorizer;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.PartnerRepsDAO;
import com.barraiser.onboarding.dal.PartnerRepsRepository;
import com.barraiser.onboarding.dal.UserDetailsDAO;
import com.barraiser.onboarding.dal.UserDetailsRepository;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.common.graphql.input.PartnerInput;
import com.barraiser.common.graphql.types.PartnerRepDetails;
import com.barraiser.common.graphql.types.UserDetails;
import com.barraiser.onboarding.partner.auth.PartnerPortalAuthorizer;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GetPartnerRepsTest {
	@InjectMocks
	private GetPartnerReps getPartnerReps;
	@Mock
	private GraphQLUtil graphQLUtil;
	@Mock
	private PartnerRepsRepository partnerRepsRepository;
	@Mock
	private UserDetailsRepository userDetailsRepository;
	@Mock
	private Authorizer authorizer;
	@Mock
	private DataFetchingEnvironment dataFetchingEnvironment;

	@Test(expected = AuthenticationException.class)
	public void shouldNotAllowUnAuthenticatedUser() throws Exception {
		// GIVEN
		when(this.graphQLUtil.getLoggedInUser(any()))
				.thenThrow(new AuthenticationException(""));
		// WHEN
		this.getPartnerReps.get(dataFetchingEnvironment);
	}

	@Test(expected = AuthorizationException.class)
	public void shouldNotAllowUnAuthorizedUser() throws Exception {
		// GIVEN
		final AuthenticatedUser user = AuthenticatedUser.builder().build();
		when(this.graphQLUtil.getLoggedInUser(this.dataFetchingEnvironment))
				.thenReturn(user);
		when(this.graphQLUtil.getArgument(any(), any(), any()))
				.thenReturn(PartnerInput.builder().build());
		doThrow(new AuthorizationException()).when(this.authorizer).can(any(), any(), any());

		// WHEN
		this.getPartnerReps.get(dataFetchingEnvironment);
	}

	@Test
	public void shouldCheckForAuthorization() throws Exception {
		// GIVEN
		this.executeGivenSectionForGet();

		// WHEN
		this.getPartnerReps.get(dataFetchingEnvironment);

		// THEN
		verify(this.authorizer).can(
				argThat(arg -> arg.getUserName().equals("logged_in_user_id")),
				eq(PartnerPortalAuthorizer.ACTION_READ_AND_WRITE),
				argThat(arg -> arg.getType().equals(PartnerPortalAuthorizer.RESOURCE_TYPE) &&
						arg.getResource().equals("test_partner_id")));
	}

	@Test
	public void shouldGetPartnerRepsWhoseUserDetailsArePresentInDB() throws Exception {
		// GIVEN
		this.executeGivenSectionForGet();
		when(this.partnerRepsRepository.findAllByPartnerId(any())).thenReturn(List.of(
				PartnerRepsDAO.builder().id("test_user_1").partnerId("test_partner_id")
						.createdOn(Instant.ofEpochSecond(1626109847)).build(),
				PartnerRepsDAO.builder().id("test_user_2").partnerId("test_partner_id").build(),
				PartnerRepsDAO.builder().id("test_user_3").partnerId("test_partner_id").build(),
				PartnerRepsDAO.builder().id("test_user_4").partnerId("test_partner_id").build(),
				PartnerRepsDAO.builder().id("test_user_5").partnerId("test_partner_id").build()));
		when(this.userDetailsRepository.findById("test_user_1")).thenReturn(Optional.of(
				UserDetailsDAO.builder()
						.id("test_user_1")
						.email("test_user_1@gmail.com")
						.phone("1111111111")
						.firstName("Test")
						.lastName("User 1")
						.build()));
		when(this.userDetailsRepository.findById("test_user_2")).thenReturn(Optional.of(
				UserDetailsDAO.builder()
						.id("test_user_2")
						.email("test_user_2@gmail.com")
						.build()));
		when(this.userDetailsRepository.findById("test_user_3")).thenReturn(Optional.of(
				UserDetailsDAO.builder()
						.id("test_user_3")
						.email("test_user_3@gmail.com")
						.build()));

		// WHEN
		final DataFetcherResult result = (DataFetcherResult) this.getPartnerReps.get(dataFetchingEnvironment);
		final List<PartnerRepDetails> partnerRepsDetails = (List<PartnerRepDetails>) result.getData();

		// THEN
		assertEquals(1626109847, partnerRepsDetails.get(0).getAccessGrantedOn());
		partnerRepsDetails
				.forEach(partnerRepDetails -> assertEquals("test_partner_id", partnerRepDetails.getPartnerId()));

		assertThat(partnerRepsDetails.get(0).getUserDetails()).usingRecursiveComparison()
				.isEqualTo(UserDetails.builder()
						.id("test_user_1")
						.email("test_user_1@gmail.com")
						.phone("1111111111")
						.firstName("Test")
						.lastName("User 1")
						.build());
		assertThat(partnerRepsDetails.get(1).getUserDetails()).usingRecursiveComparison()
				.isEqualTo(UserDetails.builder()
						.id("test_user_2")
						.email("test_user_2@gmail.com")
						.build());
		assertThat(partnerRepsDetails.get(2).getUserDetails()).usingRecursiveComparison()
				.isEqualTo(UserDetails.builder()
						.id("test_user_3")
						.email("test_user_3@gmail.com")
						.build());

	}

	private void executeGivenSectionForGet() {
		when(this.graphQLUtil.getLoggedInUser(this.dataFetchingEnvironment))
				.thenReturn(AuthenticatedUser.builder().userName("logged_in_user_id").build());
		when(this.graphQLUtil.getArgument(any(), any(), any()))
				.thenReturn(PartnerInput.builder()
						.partnerId("test_partner_id")
						.build());
		doNothing().when(this.authorizer).can(any(), any(), any());
		when(this.partnerRepsRepository.findAllByPartnerId(any())).thenReturn(List.of());
		when(this.userDetailsRepository.findById(any())).thenReturn(Optional.empty());
	}

}
