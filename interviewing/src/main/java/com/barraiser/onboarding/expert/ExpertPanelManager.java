/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.expert;

import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.internalInterviews.dal.InterviewStructureToExpertsDAO;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.internalInterviews.repository.InterviewStructureToExpertsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Component
public class ExpertPanelManager {

	private final InterviewStructureToExpertsRepository interviewStructureToExpertsRepository;

	public List<String> getInterviewPanel(final InterviewDAO interviewDAO) {
		final Optional<InterviewStructureToExpertsDAO> interviewStructureToExpertsDAOOptional = this.interviewStructureToExpertsRepository
				.findByInterviewStructureId(interviewDAO.getInterviewStructureId());

		return interviewStructureToExpertsDAOOptional.isPresent()
				? interviewStructureToExpertsDAOOptional.get().getEligibleExperts()
				: List.of();
	}
}
