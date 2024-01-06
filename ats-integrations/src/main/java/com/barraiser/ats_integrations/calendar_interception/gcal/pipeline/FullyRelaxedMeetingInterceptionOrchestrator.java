/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.calendar_interception.gcal.pipeline;

import com.barraiser.ats_integrations.calendar_interception.ProcessedEventManagementHelper;
import com.barraiser.ats_integrations.calendar_interception.SchedulingProcessing;
import com.barraiser.ats_integrations.calendar_interception.dto.SchedulingData;
import com.barraiser.ats_integrations.errorhandling.ATSAnomalyException;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;

/*
    Calendar events which contain the keywords added by
    self serve partners are handled in this class
    to add new interviews and evaluations
 */

@AllArgsConstructor
@Component
@Log4j2
public class FullyRelaxedMeetingInterceptionOrchestrator implements SchedulingProcessing {
	private final FullyRelaxedMeetingSchedulingSetupDataAccumulator fullyRelaxedMeetingSchedulingSetupDataAccumulator;
	private final FullyRelaxedMeetingEvaluationCreationProcessor fullyRelaxedMeetingEvaluationCreationProcessor;
	private final FullyRelaxedMeetingInterviewCreationProcessor fullyRelaxedMeetingInterviewCreationProcessor;
	private final CalendarInviteUpdator calendarInviteUpdator;

	private final FullyRelaxedMeetingInterviewerCreationProcessor fullyRelaxedMeetingInterviewerCreationProcessor;
	private final ProcessedEventManagementHelper processedEventManagementHelper;

	private final DeallocateInterviewerProcessor deallocateInterviewerProcessor;
	private final BRInterviewAppAdditionProcessor brInterviewAppAdditionProcessor;

	@Override
	public void process(SchedulingData data) throws IOException, ATSAnomalyException {
		// todo: Remove log.info

		// Cancel Pre Existing Interview for event id with different duration ->
		// Deallocate Expert
		this.deallocateInterviewerProcessor.process(data);

		this.processedEventManagementHelper.addATSProcessedEvent(data.getBrCalendarEvent());
		log.info(String.format("ATS Processed event added to DB with id %s, startTime %s, endTime %s",
				data.getBrCalendarEvent().getProviderEventId(), data.getBrCalendarEvent().getStart(),
				data.getBrCalendarEvent().getEnd()));

		this.fullyRelaxedMeetingSchedulingSetupDataAccumulator.process(data);
		log.info(String.format("Data Accumulation Done For Partner %s, for event subject %s",
				data.getPartnerId(), data.getBrCalendarEvent().getSummary()));

		this.fullyRelaxedMeetingInterviewerCreationProcessor.process(data);
		log.info(String.format("Interviewer Added For Partner %s, with Email id %s",
				data.getPartnerId(), data.getInterviewerId()));

		this.fullyRelaxedMeetingEvaluationCreationProcessor.process(data);
		log.info(String.format("Evaluation Creation Done For Partner %s, with Evaluation id %s",
				data.getPartnerId(), data.getBrEvaluationId()));

		this.fullyRelaxedMeetingInterviewCreationProcessor.process(data);
		log.info(String.format("Interview Creation Done For Partner %s, with Interview id %s",
				data.getPartnerId(), data.getBrInterviewId()));

		this.calendarInviteUpdator.process(data);
		log.info(String.format("Calendar Invite Successfully Parsed for partner %s, of email id%s",
				data.getPartnerId(), data.getBrCalendarEvent().getOrganizer().getEmailId()));

		// Step is currently required only for Microsoft events
		this.brInterviewAppAdditionProcessor.process(data);

	}
}
