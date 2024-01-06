/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.message;

import com.barraiser.communication.common.CommunicationStaticAppConfig;
import com.barraiser.communication.dal.NotificationDAO;
import com.barraiser.communication.pojo.SlackMessageParameters;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.element.BlockElement;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.slack.api.model.block.Blocks.*;
import static com.slack.api.model.block.composition.BlockCompositions.markdownText;
import static com.slack.api.model.block.composition.BlockCompositions.plainText;
import static com.slack.api.model.block.element.BlockElements.button;

@Service
@Component
@AllArgsConstructor
public class SlackMessageComposer {
	private final CommunicationStaticAppConfig communicationStaticAppConfig;

	public List<LayoutBlock> getSlackMessageBlock(final SlackMessageParameters slackMessageParameters,
			final NotificationDAO selectedChannel) {
		List<LayoutBlock> messageBlock = new ArrayList<LayoutBlock>();

		final LayoutBlock headerBlock = this.getSlackBlockHeader(
				selectedChannel.getTemplate().getHeader(),
				slackMessageParameters.getUserName(),
				slackMessageParameters.getJobRole());
		messageBlock.add(headerBlock);

		String interviewRound;
		try {
			interviewRound = slackMessageParameters.getInterviewRound().toString();
		} catch (Exception e) {
			interviewRound = "00";
		}
		final LayoutBlock titleBlock = this.getSlackBlockTitle(
				selectedChannel.getTemplate().getTitle(),
				slackMessageParameters.getJobRole(),
				slackMessageParameters.getDomain(),
				interviewRound);
		messageBlock.add(titleBlock);

		final LayoutBlock buttonBlock = this.getSlackBlockButton(
				selectedChannel.getTemplate().getButton(),
				slackMessageParameters.getPartnerId(),
				slackMessageParameters.getEvaluationId());
		messageBlock.add(buttonBlock);

		final LayoutBlock footerBlock = this.getSlackBlockFooter(
				selectedChannel.getTemplate().getFooter());
		messageBlock.add(footerBlock);

		return messageBlock;
	}

	private LayoutBlock getSlackBlockHeader(final String headerTemplate, final String userName, final String jobRole) {
		LayoutBlock header = header(
				headerBlockBuilder -> headerBlockBuilder.text(
						plainText(String.format(headerTemplate, userName, jobRole))));
		return header;
	}

	private LayoutBlock getSlackBlockTitle(final String titleTemplate, final String jobRole, final String domain,
			final String interviewRound) {
		LayoutBlock title = null;
		if (!interviewRound.equals("00")) {
			title = section(
					section -> section.text(
							markdownText(String.format(titleTemplate, interviewRound, jobRole, domain))));
		} else {
			title = section(
					section -> section.text(
							markdownText(String.format(titleTemplate, jobRole, domain))));
		}
		return title;
	}

	private LayoutBlock getSlackBlockButton(Map<String, String> buttonDictionary, final String partnerId,
			final String evaluationId) {
		List<BlockElement> buttonList = new ArrayList<>();
		for (String buttonName : buttonDictionary.keySet()) {
			String link = null;
			if (buttonDictionary.get(buttonName).equals(this.communicationStaticAppConfig.getEvaluationLink())) {
				link = String.format(buttonDictionary.get(buttonName), partnerId, evaluationId);
			} else if (buttonDictionary.get(buttonName).equals(this.communicationStaticAppConfig.getBgsLink())) {
				link = String.format(buttonDictionary.get(buttonName), evaluationId);
			}
			final String buttonLink = link;
			BlockElement button = button(
					b -> b.text(plainText(buttonName))
							.value("click_me_123")
							.url(buttonLink));
			buttonList.add(button);
		}

		LayoutBlock buttons = actions(
				actions -> actions.elements(
						buttonList));

		return buttons;
	}

	private LayoutBlock getSlackBlockFooter(final String footerTemplate) {
		LayoutBlock footer = section(
				section -> section.text(
						markdownText("_" + footerTemplate + "_")));
		return footer;
	}
}
