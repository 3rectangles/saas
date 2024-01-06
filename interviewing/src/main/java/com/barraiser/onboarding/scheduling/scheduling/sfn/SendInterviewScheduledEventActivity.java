/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.sfn;

import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.interview_overbooked_event.InterviewOverbookedEvent;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.interview_scheduled_event.InterviewScheduledEvent;
import com.barraiser.commons.eventing.schema.commons.InterviewDetailEvent;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.events.InterviewingEventProducer;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.jobrole.JobRoleManager;
import com.barraiser.onboarding.scheduling.scheduling.SchedulingProcessingData;
import com.barraiser.onboarding.scheduling.scheduling.sfn.InterviewSchedulingActivity;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class SendInterviewScheduledEventActivity implements InterviewSchedulingActivity {
	public static final String SEND_INTERVIEW_SCHEDULED_EVENT = "send-interview-scheduled-event";

	private final InterviewingEventProducer eventProducer;
	private final EvaluationRepository evaluationRepository;
	private final JobRoleManager jobRoleManager;
	private final PartnerCompanyRepository partnerCompanyRepository;
	private final InterViewRepository interViewRepository;
	private final ObjectMapper objectMapper;

	private void sendInterviewScheduledEvent(final InterviewDAO interviewDAO) throws Exception {
		final Event<InterviewScheduledEvent> event = new Event<>();
		event.setPayload(new InterviewScheduledEvent()
				.interview(new InterviewDetailEvent().id(interviewDAO.getId()))
				.partnerId(this.getPartnerId(interviewDAO)));
		this.eventProducer.pushEvent(event);
	}

	private void sendInterviewOverBookedEvent(final InterviewDAO interviewDAO) throws Exception {
		final Event<InterviewOverbookedEvent> event = new Event<>();
		event.setPayload(new InterviewOverbookedEvent()
				.interview(new InterviewDetailEvent().id(interviewDAO.getId()))
				.partnerId(this.getPartnerId(interviewDAO)));
		this.eventProducer.pushEvent(event);
	}

	private String getPartnerId(final InterviewDAO interviewDAO) {
		final EvaluationDAO evaluationDAO = this.evaluationRepository.findById(interviewDAO.getEvaluationId()).get();
		final JobRoleDAO jobRoleDAO = this.jobRoleManager.getJobRoleFromEvaluation(evaluationDAO).get();
		return this.partnerCompanyRepository.findByCompanyId(jobRoleDAO.getCompanyId()).get().getId();
	}

	@Override
	public String name() {
		return SEND_INTERVIEW_SCHEDULED_EVENT;
	}

	@Override
	@SneakyThrows
	public SchedulingProcessingData process(String input) throws Exception {
		final SchedulingProcessingData data = this.objectMapper.readValue(input, SchedulingProcessingData.class);
		final InterviewDAO interviewDAO = this.interViewRepository.findById(data.getInput().getInterviewId()).get();

		this.sendInterviewScheduledEvent(interviewDAO);

		return data;
	}
}
