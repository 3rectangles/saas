/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.common;

import com.barraiser.ats_integrations.dal.PartnerATSIntegrationDAO;
import com.barraiser.ats_integrations.dal.PartnerATSIntegrationRepository;
import com.barraiser.common.graphql.input.ActivateATSWebhookInput;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
@AllArgsConstructor
public class ATSWebhookHandler {
	private final List<ATSWebhookHandlerStrategy> atsWebhookHandlerStrategies;
	private final PartnerATSIntegrationRepository partnerATSIntegrationRepository;

	public void activateATSWebhook(final ActivateATSWebhookInput input) {
		log.info(String.format(
				"Fetching the ATSWebhookHandlerStrategy for ATSProvider:%s",
				input.getAtsProvider()));

		final PartnerATSIntegrationDAO partnerATSIntegrationDAO = this.partnerATSIntegrationRepository
				.findByPartnerIdAndAtsProvider(
						input.getPartnerId(),
						input.getAtsProvider())
				.get();

		final ATSWebhookHandlerStrategy atsWebhookHandlerStrategy = this
				.getATSWebhookHandlerStrategy(partnerATSIntegrationDAO);

		atsWebhookHandlerStrategy.activateATSWebhook(partnerATSIntegrationDAO);
	}

	private ATSWebhookHandlerStrategy getATSWebhookHandlerStrategy(
			final PartnerATSIntegrationDAO partnerATSIntegrationDAO) {
		return this.atsWebhookHandlerStrategies
				.stream()
				.filter(atsWebhookHandlerStrategy -> atsWebhookHandlerStrategy
						.getATSProvider()
						.equals(partnerATSIntegrationDAO.getAtsProvider()))
				.findFirst()
				.get();
	}
}
