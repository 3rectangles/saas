/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.ats_integrations.greenhouse;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GreenhouseTest {
	@JsonProperty("partner_test_id")
	private String partnerTestId;

	@JsonProperty("partner_test_name")
	String partnerTestName;
}
