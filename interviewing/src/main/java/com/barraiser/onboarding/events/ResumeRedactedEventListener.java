/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.events;

import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.EventListener;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.resumeredactionevent.ResumeRedactionEvent;
import com.barraiser.onboarding.candidate.CandidateInformationManager;
import com.barraiser.onboarding.dal.CandidateDAO;
import com.barraiser.onboarding.user.resume.client.ResumeRedactionFeignClient;
import com.barraiser.onboarding.user.resume.dto.ResumeRedactionRequest;
import com.barraiser.onboarding.user.resume.dto.ResumeRedactionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
@RequiredArgsConstructor
public class ResumeRedactedEventListener implements EventListener<InterviewingConsumer> {
	private final ObjectMapper objectMapper;
	private final ResumeRedactionFeignClient resumeRedactionFeignClient;
	private final CandidateInformationManager candidateInformationManager;

	@Override
	public List<Class> eventsToListen() {
		return List.of(ResumeRedactionEvent.class);
	}

	@Override
	public void handleEvent(final Event event) throws Exception {
		final ResumeRedactionEvent payload = this.objectMapper.convertValue(event.getPayload(),
				ResumeRedactionEvent.class);
		ResumeRedactionResponse resumeRedactionResponse = ResumeRedactionResponse.builder().isResumeRedacted(false)
				.resumeLink(payload.getResumeLink()).build();
		try {
			resumeRedactionResponse = this.resumeRedactionFeignClient.redactResume(ResumeRedactionRequest.builder()
					.resumeLink(payload.getResumeLink()).intervieweeId(payload.getIntervieweeId()).build());
		} catch (Exception e) {
			log.error("Resume redaction failed");
		}

		final CandidateDAO candidate = this.candidateInformationManager.getCandidate(payload.getIntervieweeId());
		this.candidateInformationManager.updateCandidate(
				candidate.toBuilder().redactedResumeUrl(resumeRedactionResponse.getResumeLink()).build());
	}
}
