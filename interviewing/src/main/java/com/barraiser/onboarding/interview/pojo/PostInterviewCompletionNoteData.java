/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.pojo;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
public class PostInterviewCompletionNoteData {
	private String interviewId;
	private String partnerId;
	private String evaluationId;
	private String interviewerId;
	private String interviewRound;
}
