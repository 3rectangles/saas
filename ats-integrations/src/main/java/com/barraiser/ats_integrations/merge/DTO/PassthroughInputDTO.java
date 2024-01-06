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
public class PassthroughInputDTO {

	@JsonProperty("method")
	private String method;

	@JsonProperty("path")
	private String path;

	@JsonProperty("data")
	private String data;

	@JsonProperty("headers")
	private Headers headers;

	@SuperBuilder(toBuilder = true)
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Headers {
		@JsonProperty("Content-Type")
		private String contentType;
	}
}
