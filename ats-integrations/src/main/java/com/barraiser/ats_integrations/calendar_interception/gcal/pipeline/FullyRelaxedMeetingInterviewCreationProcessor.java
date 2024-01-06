/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.calendar_interception.gcal.pipeline;

import com.barraiser.ats_integrations.calendar_interception.ProcessedEventManagementHelper;
import com.barraiser.ats_integrations.calendar_interception.SchedulingProcessing;
import com.barraiser.ats_integrations.calendar_interception.dto.SchedulingData;
import com.barraiser.ats_integrations.common.client.InterviewManagementFeignClient;
import com.barraiser.ats_integrations.errorhandling.ATSAnomalyException;
import com.barraiser.common.graphql.input.ScheduleInterviewInput;
import com.barraiser.common.graphql.types.Interview;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.barraiser.common.utilities.DateUtils.TIMEZONE_ASIA_KOLKATA;

@AllArgsConstructor
@Component
@Log4j2
public class FullyRelaxedMeetingInterviewCreationProcessor implements SchedulingProcessing {
	final InterviewManagementFeignClient interviewManagementFeignClient;

	final ProcessedEventManagementHelper processedEventManagementHelper;
	private static final String SOURCE = "%s_CAL_INTERCEPTION";

	@Override
	public void process(SchedulingData data) throws IOException, ATSAnomalyException {
		final String scheduledInterviewId = this.scheduleInterviewInBRSystem(data);

		// Updating Calendar Event record with scheduled interviewId
		this.processedEventManagementHelper.updateATSProcessedEvent(data.getBrCalendarEvent(), scheduledInterviewId);

		data.setBrInterviewId(scheduledInterviewId);
	}

	private String scheduleInterviewInBRSystem(final SchedulingData data) {

		// TODO: Sending "NULL" when interview structure is null
		final Interview schedulableInterview = this.interviewManagementFeignClient
				.createInterview(data.getBrEvaluationId(),
						data.getBrInterviewStructureId() == null ? "NULL" : data.getBrInterviewStructureId())
				.getBody();

		this.interviewManagementFeignClient.scheduleSaasInterview(ScheduleInterviewInput.builder()
				.interviewId(schedulableInterview.getId())
				.startDate(data.getInterviewStart())
				.endDate(data.getInterviewEnd())
				.interviewerId(data.getInterviewerId())
				.schedulingPlatform(String.format(SOURCE, data.getPartnerId()))
				.timezone(TIMEZONE_ASIA_KOLKATA)
				.meetingLink(data.getAtsMeetingLink())
				.build());

		return schedulableInterview.getId();
	}
}
