/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.calendar_interception.gcal;

import com.barraiser.ats_integrations.calendar_interception.ZapierEventHelper;
import com.barraiser.ats_integrations.calendar_interception.events.ATSCalendarEventsConsumer;
import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.EventListener;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.br_google_calendar_event.BRGoogleCalendarEvent;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.br_outlook_calendar_event.BROutlookCalendarEvent;
import com.barraiser.commons.eventing.schema.commons.calendar.BRCalendarEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
@AllArgsConstructor
public class ATSCalendarEventsHandler implements EventListener<ATSCalendarEventsConsumer> {

	private ObjectMapper objectMapper;
	private InviteInterceptionOrchestrator inviteInterceptionOrchestrator;
	private ZapierEventHelper zapierEventHelper;

	@Override
	public List<Class> eventsToListen() {
		return List.of(BRGoogleCalendarEvent.class, BROutlookCalendarEvent.class);
	}

	@Override
	public void handleEvent(Event event) throws Exception {

		final BRCalendarEvent brCalendarEvent = this.objectMapper.convertValue(event.getPayload(),
				BRCalendarEvent.class);
		brCalendarEvent.setEventType(event.getEventType());
		try {
			this.inviteInterceptionOrchestrator.process(brCalendarEvent);
		} catch (Exception e) {
			log.warn("Exception during ATS Process : ", e, e);

			this.zapierEventHelper.sendExceptionToZap(brCalendarEvent, e);
		}
	}
}
