/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dyte.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class DyteAddParticipantResponseDTO {

	private Boolean success;

	private String message;

	private Data data;

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder(toBuilder = true)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class Data {

		private AuthResponse authResponse;

		@Getter
		@AllArgsConstructor
		@NoArgsConstructor
		@Builder(toBuilder = true)
		@JsonInclude(JsonInclude.Include.NON_NULL)
		public static class AuthResponse {

			private Boolean userAdded;

			private String id;

			private String authToken;
		}
	}
}