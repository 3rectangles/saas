/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder(toBuilder = true)
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ATSConfigDTO {

	private CalendarInterception calendarInterception;

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder(toBuilder = true)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class CalendarInterception {

		private Boolean isEnabled;

		private List<String> internalInterviewersEmailDomain;

		private List<String> allowedParticipantEmailDomains;

	}
}
