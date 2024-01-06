/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.calendar_interception.gcal.pipeline;

import com.barraiser.ats_integrations.calendar_interception.ProcessedEventManagementHelper;
import com.barraiser.ats_integrations.calendar_interception.SchedulingProcessing;
import com.barraiser.ats_integrations.calendar_interception.dto.SchedulingData;
import com.barraiser.ats_integrations.common.client.CandidateFeignClient;
import com.barraiser.ats_integrations.common.client.InterviewManagementFeignClient;
import com.barraiser.common.graphql.input.ScheduleInterviewInput;
import com.barraiser.common.graphql.input.UpdateCandidateInput;
import com.barraiser.common.graphql.types.Interview;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.barraiser.common.utilities.DateUtils.TIMEZONE_ASIA_KOLKATA;

@Log4j2
@AllArgsConstructor
@Component
public class BRInterviewLifecycleManagementProcessor implements SchedulingProcessing {

	private final InterviewManagementFeignClient interviewManagementFeignClient;
	private final CandidateFeignClient candidateFeignClient;
	private final ProcessedEventManagementHelper processedEventManagementHelper;

	private static final String SOURCE = "%s_CAL_INTERCEPTION";

	@Override
	public void process(SchedulingData data) throws IOException {
		final String scheduledInterviewId = this.scheduleInterviewInBRSystem(data);

		// Updating Calendar Event record with scheduled interviewId
		this.processedEventManagementHelper.updateATSProcessedEvent(data.getBrCalendarEvent(), scheduledInterviewId);

		data.setBrInterviewId(scheduledInterviewId);
	}

	private String scheduleInterviewInBRSystem(final SchedulingData data) {

		data.setBrCandidateId(this.candidateFeignClient.getCandidateId(data.getBrEvaluationId()).getBody());
		try {
			this.candidateFeignClient.updateCandidate(UpdateCandidateInput.builder()
					.candidateId(data.getBrCandidateId())
					.firstName(data.getCandidateDetails().getFirstName())
					.lastName(data.getCandidateDetails().getLastName())
					.email(data.getCandidateDetails().getEmailId())
					.phoneNumber(data.getCandidateDetails().getMobileNumber())
					.atsSource(data.getAtsProvider().getValue())
					.resumeLink(data.getCandidateDetails().getResumeLink())
					.build());
		} catch (Exception e) {
			log.warn("Candidate Update failed for evaluationId: " + data.getBrEvaluationId(), e, e);
		}

		// TODO: Pass interview structure id appropriately. Handle null by converting
		// entire input to request body or by making param non required.
		final Interview schedulableInterview = this.interviewManagementFeignClient
				.createInterview(data.getBrEvaluationId(),
						data.getBrInterviewStructureId() != null ? data.getBrInterviewStructureId() : "NULL")
				.getBody();

		this.interviewManagementFeignClient.scheduleSaasInterview(ScheduleInterviewInput.builder()
				.interviewId(schedulableInterview.getId())
				.atsInterviewFeedbackLink(data.getAtsInterviewFeedbackLink())
				.startDate(data.getInterviewStart())
				.endDate(data.getInterviewEnd())
				.interviewerId(data.getInterviewerId())
				.schedulingPlatform(String.format(SOURCE, data.getAtsProvider().getValue()))
				.timezone(TIMEZONE_ASIA_KOLKATA)
				.meetingLink(data.getAtsMeetingLink())
				.build());

		return schedulableInterview.getId();
	}

}
