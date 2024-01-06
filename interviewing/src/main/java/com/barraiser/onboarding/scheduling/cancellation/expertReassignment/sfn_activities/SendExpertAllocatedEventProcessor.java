/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation.expertReassignment.sfn_activities;

import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.expert_allocated_event.ExpertAllocatedEvent;
import com.barraiser.onboarding.events.InterviewingEventProducer;
import com.barraiser.onboarding.interview.PartnerConfigManager;
import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.DTO.ExpertAllocatorData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class SendExpertAllocatedEventProcessor implements ExpertAllocatorSfnActivity {
	public static final String SEND_EXPERT_ALLOCATED_EVENT_ACTIVITY_NAME = "send-expert-allocated-event";
	private final InterviewingEventProducer eventProducer;
	private final PartnerConfigManager partnerConfigManager;
	private final ObjectMapper objectMapper;

	@Override
	public String name() {
		return SEND_EXPERT_ALLOCATED_EVENT_ACTIVITY_NAME;
	}

	@Override
	public ExpertAllocatorData process(final String input) throws Exception {
		final ExpertAllocatorData data = this.objectMapper.readValue(input, ExpertAllocatorData.class);
		final Event<ExpertAllocatedEvent> event = new Event<>();
		final String partnerId = this.partnerConfigManager.getPartnerCompanyForInterviewId(data.getInterviewId())
				.getId();
		event.setPayload(new ExpertAllocatedEvent()
				.interviewId(data.getInterviewId())
				.expertId(data.getInterviewerId())
				.partnerId(partnerId)
				.isExpertDuplicate(Boolean.TRUE.equals(data.getIsExpertDuplicate()))
				.isOnlyCandidateChanged(Boolean.TRUE.equals(data.getIsOnlyCandidateChanged())));
		this.eventProducer.pushEvent(event);
		return data;
	}
}
