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
public class EmailAddressDTO {
	@JsonProperty("value")
	private String value;

	@JsonProperty("email_address_type")
	private String emailAddressType;

	@JsonProperty("modified_at")
	private String modifiedAt;
}
