/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.match_interviewers.internalInterviews;

import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.InterviewerData;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.InterviewersPerDayData;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.MatchInterviewersData;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.MatchInterviewersProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Log4j2
@Component
@AllArgsConstructor
public class InterviewersSortingForInternalInterviewsProcessor implements MatchInterviewersProcessor {
	private final ObjectMapper objectMapper;

	@Override
	public void process(final MatchInterviewersData data) {
		log.info("start sorting for interview_id : {}", data.getInterviewId());
		this.sorting(data);
	}

	/**
	 * RANDOMIZING FOR NOW .
	 *
	 * @param data
	 */
	private void sorting(final MatchInterviewersData data) {

		for (final InterviewersPerDayData interviewersPerDayData : data.getInterviewersPerDayDataList()) {
			List<InterviewerData> interviewers = interviewersPerDayData.getInterviewers();

			// RANDOMIZING HERE
			Collections.shuffle(interviewers);

			try {
				log.info(
						"sorted interviewers for date : {} , interviewers : {} ",
						interviewersPerDayData.getDate(),
						this.objectMapper.writeValueAsString(interviewers));
			} catch (final Exception ignored) {
			}

			interviewersPerDayData.setInterviewers(interviewers);
		}
	}

}
