/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.calendar_interception;

import com.barraiser.ats_integrations.dal.ATSProcessedEventsDAO;
import com.barraiser.ats_integrations.dal.ATSProcessedEventsRepository;
import com.barraiser.commons.eventing.schema.commons.calendar.BRCalendarEvent;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Component
public class ProcessedEventManagementHelper {

	ATSProcessedEventsRepository atsProcessedEventsRepository;

	public void addATSProcessedEvent(BRCalendarEvent event) {
		Optional<ATSProcessedEventsDAO> atsProcessedEventsDAO = this.atsProcessedEventsRepository
				.findByCalendarEntityIdAndCalendarEventStartTimeAndCalendarEventEndTime(
						event.getProviderEventId(),
						event.getStart().toEpochSecond(),
						event.getEnd().toEpochSecond());

		if (atsProcessedEventsDAO.isEmpty()) {
			this.atsProcessedEventsRepository.save(
					ATSProcessedEventsDAO.builder()
							.id(UUID.randomUUID().toString())
							.calendarEntityId(event.getProviderEventId())
							.calendarEventStartTime(event.getStart().toEpochSecond())
							.calendarEventEndTime(event.getEnd().toEpochSecond())
							.build());
		}
	}

	public void updateATSProcessedEvent(BRCalendarEvent event, String brInterviewId) {

		Optional<ATSProcessedEventsDAO> atsProcessedEventsDAO = this.atsProcessedEventsRepository
				.findByCalendarEntityIdAndCalendarEventStartTimeAndCalendarEventEndTime(
						event.getProviderEventId(),
						event.getStart().toEpochSecond(),
						event.getEnd().toEpochSecond());

		this.atsProcessedEventsRepository.save(
				atsProcessedEventsDAO.get().toBuilder()
						.interviewId(brInterviewId)
						.build());
	}

}
