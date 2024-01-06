/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.channels.ivr;

import com.barraiser.communication.automation.channels.ivr.dto.IvrRecipient;
import com.barraiser.communication.automation.channels.whatsapp.dto.WhatsappRecipient;
import com.barraiser.communication.automation.pipeline.CommunicationProcessor;
import com.barraiser.communication.automation.constants.Channel;
import com.barraiser.communication.automation.channels.ivr.dto.IvrData;

import com.barraiser.communication.client.MessagebirdFeignClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Map;

@Log4j2
@Component
@AllArgsConstructor
public class IvrSender implements CommunicationProcessor<IvrData> {

	private final MessagebirdFeignClient messagebirdFeignClient;
	private final ObjectMapper objectMapper;
	private final String MESSAGE_BIRD_BASE_URL = "https://flows.messagebird.com/flows/";
	private final String MESSAGE_BIRD_WEBHOOK_URL_CONTEXT_VARIABLE_KEY = "message_bird_flow_id";

	@Override
	public Channel getChannel() {
		return Channel.IVR;
	}

	@Override
	public void process(IvrData data) {

		if (!this.hasRecepient(data.getIvrRecipient())) {
			log.info("NO_RECEPIENT:Not sending IVR  as no recepient available.");
			return;
		}

		final ObjectNode requestBody = this.objectMapper.createObjectNode();
		requestBody.put("body", data.getIvrMessage().getBody());
		requestBody.put("phone", data.getIvrRecipient().getToPhoneNumber());
		final Map<String, String> ivrContextVariables = data.getIvrContextVariables();
		ivrContextVariables.forEach(requestBody::put);
		final String messageBirdFlowId = ivrContextVariables.get(this.MESSAGE_BIRD_WEBHOOK_URL_CONTEXT_VARIABLE_KEY);
		final String uri = String.format("%s%s/invoke", this.MESSAGE_BIRD_BASE_URL,
				messageBirdFlowId.replace("\"", ""));
		this.messagebirdFeignClient.startIVRFlow(URI.create(uri), requestBody);
	}

	private Boolean hasRecepient(final IvrRecipient ivrRecipient) {
		return ivrRecipient.getToPhoneNumber() != null;
	}

}
