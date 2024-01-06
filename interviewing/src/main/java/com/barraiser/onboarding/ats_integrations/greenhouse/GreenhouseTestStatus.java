/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.ats_integrations.greenhouse;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GreenhouseTestStatus {
	@JsonProperty("partner_status")
	private String partnerStatus;

	@JsonProperty("partner_profile_url")
	private String partnerProfileUrl;

	@JsonProperty("partner_score")
	private Double partnerScore;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonProperty("metadata")
	private Metadata metadata;

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder(toBuilder = true)
	public static class Metadata {
		@JsonProperty("Started At")
		private String startedAt;

		@JsonProperty("Completed At")
		private String completedAt;

		@JsonProperty("Notes")
		private String notes;
	}
}
