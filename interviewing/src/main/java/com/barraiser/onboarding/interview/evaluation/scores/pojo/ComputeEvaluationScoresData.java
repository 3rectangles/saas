/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.scores.pojo;

import com.barraiser.onboarding.interview.pojo.FeedbackData;
import com.barraiser.onboarding.interview.pojo.InterviewData;
import com.barraiser.onboarding.interview.pojo.QuestionData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ComputeEvaluationScoresData {
	private String editId;
	private String evaluationId;
	private Map<String, Double> skillWeightageMap;
	private List<QuestionData> questions;
	private List<FeedbackData> softSkillFeedbackList;
	private List<InterviewData> interviews;
}
