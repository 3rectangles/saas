/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.channels.slack.dto;

import com.barraiser.communication.automation.constants.Channel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder(toBuilder = true)
@Getter
public class SlackRecipient {
	String recipientId;

	@JsonIgnore
	String recipientSecret;
}
