/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.channels.slack;

import com.barraiser.communication.automation.channels.slack.dto.SlackData;
import com.barraiser.communication.automation.channels.slack.dto.SlackRecipient;
import com.barraiser.communication.automation.constants.Channel;
import com.barraiser.communication.automation.dal.SlackRecipientConfigurationDAO;
import com.barraiser.communication.automation.dal.SlackRecipientConfigurationRepository;
import com.barraiser.communication.automation.pipeline.CommunicationProcessor;
import com.barraiser.communication.dal.ChannelConfigurationDAO;
import com.barraiser.communication.dal.ChannelConfigurationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Log4j2
@AllArgsConstructor
public class SlackRecipientsFetcher implements CommunicationProcessor<SlackData> {
	private final ChannelConfigurationRepository channelConfigurationRepository;
	private final SlackRecipientConfigurationRepository slackRecipientConfigurationRepository;

	@Override
	public Channel getChannel() {
		return Channel.SLACK;
	}

	@Override
	public void process(SlackData data) {
		final SlackRecipientConfigurationDAO slackRecipientConfigurationDAO = this.slackRecipientConfigurationRepository
				.findByPartnerIdAndEventType(
						data
								.getInput()
								.getEntity()
								.getPartnerId(),
						data
								.getInput()
								.getEventType())
				.get();

		final List<SlackRecipient> slackRecipients = new ArrayList<>();

		for (String channelConfigurationId : slackRecipientConfigurationDAO.getChannelConfigurationIds()) {
			final ChannelConfigurationDAO channelConfigurationDAO = this.channelConfigurationRepository
					.findById(channelConfigurationId)
					.get();

			slackRecipients.add(SlackRecipient
					.builder()
					.recipientId(channelConfigurationDAO
							.getRecipientId())
					.recipientSecret(channelConfigurationDAO
							.getSecrets())
					.build());
		}

		data.setRecipients(slackRecipients);
	}
}
