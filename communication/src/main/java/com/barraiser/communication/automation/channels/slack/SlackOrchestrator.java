/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.channels.slack;

import com.barraiser.communication.automation.channels.slack.dto.SlackData;
import com.barraiser.communication.automation.constants.Channel;
import com.barraiser.communication.automation.pipeline.CommunicationOrchestrator;
import com.barraiser.communication.automation.pipeline.pojo.CommunicationInput;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.slack.api.methods.SlackApiException;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Log4j2
@AllArgsConstructor
public class SlackOrchestrator implements CommunicationOrchestrator {
	private final SlackMessageSender slackMessageSender;
	private final SlackRecipientsFetcher slackRecipientsFetcher;
	private final SlackTemplatePopulator slackTemplatePopulator;
	private final ObjectMapper objectMapper;

	@Override
	public Channel getChannel() {
		return Channel.SLACK;
	}

	@Override
	public JsonNode communicate(CommunicationInput input) throws Exception {
		SlackData data = new SlackData();
		data.setInput(input);

		this.slackTemplatePopulator.process(data);

		this.slackRecipientsFetcher.process(data);

		this.slackMessageSender.process(data);

		return this.objectMapper.valueToTree(data);
	}
}
