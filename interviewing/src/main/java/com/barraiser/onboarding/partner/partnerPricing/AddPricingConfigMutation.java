/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.partner.partnerPricing;

import com.barraiser.common.DTO.pricing.AddPartnerPricingConfigRequestDTO;
import com.barraiser.common.DTO.pricing.AddPricingConfigResult;
import com.barraiser.common.graphql.input.AddPricingConfigInput;
import com.barraiser.onboarding.graphql.AuthorizationResult;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLMutation_deprecated;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.partner.partnerPricing.auth.AddPartnerConfigAuthorizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import com.barraiser.commons.auth.AuthenticatedUser;

import java.util.stream.Collectors;

@Log4j2
@Component
public class AddPricingConfigMutation extends AuthorizedGraphQLMutation_deprecated<AddPricingConfigResult> {
	private final GraphQLUtil graphQLUtil;
	private final ObjectMapper objectMapper;
	private final PricingServiceClient pricingServiceClient;

	@Override
	protected AddPricingConfigResult fetch(final DataFetchingEnvironment environment,
			final AuthorizationResult authorizationResult) {
		final AuthenticatedUser user = this.graphQLUtil.getLoggedInUser(environment);
		final AddPricingConfigInput input = this.graphQLUtil.getInput(environment, AddPricingConfigInput.class);
		final AddPartnerPricingConfigRequestDTO addPartnerPricingConfigRequestDTO = AddPartnerPricingConfigRequestDTO
				.builder()
				.createdBy(user.getUserName())
				.partnerPricingInputDTOList(input.getPricing().stream()
						.map(x -> this.objectMapper.convertValue(x,
								AddPartnerPricingConfigRequestDTO.PartnerPricingInputDTO.class))
						.collect(Collectors.toList()))
				.build();
		final AddPricingConfigResult result = this.pricingServiceClient.addPartnerPricing(input.getPartnerId(),
				addPartnerPricingConfigRequestDTO);
		return result;
	}

	public AddPricingConfigMutation(final AddPartnerConfigAuthorizer abacAuthorizer,
			final GraphQLUtil graphQLUtil, final ObjectMapper objectMapper,
			final PricingServiceClient pricingServiceClient) {
		super(abacAuthorizer);
		this.graphQLUtil = graphQLUtil;
		this.objectMapper = objectMapper;
		this.pricingServiceClient = pricingServiceClient;
	}

	@Override
	public String name() {
		return "addPricingConfig";
	}
}
