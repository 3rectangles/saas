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
public class QuestionData {

	private String id;

	private String type;

	private List<FeedbackData> feedbacks;

	private String interviewerId;

	private String difficulty;

	private Boolean irrelevant;

	private Integer serialNumber;

	private String question;

	private Long startTimeEpoch;

	private List<QuestionData> followUpQuestions;

	private Boolean isDefault;

	private Long startTime;

	private Integer rescheduleCount;

	private String timeSpentFlag;

	private String interviewId;

	private Boolean isSaasQuestion;
}
