/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.channels.whatsapp.dto;

import com.barraiser.communication.automation.pipeline.pojo.CommunicationInput;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class WhatsappData {

	private WhatsappMessage whatsappMessage;

	private WhatsappRecipient whatsappRecipient;

	@JsonIgnore
	private CommunicationInput input;
}
