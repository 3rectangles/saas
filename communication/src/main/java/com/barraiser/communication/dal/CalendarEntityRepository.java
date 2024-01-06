/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CalendarEntityRepository extends JpaRepository<CalendarEntityDAO, String> {
	Optional<CalendarEntityDAO> findByEventId(String eventId);

	List<CalendarEntityDAO> findByEntityIdAndEntityRescheduleCount(String entityId, Integer rescheduleCount);

	CalendarEntityDAO findByEntityIdAndEntityRescheduleCountAndRecipientId(String entityId,
			Integer entityRescheduleCount,
			String recipientId);

	CalendarEntityDAO findTopByEntityIdAndRecipientIdAndStatusOrderByCreatedOnAsc(String oldEntityId, String userId,
			CalendarStatus created);
}
