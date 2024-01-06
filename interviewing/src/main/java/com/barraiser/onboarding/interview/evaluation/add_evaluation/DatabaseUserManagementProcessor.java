/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.add_evaluation;

import com.barraiser.common.utilities.PhoneParser;
import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.resumeredactionevent.ResumeRedactionEvent;
import com.barraiser.onboarding.candidate.CandidateInformationManager;
import com.barraiser.onboarding.dal.CandidateDAO;
import com.barraiser.onboarding.dal.ParsedResumeDAO;
import com.barraiser.onboarding.dal.UserDetailsDAO;
import com.barraiser.onboarding.dal.UserDetailsRepository;
import com.barraiser.onboarding.events.InterviewingEventProducer;
import com.barraiser.onboarding.resume.ParsedResumeRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.UUID;

@AllArgsConstructor
@Component
@Log4j2
public class DatabaseUserManagementProcessor implements AddEvaluationProcessor {
	private final UserDetailsRepository userDetailsRepository;
	private final CandidateInformationManager candidateInformationManager;
	private final PhoneParser phoneParser;
	private final InterviewingEventProducer eventProducer;
	private final ParsedResumeRepository parsedResumeRepository;

	@Override
	public void process(final AddEvaluationProcessingData data) {
		if ((data.getIsCandidateAnonymous() == null || !data.getIsCandidateAnonymous())) {
			this.createOrUpdateUserDetails(data);
		}
		this.createCandidate(data);
	}

	private void createOrUpdateUserDetails(final AddEvaluationProcessingData data) {
		final String userId = data.getUserId();

		final String firstName;
		final String lastName;
		if (data.getShouldNameSplit() != null && !data.getShouldNameSplit()) {
			firstName = data.getCandidateName();
			lastName = null;
		} else {
			firstName = data.getCandidateName().split("\\s+", 2)[0];
			lastName = data.getCandidateName().split("\\s+", 2).length >= 2
					? data.getCandidateName().split("\\s+", 2)[1]
					: null;
		}

		final UserDetailsDAO userDetailsDAO = this.userDetailsRepository
				.findById(userId)
				.orElse(UserDetailsDAO.builder()
						.id(userId)
						.firstName(firstName)
						.lastName(lastName)
						.build());

		final String formattedPhone = this.phoneParser.getFormattedPhone(data.getPhone());

		this.userDetailsRepository.save(userDetailsDAO.toBuilder()
				.email(data.getEmail())
				.phone(formattedPhone)
				.build());
	}

	private void createCandidate(final AddEvaluationProcessingData data) {
		final String userId = data.getUserId();
		final String candidateId = UUID.randomUUID().toString();

		final String firstName;
		final String lastName;

		if (data.getShouldNameSplit() != null && !data.getShouldNameSplit()) {
			firstName = data.getCandidateName();
			lastName = null;
		} else {
			firstName = data.getCandidateName().split("\\s+", 2)[0];
			lastName = data.getCandidateName().split("\\s+", 2).length >= 2
					? data.getCandidateName().split("\\s+", 2)[1]
					: null;
		}

		final ParsedResumeDAO parsedResumeDAO = this.parsedResumeRepository.findByDocumentId(data.getDocumentId());

		this.candidateInformationManager.updateCandidate(
				CandidateDAO.builder()
						.id(candidateId)
						.userId(userId)
						.firstName(firstName)
						.lastName(lastName)
						.workExperienceInMonths(data.getWorkExperience())
						.resumeUrl(data.getResumeUrl())
						.redactedResumeUrl(data.getResumeUrl())
						.resumeId(data.getDocumentId())
						.designation(parsedResumeDAO != null ? parsedResumeDAO.getCurrentDesignation() : null)
						.almaMater(parsedResumeDAO != null ? parsedResumeDAO.getAlmaMater() : null)
						.currentCompanyName(parsedResumeDAO != null ? parsedResumeDAO.getCurrentEmployer() : null)
						.build());

		data.setCandidateId(candidateId);
		this.sendRedactedResumeEvent(data.getResumeUrl(), candidateId);
		data.setParsedResumeDAO(parsedResumeDAO);
	}

	private Boolean isCandidateAnonymous(final AddEvaluationProcessingData data) {
		return data.getIsCandidateAnonymous() != null && Boolean.TRUE.equals(data.getIsCandidateAnonymous());
	}

	public void sendRedactedResumeEvent(final String resumeLink, final String candidateId) {
		final Event<ResumeRedactionEvent> event = new Event<>();
		try {
			event.setPayload(new ResumeRedactionEvent().intervieweeId(candidateId).resumeLink(resumeLink));
			this.eventProducer.pushEvent(event);
		} catch (final Exception e) {
			log.error("could not send event for resume redaction", e);
		}
	}
}
