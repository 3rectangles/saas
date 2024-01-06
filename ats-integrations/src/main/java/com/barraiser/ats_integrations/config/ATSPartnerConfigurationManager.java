/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.config;

import com.barraiser.ats_integrations.common.dto.ATSConfigDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Component
public class ATSPartnerConfigurationManager {

	private final ATSPartnerConfigurationProvider atsPartnerConfigurationProvider;
	private final String ATS_CONFIGURATION_MODULE = "ATS_INTEGRATIONS";

	public Boolean isGoogleCalendarInterceptionEnabled(final String partnerId) {
		final ATSConfigDTO.CalendarInterception calendarInterceptionConfig = this
				.getCalendarInterceptionConfiguration(partnerId);
		return calendarInterceptionConfig != null ? calendarInterceptionConfig.getIsEnabled() : Boolean.FALSE;
	}

	public List<String> getInternalInterviewersEmailDomains(final String partnerId) {
		final ATSConfigDTO.CalendarInterception calendarInterceptionConfig = this
				.getCalendarInterceptionConfiguration(partnerId);
		return calendarInterceptionConfig != null ? calendarInterceptionConfig.getInternalInterviewersEmailDomain()
				: null;
	}

	private ATSConfigDTO.CalendarInterception getCalendarInterceptionConfiguration(final String partnerId) {
		return this.atsPartnerConfigurationProvider
				.getConfiguration(partnerId, this.ATS_CONFIGURATION_MODULE, ATSConfigDTO.class)
				.getCalendarInterception();
	}

	public Map<String, List<String>> getAllowedParticipantEmailDomains() {
		final Map<String, ATSConfigDTO> partnerToATSConfigMapping = this.atsPartnerConfigurationProvider
				.getCustomerConfigurations(this.ATS_CONFIGURATION_MODULE, ATSConfigDTO.class);
		final Map<String, List<String>> partnerToAllowedParticipantDomainsMapping = new HashMap<>();

		for (Map.Entry<String, ATSConfigDTO> partnerToATSConfig : partnerToATSConfigMapping.entrySet()) {

			if (partnerToATSConfig.getValue().getCalendarInterception() != null &&
					partnerToATSConfig.getValue().getCalendarInterception()
							.getAllowedParticipantEmailDomains() != null) {
				partnerToAllowedParticipantDomainsMapping.put(partnerToATSConfig.getKey(),
						partnerToATSConfig.getValue().getCalendarInterception().getAllowedParticipantEmailDomains());
			}

		}

		return partnerToAllowedParticipantDomainsMapping;
	}
}
