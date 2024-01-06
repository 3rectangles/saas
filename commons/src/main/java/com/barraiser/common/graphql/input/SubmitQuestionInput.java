/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.input;

import com.barraiser.common.graphql.types.Question;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SubmitQuestionInput {
	@NotNull(message = "Interview Start Time not present")
	private Long interviewStart;

	@NotNull(message = "Interview End Time not present")
	private Long interviewEnd;

	@NotNull(message = "Last Question End Time not present")
	private Long lastQuestionEnd;

	private Boolean finalSubmission;
	@NotNull(message = "Invalid interview")
	private String interviewId;

	@Size(min = 1)
	private List<Question> questions;

	private Boolean onlyTagTime;

	private Long videoStartTime;
}
