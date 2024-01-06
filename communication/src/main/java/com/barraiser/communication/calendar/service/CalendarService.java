/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.calendar.service;

import com.barraiser.common.model.CalendarEvent;
import com.barraiser.common.model.CreateCalendarEventRequest;
import com.barraiser.communication.dal.CalendarEntityDAO;
import com.barraiser.communication.dal.CalendarEntityRepository;
import com.barraiser.communication.dal.CalendarStatus;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Log4j2
@Component
@AllArgsConstructor
public class CalendarService {
	private final CalendarEntityRepository calendarEntityRepository;
	private final CalendaringService calendaringService;

	public CalendarEvent createEvent(final CreateCalendarEventRequest request) throws Exception {

		final com.barraiser.communication.calendar.service.CalendarEvent calendarEvent = this.calendaringService
				.createEvent(request);

		this.calendarEntityRepository.save(
				CalendarEntityDAO.builder()
						.id(UUID.randomUUID().toString())
						.accountId(calendarEvent.getAccountId())
						.eventId(calendarEvent.getEventId())
						.entityId(request.getEntityId())
						.entityRescheduleCount(request.getEntityRescheduleCount())
						.entityType(request.getEntityType())
						.status(CalendarStatus.CREATED)
						.recipientId(request.getRecipientId())
						.build());

		return CalendarEvent.builder()
				.eventId(calendarEvent.getEventId())
				.accountId(calendarEvent.getAccountId())
				.build();
	}

	public void deleteEvent(final String accountId, final String eventId) throws Exception {
		this.calendaringService.deleteEvent(accountId, eventId);
	}

	public void deleteEventsForEntity(final String entityId, final Integer entityRescheduleCount) {

		this.calendarEntityRepository
				.findByEntityIdAndEntityRescheduleCount(entityId, entityRescheduleCount)
				.forEach(
						x -> {
							this.deleteCalendarEvent(x, entityId);
						});
	}

	public void deleteEventsForEntityAndUserId(final String entityId, final String userId,
			final Integer entityRescheduleCount) {
		final CalendarEntityDAO calendarEntityDAO = this.calendarEntityRepository
				.findByEntityIdAndEntityRescheduleCountAndRecipientId(entityId, entityRescheduleCount, userId);
		this.deleteCalendarEvent(calendarEntityDAO, entityId);
	}

	public CalendarEvent updateCalendarInvite(
			final String oldEntityId,
			final String userId,
			final CreateCalendarEventRequest createCalendarEventRequest) throws Exception {
		final CalendarEntityDAO calendarEntityDAO = this.calendarEntityRepository
				.findTopByEntityIdAndRecipientIdAndStatusOrderByCreatedOnAsc(
						oldEntityId,
						userId, CalendarStatus.CREATED);

		com.barraiser.communication.calendar.service.CalendarEvent calendarEvent = this.calendaringService
				.updateEvent(
						calendarEntityDAO.getAccountId(),
						calendarEntityDAO.getEventId(),
						createCalendarEventRequest);

		final CalendarEntityDAO cancelOldCalendarEntityDAO = calendarEntityDAO
				.toBuilder()
				.status(CalendarStatus.CANCELLED)
				.build();

		this.calendarEntityRepository.save(cancelOldCalendarEntityDAO);

		this.calendarEntityRepository.save(
				CalendarEntityDAO.builder()
						.id(UUID.randomUUID().toString())
						.accountId(calendarEvent.getAccountId())
						.eventId(calendarEvent.getEventId())
						.entityId(createCalendarEventRequest.getEntityId())
						.entityRescheduleCount(createCalendarEventRequest.getEntityRescheduleCount())
						.entityType(createCalendarEventRequest.getEntityType())
						.status(CalendarStatus.CREATED)
						.recipientId(createCalendarEventRequest.getRecipientId())
						.build());

		return CalendarEvent
				.builder()
				.eventId(calendarEvent.getEventId())
				.accountId(calendarEvent.getAccountId())
				.build();
	}

	private void deleteCalendarEvent(final CalendarEntityDAO calendarEntityDAO, final String entityId) {
		try {
			this.deleteEvent(calendarEntityDAO.getAccountId(), calendarEntityDAO.getEventId());
		} catch (final GoogleJsonResponseException ex) {
			if (ex.getStatusCode() == 404) {
				log.info(ex.getMessage(), ex);
			}
		} catch (final Exception e) {
			log.error(
					String.format(
							"Issue cancelling calendar event for entity : %s",
							entityId),
					e);
		}
	}
}
