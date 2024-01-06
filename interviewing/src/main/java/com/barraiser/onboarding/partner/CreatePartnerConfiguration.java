/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.partner;

import com.barraiser.common.graphql.input.AddPartnerConfigurationInput;
import com.barraiser.onboarding.auth.AuthorizationException;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.PartnerConfigurationDAO;
import com.barraiser.onboarding.dal.PartnerConfigurationRepository;
import com.barraiser.onboarding.graphql.GraphQLMutation;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.interview.PartnerConfigManager;
import com.barraiser.commons.auth.UserRole;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Log4j2
@Component
@AllArgsConstructor
public class CreatePartnerConfiguration implements GraphQLMutation {
	private static final String INTERVIEW_SCHEDULING_KEY = "interviewScheduling";
	private static final String IS_CANDIDATE_SCHEDULING_ENABLED_KEY = "isCandidateSchedulingEnabled";
	private final GraphQLUtil graphQLUtil;
	private final ObjectMapper objectMapper;
	private final PartnerConfigurationRepository partnerConfigurationRepository;
	private final PartnerConfigManager partnerConfigManager;

	@Override
	public String name() {
		return "createPartnerConfiguration";
	}

	public Object get(final DataFetchingEnvironment environment) throws Exception {

		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);

		if (!authenticatedUser.getRoles().contains(UserRole.ADMIN)) {
			throw new AuthorizationException("User not authorised");
		}

		final AddPartnerConfigurationInput input = this.graphQLUtil.getInput(environment,
				AddPartnerConfigurationInput.class);

		final String partnerId = input.getPartnerId();
		final String config = input.getConfig();

		JsonNode jsonConfig = this.objectMapper.readValue(config, JsonNode.class);

		final PartnerConfigurationDAO updatedPartnerConfiguration = PartnerConfigurationDAO.builder()
				.id(UUID.randomUUID().toString())
				.createdBy(authenticatedUser.getUserName())
				.partnerId(partnerId)
				.config(jsonConfig)
				.build();

		Boolean isCandidateSchedulingEnabled = jsonConfig.get(INTERVIEW_SCHEDULING_KEY)
				.get(IS_CANDIDATE_SCHEDULING_ENABLED_KEY).asBoolean();
		this.partnerConfigManager.updateIsCandidateSchedulingEnabledFlag(partnerId, isCandidateSchedulingEnabled);
		this.partnerConfigurationRepository.save(updatedPartnerConfiguration);

		return DataFetcherResult.newResult().data(true).build();
	}

}
