/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.feeback.evaluation;

import com.barraiser.common.graphql.types.Feedback;
import com.barraiser.common.graphql.types.Question;
import com.barraiser.onboarding.common.Constants;
import com.barraiser.onboarding.dal.SkillDAO;
import com.barraiser.onboarding.interview.evaluation.scores.EvaluationStrategy_V5Constants;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class MissedCategoryCriteria implements FeedbackEvaluator {
	@Override
	public List<String> getImprovement(final FeedbackSummaryData data) {
		return Arrays.asList(this.checkIfAnySkillIsMissing(data));
	}

	@Override
	public int order() {
		return 0;
	}

	private String checkIfAnySkillIsMissing(final FeedbackSummaryData data) {

		String missedCategories = "";
		List<String> categoriesAskedInInterview = data.getAllQuestions().stream()
				.filter(
						x -> !EvaluationStrategy_V5Constants.QuestionType.DELETED
								.getValue()
								.equals(x.getType())
								&& !EvaluationStrategy_V5Constants.QuestionType.NON_EVALUATIVE
										.getValue()
										.equals(x.getType()))
				.map(Question::getFeedbacks)
				.flatMap(Collection::stream)
				.map(Feedback::getCategoryId)
				.distinct()
				.collect(Collectors.toList());
		// We do not want to show others in missed category
		final List<SkillDAO> categoriesInInterviewStructure = data.getSkillDAOs().stream()
				.filter(x -> !Constants.OTHERS_SKILL_ID.equals(x.getId())).collect(Collectors.toList());
		for (final SkillDAO skill : categoriesInInterviewStructure) {
			if (!categoriesAskedInInterview.contains(skill.getId())) {
				missedCategories += "<b>" + skill.getName() + "</b>, ";
			}
		}
		if (missedCategories.equals("")) {
			return null;
		}
		return String.format(
				FeedbackSummaryConstants.MISSED_CATEGORY_CRITERIA_IMPROVEMENT_MESSAGE,
				missedCategories.substring(0, missedCategories.length() - 2).toLowerCase());
	}
}
