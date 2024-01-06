/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.ats_integrations.publisher;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.QueueDoesNotExistException;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SqsProducer {

	private final AmazonSQS amazonSQS;

	private final ObjectMapper objectMapper;

	public <T> SendMessageResult publish(String queueUrl, T obj) throws Exception {
		if (StringUtils.isNotEmpty(queueUrl)) {
			try {
				return publishInternal(queueUrl, obj);
			} catch (QueueDoesNotExistException e) {
				int lastIndexOf = queueUrl.lastIndexOf("/") + 1;
				this.amazonSQS.createQueue(queueUrl.substring(lastIndexOf));
				return publishInternal(queueUrl, obj);
			}
		}
		return null;
	}

	private <T> SendMessageResult publishInternal(String queueUrl, T obj) throws JsonProcessingException {
		return this.amazonSQS.sendMessage(
				new SendMessageRequest().withQueueUrl(queueUrl).withMessageBody(objectMapper.writeValueAsString(obj)));
	}

}
