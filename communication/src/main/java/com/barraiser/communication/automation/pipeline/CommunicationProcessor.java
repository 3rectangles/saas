/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.pipeline;

import com.barraiser.communication.automation.constants.Channel;
import com.slack.api.methods.SlackApiException;

import java.io.IOException;

public interface CommunicationProcessor<T> {
	Channel getChannel();

	void process(final T data) throws Exception;
}
