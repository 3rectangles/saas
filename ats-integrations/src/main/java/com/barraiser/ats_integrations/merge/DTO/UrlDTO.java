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
public class UrlDTO {
	@JsonProperty("value")
	private String value;

	@JsonProperty("url_type")
	private String urlType; // PERSONAL, COMPANY, PORTFOLIO, BLOG, SOCIAL_MEDIA, OTHER, JOB_POSTING, -, or
	// original value

	@JsonProperty("modified_at")
	private String modifiedAt;
}
