/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.smartRecruiters;

import com.barraiser.ats_integrations.dal.ATSCredentialDAO;
import com.barraiser.ats_integrations.dal.ATSCredentialRepository;
import com.barraiser.ats_integrations.dal.PartnerATSIntegrationDAO;
import com.barraiser.common.security.DataSecurityManager;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@AllArgsConstructor
public class SmartRecruitersAccessManager {
	private final ATSCredentialRepository atsCredentialRepository;
	private final DataSecurityManager dataSecurityManager;

	public String getApiKey(final PartnerATSIntegrationDAO partnerATSIntegrationDAO) {
		final ATSCredentialDAO atsCredentialDAO = this.atsCredentialRepository
				.findByPartnerATSIntegrationId(partnerATSIntegrationDAO.getId())
				.get();

		return atsCredentialDAO.getToken();
	}
}
