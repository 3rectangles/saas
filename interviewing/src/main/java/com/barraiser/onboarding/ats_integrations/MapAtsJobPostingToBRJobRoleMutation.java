/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.ats_integrations;

import com.barraiser.ats_integrations.common.AtsJobsConfigurationManager;
import com.barraiser.common.graphql.input.MapAtsJobPostingToBRJobRoleInput;
import com.barraiser.onboarding.graphql.DataFetcherType;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@AllArgsConstructor
public class MapAtsJobPostingToBRJobRoleMutation implements NamedDataFetcher {
	private final GraphQLUtil graphQLUtil;
	private final AtsJobsConfigurationManager atsJobsConfigurationManager;

	@Override
	public String name() {
		return "mapAtsJobPostingToBRJobRole";
	}

	@Override
	public String type() {
		return DataFetcherType.MUTATION.getValue();
	}

	@Override
	public Object get(DataFetchingEnvironment environment) throws Exception {
		final MapAtsJobPostingToBRJobRoleInput input = this.graphQLUtil
				.getInput(
						environment,
						MapAtsJobPostingToBRJobRoleInput.class);

		log.info(String.format(
				"Mapping ats job posting to job role for partnerId:%s atsIntegrationId:%s",
				input.getPartnerId(),
				input.getAtsProvider()));

		this.atsJobsConfigurationManager
				.mapJobPostingToBRJobRole(input);

		return true;
	}
}
