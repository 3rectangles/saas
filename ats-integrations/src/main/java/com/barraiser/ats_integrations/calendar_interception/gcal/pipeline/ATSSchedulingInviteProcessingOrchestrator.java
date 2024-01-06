/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.calendar_interception.gcal.pipeline;

import com.barraiser.ats_integrations.calendar_interception.CalendarInterceptionHelper;
import com.barraiser.ats_integrations.calendar_interception.ProcessedEventManagementHelper;
import com.barraiser.ats_integrations.calendar_interception.dto.SchedulingData;
import com.barraiser.ats_integrations.errorhandling.ATSAnomalyException;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * This class is used to handle
 * actions at barraiser end , for the case
 * where ATS scheduling is enabled for partner
 * without ATS integration.
 */
@Log4j2
@Component
@AllArgsConstructor
public class ATSSchedulingInviteProcessingOrchestrator {

	@Qualifier("google")
	private final SchedulingSetupDataAccumulator schedulingSetupDataAccumulator;

	private final InterviewerCreationProcessor interviewerCreationProcessor;
	private final BREvaluationCreationProcessor brEvaluationCreationProcessor;
	private final BRInterviewLifecycleManagementProcessor brInterviewLifecycleManagementProcessor;

	private final CalendarInviteParser calendarInviteParser;
	private final CalendarInterceptionHelper calendarInterceptionHelper;
	private final CalendarInviteUpdator calendarInviteUpdator;

	private final ProcessedEventManagementHelper processedEventManagementHelper;

	private final DeallocateInterviewerProcessor deallocateInterviewerProcessor;
	private final BRInterviewAppAdditionProcessor brInterviewAppAdditionProcessor;

	public void process(final SchedulingData data) throws IOException, ATSAnomalyException {

		final Boolean isAtsSchedulingEvent = this.calendarInterceptionHelper
				.isATSSchedulingEvent(data.getBrCalendarEvent().getDescription());

		if (isAtsSchedulingEvent) {
			this.processedEventManagementHelper.addATSProcessedEvent(data.getBrCalendarEvent());

			this.calendarInviteParser.process(data);
			log.info("Parsing done for interception event partnerId: " + data.getPartnerId());

			this.schedulingSetupDataAccumulator.process(data);
			log.info("Data Accumulation done for event ATSEvaluationId: " + data.getAtsEvaluationId());

			if (data.getIsCalendarInterceptionEnabled()) {

				// Cancel Pre Existing Interview for event id with different duration
				// Deallocating Expert
				this.deallocateInterviewerProcessor.process(data);

				this.interviewerCreationProcessor.process(data);
				log.info("Interviewer Creation done for event ATSEvaluationId: " + data.getAtsEvaluationId());
				this.brEvaluationCreationProcessor.process(data);
				log.info("Evaluation Creation done for event BREvaluationId: " + data.getBrEvaluationId());
				this.brInterviewLifecycleManagementProcessor.process(data);
				log.info("Interview Update done for event BRInterviewId: " + data.getBrInterviewId());
				this.calendarInviteUpdator.process(data);
				log.info("Calendar Interception steps done for event: " + data.getBrCalendarEvent().getDescription());

				// Step is currently required only for Microsoft events
				this.brInterviewAppAdditionProcessor.process(data);
			}
		}

	}

}
