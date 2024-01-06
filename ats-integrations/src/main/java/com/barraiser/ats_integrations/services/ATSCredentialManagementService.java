/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.services;

import com.barraiser.ats_integrations.dal.ATSCredentialRepository;
import com.barraiser.ats_integrations.dal.PartnerATSIntegrationDAO;
import com.barraiser.ats_integrations.dal.PartnerATSIntegrationRepository;

import com.barraiser.commons.dto.ats.ATSSecretDTO;
import com.barraiser.commons.dto.ats.enums.ATSAggregator;
import com.barraiser.commons.dto.ats.enums.ATSProvider;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Log4j2
@AllArgsConstructor
public class ATSCredentialManagementService {

	private PartnerATSIntegrationRepository partnerATSIntegrationRepository;
	private ATSCredentialRepository atsCredentialRepository;

	/**
	 * TODO : Unify.
	 * <p>
	 * Note that currently only merge credentials are in ats_credential .
	 * <p>
	 * All other ATS creds are getting stored in : ats_access_token repo.
	 *
	 * @param partnerId
	 * @return
	 */
	public List<ATSSecretDTO> getATSSecretsForPartner(final String partnerId) {

		final List<PartnerATSIntegrationDAO> partnerATSIntegrationDAOList = this.partnerATSIntegrationRepository
				.findAllByPartnerId(partnerId);

		final Map<String, PartnerATSIntegrationDAO> partnerIntegrationIdToPartnerIntegrationsMapping = partnerATSIntegrationDAOList
				.stream()
				.collect(Collectors.toMap(
						PartnerATSIntegrationDAO::getId,
						Function.identity()));

		final List<String> partnerATSIntegrationIds = partnerATSIntegrationDAOList.stream().map(x -> x.getId())
				.collect(Collectors.toList());

		final List<ATSSecretDTO> atsSecretDTOList = this.atsCredentialRepository
				.findAllByPartnerATSIntegrationIdIn(partnerATSIntegrationIds)
				.stream()
				.map(
						x -> ATSSecretDTO.builder()
								.atsProvider(ATSProvider.fromString(partnerIntegrationIdToPartnerIntegrationsMapping
										.get(x.getPartnerATSIntegrationId())
										.getAtsProvider()))
								.atsAggregator(
										(partnerIntegrationIdToPartnerIntegrationsMapping
												.get(x.getPartnerATSIntegrationId())
												.getAtsAggregator()))
								.partnerId(partnerId)
								.tokenType(x.getTokenType())
								.tokenValue(x.getToken())
								.build())
				.collect(Collectors.toList());

		return atsSecretDTOList;
	}
}
