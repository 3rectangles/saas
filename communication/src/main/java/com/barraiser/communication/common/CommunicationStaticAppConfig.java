/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.common;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class CommunicationStaticAppConfig {
	@Value("${queue.slack-notification}")
	private String slackEventSQSUrl;

	@Value("${communication.api-key-name}")
	private String communicationServiceApiKeyName;

	@Value("${communication.email.fromAddress}")
	private String emailFromAddress;

	@Value("${notification.interview.lifecycle.email}")
	private String interviewLifecycleInformationEmail;

	@Value("${aws.sns.sms.senderId}")
	private String awsSnsSmsSenderId;

	@Value("${aws.sns.sms.maxPrice}")
	private String awsSnsSmsMaxPrice;

	@Value("${aws.sns.sms.smsType}")
	private String awsSnsSmsType;

	@Value("${interview.confirmation.messagebird.whatsapp.namespace}")
	private String messageBirdWhatsappNamespace;

	@Value("${interview.confirmation.messagebird.whatsapp.candidateChannelId}")
	private String messageBirdCandidateWhatsappChannelId;

	@Value("${communication.phone.countryCode.india}")
	private Integer phoneCountryCodeIndia;

	@Value("${communication.phone.length.india}")
	private Integer phoneLengthIndia;

	@Value("${interview.followUpForScheduling.messagebird.ivr.flowId}")
	private String candidateFollowUpForSchedulingMessagebirdIvrFlowId;

	@Value("${interview.confirmation.messagebird.whatsapp.expertChannelId}")
	private String messageBirdExpertWhatsappChannelId;

	@Value("${evaluation-link}")
	private String evaluationLink;

	@Value("${bgs-link}")
	private String bgsLink;

	@Value("${login-link}")
	private String loginLink;
}
