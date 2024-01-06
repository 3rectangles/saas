/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ATSProcessedEventsRepository
		extends JpaRepository<ATSProcessedEventsDAO, String> {

	Optional<ATSProcessedEventsDAO> findByCalendarEntityIdAndCalendarEventStartTimeAndCalendarEventEndTime(
			String calendarEntityId, Long calendarEventStartTime, Long calendarEventEndTime);

	List<ATSProcessedEventsDAO> findAllByCalendarEntityIdAndCalendarEventStartTimeIsNot(
			String calendarEntityId, Long calendarEventStartTime);

}
