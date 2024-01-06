/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.partner.partnerPricing;

import com.barraiser.common.DTO.pricing.PartnerPricingStageDetailsRequestDTO;
import com.barraiser.common.graphql.input.UpdatePartnerPricingStageDetailsInput;
import com.barraiser.common.graphql.types.PartnerPricingStageUpdationResult;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.graphql.AuthorizationResult;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLMutation_deprecated;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.partner.partnerPricing.auth.UpdatePartnerPricingStageDetailsAuthorizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class UpdatePartnerPricingStageDetailsMutation
		extends AuthorizedGraphQLMutation_deprecated<PartnerPricingStageUpdationResult> {
	private final GraphQLUtil graphQLUtil;
	private final PricingServiceClient pricingServiceClient;
	private final ObjectMapper objectMapper;

	@Override
	protected PartnerPricingStageUpdationResult fetch(final DataFetchingEnvironment environment,
			final AuthorizationResult authorizationResult) {
		final AuthenticatedUser user = this.graphQLUtil.getLoggedInUser(environment);
		final UpdatePartnerPricingStageDetailsInput input = this.graphQLUtil.getInput(environment,
				UpdatePartnerPricingStageDetailsInput.class);
		final PartnerPricingStageDetailsRequestDTO partnerPricingStageDetailsRequestDTO = this.objectMapper
				.convertValue(input, PartnerPricingStageDetailsRequestDTO.class).toBuilder()
				.createdBy(user.getUserName()).build();
		return this.pricingServiceClient.updatePartnerStageDetails(input.getPartnerId(),
				partnerPricingStageDetailsRequestDTO);
	}

	public UpdatePartnerPricingStageDetailsMutation(final UpdatePartnerPricingStageDetailsAuthorizer abacAuthorizer,
			final GraphQLUtil graphQLUtil, final PricingServiceClient pricingServiceClient,
			final ObjectMapper objectMapper) {
		super(abacAuthorizer);
		this.graphQLUtil = graphQLUtil;
		this.pricingServiceClient = pricingServiceClient;
		this.objectMapper = objectMapper;
	}

	@Override
	public String name() {
		return "updatePartnerPricingStageDetails";
	}
}
