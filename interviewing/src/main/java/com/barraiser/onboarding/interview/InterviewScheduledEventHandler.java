/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.EventListener;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.interview_scheduled_event.InterviewScheduledEvent;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.events.InterviewingConsumer;
import com.barraiser.onboarding.interview.feeback.firestore.v1.InterviewFlow;
import com.barraiser.onboarding.interview.feeback.firestore.v1.InterviewingFirestoreV1Manager;
import com.barraiser.onboarding.interviewing.InterviewingFirestoreData;
import com.barraiser.onboarding.jobRoleManagement.JobRoleConfiguration.utils.DefaultQuestionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
@AllArgsConstructor
public class InterviewScheduledEventHandler implements EventListener<InterviewingConsumer> {
	private final ObjectMapper objectMapper;
	private final DefaultQuestionManager defaultQuestionManager;
	private final InterViewRepository interViewRepository;
	private final InterviewingFirestoreV1Manager interviewingFirestoreV1Manager;
	private final InterviewUtil interviewUtil;

	@Override
	public List<Class> eventsToListen() {
		return List.of(InterviewScheduledEvent.class);
	}

	@Override
	public void handleEvent(Event event) throws Exception {
		final InterviewScheduledEvent interviewScheduledEvent = this.objectMapper.convertValue(
				event.getPayload(),
				InterviewScheduledEvent.class);
		final String interviewId = interviewScheduledEvent.getInterview().getId();
		final InterviewDAO interviewDAO = this.interViewRepository.findById(interviewId).get();
		if (Boolean.FALSE.equals(interviewDAO.getIsTaggingAgentNeeded())) {
			final String interviewFlow = this.interviewUtil.getInterviewStructureForInterview(interviewDAO)
					.getInterviewFlow();
			final InterviewFlow interviewFlowDoc = this.objectMapper.readValue(interviewFlow, InterviewFlow.class);
			this.interviewingFirestoreV1Manager.setBaseDoc(interviewId,
					InterviewingFirestoreData.builder().interviewFlow(interviewFlowDoc).build());
		} else {
			this.defaultQuestionManager.addDefaultQuestionsToInterview(interviewDAO);
		}
	}
}
