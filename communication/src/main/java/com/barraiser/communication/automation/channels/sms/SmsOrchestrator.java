/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.channels.sms;

import com.barraiser.communication.automation.pipeline.CommunicationOrchestrator;
import com.barraiser.communication.automation.channels.sms.dto.SmsData;
import com.barraiser.communication.automation.constants.Channel;
import com.barraiser.communication.automation.pipeline.pojo.CommunicationInput;
import com.barraiser.communication.common.CommunicationStaticAppConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
public class SmsOrchestrator implements CommunicationOrchestrator {

	private final SmsTemplatePopulator smsTemplatePopulator;
	private final SmsRecipientsFetcher smsRecipientsFetcher;
	private final SmsSender smsSender;
	private final CommunicationStaticAppConfig staticAppConfig;
	private final ObjectMapper objectMapper;

	@Override
	public Channel getChannel() {
		return Channel.SMS;
	}

	@Override
	public JsonNode communicate(final CommunicationInput input) {
		SmsData data = new SmsData();
		data.setInput(input);

		this.smsTemplatePopulator.process(data);

		this.smsRecipientsFetcher.process(data);

		this.smsSender.process(data);

		return this.objectMapper.valueToTree(data);
	}

}
