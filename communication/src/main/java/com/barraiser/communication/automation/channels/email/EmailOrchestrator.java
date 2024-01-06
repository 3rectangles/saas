/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.channels.email;

import com.barraiser.communication.automation.pipeline.CommunicationOrchestrator;
import com.barraiser.communication.automation.channels.email.dto.EmailData;
import com.barraiser.communication.automation.constants.Channel;
import com.barraiser.communication.automation.pipeline.pojo.CommunicationInput;
import com.barraiser.communication.common.CommunicationStaticAppConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Log4j2
@RequiredArgsConstructor
public class EmailOrchestrator implements CommunicationOrchestrator {
	private final EmailTemplatePopulator emailTemplatePopulator;
	private final EmailRecipientsFetcher emailRecipientsFetcher;
	private final EmailSender emailSender;
	private final CommunicationStaticAppConfig staticAppConfig;
	private final ObjectMapper objectMapper;

	@Override
	public Channel getChannel() {
		return Channel.EMAIL;
	}

	@Override
	public JsonNode communicate(final CommunicationInput input) throws IOException {
		EmailData data = new EmailData();
		try {

			data.setInput(input);

			data.setFromEmail(this.staticAppConfig.getEmailFromAddress());

			this.emailTemplatePopulator.process(data);

			this.emailRecipientsFetcher.process(data);

			this.emailSender.process(data);

			return this.objectMapper.valueToTree(data);
		} catch (final Exception e) {
			log.error(e.getMessage() + " : " + this.objectMapper.writeValueAsString(data), e);
			throw e;
		}
	}
}
