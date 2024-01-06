/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.channels.slack;

import com.barraiser.communication.automation.channels.slack.dto.SlackMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.element.BlockElement;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.composition.BlockCompositions.markdownText;
import static com.slack.api.model.block.composition.BlockCompositions.plainText;
import static com.slack.api.model.block.element.BlockElements.button;

@Component
@Log4j2
@AllArgsConstructor
public class SlackMessageBlockComposer {
	private final ObjectMapper objectMapper;

	public List<LayoutBlock> getSlackMessageBlock(final SlackMessage slackMessage) throws JsonProcessingException {
		final List<LayoutBlock> messageLayoutBlocks = new ArrayList<>();

		messageLayoutBlocks.add(this.getSlackHeaderBlock(slackMessage.getHeader()));

		messageLayoutBlocks.add(this.getSlackTitleBlock((slackMessage.getTitle())));

		messageLayoutBlocks.add(this.getSlackButtonBlock(slackMessage.getButton()));

		messageLayoutBlocks.add(this.getSlackFooterBlock(slackMessage.getFooter()));

		return messageLayoutBlocks;
	}

	private LayoutBlock getSlackHeaderBlock(final String header) {
		return header(
				headerBlockBuilder -> headerBlockBuilder.text(
						plainText(header)));
	}

	private LayoutBlock getSlackTitleBlock(final String title) {
		return section(
				section -> section.text(
						markdownText(title)));
	}

	private LayoutBlock getSlackButtonBlock(final String buttonString) throws JsonProcessingException {
		List<BlockElement> buttonList = new ArrayList<>();
		final Map<String, String> buttonDictionary = this.objectMapper
				.readValue(
						buttonString,
						new TypeReference<>() {
						});

		for (String buttonName : buttonDictionary.keySet()) {
			BlockElement button = button(
					b -> b.text(plainText(buttonName))
							.value("click_me_123")
							.url(buttonDictionary.get(buttonName)));
			buttonList.add(button);
		}

		LayoutBlock buttons = actions(
				actions -> actions.elements(
						buttonList));

		return buttons;
	}

	private LayoutBlock getSlackFooterBlock(final String footer) {
		return section(
				section -> section.text(
						markdownText(
								String.format(
										"_%s_",
										footer))));
	}
}
