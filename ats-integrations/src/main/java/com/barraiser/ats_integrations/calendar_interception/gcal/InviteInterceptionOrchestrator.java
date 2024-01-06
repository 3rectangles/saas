/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.calendar_interception.gcal;

import com.barraiser.ats_integrations.calendar_interception.CalendarInterceptionHelper;
import com.barraiser.ats_integrations.calendar_interception.dto.SchedulingData;
import com.barraiser.ats_integrations.calendar_interception.gcal.pipeline.ATSSchedulingInviteProcessingOrchestrator;
import com.barraiser.ats_integrations.calendar_interception.gcal.pipeline.FullyRelaxedMeetingInterceptionOrchestrator;
import com.barraiser.ats_integrations.common.client.PartnerInformationServiceFeignClient;
import com.barraiser.ats_integrations.errorhandling.ATSAnomalyException;
import com.barraiser.commons.eventing.schema.commons.calendar.BRCalendarEvent;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Log4j2
@Component
@AllArgsConstructor
public class InviteInterceptionOrchestrator {

	private final CalendarInterceptionHelper calendarInterceptionHelper;
	private final FullyRelaxedMeetingInterceptionOrchestrator fullyRelaxedMeetingInterceptionOrchestrator;
	private final ATSSchedulingInviteProcessingOrchestrator atsSchedulingInviteProcessingOrchestrator;
	private final PartnerInformationServiceFeignClient partnerInformationServiceFeignClient;

	public void process(final BRCalendarEvent event) throws IOException, ATSAnomalyException {
		if (this.shouldBeProcessed(event)) {
			final SchedulingData data = new SchedulingData();
			data.setBrCalendarEvent(event);

			final String partnerId = this.calendarInterceptionHelper.getPartnerId(event);
			if (partnerId != null) {
				data.setPartnerId(partnerId);
				if (this.calendarInterceptionHelper.isPartnerSaasTrial(partnerId) &&
						this.calendarInterceptionHelper.isKeywordPresent(event, partnerId)) {

					if (!this.partnerInformationServiceFeignClient.isInterviewLimitReached(partnerId)) {
						this.fullyRelaxedMeetingInterceptionOrchestrator.process(data);
					} else {
						log.info("Skipping event with body : " + event.getDescription()
								+ " , as free trial Interview Limit has been reached");
					}

				} else {
					this.atsSchedulingInviteProcessingOrchestrator.process(data);
				}
			} else {
				log.info("Skipping event with body : " + event.getDescription()
						+ " , as no partner Id was found");
			}

		} else {
			log.info("Skipping event with body : " + event.getDescription()
					+ " , as its not an ATS Scheduling event");
		}
	}

	/**
	 * This method is used to decide if it's an event
	 * created because of an interview scheduled via
	 * ATS
	 */
	private Boolean shouldBeProcessed(final BRCalendarEvent event) {
		return !this.calendarInterceptionHelper.isCancelledEvent(event)
				&& !this.calendarInterceptionHelper.isOfThePast(event)
				&& !this.calendarInterceptionHelper.isMeetingLinkNotPresent(event)
				&& !this.calendarInterceptionHelper.isInterceptedEvent(event)
				&& !this.calendarInterceptionHelper.isEventProcessed(event);
	}
}
