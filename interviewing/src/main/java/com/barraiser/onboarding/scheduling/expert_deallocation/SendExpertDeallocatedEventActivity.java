/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.expert_deallocation;

import com.barraiser.common.utilities.DateUtils;
import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.expert_deallocated_event.CommunicationDetailsEvent;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.expert_deallocated_event.ExpertDeallocatedEvent;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.UserDetailsDAO;
import com.barraiser.onboarding.dal.UserDetailsRepository;
import com.barraiser.onboarding.events.InterviewingEventProducer;
import com.barraiser.onboarding.interview.PartnerConfigManager;
import com.barraiser.onboarding.scheduling.expert_deallocation.dto.ExpertDeAllocatorData;
import com.barraiser.onboarding.user.TimezoneManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Log4j2
public class SendExpertDeallocatedEventActivity implements ExpertDeallocationSfnActivity {
	private final ObjectMapper objectMapper;
	private final InterviewingEventProducer eventProducer;
	private final UserDetailsRepository userDetailsRepository;
	private final TimezoneManager timezoneManager;
	private final DateUtils dateUtils;
	private final PartnerConfigManager partnerConfigManager;

	@Override
	public String name() {
		return "send-expert-deallocated-event";
	}

	@Override
	public ExpertDeAllocatorData process(final String input) throws Exception {
		final ExpertDeAllocatorData data = this.objectMapper.readValue(input,
				ExpertDeAllocatorData.class);
		if (data.getNewInterviewThatExpertCanTake() != null) {
			return data;
		}
		this.generateExpertDeallocatedEvent(data.getInterview(), data.getOriginalInterviewerId(),
				data.getDeAllocationTime());
		return data;
	}

	private void generateExpertDeallocatedEvent(final InterviewDAO interviewDAO, final String expertId,
			final Long cancellationTime) {
		final UserDetailsDAO expert = this.userDetailsRepository.findById(expertId).get();
		final String timezoneOfExpert = this.timezoneManager.getTimezoneOfExpert(expertId);

		final Event<ExpertDeallocatedEvent> event = new Event<>();
		event.setPayload(new ExpertDeallocatedEvent()
				.interviewId(interviewDAO.getId())
				.expertFirstName(expert.getFirstName())
				.cancellationTime(this.dateUtils.getFormattedDateString(cancellationTime, timezoneOfExpert,
						DateUtils.TIME_IN_12_HOUR_FORMAT_WITH_SLASH))
				.interviewTime(this.dateUtils.getFormattedDateString(interviewDAO.getStartDate(), timezoneOfExpert,
						DateUtils.TIME_IN_12_HOUR_FORMAT_WITH_SLASH))
				.partnerId(this.partnerConfigManager.getPartnerCompanyForInterviewId(interviewDAO.getId()).getId())
				.communicationDetails(new CommunicationDetailsEvent().userId(expert.getId())));
		try {
			this.eventProducer.pushEvent(event);
		} catch (final Exception err) {
			log.error(err);
		}
	}
}
