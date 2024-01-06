/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.jira.interview;

import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.interviewcancellation.InterviewCancellation;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.interviewcompletion.InterviewCompletion;
import com.barraiser.commons.eventing.schema.commons.InterviewDetailEvent;
import com.barraiser.commons.eventing.schema.commons.IntervieweeDetails;
import com.barraiser.commons.eventing.schema.commons.JobRole;
import com.barraiser.onboarding.candidate.CandidateInformationManager;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.events.InterviewingEventProducer;
import com.barraiser.onboarding.interview.InterviewUtil;
import com.barraiser.onboarding.interview.jobrole.JobRoleManager;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Log4j2
@Service
@AllArgsConstructor
public class InterviewEventGenerator {

	private final InterviewingEventProducer eventProducer;
	private final CandidateInformationManager candidateInformationManager;
	private final PartnerCompanyRepository partnerCompanyRepository;
	private final EvaluationRepository evaluationRepository;
	private final JobRoleManager jobRoleManager;
	private final InterviewUtil interviewUtil;

	public InterviewDetailEvent getInterviewEventData(final InterviewDAO interview) {

		final CandidateDAO interviewee = this.candidateInformationManager.getCandidate(interview.getIntervieweeId());
		final EvaluationDAO correspondingEvaluation = this.evaluationRepository.findById(interview.getEvaluationId())
				.get();
		final Optional<PartnerCompanyDAO> partnerCompanyDAO = this.partnerCompanyRepository
				.findByCompanyId(correspondingEvaluation.getCompanyId());
		final JobRoleDAO jobRole = this.jobRoleManager.getJobRoleFromEvaluation(correspondingEvaluation).get();
		final Integer interviewRoundNumber = this.interviewUtil.getRoundNumberOfInterview(interview);

		return new InterviewDetailEvent()
				.interviewee(new IntervieweeDetails()
						.id(interview.getIntervieweeId())
						.firstName(interviewee.getFirstName())
						.lastName(interviewee.getLastName()))
				.id(interview.getId())
				.jobRole(new JobRole()
						.id(jobRole.getEntityId().getId())
						.version(jobRole.getEntityId().getVersion())
						.name(jobRole.getInternalDisplayName()))
				.partnerId(partnerCompanyDAO.get().getId())
				.evaluationId(interview.getEvaluationId())
				.interviewRound(interviewRoundNumber);

	}

	public void sendInterviewCompletionEvent(final InterviewDAO interview) {

		final Event<InterviewCompletion> event = new Event<>();

		event.setPayload(new InterviewCompletion()
				.interview(this.getInterviewEventData(interview)));
		try {
			this.eventProducer.pushEvent(event);
		} catch (final Exception err) {
			log.error(err);
		}

	}

	public void sendInterviewCancellationEvent(final InterviewDAO interview) {

		final Event<InterviewCancellation> event = new Event<>();

		event.setPayload(new InterviewCancellation()
				.interview(this.getInterviewEventData(interview)));
		try {
			event.setPayload(new InterviewCancellation());
		} catch (final Exception err) {
			log.error(err);
		}
	}
}
