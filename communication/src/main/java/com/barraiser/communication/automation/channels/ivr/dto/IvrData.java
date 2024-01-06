/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.channels.ivr.dto;

import com.barraiser.communication.automation.pipeline.pojo.CommunicationInput;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.Map;

@Data
public class IvrData {

	private IvrMessage ivrMessage;

	private IvrRecipient ivrRecipient;

	private Map<String, String> ivrContextVariables;

	@JsonIgnore
	private CommunicationInput input;
}
