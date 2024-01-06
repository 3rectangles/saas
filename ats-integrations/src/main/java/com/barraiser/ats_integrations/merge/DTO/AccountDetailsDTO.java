/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.merge.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AccountDetailsDTO {
	@JsonProperty("id")
	private String id;

	@JsonProperty("integration")
	private String integration;

	@JsonProperty("integration_slug")
	private String integrationSlug;

	@JsonProperty("category")
	private String category;

	@JsonProperty("end_user_origin_id")
	private String endUserOriginId;

	@JsonProperty("end_user_organization_name")
	private String endUserOrganizationName;

	@JsonProperty("end_user_email_address")
	private String endUserEmailAddress;

	@JsonProperty("status")
	private String status;

	@JsonProperty("webhook_listener_url")
	private String webhookListenerUrl;
}
