/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.ats_integrations.greenhouse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GreenhouseSendTestRequestBody {
	@JsonProperty("partner_test_id")
	private String partnerTestId;

	@JsonProperty("url")
	private String url;

	private Candidate candidate;

	@Builder(toBuilder = true)
	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	public static class Candidate {
		@JsonProperty("first_name")
		private String firstName;

		@JsonProperty("last_name")
		private String lastName;

		@JsonProperty("greenhouse_profile_url")
		private String greenhouseProfileUrl;

		@JsonProperty("phone_number")
		private String phoneNumber;

		@JsonProperty("resume_url")
		private String resumeUrl;

		@JsonProperty("email")
		private String email;
	}

}
