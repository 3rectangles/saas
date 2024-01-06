/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.match_interviewers;

import com.barraiser.common.dal.Money;
import com.barraiser.common.enums.RoundType;
import com.barraiser.common.model.InterviewPriceResponseDTO;
import com.barraiser.onboarding.expert.CostUtil;
import com.barraiser.onboarding.partner.partnerPricing.PricingServiceClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class MaxCostBasedFilteringProcessor implements MatchInterviewersProcessor {
	private final PricingServiceClient pricingServiceClient;
	private final CostUtil costUtil;
	private final ObjectMapper objectMapper;
	private final ExpertCostForSchedulingFeatureToggleManager expertCostForSchedulingFeatureToggleManager;

	@Override
	public void process(final MatchInterviewersData data) throws IOException {

		final Boolean shouldInterviewersBeFilteredBasisOnCost = this
				.shouldInterviewersBeFilteredBasisOnCost(data.getInterviewId());
		if (!Boolean.TRUE.equals(shouldInterviewersBeFilteredBasisOnCost)) {
			return;
		}

		final InterviewPriceResponseDTO interviewPriceResponseDTO = this.pricingServiceClient
				.getInterviewPrice(data.getPartnerCompanyId(),
						data.getJobRoleId(), data.getInterviewStructureId(),
						data.getWorkExperienceOfIntervieweeInMonths(), RoundType.fromString(data.getInterviewRound()),
						data.getDurationOfInterview())
				.getBody();
		log.info("interview price response for interview_id : {} is {} ", data.getInterviewId(),
				this.objectMapper.writeValueAsString(interviewPriceResponseDTO));
		final Double margin = data.getBarRaiserUsedMarginPercentage() != null ? data.getBarRaiserUsedMarginPercentage()
				: Boolean.TRUE.equals(data.getIsFallbackEnabled())
						? interviewPriceResponseDTO.getBarRaiserMarginPercentage() / 2
						: interviewPriceResponseDTO.getBarRaiserMarginPercentage();

		final Money interviewCost = data.getInterviewCost() != null ? data.getInterviewCost()
				: interviewPriceResponseDTO.getMaximumInterviewPrice();
		data.setBarRaiserConfiguredMarginPercentage(
				data.getBarRaiserConfiguredMarginPercentage() != null ? data.getBarRaiserConfiguredMarginPercentage()
						: interviewPriceResponseDTO.getBarRaiserMarginPercentage());
		data.setBarRaiserUsedMarginPercentage(margin);
		log.info("margin for interview_id : {} is {}", data.getInterviewId(), margin);
		final Double maxPriceThatCanBePaidToExpertsInINR = this.getMaxPriceThatCanBePaidToExpertInINR(
				interviewCost,
				margin);
		data.setInterviewers(this.filterExpertsHavingCostGreaterThanMaxPrice(data.getInterviewers(),
				maxPriceThatCanBePaidToExpertsInINR));
		data.setInterviewCost(interviewCost);
	}

	private Double getMaxPriceThatCanBePaidToExpertInINR(final Money interviewPrice, final Double margin) {

		return this.costUtil.convertToINR((interviewPrice.getValue() * (1 - margin / 100)),
				interviewPrice.getCurrency());
	}

	private List<InterviewerData> filterExpertsHavingCostGreaterThanMaxPrice(final List<InterviewerData> interviewers,
			final Double maxPriceThatCanBePaidToExperts) {
		return interviewers.stream().filter(x -> x.getMinCostInINR() <= maxPriceThatCanBePaidToExperts)
				.collect(Collectors.toList());
	}

	private Boolean shouldInterviewersBeFilteredBasisOnCost(final String interviewId) {
		return this.expertCostForSchedulingFeatureToggleManager.isFeatureToggleOn(interviewId);
	}
}
