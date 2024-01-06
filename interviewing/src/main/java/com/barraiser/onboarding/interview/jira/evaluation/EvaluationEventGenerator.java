/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.jira.evaluation;

import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.evaluationcancelled.EvaluationCancellation;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.evaluationcompleted.EvaluationCompleted;
import com.barraiser.commons.eventing.schema.commons.Evaluation;
import com.barraiser.commons.eventing.schema.commons.JobRole;
import com.barraiser.commons.eventing.schema.commons.UserDetails;
import com.barraiser.onboarding.candidate.CandidateInformationManager;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.events.InterviewingEventProducer;
import com.barraiser.onboarding.interview.jobrole.JobRoleManager;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Log4j2
@Service
@AllArgsConstructor
public class EvaluationEventGenerator {

	private final PartnerCompanyRepository partnerCompanyRepository;
	private final CompanyRepository companyRepository;
	private final CandidateInformationManager candidateInformationManager;
	private final JobRoleManager jobRoleManager;
	private final InterviewingEventProducer eventProducer;

	public void sendEvaluationCompletedEvent(final EvaluationDAO evaluation) {

		final Event<EvaluationCompleted> event = new Event<>();
		event.setPayload(new EvaluationCompleted()
				.evaluation(this.getEvaluationData(evaluation)));
		try {
			this.eventProducer.pushEvent(event);
		} catch (final Exception err) {
			log.error(err);
		}
	}

	public void sendEvaluationCancelledEvent(final EvaluationDAO evaluation) {

		final Event<EvaluationCancellation> event = new Event<>();
		event.setPayload(new EvaluationCancellation()
				.evaluation(this.getEvaluationData(evaluation)));
		try {
			this.eventProducer.pushEvent(event);
		} catch (final Exception err) {
			log.error(err);
		}
	}

	public Evaluation getEvaluationData(final EvaluationDAO evaluation) {
		final CompanyDAO company = this.companyRepository
				.findById(evaluation.getCompanyId())
				.orElseThrow(() -> new RuntimeException("No company exist for the evaluation : " + evaluation.getId()));
		final Optional<PartnerCompanyDAO> partnerCompanyDAO = this.partnerCompanyRepository
				.findByCompanyId(company.getId());
		final CandidateDAO candidate = this.candidateInformationManager.getCandidate(evaluation.getCandidateId());

		final JobRoleDAO jobRole = this.jobRoleManager.getJobRoleFromEvaluation(evaluation).get();

		return new Evaluation()
				.candidate(new UserDetails()
						.id(candidate.getId())
						.name(String.format("%s %s", candidate.getFirstName(), candidate.getLastName())))
				.id(evaluation.getId())
				.partnerId(partnerCompanyDAO.get().getId())
				.jobRole(new JobRole()
						.id(jobRole.getEntityId().getId())
						.version(jobRole.getEntityId().getVersion())
						.name(jobRole.getInternalDisplayName()));
	}
}
