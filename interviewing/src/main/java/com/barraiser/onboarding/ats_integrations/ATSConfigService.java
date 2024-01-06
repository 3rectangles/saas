/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.ats_integrations;

import com.barraiser.ats_integrations.dal.PartnerATSIntegrationRepository;
import com.barraiser.commons.dto.ats.enums.ATSProvider;

import com.barraiser.commons.dto.ats.ATSIntegrationDTO;
import com.barraiser.onboarding.dal.PartnerCompanyRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static com.barraiser.common.constants.Constants.PARTNERSHIP_MODEL_ID_PURE_SAAS;

/**
 * This class is used to manage ATS related configuration.
 * CRUD related to ATS configurations for partner for example :
 * getting all the ATS integrations supported for a partner , updating
 * integration frequency schedule etc.
 */
@Component
@Log4j2
@AllArgsConstructor
public class ATSConfigService {

	private final PartnerATSIntegrationRepository partnerATSIntegrationRepository;
	private final PartnerCompanyRepository partnerCompanyRepository;

	public List<ATSIntegrationDTO> getAllSupportedIntegrations() {

		// 1. Get all saas companies
		final List<String> saasCompanyPartnerIds = this.partnerCompanyRepository
				.findByPartnershipModelIdIn(List.of(PARTNERSHIP_MODEL_ID_PURE_SAAS))
				.stream()
				.map(x -> x.getId())
				.collect(Collectors.toList());

		// 2. Get all enabled partner integrations.
		return this.partnerATSIntegrationRepository.findAllByPartnerIdIn(saasCompanyPartnerIds)
				.stream()
				.map(pi -> ATSIntegrationDTO.builder()
						.partnerId(pi.getPartnerId())
						.atsAggregator(pi.getAtsAggregator())
						.atsProvider(ATSProvider.fromString(pi.getAtsProvider()))
						.build())
				.collect(Collectors.toList());
	}

}
