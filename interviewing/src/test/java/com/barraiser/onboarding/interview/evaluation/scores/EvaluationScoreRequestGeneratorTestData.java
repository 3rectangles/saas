/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.scores;

import com.barraiser.onboarding.dal.FeedbackDAO;
import com.barraiser.onboarding.dal.QuestionDAO;
import com.barraiser.onboarding.dal.SkillWeightageDAO;
import com.barraiser.onboarding.interview.evaluation.scores.pojo.ComputeEvaluationScoresData;
import com.barraiser.onboarding.interview.pojo.FeedbackData;
import com.barraiser.onboarding.interview.pojo.InterviewData;
import com.barraiser.onboarding.interview.pojo.QuestionData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class EvaluationScoreRequestGeneratorTestData {
	private String evaluationId;
	private List<InterviewData> interviews;
	private List<QuestionDAO> questions1;
	private List<QuestionDAO> questions2;
	private List<FeedbackDAO> feedbacks;
	private List<FeedbackDAO> softSkillsFeedback;
	private ComputeEvaluationScoresData expectedData;
	private List<SkillWeightageDAO> skillWeights;
}
