/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.match_interviewers;

import com.barraiser.onboarding.dal.ExpertDAO;
import com.barraiser.onboarding.dal.ExpertRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class GetDuplicateExpertsProcessor implements MatchInterviewersProcessor {
	private final ExpertRepository expertRepository;
	private final ObjectMapper objectMapper;

	@Override
	public void process(final MatchInterviewersData data) {
		data.setDuplicateExperts(this.getDuplicateExpertsForActualExperts(data.getInterviewers()));
	}

	private List<InterviewerData> getDuplicateExpertsForActualExperts(final List<InterviewerData> actualInterviewers) {
		final List<ExpertDAO> expertDAOs = this.expertRepository.findAllByDuplicatedFromIn(actualInterviewers.stream()
				.map(InterviewerData::getId).collect(Collectors.toList()));
		return expertDAOs.stream().map(x -> this.objectMapper.convertValue(x, InterviewerData.class))
				.collect(Collectors.toList());
	}
}
