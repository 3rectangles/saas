/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class InterviewData {
	private String id;
	private Long lastQuestionEnd;
	private List<QuestionData> questions;
	private Integer rescheduleCount;
	private String interviewerId;
	private String interviewRound;
	private String evaluationId;
	private Boolean isSaasInterview;
}
