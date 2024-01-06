/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.ats_integrations.greenhouse;

import com.barraiser.onboarding.dal.JobRoleDAO;
import com.barraiser.onboarding.dal.JobRoleRepository;
import com.barraiser.onboarding.dal.PartnerCompanyDAO;
import com.barraiser.onboarding.dal.PartnerCompanyRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Log4j2
@AllArgsConstructor
public class GreenhouseListTestRequestHandler {
	private final PartnerCompanyRepository partnerCompanyRepository;
	private final JobRoleRepository jobRoleRepository;

	public List<GreenhouseTest> getGreenHouseTests(final String partnerId) {
		log.info(
				String.format(
						"GreenhouseListTestRequestHandler for partnerId %s called", partnerId));

		final PartnerCompanyDAO partnerCompanyDAO = this.partnerCompanyRepository
				.findById(partnerId)
				.orElseThrow(
						() -> new IllegalArgumentException(
								"Partner company does not exist"));

		final List<JobRoleDAO> jobRoleDAOList = this.jobRoleRepository.findLatestByCompanyIdAndDeprecatedOnIsNull(
				partnerCompanyDAO.getCompanyId());

		return jobRoleDAOList.stream()
				.map(
						x -> GreenhouseTest.builder()
								.partnerTestId(x.getEntityId().getId())
								.partnerTestName(x.getInternalDisplayName())
								.build())
				.collect(Collectors.toList());
	}

}
