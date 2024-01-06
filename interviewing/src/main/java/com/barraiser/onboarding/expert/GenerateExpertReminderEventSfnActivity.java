/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.expert;

import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.expert_reminder_for_interview_event.ExpertReminderForInterviewEvent;
import com.barraiser.onboarding.events.InterviewingEventProducer;
import com.barraiser.onboarding.interview.PartnerConfigManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class GenerateExpertReminderEventSfnActivity implements ExpertReminderSfnActivity {
	private final ObjectMapper objectMapper;
	private final PartnerConfigManager partnerConfigManager;
	private final InterviewingEventProducer eventProducer;

	@Override
	public String name() {
		return "generate-expert-reminder-event";
	}

	@Override
	public ExpertReminderData process(final String input) throws Exception {
		final ExpertReminderData data = this.objectMapper.readValue(input, ExpertReminderData.class);

		this.generateExpertReminderEvent(data);

		return data;
	}

	private void generateExpertReminderEvent(final ExpertReminderData data) throws Exception {
		final Event<ExpertReminderForInterviewEvent> event = new Event<>();

		event.setPayload(new ExpertReminderForInterviewEvent()
				.partnerId(this.partnerConfigManager.getPartnerCompanyForInterviewId(data.getInterviewId()).getId())
				.durationBeforeInterview(data.getDurationBeforeInterviewInMinutesToSendReminder())
				.interviewId(data.getInterviewId()));

		this.eventProducer.pushEvent(event);
	}
}
