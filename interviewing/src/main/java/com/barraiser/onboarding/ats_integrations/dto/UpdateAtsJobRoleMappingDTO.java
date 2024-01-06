/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.ats_integrations.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class UpdateAtsJobRoleMappingDTO {
	private List<JobRoleMapping> jobRoleMappings;

	private String partnerId;

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder(toBuilder = true)
	public static class JobRoleMapping {
		private String jobRoleId;

		private String atsJobPostingId;

		private List<InterviewStructureMapping> interviewStructureMappings;

	}

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder(toBuilder = true)
	public static class InterviewStructureMapping {
		private String interviewStructureId;

		private String atsInterviewStructureId;
	}
}
