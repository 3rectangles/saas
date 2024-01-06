/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.cost;

import com.barraiser.common.DTO.pricing.UpdateInterviewCostDetailsRequestDTO;
import com.barraiser.common.dal.Money;
import com.barraiser.common.enums.RoundType;
import com.barraiser.onboarding.candidate.CandidateInformationManager;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.jobrole.JobRoleManager;
import com.barraiser.onboarding.partner.partnerPricing.PricingServiceClient;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class InterviewCostDetailsCalculator {
	private final EvaluationRepository evaluationRepository;
	private final ExpertRepository expertRepository;
	private final CandidateInformationManager candidateInformationManager;
	private final JobRoleManager jobRoleManager;
	private final PartnerCompanyRepository partnerCompanyRepository;
	private final InterviewStructureRepository interviewStructureRepository;
	private final PricingServiceClient pricingServiceClient;

	public void calculateAndSaveInterviewCost(final InterviewDAO interviewDAO,
			final String expertId) {
		this.calculateAndSaveInterviewCost(interviewDAO, expertId, null, null, null);
	}

	private Boolean isExpertCostDetailsPresent(final ExpertDAO expertDAO) {
		return expertDAO.getBaseCost() != null && expertDAO.getMultiplier() != null;
	}

	public void calculateAndSaveInterviewCost(final InterviewDAO interviewDAO,
			final String expertId, final Money interviewPrice, final Double usedMargin, final Double configuredMargin) {
		final ExpertDAO expertDAO = this.expertRepository.findById(expertId).get();
		final EvaluationDAO evaluationDAO = this.evaluationRepository.findById(interviewDAO.getEvaluationId()).get();
		final CandidateDAO candidateDAO = this.candidateInformationManager
				.getCandidate(interviewDAO.getIntervieweeId());

		final JobRoleDAO jobRoleDAO = this.jobRoleManager.getJobRoleFromEvaluation(evaluationDAO).get();
		final PartnerCompanyDAO partnerCompanyDAO = this.partnerCompanyRepository
				.findByCompanyId(jobRoleDAO.getCompanyId()).get();
		final InterviewStructureDAO interviewStructureDAO = this.interviewStructureRepository
				.findById(interviewDAO.getInterviewStructureId()).get();
		final UpdateInterviewCostDetailsRequestDTO updateInterviewCostDetailsRequestDTO = UpdateInterviewCostDetailsRequestDTO
				.builder()
				.interviewId(interviewDAO.getId())
				.rescheduleCount(interviewDAO.getRescheduleCount())
				.expertCostPerHour(
						this.isExpertCostDetailsPresent(expertDAO)
								? Money.builder().value(expertDAO.getBaseCost() * expertDAO.getMultiplier())
										.currency(expertDAO.getCurrency()).build()
								: null)
				.jobRoleId(evaluationDAO.getJobRoleId())
				.interviewStructureId(interviewDAO.getInterviewStructureId())
				.workExperienceOfCandidateInMonths(candidateDAO.getWorkExperienceInMonths())
				.roundType(RoundType.fromString(interviewDAO.getInterviewRound()))
				.durationOfInterview(interviewStructureDAO.getDuration().longValue())
				.expertId(expertId)
				.minPriceOfExpertPerHour(expertDAO.getMinPrice())
				.interviewPrice(interviewPrice)
				.usedMargin(usedMargin)
				.configuredMargin(configuredMargin)
				.build();
		this.pricingServiceClient.storeInterviewCostDetails(partnerCompanyDAO.getId(),
				updateInterviewCostDetailsRequestDTO);
	}
}
