/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.pipeline;

import com.barraiser.common.entity.Entity;
import com.barraiser.communication.automation.dal.CommunicationTemplateConfigDAO;
import com.barraiser.communication.automation.dal.CommunicationTemplateConfigRepository;
import com.barraiser.communication.automation.pipeline.pojo.CommunicationInput;
import com.barraiser.communication.automation.template.TemplatePopulator;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Component
@RequiredArgsConstructor
public class CommunicationInputsFetcher {
	private final CommunicationTemplateConfigRepository communicationTemplateConfigRepository;
	private final TemplatePopulator templatePopulator;

	public List<CommunicationInput> getInputs(final String eventType, final Object eventPayload, final Entity entity) {
		final List<CommunicationTemplateConfigDAO> templateConfigs = this.getTemplateConfigs(eventType, eventPayload,
				entity.getPartnerId());

		return templateConfigs.stream()
				.map(t -> CommunicationInput.builder()
						.recipientType(t.getRecipientType())
						.templateId(this.getTemplateId(t.getTemplateRule(), eventPayload))
						.channel(t.getChannel())
						.entity(entity)
						.eventType(eventType)
						.eventPayload(eventPayload)
						.build())
				.filter(t -> t.getTemplateId() != null)
				.collect(Collectors.toList());
	}

	private List<CommunicationTemplateConfigDAO> getTemplateConfigs(final String eventType, final Object eventPayload,
			final String partnerId) {
		// Fetch partner specific template configs
		List<CommunicationTemplateConfigDAO> templateConfigs = this.communicationTemplateConfigRepository
				.findAllByEventTypeAndPartnerId(eventType, partnerId);
		// Fetch default template configs if not customised by partner
		if (templateConfigs.isEmpty()) {
			templateConfigs.addAll(this.communicationTemplateConfigRepository
					.findAllByEventTypeAndPartnerId(eventType, null));
		}
		// Fetch our template configs
		templateConfigs.addAll(this.communicationTemplateConfigRepository
				.findAllByEventTypeAndPartnerId(eventType, "BarRaiser"));

		templateConfigs = this.excludeRecipients(templateConfigs, eventPayload);

		return templateConfigs.stream().filter(t -> Boolean.TRUE.equals(t.getEnabled())).collect(Collectors.toList());
	}

	private List<CommunicationTemplateConfigDAO> excludeRecipients(
			final List<CommunicationTemplateConfigDAO> templateConfigs, final Object eventPayload) {
		final String excludedRecipients = (String) ((Map<?, ?>) eventPayload).get("excludeRecipients");
		if (excludedRecipients != null) {
			List<String> excludedRecipientsList = Arrays.asList(excludedRecipients.split("\\s*,\\s*"));
			return templateConfigs.stream()
					.filter(t -> !excludedRecipientsList.contains(t.getRecipientType().getValue()))
					.collect(Collectors.toList());
		}
		return templateConfigs;
	}

	private String getTemplateId(final String templateRule, final Object eventPayload) {
		final Map<String, Object> data = new HashMap<>();
		data.put("event", eventPayload);
		final String templateId = this.templatePopulator.populateTemplate(templateRule, data);
		if (templateId == null || Strings.isBlank(templateId.trim())) {
			return null;
		}
		return templateId;
	}

}
