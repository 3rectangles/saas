/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.calendar_interception.gcal.pipeline;

import com.barraiser.ats_integrations.calendar_interception.RegexMatchingHelper;
import com.barraiser.ats_integrations.calendar_interception.SchedulingProcessing;
import com.barraiser.ats_integrations.calendar_interception.dto.SchedulingData;
import com.barraiser.ats_integrations.calendar_interception.dto.VariableRegexMapping;
import com.barraiser.ats_integrations.config.ATSPartnerConfigurationManager;
import com.barraiser.ats_integrations.dal.ATSCommunicationTemplateConfigDAO;
import com.barraiser.ats_integrations.dal.ATSCommunicationTemplateConfigRepository;
import com.barraiser.ats_integrations.dal.PartnerATSIntegrationDAO;
import com.barraiser.ats_integrations.dal.PartnerATSIntegrationRepository;
import com.barraiser.common.ats_integrations.ATSProvider;
import com.barraiser.commons.dto.ats.enums.ATSAggregator;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@AllArgsConstructor
@Component
public class CalendarInviteParser implements SchedulingProcessing {

	private final ATSCommunicationTemplateConfigRepository atsCommunicationTemplateConfigRepository;
	private final RegexMatchingHelper regexMatchingHelper;

	private final PartnerATSIntegrationRepository partnerATSIntegrationRepository;
	private final ATSPartnerConfigurationManager atsPartnerConfigurationManager;

	private final static String EVENT_TYPE_SCHEDULING = "SCHEDULING";

	@Override
	public void process(final SchedulingData data) throws IOException {
		this.parseMeetingInvite(data);
	}

	private void parseMeetingInvite(final SchedulingData data) {

		// TODO: Check if partner is Saas Partner
		final String partnerId = data.getPartnerId();

		// Determining ATS Provider and Aggregator of partner
		final PartnerATSIntegrationDAO partnerATSIntegrationDAO = this.partnerATSIntegrationRepository
				.findAllByPartnerId(partnerId).get(0);

		final ATSProvider atsProvider = ATSProvider.fromString(partnerATSIntegrationDAO.getAtsProvider());
		final ATSAggregator atsAggregator = partnerATSIntegrationDAO.getAtsAggregator();

		final Optional<ATSCommunicationTemplateConfigDAO> atsEventToCommunicationTemplateDAOOptional = this.atsCommunicationTemplateConfigRepository
				.findByPartnerIdAndAtsProviderAndEventType(partnerId, atsProvider, EVENT_TYPE_SCHEDULING);

		if (atsEventToCommunicationTemplateDAOOptional.isEmpty()) {
			throw new IllegalArgumentException(
					String.format("ATS communication configuration absent for partner : %s and ats : %s for event : %s",
							partnerId, atsProvider.getValue(), EVENT_TYPE_SCHEDULING));
		}

		// Parsing subject of invite
		this.parseMeetingContent(data, data.getBrCalendarEvent().getSummary(),
				atsEventToCommunicationTemplateDAOOptional.get());

		// Parsing body of invite
		this.parseMeetingContent(data, data.getBrCalendarEvent().getDescription(),
				atsEventToCommunicationTemplateDAOOptional.get());

		if (Boolean.FALSE.equals(this.checkIfAllDataObtainedFromInvite(data.getInviteVariableValueMapping()))) {
			throw new IllegalArgumentException("All data needed from invite not obtained.");
		}

		data.setAtsExpertInterviewLandingPageLink(
				data.getInviteVariableValueMapping().get("ATS_EXPERT_INTERVIEW_LANDING_LINK"));
		data.setPartnerId(partnerId);
		data.setAtsProvider(atsProvider);
		data.setAtsAggregator(atsAggregator);

	}

	private void parseMeetingContent(final SchedulingData data, final String content,
			final ATSCommunicationTemplateConfigDAO atsCommunicationTemplateConfigDAO) {

		Map<String, String> variableValueMapping = data.getInviteVariableValueMapping();

		if (variableValueMapping == null) {
			variableValueMapping = new HashMap<>();
		}

		variableValueMapping
				.putAll(this.getVariableValueMappingFromOriginalInvite(content, atsCommunicationTemplateConfigDAO));
		data.setInviteVariableValueMapping(variableValueMapping);
	}

	private Boolean checkIfAllDataObtainedFromInvite(
			final Map<String, String> variableValueMappingFromOriginalInvite) {

		for (Map.Entry<String, String> variableValueMapping : variableValueMappingFromOriginalInvite.entrySet()) {
			if (variableValueMapping.getValue() == null) {
				return Boolean.FALSE;
			}
		}

		return Boolean.TRUE;
	}

	private Map<String, String> getVariableValueMappingFromOriginalInvite(final String content,
			final ATSCommunicationTemplateConfigDAO atsCommunicationTemplateConfigDAO) {
		final Map<String, String> variableRegexMap = atsCommunicationTemplateConfigDAO.getVariableRegexMapping()
				.stream()
				.collect(Collectors.toMap(VariableRegexMapping::getVariableName, VariableRegexMapping::getRegex));

		final Map<String, String> variableValueMap = new HashMap<>();

		for (Map.Entry<String, String> variableRegexMapping : variableRegexMap.entrySet()) {

			final List<String> matchedValuesForRegex = this.regexMatchingHelper
					.getMatchedValuesForRegex(content, variableRegexMapping.getValue());

			if (matchedValuesForRegex.size() != 0) {
				variableValueMap.put(variableRegexMapping.getKey(), this.regexMatchingHelper
						.getMatchedValuesForRegex(content, variableRegexMapping.getValue()).get(0));
			}
		}

		return variableValueMap;
	}

}
