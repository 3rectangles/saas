/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.calendar_interception.gcal.pipeline;

import com.barraiser.ats_integrations.calendar_interception.SchedulingProcessing;
import com.barraiser.ats_integrations.calendar_interception.dto.SchedulingData;
import com.barraiser.ats_integrations.common.client.InterviewManagementFeignClient;
import com.barraiser.ats_integrations.dal.ATSProcessedEventsDAO;
import com.barraiser.ats_integrations.dal.ATSProcessedEventsRepository;
import com.barraiser.ats_integrations.errorhandling.ATSAnomalyException;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Log4j2
@AllArgsConstructor
@Component
public class DeallocateInterviewerProcessor implements SchedulingProcessing {

	private ATSProcessedEventsRepository atsProcessedEventsRepository;
	private InterviewManagementFeignClient interviewManagementFeignClient;

	@Override
	public void process(SchedulingData data) throws IOException, ATSAnomalyException {

		List<ATSProcessedEventsDAO> atsProcessedEventsDAOs = this.atsProcessedEventsRepository
				.findAllByCalendarEntityIdAndCalendarEventStartTimeIsNot(
						data.getBrCalendarEvent().getProviderEventId(),
						data.getBrCalendarEvent().getStart().toEpochSecond());

		atsProcessedEventsDAOs.stream().forEach(
				atsProcessedEventsDAO -> {
					if (atsProcessedEventsDAO.getInterviewId() != null) {
						this.interviewManagementFeignClient
								.deallocateInterviewer(atsProcessedEventsDAO.getInterviewId());

						log.info("Successfully removed Interviewer from interviewId: "
								+ atsProcessedEventsDAO.getInterviewId());

					}
				});
	}

}
