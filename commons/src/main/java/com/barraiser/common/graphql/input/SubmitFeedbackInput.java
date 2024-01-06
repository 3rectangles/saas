/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.input;

import com.barraiser.common.graphql.types.OverallFeedback;
import com.barraiser.common.graphql.types.Question;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SubmitFeedbackInput {
	@NotNull(message = "Interview Start Time is not Present")
	private Long interviewStart;

	private Long interviewEnd;

	private Long lastQuestionEnd;

	private Boolean finalSubmission;

	@NotNull(message = "Invalid interview")
	private String interviewId;

	@Valid
	@Size(min = 1, message = "No Questions Present") // TODO: Fixme!! Not Working
	private List<Question> questions;

	@Valid
	@NotNull(message = "Overall feedback is missing")
	private OverallFeedback overallFeedback;

	private Boolean looksGood;

	private Boolean isFeedbackInconsistent;

	private Boolean includeFeedbackImprovements;

	private Boolean wasInterviewerVideoOn;
}
