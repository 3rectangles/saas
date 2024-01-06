/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.match_interviewers;

import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.InterviewStatus;
import com.barraiser.onboarding.interview.InterViewRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class FeedbackTimeBasedFilteringProcessor implements MatchInterviewersProcessor {
	private final InterViewRepository interViewRepository;

	@Override
	public void process(final MatchInterviewersData data) {

		this.filterThroughPendingFeedbackStatus(data);
		data.setInterviewersId(
				data.getInterviewers().stream().map(InterviewerData::getId).collect(Collectors.toList()));
	}

	private void filterThroughPendingFeedbackStatus(final MatchInterviewersData data) {

		final List<InterviewDAO> pendingFeedbackInterviews = this.interViewRepository.findAllByStatusAndInterviewerIdIn(
				InterviewStatus.PENDING_FEEDBACK_SUBMISSION.getValue(), data.getInterviewers().stream()
						.map(InterviewerData::getId).collect(Collectors.toList()));
		for (InterviewDAO pendingFeedbackInterview : pendingFeedbackInterviews) {
			if (pendingFeedbackInterview.getEndDate() != null || pendingFeedbackInterview.getActualEndDate() != null) {
				final Long interviewEndDateEpoch = pendingFeedbackInterview.getActualEndDate() != null
						? pendingFeedbackInterview.getActualEndDate()
						: pendingFeedbackInterview.getEndDate();
				if (((System.currentTimeMillis() / 1000 - interviewEndDateEpoch) / (60 * 60)) > 72) {
					final Optional<InterviewerData> interviewer = data.getInterviewers().stream()
							.filter(x -> x.getId().equals(pendingFeedbackInterview.getInterviewerId())).findFirst();
					interviewer.ifPresent(data.getInterviewers()::remove);
				}
			}
		}
	}
}
