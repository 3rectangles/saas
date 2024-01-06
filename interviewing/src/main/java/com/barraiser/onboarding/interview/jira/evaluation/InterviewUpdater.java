/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.jira.evaluation;

import com.barraiser.onboarding.dal.EvaluationDAO;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.InterviewStatus;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.InterviewService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class InterviewUpdater {
	private final InterviewService interviewService;

	public void updatePendingScheduling(final EvaluationDAO evaluationDAO) {
		List<InterviewDAO> interviewDAOList = this.interviewService.findAllByEvaluationId(evaluationDAO.getId());
		this.interviewService.saveAll(
				interviewDAOList.stream()
						.map(interviewDAO -> {
							if (InterviewStatus.PENDING_SCHEDULING.getValue()
									.equalsIgnoreCase(interviewDAO.getStatus())) {
								return interviewDAO.toBuilder().isPendingScheduling(true).build();
							}
							return interviewDAO;
						})
						.collect(Collectors.toList()));
	}
}
