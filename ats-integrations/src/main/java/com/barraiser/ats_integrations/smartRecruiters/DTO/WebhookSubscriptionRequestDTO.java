/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.smartRecruiters.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WebhookSubscriptionRequestDTO {
	@JsonProperty("callbackUrl")
	private String callbackUrl;

	@JsonProperty("events")
	private List<String> events;

	@JsonProperty("alertingEmailAddress")
	private String alertingEmailAddress;

	@JsonProperty("callbackAuthentication")
	private CallbackAuthentication callbackAuthentication;

	@SuperBuilder(toBuilder = true)
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class CallbackAuthentication {
		@JsonProperty("type")
		private String type;

		@JsonProperty("headerName")
		private String headerName;

		@JsonProperty("headerValue")
		private String headerValue;
	}
}
