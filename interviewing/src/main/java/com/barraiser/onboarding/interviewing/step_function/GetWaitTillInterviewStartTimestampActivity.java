/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interviewing.step_function;

import com.barraiser.common.utilities.DateUtils;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interviewing.step_function.dto.InterviewingLifecycleDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Log4j2
public class GetWaitTillInterviewStartTimestampActivity implements InterviewingLifecycleSfnActivity {
	private final static Long INTERVIEW_START_BUFFER_IN_SECS = 10 * 60L;

	private final ObjectMapper objectMapper;
	private final InterViewRepository interViewRepository;

	@Override
	public String name() {
		return "interviewing-get-wait-time";
	}

	@Override
	public InterviewingLifecycleDTO process(String input) throws Exception {
		final InterviewingLifecycleDTO data = this.objectMapper.readValue(input, InterviewingLifecycleDTO.class);
		final InterviewDAO interviewDAO = this.interViewRepository.findById(data.getInterviewId()).get();
		final String timestamp = DateUtils
				.getISO8601DateStringWithOffset(interviewDAO.getStartDate() - INTERVIEW_START_BUFFER_IN_SECS, null);
		data.setWaitTillInterviewStartTimestamp(timestamp);
		return data;
	}
}
