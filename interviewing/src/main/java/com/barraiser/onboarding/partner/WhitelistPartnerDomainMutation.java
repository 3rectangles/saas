/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.partner;

import com.barraiser.onboarding.auth.AuthorizationResourceDTO;
import com.barraiser.onboarding.auth.Authorizer;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.PartnerWhitelistedDomainDAO;
import com.barraiser.onboarding.dal.PartnerWhitelistedDomainsRepository;
import com.barraiser.onboarding.graphql.DataFetcherType;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.common.graphql.input.WhitelistPartnerDomainInput;
import com.barraiser.onboarding.partner.auth.PartnerPortalAuthorizer;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Log4j2
@RequiredArgsConstructor
@Component
public class WhitelistPartnerDomainMutation implements NamedDataFetcher {
	private final GraphQLUtil graphQLUtil;
	private final Authorizer authorizer;
	private final PartnerWhitelistedDomainsRepository partnerWhitelistedDomainsRepository;

	@Override
	public String name() {
		return "whitelistPartnerDomain";
	}

	@Override
	public String type() {
		return DataFetcherType.MUTATION.getValue();
	}

	@Override
	public Object get(DataFetchingEnvironment environment) throws Exception {
		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);
		final WhitelistPartnerDomainInput input = this.graphQLUtil.getInput(environment,
				WhitelistPartnerDomainInput.class);
		final AuthorizationResourceDTO authorizationResource = AuthorizationResourceDTO.builder()
				.type(PartnerPortalAuthorizer.RESOURCE_TYPE)
				.resource(input.getPartnerId())
				.build();
		this.authorizer.can(authenticatedUser, PartnerPortalAuthorizer.ACTION_READ_AND_WRITE, authorizationResource);
		final Optional<PartnerWhitelistedDomainDAO> partnerWhitelistedDomainDAO = this.partnerWhitelistedDomainsRepository
				.findByPartnerIdAndEmailDomain(input.getPartnerId(), input.getEmailDomain());
		if (partnerWhitelistedDomainDAO.isPresent()) {
			return true;
		}
		this.partnerWhitelistedDomainsRepository.save(
				PartnerWhitelistedDomainDAO.builder().id(UUID.randomUUID().toString())
						.partnerId(input.getPartnerId())
						.emailDomain(input.getEmailDomain())
						.build());
		return true;
	}
}
