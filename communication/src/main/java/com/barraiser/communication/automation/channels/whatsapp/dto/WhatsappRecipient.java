/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.channels.whatsapp.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder(toBuilder = true)
@Getter
public class WhatsappRecipient {
	private final String toPhoneNumber;
}
