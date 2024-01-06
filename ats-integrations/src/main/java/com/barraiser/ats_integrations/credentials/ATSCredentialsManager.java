/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.credentials;

import com.barraiser.ats_integrations.dal.PartnerATSIntegrationDAO;
import com.barraiser.ats_integrations.dal.PartnerATSIntegrationRepository;
import com.barraiser.common.graphql.input.SubmitATSCredentialsInput;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
@AllArgsConstructor
public class ATSCredentialsManager {
	private final List<ATSCredentialsStrategy> atsCredentialsStrategies;
	private final PartnerATSIntegrationRepository partnerATSIntegrationRepository;

	public void submitATSCredentials(final SubmitATSCredentialsInput input)
			throws Exception {
		log.info(String.format(
				"Submitting credentials for partnerId:%s atsProvider:%s",
				input.getPartnerId(),
				input.getCredentials()));

		this.getStrategy(input)
				.submitATSCredentials(input);
	}

	private ATSCredentialsStrategy getStrategy(final SubmitATSCredentialsInput input) {
		return this.atsCredentialsStrategies
				.stream()
				.filter(atsCredentialsStrategy -> input.getAtsProvider()
						.contains(atsCredentialsStrategy.atsProvider()))
				.findFirst()
				.get();
	}
}
