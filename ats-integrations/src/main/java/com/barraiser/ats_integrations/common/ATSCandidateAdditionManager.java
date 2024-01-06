/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.common;

import com.barraiser.ats_integrations.dal.ATSToBREvaluationDAO;
import com.barraiser.ats_integrations.dal.ATSToBREvaluationRepository;
import com.barraiser.ats_integrations.dal.PartnerATSIntegrationDAO;
import com.barraiser.ats_integrations.dal.PartnerATSIntegrationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Log4j2
@AllArgsConstructor
public class ATSCandidateAdditionManager {
	private final List<ATSCandidateAdditionStrategy> atsCandidateAdditionStrategies;
	private final ATSToBREvaluationRepository atsToBREvaluationRepository;
	private final PartnerATSIntegrationRepository partnerATSIntegrationRepository;

	public void performNecessaryOperationsUponCandidateAddition(final String evaluationId) throws Exception {
		Optional<ATSToBREvaluationDAO> atsToBREvaluationDAO = this.atsToBREvaluationRepository
				.findByBrEvaluationId(evaluationId);

		if (atsToBREvaluationDAO.isEmpty()) {
			log.info(String.format(
					"No ats evaluation found for evaluationId:%s",
					evaluationId));

			return;
		}

		Optional<PartnerATSIntegrationDAO> partnerATSIntegrationDAO = this.partnerATSIntegrationRepository
				.findByPartnerIdAndAtsProvider(
						atsToBREvaluationDAO
								.get()
								.getPartnerId(),
						atsToBREvaluationDAO
								.get()
								.getAtsProvider());

		this.getATSCandidateAdditionStrategy(partnerATSIntegrationDAO.get())
				.performNecessaryOperationsUponCandidateAddition(
						partnerATSIntegrationDAO.get(),
						atsToBREvaluationDAO.get());
	}

	private ATSCandidateAdditionStrategy getATSCandidateAdditionStrategy(
			final PartnerATSIntegrationDAO partnerATSIntegrationDAO) {
		log.info(String.format(
				"Fetching strategy for partnerId:%s atsProvider:%s",
				partnerATSIntegrationDAO.getPartnerId(),
				partnerATSIntegrationDAO.getAtsProvider()));

		return this.atsCandidateAdditionStrategies
				.stream()
				.filter(x -> partnerATSIntegrationDAO
						.getAtsProvider()
						.contains(x.atsProvider()))
				.findFirst()
				.get();
	}
}
