/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation.expertReassignment.sfn_activities;

import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.DTO.ExpertAllocatorData;
import com.barraiser.onboarding.scheduling.scheduling.sfn.LastMinuteDuplicateExpertHandler;
import com.barraiser.onboarding.user.expert.ExpertUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Log4j2
@Component
@AllArgsConstructor
public class LastMinuteDuplicateExpertHandlerActivity implements ExpertAllocatorSfnActivity {
	public static final String HANDLE_DUPLICATE_EXPERT_ACTIVITY_NAME = "handle-last-minute-duplicate-expert-in-interview";

	private final InterViewRepository interViewRepository;
	private final LastMinuteDuplicateExpertHandler lastMinuteDuplicateExpertHandler;
	private final ExpertUtil expertUtil;
	private final ObjectMapper objectMapper;

	@Override
	public String name() {
		return HANDLE_DUPLICATE_EXPERT_ACTIVITY_NAME;
	}

	@Override
	public ExpertAllocatorData process(final String input) throws IOException {
		final ExpertAllocatorData data = objectMapper.readValue(input, ExpertAllocatorData.class);
		final InterviewDAO interviewDAO = this.interViewRepository.findById(data.getInterviewId()).get();
		if (this.expertUtil.isExpertDuplicate(interviewDAO.getInterviewerId())) {
			this.lastMinuteDuplicateExpertHandler.handleLastMinuteDuplicateExpert(interviewDAO);
		}
		return data;
	}
}
