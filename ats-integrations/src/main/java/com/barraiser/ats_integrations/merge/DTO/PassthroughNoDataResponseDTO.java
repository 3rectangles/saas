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
public class PassthroughNoDataResponseDTO {
	// There are two kinds of passthrough responses
	// One returns Response Object with a data parameter
	// One returns Response Object without a data parameter
	// This object is for without data parameter

	@JsonProperty("method")
	private String method;

	@JsonProperty("path")
	private String path;

	@JsonProperty("status")
	private int status;

	@JsonProperty("response_type")
	private String responseType;

	@JsonProperty("response")
	private Object response;

	@JsonProperty("headers")
	private Headers headers;

	@JsonProperty("response_headers")
	private ResponseHeaders responseHeaders;

	@SuperBuilder(toBuilder = true)
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Response {
		@JsonProperty("data")
		private Object data;
	}

	@SuperBuilder(toBuilder = true)
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Headers {
		@JsonProperty("EXTRA-HEADER")
		private String extraHeader;

		@JsonProperty("Authorization")
		private String authorization;
	}

	@SuperBuilder(toBuilder = true)
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ResponseHeaders {
		@JsonProperty("X-Page-Token")
		private String xPageToken;
	}
}
