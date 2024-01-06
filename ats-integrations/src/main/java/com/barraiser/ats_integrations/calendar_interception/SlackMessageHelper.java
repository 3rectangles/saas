/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.calendar_interception;

import com.barraiser.commons.eventing.schema.commons.calendar.BRCalendarEvent;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import com.slack.api.model.block.LayoutBlock;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import static com.slack.api.model.block.Blocks.header;
import static com.slack.api.model.block.Blocks.section;
import static com.slack.api.model.block.composition.BlockCompositions.markdownText;
import static com.slack.api.model.block.composition.BlockCompositions.plainText;

@AllArgsConstructor
@Component
@Log4j2
public class SlackMessageHelper {

	private final MethodsClient methodsClient;
	// Posting to Slack Channel - "alerting-interception-failures"
	private static final String SLACK_CHANNEL_ID = "C05LD15DEQ5";
	private static final String SLACK_CHANNEL_TOKEN = "xoxb-1751859617011-2354816276471-FC0gK43nUGfX9iRNwvWuBJVN";
	private static final String SLACK_MESSAGE_POST_FAILURE = "Slack Message Post Failed ";
	private static final String ATS_FAILURE_HEADER_MESSAGE = "Calendar Interception Failed";
	private static final String ATS_FAILURE_MESSAGE_BODY = "Failed for organiser Email Id: %s \n Event Id: %s \n Event Description: \n %s \n\n Stack Trace: \n %s ";

	public void sendErrorMessageSlack(final BRCalendarEvent brCalendarEvent, Exception E) {
		List<LayoutBlock> slackMessage = this.getSlackMessageBlock(brCalendarEvent, E);

		final ChatPostMessageRequest messageRequest = ChatPostMessageRequest.builder()
				.channel(SLACK_CHANNEL_ID)
				.token(SLACK_CHANNEL_TOKEN)
				.blocks(slackMessage)
				.build();

		try {
			this.methodsClient.chatPostMessage(messageRequest);
		} catch (final IOException | SlackApiException e) {
			log.info(SLACK_MESSAGE_POST_FAILURE);
		}
	}

	private List<LayoutBlock> getSlackMessageBlock(final BRCalendarEvent event, final Exception E) {
		List<LayoutBlock> messageBlock = new ArrayList<>();

		messageBlock.add(header(
				headerBlockBuilder -> headerBlockBuilder.text(
						plainText(ATS_FAILURE_HEADER_MESSAGE))));

		messageBlock.add(
				section(
						section -> section.text(markdownText(
								String.format(ATS_FAILURE_MESSAGE_BODY,
										event.getOrganizer().getEmailId(),
										event.getProviderEventId(),
										event.getDescription(),
										this.getStackTraceString(E))))));

		return messageBlock;
	}

	private String getStackTraceString(Exception E) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		E.printStackTrace(pw);
		return sw.toString();
	}
}
