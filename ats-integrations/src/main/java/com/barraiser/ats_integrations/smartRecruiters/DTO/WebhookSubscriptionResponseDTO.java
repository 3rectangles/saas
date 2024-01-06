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
public class WebhookSubscriptionResponseDTO {
	@JsonProperty("id")
	private String id;

	@JsonProperty("callbackUrl")
	private String callbackUrl;

	@JsonProperty("events")
	private List<Event> events;

	@JsonProperty("status")
	private String status;

	@SuperBuilder(toBuilder = true)
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Event {
		@JsonProperty("name")
		private String name;

		@JsonProperty("version")
		private String version;
	}
}
