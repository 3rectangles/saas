/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.match_interviewers;

import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.InterviewStatus;
import com.barraiser.onboarding.interview.InterViewRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class GetAllUnusedInterviewersProcessor implements MatchInterviewersProcessor {
	private final InterViewRepository interViewRepository;

	@Override
	public void process(MatchInterviewersData data) {
		data.setInterviewers(this.getAllUnusedInterviewers(data.getEvaluationId(), data.getInterviewers()));
	}

	private List<InterviewerData> getAllUnusedInterviewers(String evaluationId, List<InterviewerData> interviewers) {

		List<String> usedInterviewers = this.interViewRepository.findAllByEvaluationId(evaluationId).stream()
				.filter(x -> !x.getStatus().equalsIgnoreCase(InterviewStatus.CANCELLATION_DONE.getValue()))
				.map(InterviewDAO::getInterviewerId).collect(Collectors.toList());

		return interviewers.stream()
				.filter(x -> !usedInterviewers.contains(x.getId()))
				.collect(Collectors.toList());
	}
}
