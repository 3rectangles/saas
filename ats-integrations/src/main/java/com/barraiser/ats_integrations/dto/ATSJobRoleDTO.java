/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ATSJobRoleDTO {
	private String id;
}
