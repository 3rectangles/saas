/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.channels.whatsapp;

import com.barraiser.communication.automation.channels.whatsapp.dto.WhatsappMessage;
import com.barraiser.communication.automation.pipeline.CommunicationProcessor;
import com.barraiser.communication.automation.QueryDataFetcher;
import com.barraiser.communication.automation.channels.whatsapp.dto.WhatsappData;
import com.barraiser.communication.automation.constants.Channel;
import com.barraiser.communication.automation.dal.WhatsappTemplateDAO;
import com.barraiser.communication.automation.dal.WhatsappTemplateRepository;
import com.barraiser.communication.automation.template.TemplatePopulator;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Log4j2
@RequiredArgsConstructor
public class WhatsappTemplatePopulator implements CommunicationProcessor<WhatsappData> {
	private final WhatsappTemplateRepository whatsappTemplateRepository;
	private final QueryDataFetcher queryDataFetcher;
	private final TemplatePopulator templatePopulator;

	@Override
	public Channel getChannel() {
		return Channel.WHATSAPP;
	}

	@Override
	public void process(WhatsappData data) {
		final WhatsappTemplateDAO whatsappTemplateDAO = this.whatsappTemplateRepository
				.findById(data.getInput().getTemplateId()).get();
		final Object queryData = this.queryDataFetcher.fetchQueryData(whatsappTemplateDAO.getQuery(),
				data.getInput().getEntity());
		((Map<String, Object>) queryData).put("event", data.getInput().getEventPayload());
		final String variablesPopulated = this.templatePopulator
				.populateTemplate(whatsappTemplateDAO.getMessageBirdTemplateVariables(), queryData);
		List<String> templateVariables = new ArrayList<>(Arrays.asList(variablesPopulated.split(",")));
		templateVariables = templateVariables.stream().map(
				t -> Strings.isBlank(t) ? " " : t).collect(Collectors.toList());
		data.setWhatsappMessage(WhatsappMessage.builder()
				.templateName(whatsappTemplateDAO.getMessageBirdTemplateName())
				.templateVariables(templateVariables)
				.build());
	}

}
