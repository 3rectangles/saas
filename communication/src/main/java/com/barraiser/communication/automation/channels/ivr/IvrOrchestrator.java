/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.channels.ivr;

import com.barraiser.communication.automation.pipeline.CommunicationOrchestrator;
import com.barraiser.communication.automation.channels.ivr.dto.IvrData;
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
public class IvrOrchestrator implements CommunicationOrchestrator {

	private final IvrTemplatePopulator ivrTemplatePopulator;
	private final IvrRecipientsFetcher ivrRecipientsFetcher;
	private final IvrSender ivrSender;
	private final ObjectMapper objectMapper;

	@Override
	public Channel getChannel() {
		return Channel.IVR;
	}

	@Override
	public JsonNode communicate(final CommunicationInput input) {

		IvrData data = new IvrData();

		data.setInput(input);

		this.ivrTemplatePopulator.process(data);

		this.ivrRecipientsFetcher.process(data);

		this.ivrSender.process(data);

		return this.objectMapper.valueToTree(data);
	}

}
