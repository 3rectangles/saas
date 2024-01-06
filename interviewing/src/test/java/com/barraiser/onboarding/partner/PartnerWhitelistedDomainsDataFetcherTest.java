/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.partner;

import com.barraiser.onboarding.auth.AuthenticationException;
import com.barraiser.onboarding.auth.AuthorizationException;
import com.barraiser.onboarding.auth.Authorizer;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.PartnerWhitelistedDomainDAO;
import com.barraiser.onboarding.dal.PartnerWhitelistedDomainsRepository;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.common.graphql.input.PartnerInput;
import com.barraiser.common.graphql.types.PartnerWhitelistedDomains;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PartnerWhitelistedDomainsDataFetcherTest {
	@Mock
	private GraphQLUtil graphQLUtil;
	@Mock
	private Authorizer authorizer;
	@Mock
	private PartnerWhitelistedDomainsRepository partnerWhitelistedDomainsRepository;
	@InjectMocks
	private PartnerWhitelistedDomainsDataFetcher partnerWhitelistedDomainsDataFetcher;
	@Mock
	private DataFetchingEnvironment dataFetchingEnvironment;

	@Test(expected = AuthenticationException.class)
	public void shouldNotAllowUnAuthenticatedUser() throws Exception {
		// GIVEN
		when(this.graphQLUtil.getLoggedInUser(any()))
				.thenThrow(new AuthenticationException(""));
		// WHEN
		this.partnerWhitelistedDomainsDataFetcher.get(any());
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
		this.partnerWhitelistedDomainsDataFetcher.get(dataFetchingEnvironment);
	}

	@Test
	public void shouldReturnEmailDomains() throws Exception {
		final AuthenticatedUser user = AuthenticatedUser.builder().build();
		when(this.graphQLUtil.getLoggedInUser(this.dataFetchingEnvironment))
				.thenReturn(user);
		when(this.graphQLUtil.getArgument(any(), any(), any()))
				.thenReturn(PartnerInput.builder().build());
		when(this.partnerWhitelistedDomainsRepository.findAllByPartnerId(any()))
				.thenReturn(List.of(
						PartnerWhitelistedDomainDAO.builder().emailDomain("barraiser.com").build(),
						PartnerWhitelistedDomainDAO.builder().emailDomain("barraisertest.com").build(),
						PartnerWhitelistedDomainDAO.builder().emailDomain("barraiserexpert.com").build()));

		final DataFetcherResult result = (DataFetcherResult) this.partnerWhitelistedDomainsDataFetcher
				.get(dataFetchingEnvironment);
		final PartnerWhitelistedDomains partnerWhitelistedDomains = (PartnerWhitelistedDomains) result.getData();
		assertEquals("barraiser.com", partnerWhitelistedDomains.getEmailDomains().get(0));
		assertEquals("barraisertest.com", partnerWhitelistedDomains.getEmailDomains().get(1));
		assertEquals("barraiserexpert.com", partnerWhitelistedDomains.getEmailDomains().get(2));
	}

	@Test
	public void shouldNotReturnNullWhenNoDataFound() throws Exception {
		final AuthenticatedUser user = AuthenticatedUser.builder().build();
		when(this.graphQLUtil.getLoggedInUser(this.dataFetchingEnvironment))
				.thenReturn(user);
		when(this.graphQLUtil.getArgument(any(), any(), any()))
				.thenReturn(PartnerInput.builder().build());
		when(this.partnerWhitelistedDomainsRepository.findAllByPartnerId(any()))
				.thenReturn(List.of());
		final DataFetcherResult result = (DataFetcherResult) this.partnerWhitelistedDomainsDataFetcher
				.get(dataFetchingEnvironment);
		final PartnerWhitelistedDomains partnerWhitelistedDomains = (PartnerWhitelistedDomains) result.getData();
		assertEquals(0, partnerWhitelistedDomains.getEmailDomains().size());
	}
}
