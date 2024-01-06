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
public class ATSJobRoleMappingsDTO {

	private List<ATSJobRoleMappingsDTO.JobRoleMapping> jobRoleMappings;

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder(toBuilder = true)
	public static class JobRoleMapping {

		private String partnerId;

		private String atsProvider;

		private String atsJobRoleId;

		private String brJobRoleId;
	}

}
