/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ATSInterviewStructureMappingsDTO {

	private List<ATSInterviewStructureMappingsDTO.InterviewStructureMapping> interviewStructureMappings;

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder(toBuilder = true)
	public static class InterviewStructureMapping {

		private String partnerId;

		private String atsProvider;

		private String atsInterviewStructureId;

		private String brInterviewStructureId;
	}

}
