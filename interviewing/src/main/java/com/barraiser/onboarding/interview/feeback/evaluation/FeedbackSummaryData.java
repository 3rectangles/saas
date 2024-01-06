/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.feeback.evaluation;

import com.barraiser.onboarding.dal.SkillDAO;
import com.barraiser.onboarding.dal.SkillWeightageDAO;
import com.barraiser.common.graphql.types.OverallFeedback;
import com.barraiser.common.graphql.types.Question;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class FeedbackSummaryData {
	private Integer bgsScore;
	private List<Question> parentQuestions;
	private List<Question> followUpQuestions;
	private List<Question> allQuestions;
	private OverallFeedback overallFeedback;
	private List<SkillDAO> skillDAOs;
	private List<SkillWeightageDAO> skillWeightageDAOs;
	private String interviewFlowVersion;
}
