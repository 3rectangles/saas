/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.ats_integrations;

import com.barraiser.ats_integrations.common.AtsJobsConfigurationManager;
import com.barraiser.common.graphql.types.Partner;
import com.barraiser.onboarding.graphql.MultiParentTypeDataFetcher;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
@AllArgsConstructor
public class AtsIntegrationDataFetcher implements MultiParentTypeDataFetcher {
	private final AtsJobsConfigurationManager atsJobsConfigurationManager;

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(List.of("Partner", "atsIntegrations"));
	}

	@Override
	public Object get(DataFetchingEnvironment environment) throws Exception {
		final Partner partner = environment.getSource();

		log.info(String.format(
				"Fetching all ATS Integration data for partnerId:%s",
				partner.getId()));

		return DataFetcherResult
				.newResult()
				.data(this.atsJobsConfigurationManager
						.getAtsIntegrations(partner.getId()))
				.build();
	}
}
