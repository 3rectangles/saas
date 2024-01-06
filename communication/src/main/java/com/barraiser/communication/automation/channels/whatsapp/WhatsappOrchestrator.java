/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.channels.whatsapp;

import com.barraiser.communication.automation.pipeline.CommunicationOrchestrator;
import com.barraiser.communication.automation.channels.whatsapp.dto.WhatsappData;
import com.barraiser.communication.automation.constants.Channel;
import com.barraiser.communication.automation.pipeline.pojo.CommunicationInput;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
public class WhatsappOrchestrator implements CommunicationOrchestrator {

	private final WhatsappTemplatePopulator whatsappTemplatePopulator;
	private final WhatsappRecipientsFetcher whatsappRecipientsFetcher;
	private final WhatsappSender whatsappSender;
	private final ObjectMapper objectMapper;

	@Override
	public Channel getChannel() {
		return Channel.WHATSAPP;
	}

	@Override
	public JsonNode communicate(final CommunicationInput input) {
		WhatsappData data = new WhatsappData();
		data.setInput(input);

		this.whatsappTemplatePopulator.process(data);

		this.whatsappRecipientsFetcher.process(data);

		this.whatsappSender.process(data);

		return this.objectMapper.valueToTree(data);
	}

}
