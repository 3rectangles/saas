/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.merge;

import com.barraiser.ats_integrations.config.MergeSecretFactory;
import com.barraiser.ats_integrations.dal.*;
import com.barraiser.ats_integrations.merge.DTO.AccountDetailsDTO;
import com.barraiser.common.ats_integrations.ATSProvider;
import com.barraiser.commons.dto.ats.enums.ATSAggregator;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Log4j2
@AllArgsConstructor
public class MergeAccessManager {
	private static final String AUTHORIZATION_HEADER_TYPE = "Bearer";

	private final MergeSecretFactory mergeSecretFactory;
	private final MergeATSClient mergeATSClient;
	private final PartnerATSIntegrationRepository partnerATSIntegrationRepository;
	private final ATSCredentialRepository atsCredentialRepository;

	public String getAuthorizationHeader()
			throws Exception {
		final MergeSecret mergeSecret = this.mergeSecretFactory.getMergeSecret();

		return String.format(
				"%s %s",
				AUTHORIZATION_HEADER_TYPE,
				mergeSecret.getApiKey());
	}

	public void storeAccountToken(
			final String partnerId,
			final String accountToken)
			throws Exception {
		final String authorization = this.getAuthorizationHeader();

		final AccountDetailsDTO accountDetailsDTO = this.mergeATSClient
				.getAccountDetails(
						authorization,
						accountToken)
				.getBody();

		final PartnerATSIntegrationDAO partnerATSIntegrationDAO = PartnerATSIntegrationDAO
				.builder()
				.id(UUID.randomUUID().toString())
				.partnerId(partnerId)
				.atsProvider(String.format(
						"%s_%s",
						ATSProvider.MERGE.getValue(),
						accountDetailsDTO.getIntegrationSlug()))
				.atsProviderDisplayableName(accountDetailsDTO.getIntegration())
				.atsAggregator(ATSAggregator.MERGE)
				.build();

		this.partnerATSIntegrationRepository
				.save(partnerATSIntegrationDAO);

		final ATSCredentialDAO atsCredentialDAO = ATSCredentialDAO
				.builder()
				.id(UUID.randomUUID().toString())
				.partnerATSIntegrationId(partnerATSIntegrationDAO.getId())
				.token(accountToken)
				.build();

		this.atsCredentialRepository
				.save(atsCredentialDAO);
	}

	public String getXAccountToken(final PartnerATSIntegrationDAO partnerATSIntegrationDAO) {
		return this.atsCredentialRepository
				.findByPartnerATSIntegrationId(partnerATSIntegrationDAO.getId())
				.get()
				.getToken();
	}
}
