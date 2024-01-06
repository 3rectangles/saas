/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.merge.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MergeLinkTokenRequestDTO {
	@JsonProperty("end_user_origin_id")
	private String endUserOriginId;

	@JsonProperty("end_user_organization_name")
	private String endUserOrganizationName;

	@JsonProperty("end_user_email_address")
	private String endUserEmailAddress;

	@JsonProperty("categories")
	private List<String> categories;
}
