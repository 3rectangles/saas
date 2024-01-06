/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.smartRecruiters.requests;

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
public class MessageRequest {
	@JsonProperty("content")
	private String content;

	@JsonProperty("correlationId")
	private String correlationId;

	@JsonProperty("shareWith")
	private ShareWith shareWith;

	@SuperBuilder(toBuilder = true)
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ShareWith {
		@JsonProperty("users")
		private List<String> users;

		@JsonProperty("hiringTeamOf")
		private List<String> hiringTeamOf;

		@JsonProperty("everyone")
		private Boolean everyone;

		@JsonProperty("openNote")
		private Boolean openNote;
	}
}
