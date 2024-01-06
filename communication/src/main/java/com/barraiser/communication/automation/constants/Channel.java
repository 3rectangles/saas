/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.constants;

public enum Channel {
	EMAIL("EMAIL"),

	SLACK("SLACK"),

	SMS("SMS"),

	WHATSAPP("WHATSAPP"),

	IVR("IVR");

	private final String channel;

	Channel(final String channel) {
		this.channel = channel;
	}

	public String getValue() {
		return this.channel;
	}
}
