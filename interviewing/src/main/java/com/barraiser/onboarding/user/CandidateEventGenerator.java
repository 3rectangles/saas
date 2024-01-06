/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.user;

import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.candidateaddition.CandidateAddition;
import com.barraiser.commons.eventing.schema.commons.Candidate;
import com.barraiser.commons.eventing.schema.commons.JobRole;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.events.InterviewingEventProducer;
import com.barraiser.onboarding.interview.evaluation.add_evaluation.AddEvaluationProcessingData;
import com.barraiser.onboarding.interview.jobrole.JobRoleManager;
import com.barraiser.onboarding.partner.PartnerConfigurationManager;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@AllArgsConstructor
public class CandidateEventGenerator {
	private final InterviewingEventProducer eventProducer;
	private final PartnerCompanyRepository partnerCompanyRepository;
	private final JobRoleManager jobRoleManager;
	private final EvaluationRepository evaluationRepository;
	private final PartnerConfigurationManager partnerConfigurationManager;

	public void sendCandidateEventForAddition(final AddEvaluationProcessingData data) {
		final EvaluationDAO evaluationForUserId = this.evaluationRepository.findById(data.getEvaluationId()).get();
		final JobRoleDAO jobRoleFromEvaluation = this.jobRoleManager.getJobRoleFromEvaluation(evaluationForUserId)
				.get();
		final PartnerCompanyDAO partner = this.partnerCompanyRepository
				.findByCompanyId(jobRoleFromEvaluation.getCompanyId()).get();

		final Integer yearsOfExperience = data.getWorkExperience() != null ? data.getWorkExperience() / 12 : -1;
		final Integer remainingMonths = data.getWorkExperience() != null ? data.getWorkExperience() % 12 : -1;

		if (data.getWorkExperience() == null) {
			log.info("The work experience of the candidate is empty.");
		}

		final Event<CandidateAddition> event = new Event<>();
		event.setPayload(new CandidateAddition()
				.partnerId(partner.getId())
				.isCandidateSchedulingEnabled(
						this.partnerConfigurationManager.isCandidateSchedulingEnabled(partner.getId()))
				.candidate(new Candidate()
						.id(data.getCandidateId())
						.partnerId(partner.getId())
						.userName(data.getCandidateName())
						.email(data.getEmail())
						.phone(data.getPhone())
						.evaluationId(data.getEvaluationId())
						.jobRole(new JobRole()
								.name(jobRoleFromEvaluation.getInternalDisplayName())
								.version(jobRoleFromEvaluation.getEntityId().getVersion())
								.id(jobRoleFromEvaluation.getEntityId().getId()))
						.experience(data.getWorkExperience() != null
								? String.format("%d years %d months", yearsOfExperience, remainingMonths)
								: null)));
		try {
			this.eventProducer.pushEvent(event);
		} catch (final Exception err) {
			log.error(err);
		}

	}

}
