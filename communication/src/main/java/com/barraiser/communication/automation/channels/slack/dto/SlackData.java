/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.channels.slack.dto;

import com.barraiser.communication.automation.pipeline.pojo.CommunicationInput;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
public class SlackData {
	private SlackMessage message;

	private List<SlackRecipient> recipients;
	@JsonIgnore
	private CommunicationInput input;
}
