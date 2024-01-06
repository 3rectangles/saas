/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.partner;

import com.barraiser.common.graphql.input.PartnerAccessInput;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.commons.auth.AuthorizationResult;
import com.barraiser.commons.auth.feign.AuthorizationServiceFeignClient;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLMutation;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.partner.auth.PartnerRepAccessUpdationAuthorizationInputConstructor;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Log4j2
@Component
public class RemovePartnerRepresentative extends AuthorizedGraphQLMutation<Boolean> {
	private final GraphQLUtil graphQLUtil;
	private final PartnerRepresentativeRemovalService partnerRepresentativeRemovalService;

	public RemovePartnerRepresentative(AuthorizationServiceFeignClient authorizationServiceFeignClient,
			PartnerRepAccessUpdationAuthorizationInputConstructor partnerRepAccessUpdationAuthorizationInputConstructor,
			GraphQLUtil graphQLUtil,
			PartnerRepresentativeRemovalService partnerRepresentativeRemovalService) {

		super(authorizationServiceFeignClient, partnerRepAccessUpdationAuthorizationInputConstructor);
		this.graphQLUtil = graphQLUtil;
		this.partnerRepresentativeRemovalService = partnerRepresentativeRemovalService;
	}

	@Override
	protected Boolean fetch(DataFetchingEnvironment environment, AuthorizationResult authorizationResult)
			throws IOException {

		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);
		final PartnerAccessInput input = this.graphQLUtil.getArgument(environment, "input", PartnerAccessInput.class);

		if (authenticatedUser.getUserName().equals(input.getUserId())) {
			throw new IllegalArgumentException("You cannot revoke access to yourself");
		}

		log.info(String.format("Partner Rep access to be revoked for %s by %s", input.getUserId(),
				authenticatedUser.getUserName()));

		this.partnerRepresentativeRemovalService.removeAsPartnerRep(authenticatedUser, input.getUserId(),
				input.getPartnerId());

		return Boolean.TRUE;
	}

	@Override
	public String name() {
		return "removePartnerRepresentative";
	}

}
