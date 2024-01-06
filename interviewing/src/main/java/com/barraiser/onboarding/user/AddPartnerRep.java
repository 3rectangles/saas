/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.user;

import com.barraiser.onboarding.auth.AuthorizationResourceDTO;
import com.barraiser.onboarding.auth.Authorizer;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.common.graphql.input.PartnerAccessInput;
import com.barraiser.onboarding.partner.auth.PartnerPortalAuthorizer;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Log4j2
@Component
@AllArgsConstructor
public class AddPartnerRep implements NamedDataFetcher {
	private final GraphQLUtil graphQLUtil;
	private final PartnerRepAdditionService partnerRepAdditionService;
	private final Authorizer authorizer;

	@Override
	public String name() {
		return "addPartnerRep";
	}

	@Override
	public String type() {
		return MUTATION_TYPE;
	}

	@Transactional
	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {
		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);
		final PartnerAccessInput input = this.graphQLUtil.getArgument(environment, "input", PartnerAccessInput.class);

		this.isAuthorizedToAddPartnerRep(authenticatedUser, input);
		this.partnerRepAdditionService.addPartnerRep(input);
		return DataFetcherResult.newResult().data(true).build();
	}

	private void isAuthorizedToAddPartnerRep(final AuthenticatedUser authenticatedUser,
			final PartnerAccessInput input) {
		final AuthorizationResourceDTO authorizationResource = AuthorizationResourceDTO.builder()
				.type(PartnerPortalAuthorizer.RESOURCE_TYPE)
				.resource(input.getPartnerId())
				.build();
		this.authorizer.can(authenticatedUser, PartnerPortalAuthorizer.ACTION_READ_AND_WRITE, authorizationResource);
	}

}
