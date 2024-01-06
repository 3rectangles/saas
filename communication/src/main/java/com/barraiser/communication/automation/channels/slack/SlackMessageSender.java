/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.channels.slack;

import com.barraiser.communication.automation.channels.slack.dto.SlackData;
import com.barraiser.communication.automation.channels.slack.dto.SlackRecipient;
import com.barraiser.communication.automation.channels.whatsapp.dto.WhatsappRecipient;
import com.barraiser.communication.automation.constants.Channel;
import com.barraiser.communication.automation.pipeline.CommunicationProcessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.model.block.LayoutBlock;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@Log4j2
@AllArgsConstructor
public class SlackMessageSender implements CommunicationProcessor<SlackData> {
	private final MethodsClient methodsClient;
	private final SlackMessageBlockComposer slackMessageBlockComposer;

	@Override
	public Channel getChannel() {
		return Channel.SLACK;
	}

	@Override
	public void process(SlackData data) throws SlackApiException, IOException {

		if (!this.hasRecepients(data.getRecipients())) {
			log.info("NO_RECEPIENT:Not sending Slack message as no recepient available.");
			return;
		}

		for (SlackRecipient slackRecipient : data.getRecipients()) {
			try {
				final List<LayoutBlock> slackMessage = this.slackMessageBlockComposer
						.getSlackMessageBlock(data
								.getMessage());

				final ChatPostMessageRequest slackMessageRequest = ChatPostMessageRequest
						.builder()
						.channel(slackRecipient
								.getRecipientId())
						.token(slackRecipient
								.getRecipientSecret())
						.blocks(slackMessage)
						.build();

				final ChatPostMessageResponse response = this.methodsClient
						.chatPostMessage(slackMessageRequest);
			} catch (Exception exception) {
				log.error(
						String.format(
								"Unable to send message on slack for partnerId : %s channelId : %s",
								data
										.getInput()
										.getEntity()
										.getPartnerId(),
								slackRecipient
										.getRecipientId()),
						exception);

				throw exception;
			}
		}
	}

	private Boolean hasRecepients(final List<SlackRecipient> slackRecipients) {
		return slackRecipients != null && slackRecipients.size() > 0;
	}
}
