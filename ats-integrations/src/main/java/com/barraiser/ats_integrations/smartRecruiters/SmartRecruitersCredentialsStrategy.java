/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.smartRecruiters;

import com.barraiser.ats_integrations.credentials.ATSCredentialsStrategy;
import com.barraiser.ats_integrations.dal.ATSCredentialDAO;
import com.barraiser.ats_integrations.dal.ATSCredentialRepository;
import com.barraiser.ats_integrations.dal.PartnerATSIntegrationDAO;
import com.barraiser.ats_integrations.dal.PartnerATSIntegrationRepository;
import com.barraiser.ats_integrations.smartRecruiters.POJO.SmartRecruitersCredentials;
import com.barraiser.common.ats_integrations.ATSProvider;
import com.barraiser.common.graphql.input.SubmitATSCredentialsInput;
import com.barraiser.common.security.DataSecurityManager;
import com.barraiser.commons.dto.ats.enums.ATSAggregator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.UUID;

@Component
@Log4j2
@AllArgsConstructor
public class SmartRecruitersCredentialsStrategy implements ATSCredentialsStrategy {
	private static final String ATS_PROVIDER_DISPLAYABLE_NAME = "Smart Recruiters";

	private final ObjectMapper objectMapper;
	private final PartnerATSIntegrationRepository partnerATSIntegrationRepository;
	private final ATSCredentialRepository atsCredentialRepository;
	private final DataSecurityManager dataSecurityManager;

	@Override
	public String atsProvider() {
		return ATSProvider.SMART_RECRUITERS.getValue();
	}

	@Override
	public void submitATSCredentials(final SubmitATSCredentialsInput input)
			throws Exception {
		log.info(String.format(
				"Submitting SmartRecruiters credentials for partnerId:%s",
				input.getPartnerId()));

		final SmartRecruitersCredentials credentials = this.objectMapper
				.readValue(
						input.getCredentials(),
						SmartRecruitersCredentials.class);

		this.saveCredentialsInDatabase(
				input,
				credentials);
	}

	@Transactional
	private void saveCredentialsInDatabase(
			final SubmitATSCredentialsInput input,
			final SmartRecruitersCredentials credentials) {
		final PartnerATSIntegrationDAO partnerATSIntegrationDAO = PartnerATSIntegrationDAO
				.builder()
				.id(UUID.randomUUID().toString())
				.partnerId(input.getPartnerId())
				.atsProvider(input.getAtsProvider())
				.atsAggregator(ATSAggregator.SMART_RECRUITERS)
				.atsProviderDisplayableName(ATS_PROVIDER_DISPLAYABLE_NAME)
				.build();

		this.partnerATSIntegrationRepository
				.save(partnerATSIntegrationDAO);

		final ATSCredentialDAO atsCredentialDAO = ATSCredentialDAO
				.builder()
				.id(UUID.randomUUID().toString())
				.partnerATSIntegrationId(partnerATSIntegrationDAO.getId())
				.token(credentials.getApiKey())
				.build();

		this.atsCredentialRepository
				.save(atsCredentialDAO);
	}
}
