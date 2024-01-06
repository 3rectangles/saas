/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.availability.DTO;

import com.barraiser.commons.enums.OAuthProvider;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AddCalendarDTO {
	private String code;
	private String redirectUri;

	@JsonProperty("oAuthProvider")
	private OAuthProvider oAuthProvider;

	private Integer noOfDaysForEvents;

	private String context;
}
