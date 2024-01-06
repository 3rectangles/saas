/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.user.expert;

import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.jira.dto.IdValueField;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class ExpertUtil {
	private final UserDetailsRepository userDetailsRepository;
	private final ExpertRepository expertRepository;

	private static final Long DEFAULT_GAP_BETWEEN_INTERVIEWS = 1800L;

	public List<UserDetailsDAO> getActiveExperts() {
		return this.userDetailsRepository.findAllByIsActiveAndIsExpertPartner(true, true);
	}

	public Long getTimeGapBetweenInterviewForExpert(final String userId) {
		final Long gapBetweenInterviews = this.expertRepository.findById(userId).get().getGapBetweenInterviews();
		return gapBetweenInterviews == null ? DEFAULT_GAP_BETWEEN_INTERVIEWS : gapBetweenInterviews;
	}

	public Boolean isExpertUnderTraining(final IdValueField isUnderTrainingField) {
		return isUnderTrainingField != null && "true".equals(isUnderTrainingField.getValue());
	}

	public List<ExpertDAO> getDuplicateExpertsForGivenExpert(final String expertId) {
		return this.expertRepository.findAllByDuplicatedFrom(expertId);
	}

	public Boolean isExpertDuplicate(final String expertId) {
		final ExpertDAO expertDAO = this.expertRepository.findById(expertId).get();
		return expertDAO.getDuplicatedFrom() != null;
	}

	public Map<String, List<InterviewDAO>> splitInterviewsByExpert(final List<InterviewDAO> interviews) {
		final Map<String, List<InterviewDAO>> interviewsPerExpert = new HashMap<>();
		for (final InterviewDAO interviewDAO : interviews) {
			final List<InterviewDAO> interviewsOfAnExpert = interviewsPerExpert.getOrDefault(interviewDAO
					.getInterviewerId(),
					new ArrayList<>());
			interviewsOfAnExpert.add(interviewDAO);
			interviewsPerExpert.put(interviewDAO.getInterviewerId(), interviewsOfAnExpert);
		}
		return interviewsPerExpert;
	}
}
