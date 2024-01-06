/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.types;

import com.barraiser.common.graphql.input.SkillInput;
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
public class HighlightInput {
	@JsonProperty("start_time")
	private Integer startTime;
	@JsonProperty("end_time")
	private Integer endTime;
	@JsonProperty("question")
	private String question;
	@JsonProperty("answer")
	private String answer;
	@JsonProperty("title")
	private String description;
	@JsonProperty("sections")
	private List<SkillInput> skills;
	@JsonProperty("question_speaker_name")
	private String questionSpeakerName;
	@JsonProperty("answer_speaker_name")
	private String answerSpeakerName;
}
