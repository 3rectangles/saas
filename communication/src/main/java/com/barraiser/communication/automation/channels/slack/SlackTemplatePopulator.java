/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.channels.slack;

import com.barraiser.communication.automation.QueryDataFetcher;
import com.barraiser.communication.automation.channels.slack.dto.SlackData;
import com.barraiser.communication.automation.channels.slack.dto.SlackMessage;
import com.barraiser.communication.automation.constants.Channel;
import com.barraiser.communication.automation.dal.SlackTemplateDAO;
import com.barraiser.communication.automation.dal.SlackTemplateRepository;
import com.barraiser.communication.automation.pipeline.CommunicationProcessor;
import com.barraiser.communication.automation.template.TemplatePopulator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
public class SlackTemplatePopulator implements CommunicationProcessor<SlackData> {
	private final TemplatePopulator templatePopulator;
	private final SlackTemplateRepository slackTemplateRepository;
	private final QueryDataFetcher queryDataFetcher;
	private final ObjectMapper objectMapper;

	@Override
	public Channel getChannel() {
		return Channel.SLACK;
	}

	@Override
	public void process(SlackData data) throws JsonProcessingException {
		final SlackTemplateDAO slackTemplateDAO = this.slackTemplateRepository
				.findById(data
						.getInput()
						.getTemplateId())
				.get();

		final Object queryData = this.queryDataFetcher
				.fetchQueryData(
						slackTemplateDAO
								.getQuery(),
						data
								.getInput()
								.getEntity());

		final String bodyPopulated = this.templatePopulator
				.populateTemplate(slackTemplateDAO.getBody(), queryData);

		SlackMessage slackMessage = null;
		try {
			slackMessage = this.objectMapper.readValue(bodyPopulated, SlackMessage.class);
		} catch (JsonProcessingException exception) {
			log.error(
					String.format(
							"Unable to build slack message from templateId : %s",
							slackTemplateDAO
									.getId()),
					exception);

			throw exception;
		}

		data.setMessage(slackMessage);
	}
}
