/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.message;

import com.barraiser.communication.dal.ChannelConfigurationDAO;
import com.barraiser.communication.dal.NotificationDAO;
import com.barraiser.communication.dal.NotificationRepository;
import com.barraiser.communication.dal.ChannelConfigurationRepository;
import com.barraiser.communication.pojo.SlackMessageParameters;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.methods.response.chat.ChatPostMessageResponse;
import com.slack.api.model.block.LayoutBlock;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Log4j2
@Service
@AllArgsConstructor
public class SendMessageSlack {
	private final NotificationRepository notificationRepository;
	private final ChannelConfigurationRepository channelConfigurationRepository;
	private final MethodsClient methodsClient;
	private final SlackMessageComposer composeSlackBlock;

	public void sendMessageToRecipients(final SlackMessageParameters slackMessageParameters) {
		log.info("Slack Message Received - partnerId: " + slackMessageParameters.getPartnerId() + " ,evaluationId: "
				+ slackMessageParameters.getEvaluationId() + " ,eventType: " + slackMessageParameters.getEventType());
		final List<NotificationDAO> channelToSendEvent = this.notificationRepository
				.findAllByEventTypeAndPartnerIdIsNotNullAndDisabledOnIsNull(slackMessageParameters.getEventType());
		if (channelToSendEvent != null) {
			channelToSendEvent.stream().forEach((selectedChannel) -> {
				final ChannelConfigurationDAO channel = this.channelConfigurationRepository
						.findById(selectedChannel.getConfigId()).get();
				if (channel.getTargetEntityId().equals(slackMessageParameters.getPartnerId())) {
					List<LayoutBlock> slackMessage = composeSlackBlock.getSlackMessageBlock(slackMessageParameters,
							selectedChannel);
					final ChatPostMessageRequest messageRequest = ChatPostMessageRequest.builder()
							.channel(channel.getRecipientId())
							.token(channel.getSecrets())
							.blocks(slackMessage)
							.build();

					try {
						final ChatPostMessageResponse response = this.methodsClient.chatPostMessage(messageRequest);
						log.info("Slack Request : " + slackMessage.toString() + " Response : " + response);
					} catch (final IOException | SlackApiException e) {
						log.info("Slack Message Post Failed for partnerId: " + slackMessageParameters.getPartnerId()
								+ " ,evaluationId: " + slackMessageParameters.getEvaluationId() + " ,eventType: "
								+ slackMessageParameters.getEventType());
						e.printStackTrace();
					}
				}
			});
		}
	}
}
