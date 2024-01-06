/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.sfn;

import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.scheduling.scheduling.SchedulingProcessingData;
import com.barraiser.onboarding.user.expert.ExpertUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class GetInterviewerDetailsActivity implements InterviewSchedulingActivity {
	public static final String GET_INTERVIEWER_DETAILS = "get-interviewer-details";
	private final ExpertUtil expertUtil;
	private final InterViewRepository interViewRepository;
	private final ObjectMapper objectMapper;

	@Override
	public String name() {
		return GET_INTERVIEWER_DETAILS;
	}

	@Override
	public SchedulingProcessingData process(String input) throws Exception {
		final SchedulingProcessingData data = this.objectMapper.readValue(input, SchedulingProcessingData.class);
		final InterviewDAO currentInterview = this.interViewRepository.findById(data.getInput().getInterviewId()).get();
		data.setIsExpertDuplicate(this.expertUtil.isExpertDuplicate(currentInterview.getInterviewerId()));
		return data;
	}
}
