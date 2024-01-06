/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.user.expert.mapper;

import com.barraiser.onboarding.dal.ExpertDAO;
import com.barraiser.onboarding.search.dao.ExpertSearchDAO;
import com.barraiser.onboarding.user.expert.dto.ExpertDetails;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ExpertMapper {

	public ExpertDAO toExpertDAO(final ExpertDetails expertDetails) {

		return ExpertDAO.builder()
				.id(expertDetails.getId())
				.tenantId(expertDetails.getTenantId())
				.isActive(expertDetails.getIsActive())
				.isDemoEligible(expertDetails.getIsDemoEligible())
				.opsRep(expertDetails.getOpsRep())
				.resumeReceivedDate(expertDetails.getResumeReceivedDate())
				.offerLetter(expertDetails.getOfferLetter())
				.expertDomains(expertDetails.getExpertDomains())
				.peerDomains(expertDetails.getPeerDomains())
				.interviewerReferrer(expertDetails.getInterviewerReferrer())
				.consultancyReferrer(expertDetails.getConsultancyReferrer())
				.reachoutChannel(expertDetails.getReachoutChannel())
				.isUnderTraining(expertDetails.getIsUnderTraining())
				.companiesForWhichExpertCanTakeInterview(expertDetails.getCompaniesForWhichExpertCanTakeInterview())
				.countriesForWhichExpertCanTakeInterviews(expertDetails.getCountriesForWhichExpertCanTakeInterview())
				.gapBetweenInterviews(expertDetails.getGapBetweenInterviews())
				.baseCost(expertDetails.getPayoutDetails() != null ? expertDetails.getPayoutDetails().getBaseCost()
						: null)
				.currency(expertDetails.getPayoutDetails() != null ? expertDetails.getPayoutDetails().getCurrency()
						: null)
				.pan(expertDetails.getPayoutDetails() != null ? expertDetails.getPayoutDetails().getPan() : null)
				.bankAccount(
						expertDetails.getPayoutDetails() != null ? expertDetails.getPayoutDetails().getBankAccount()
								: null)
				.multiplier(expertDetails.getPayoutDetails() != null ? expertDetails.getPayoutDetails().getMultiplier()
						: null)
				.costLogic(expertDetails.getPayoutDetails() != null ? expertDetails.getPayoutDetails().getCostLogic()
						: null)
				.cancellationLogic(expertDetails.getPayoutDetails() != null
						? expertDetails.getPayoutDetails().getCancellationLogic()
						: null)
				.IFSC(expertDetails.getPayoutDetails().getIFSC() != null ? expertDetails.getPayoutDetails().getIFSC()
						: null)
				.build();
	}

	public ExpertDetails toExpertDetails(final ExpertDAO expertDAO) {

		return ExpertDetails.builder()
				.id(expertDAO.getId())
				.isActive(expertDAO.getIsActive())
				.opsRep(expertDAO.getOpsRep())
				.resumeReceivedDate(expertDAO.getResumeReceivedDate())
				.offerLetter(expertDAO.getOfferLetter())
				.expertDomains(expertDAO.getExpertDomains())
				.peerDomains(expertDAO.getPeerDomains())
				.interviewerReferrer(expertDAO.getInterviewerReferrer())
				.consultancyReferrer(expertDAO.getConsultancyReferrer())
				.reachoutChannel(expertDAO.getReachoutChannel())
				.isUnderTraining(expertDAO.getIsUnderTraining())
				.companiesForWhichExpertCanTakeInterview(expertDAO.getCompaniesForWhichExpertCanTakeInterview())
				.countriesForWhichExpertCanTakeInterview(expertDAO.getCountriesForWhichExpertCanTakeInterviews())
				.gapBetweenInterviews(expertDAO.getGapBetweenInterviews())
				.payoutDetails(ExpertDetails.PayoutDetails.builder()
						.baseCost(expertDAO.getBaseCost())
						.currency(expertDAO.getCurrency())
						.pan(expertDAO.getPan())
						.bankAccount(expertDAO.getBankAccount())
						.multiplier(expertDAO.getMultiplier())
						.costLogic(expertDAO.getCostLogic())
						.cancellationLogic(expertDAO.getCancellationLogic())
						.IFSC(expertDAO.getIFSC())
						.build())
				.build();
	}

	public ExpertSearchDAO toExpertSearchDAO(final ExpertDetails expertDetails) {

		return ExpertSearchDAO.builder()
				.category(expertDetails.getCategory())
				.email(expertDetails.getEmail())
				.designation(expertDetails.getDesignation())
				.expertDomains(expertDetails.getExpertDomains())
				.peerDomains(expertDetails.getPeerDomains())
				.phone(expertDetails.getPhone())
				.currentCompanyId(expertDetails.getCurrentCompanyId())
				.workExperienceInMonths(expertDetails.getWorkExperienceInMonths())
				.isDemoEligible(expertDetails.getIsDemoEligible())
				.isActive(expertDetails.getIsActive())
				.isDemoEligible(expertDetails.getIsDemoEligible())
				.lastCompaniesId(expertDetails.getLastCompaniesId())
				.isUnderTraining(expertDetails.getIsUnderTraining())
				.companiesForWhichExpertCanTakeInterview(expertDetails.getCompaniesForWhichExpertCanTakeInterview())
				.countriesForWhichExpertCanTakeInterviews(expertDetails.getCountriesForWhichExpertCanTakeInterview())
				.userId(expertDetails.getId())
				.costPerHour(expertDetails.getPayoutDetails() != null ? expertDetails.getPayoutDetails().getBaseCost()
						: null)
				.currency(expertDetails.getPayoutDetails() != null ? expertDetails.getPayoutDetails().getCurrency()
						: null)
				.multiplier(expertDetails.getPayoutDetails() != null ? expertDetails.getPayoutDetails().getMultiplier()
						: null)
				.minCostPerHour(
						expertDetails.getPayoutDetails() != null ? expertDetails.getPayoutDetails().getMinCostPerHour()
								: null)
				.build();
	}

}
