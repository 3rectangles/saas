/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.dto;

import com.barraiser.commons.dto.ats.enums.ATSProvider;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ATSUserRoleMappingsDTO {

	private List<ATSUserRoleMappingsDTO.UserRoleMapping> userRoleMappings;

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder(toBuilder = true)
	public static class UserRoleMapping {

		private String partnerId;

		private String atsProvider;

		private String atsRoleId;

		private String atsRoleName;

		private String brRoleId;

	}

}
