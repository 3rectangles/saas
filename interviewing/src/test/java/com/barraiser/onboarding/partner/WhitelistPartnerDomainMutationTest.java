/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.partner;

import com.barraiser.onboarding.auth.AuthenticationException;
import com.barraiser.onboarding.auth.AuthorizationException;
import com.barraiser.onboarding.auth.Authorizer;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.PartnerWhitelistedDomainDAO;
import com.barraiser.onboarding.dal.PartnerWhitelistedDomainsRepository;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.common.graphql.input.WhitelistPartnerDomainInput;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WhitelistPartnerDomainMutationTest {
	@Mock
	private GraphQLUtil graphQLUtil;
	@Mock
	private Authorizer authorizer;
	@Mock
	private PartnerWhitelistedDomainsRepository partnerWhitelistedDomainsRepository;
	@InjectMocks
	private WhitelistPartnerDomainMutation whitelistPartnerDomainMutation;

	@Test
	public void shouldNotAddDomainButReturnTrueWhenPartnerAndDomainAlreadyExists() throws Exception {
		when(this.graphQLUtil.getLoggedInUser(any())).thenReturn(AuthenticatedUser.builder().build());
		final WhitelistPartnerDomainInput input = WhitelistPartnerDomainInput.builder()
				.partnerId("test_p_c_1").emailDomain("test_p_c_1.com").build();
		when(this.graphQLUtil.getInput(any(), any()))
				.thenReturn(input);
		when(this.partnerWhitelistedDomainsRepository
				.findByPartnerIdAndEmailDomain(any(), any()))
						.thenReturn(Optional.ofNullable(PartnerWhitelistedDomainDAO.builder().build()));
		final boolean isWhitelisted = (boolean) this.whitelistPartnerDomainMutation.get(any());
		assertEquals(true, isWhitelisted);
		verify(this.partnerWhitelistedDomainsRepository, never())
				.save(argThat(arg -> arg.getPartnerId().equals("test_p_c_1")
						&& arg.getEmailDomain().equals("test_p_c_1.com")));
	}

	@Test
	public void shouldAddDomain() throws Exception {
		when(this.graphQLUtil.getLoggedInUser(any())).thenReturn(AuthenticatedUser.builder().build());
		final WhitelistPartnerDomainInput input = WhitelistPartnerDomainInput.builder()
				.partnerId("test_p_c_1").emailDomain("test_p_c_1.com").build();
		when(this.graphQLUtil.getInput(any(), any()))
				.thenReturn(input);
		when(this.partnerWhitelistedDomainsRepository
				.findByPartnerIdAndEmailDomain(any(), any()))
						.thenReturn(Optional.empty());
		final boolean isWhitelisted = (boolean) this.whitelistPartnerDomainMutation.get(any());
		assertEquals(true, isWhitelisted);
		verify(this.partnerWhitelistedDomainsRepository)
				.save(argThat(arg -> arg.getPartnerId().equals("test_p_c_1")
						&& arg.getEmailDomain().equals("test_p_c_1.com")));
	}

	@Test(expected = AuthorizationException.class)
	public void shouldNotAllowUnauthorizedUser() throws Exception {
		when(this.graphQLUtil.getLoggedInUser(any())).thenReturn(AuthenticatedUser.builder().build());
		final WhitelistPartnerDomainInput input = WhitelistPartnerDomainInput.builder()
				.partnerId("test_p_c_1").emailDomain("test_p_c_1.com").build();
		when(this.graphQLUtil.getInput(any(), any()))
				.thenReturn(input);
		doThrow(new AuthorizationException()).when(this.authorizer)
				.can(any(), any(), any());

		this.whitelistPartnerDomainMutation.get(any());
	}

	@Test(expected = AuthenticationException.class)
	public void shouldNotAllowUnAuthenticatedUser() throws Exception {
		// GIVEN
		when(this.graphQLUtil.getLoggedInUser(any()))
				.thenThrow(new AuthenticationException(""));
		// WHEN
		this.whitelistPartnerDomainMutation.get(any());
	}

}
