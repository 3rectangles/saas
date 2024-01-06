/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.common;

import com.barraiser.ats_integrations.dal.PartnerATSIntegrationDAO;
import com.barraiser.ats_integrations.dal.PartnerATSIntegrationRepository;
import com.barraiser.common.graphql.input.MapAtsJobPostingToBRJobRoleInput;
import com.barraiser.common.graphql.types.AtsIntegration;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Log4j2
@AllArgsConstructor
public class AtsJobsConfigurationManager {
	private final List<ATSJobConfigurationStrategy> atsJobConfigurationStrategies;
	private final PartnerATSIntegrationRepository partnerATSIntegrationRepository;
	private final ATSJobPostingToBRJobRoleMapper atsJobPostingToBRJobRoleMapper;

	public List<AtsIntegration> getAtsIntegrations(final String partnerId) {
		log.info(String.format(
				"Finding ATS integrations of partnerId:%s",
				partnerId));

		final List<PartnerATSIntegrationDAO> partnerATSIntegrationDAOS = this.partnerATSIntegrationRepository
				.findAllByPartnerId(partnerId);

		log.info(String.format(
				"Fetching ATS integration data for partnerId:%s",
				partnerId));

		return partnerATSIntegrationDAOS
				.stream()
				.map(partnerATSIntegrationDAO -> {
					try {
						return this.getATSJobConfigurationStrategy(partnerATSIntegrationDAO)
								.getAtsIntegrationData(partnerATSIntegrationDAO);
					} catch (Exception exception) {
						log.error(
								String.format(
										"Unable to fetch %s ATS integration data for partnerId:%s",
										partnerATSIntegrationDAO.getAtsProvider(),
										partnerATSIntegrationDAO.getPartnerId()),
								exception);
					}
					return null;
				})
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}

	@Deprecated(forRemoval = true)
	public void mapJobPostingToBRJobRole(final MapAtsJobPostingToBRJobRoleInput input) {
		log.info(String.format(
				"Saving the mapping of ATS job posting to BR job role for partnerId:%s atsProvider:%s",
				input.getPartnerId(),
				input.getAtsProvider()));

		atsJobPostingToBRJobRoleMapper.mapAtsJobPostingToBRJobRole(input);
	}

	private ATSJobConfigurationStrategy getATSJobConfigurationStrategy(
			final PartnerATSIntegrationDAO partnerATSIntegrationDAO) {
		return this.atsJobConfigurationStrategies
				.stream()
				.filter(x -> partnerATSIntegrationDAO
						.getAtsProvider()
						.contains(x.atsProvider()))
				.findFirst()
				.get();
	}
}
