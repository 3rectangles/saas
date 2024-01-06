/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.types;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateHighlightsInput {
	@JsonProperty("interview_id")
	private String interviewId;

	@JsonProperty("highlights")
	private List<HighlightInput> highlights;

	/**
	 * This is a flag which is used to indicate wether the highlights are complete.
	 * This is introduced since create highlights will be called multiple times to
	 * save
	 * the highlights of the interview (as we will incrementally build highlights
	 * based on transcript
	 * chunks in an effort to go NRT).
	 */
	@JsonProperty("is_completed")
	private Boolean areHighlightsComplete;
}
