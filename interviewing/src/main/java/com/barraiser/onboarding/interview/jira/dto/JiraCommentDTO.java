/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.jira.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JiraCommentDTO {
	private String id;
	private Object body;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSX")
	@JsonProperty("created")
	private OffsetDateTime created;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSX")
	@JsonProperty("updated")
	private OffsetDateTime updated;

	private Boolean jsdPublic;

	private Person author;

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder(toBuilder = true)
	public static class JiraCommentBodyV3 {
		private String type;
		private List<JiraCommentContentV3> content;

		@Getter
		@AllArgsConstructor
		@NoArgsConstructor
		@Builder(toBuilder = true)
		public static class JiraCommentContentV3 {
			private String type;
			private String text;
			private List<JiraCommentContentV3> content;
			private Object attrs;
			private List<JiraCommentContentV3> marks;
		}
	}

}
