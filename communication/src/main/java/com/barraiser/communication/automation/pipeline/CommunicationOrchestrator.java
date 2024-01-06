/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.pipeline;

import com.barraiser.communication.automation.constants.Channel;
import com.barraiser.communication.automation.pipeline.pojo.CommunicationInput;
import com.fasterxml.jackson.databind.JsonNode;
import com.slack.api.methods.SlackApiException;

import java.io.IOException;

public interface CommunicationOrchestrator {
	Channel getChannel();

	JsonNode communicate(final CommunicationInput input) throws Exception;
}
