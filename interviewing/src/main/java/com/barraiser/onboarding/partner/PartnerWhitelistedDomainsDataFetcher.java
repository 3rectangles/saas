/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.partner;

import com.barraiser.onboarding.auth.AuthorizationResourceDTO;
import com.barraiser.onboarding.auth.Authorizer;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.PartnerWhitelistedDomainDAO;
import com.barraiser.onboarding.dal.PartnerWhitelistedDomainsRepository;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.common.graphql.input.PartnerInput;
import com.barraiser.common.graphql.types.PartnerWhitelistedDomains;
import com.barraiser.onboarding.partner.auth.PartnerPortalAuthorizer;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class PartnerWhitelistedDomainsDataFetcher implements NamedDataFetcher {
	private final GraphQLUtil graphQLUtil;
	private final Authorizer authorizer;
	private final PartnerWhitelistedDomainsRepository partnerWhitelistedDomainsRepository;

	@Override
	public String name() {
		return "getPartnerWhitelistedDomains";
	}

	@Override
	public String type() {
		return QUERY_TYPE;
	}

	@Override
	public Object get(DataFetchingEnvironment environment) throws Exception {

		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);

		final PartnerInput input = this.graphQLUtil.getArgument(environment, "input", PartnerInput.class);

		final AuthorizationResourceDTO authorizationResource = AuthorizationResourceDTO.builder()
				.type(PartnerPortalAuthorizer.RESOURCE_TYPE)
				.resource(input.getPartnerId())
				.build();
		this.authorizer.can(authenticatedUser, PartnerPortalAuthorizer.ACTION_READ, authorizationResource);
		final List<String> emailDomains = this.partnerWhitelistedDomainsRepository
				.findAllByPartnerId(input.getPartnerId())
				.stream().map(PartnerWhitelistedDomainDAO::getEmailDomain).collect(Collectors.toList());
		return DataFetcherResult.newResult()
				.data(PartnerWhitelistedDomains.builder().emailDomains(emailDomains).build()).build();
	}
}
