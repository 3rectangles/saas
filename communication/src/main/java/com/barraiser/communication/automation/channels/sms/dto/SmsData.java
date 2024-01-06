/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.channels.sms.dto;

import com.barraiser.communication.automation.pipeline.pojo.CommunicationInput;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class SmsData {

	private SmsMessage smsMessage;

	private SmsRecipient smsRecipient;

	@JsonIgnore
	private CommunicationInput input;
}
