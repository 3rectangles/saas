/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.match_interviewers;

import com.barraiser.common.model.ScheduledInterviewCostDetailDTO;
import com.barraiser.onboarding.availability.AvailabilityManager;
import com.barraiser.onboarding.partner.partnerPricing.PricingServiceClient;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Log4j2
@Component
@AllArgsConstructor
public class EligibleInterviewersFetcher {
	private final GetEligibleInterviewersProcessor getEligibleInterviewersProcessor;
	private final ExpertCostForSchedulingFeatureToggleManager expertCostForSchedulingFeatureToggleManager;
	private final AvailabilityManager availabilityManager;
	private final PricingServiceClient pricingServiceClient;

	public void populateEligibleInterviewers(final MatchInterviewersData data) throws IOException {
		this.getEligibleInterviewersProcessor.populateEligibleInterviewers(data);
		if (!this.expertCostForSchedulingFeatureToggleManager.isFeatureToggleOn(data.getInterviewId())) {
			return;
		}
		if (data.getInterviewersId().size() == 0) {
			log.info("no eligible experts found for interview id: {} at margin: {}, reducing the margin to : {}",
					data.getInterviewId(),
					data.getBarRaiserUsedMarginPercentage(), data.getBarRaiserUsedMarginPercentage() / 2);
			data.setIsFallbackEnabled(Boolean.TRUE);
			data.setBarRaiserUsedMarginPercentage(data.getBarRaiserUsedMarginPercentage() / 2);
			this.getEligibleInterviewersProcessor.populateEligibleInterviewers(data);
			log.info(
					"after reducing margin for interview id: {} , evaluation id: {} , partner id : {} , job role id : {} , interview structure id : {} , round type : {}  to : {} are min required interviewers coming : {}",
					data.getInterviewId(), data.getInterview().getEvaluationId(), data.getPartnerCompanyId(),
					data.getJobRoleId(), data.getInterviewStructureId(), data.getInterviewRound(),
					data.getBarRaiserUsedMarginPercentage(),
					data.getInterviewersId().size() > 0);
		}
	}

	public void populateEligibleInterviewersForOverbooking(final MatchInterviewersData data) throws IOException {
		final ScheduledInterviewCostDetailDTO scheduledInterviewCostDetailDTO = this.pricingServiceClient
				.getScheduledInterviewCostDetails(data.getInterviewId(), data.getRescheduleCount()).getBody();
		log.info(
				"Fetching eligible experts for overbooking for interview : {} , with configured margin : {}, used margin : {}",
				data.getInterviewId(), scheduledInterviewCostDetailDTO.getConfiguredMargin(),
				scheduledInterviewCostDetailDTO.getUsedMargin());

		data.setIsFallbackEnabled(this.isFallbackCase(scheduledInterviewCostDetailDTO.getUsedMargin(),
				scheduledInterviewCostDetailDTO.getConfiguredMargin()));

		data.setBarRaiserConfiguredMarginPercentage(scheduledInterviewCostDetailDTO.getConfiguredMargin());
		data.setBarRaiserUsedMarginPercentage(scheduledInterviewCostDetailDTO.getUsedMargin());
		data.setInterviewCost(scheduledInterviewCostDetailDTO.getInterviewCost());

		this.getEligibleInterviewersProcessor.populateEligibleInterviewers(data);
	}

	private Boolean isFallbackCase(final Double usedMargin, final Double configuredMargin) {
		return usedMargin < configuredMargin;
	}

	public void popluateEligibleInterviewersBasedOnAvailability(final MatchInterviewersData data,
			final Long availabilityStartDate, final Long availabilityEndDate) throws IOException {
		this.getEligibleInterviewersProcessor.populateEligibleInterviewers(data);
		if (!this.expertCostForSchedulingFeatureToggleManager.isFeatureToggleOn(data.getInterviewId())) {
			return;
		}
		final List<String> eligibleExpertIds = data.getInterviewersId();
		final List<String> availableEligibleExpertIds = this.availabilityManager.getAllAvailableUsers(eligibleExpertIds,
				availabilityStartDate, availabilityEndDate);
		if (availableEligibleExpertIds.size() == 0) {
			log.info(
					"no eligible available experts found for interview id: {} at margin: {}, reducing the margin to : {}",
					data.getInterviewId(),
					data.getBarRaiserUsedMarginPercentage(), data.getBarRaiserUsedMarginPercentage() / 2);
			data.setIsFallbackEnabled(Boolean.TRUE);
			data.setBarRaiserUsedMarginPercentage(data.getBarRaiserUsedMarginPercentage() / 2);
			this.getEligibleInterviewersProcessor.populateEligibleInterviewers(data);
			log.info(
					"after reducing margin for interview id: {} , evaluation id: {} , partner id : {} , job role id : {} , interview structure id : {} , round type : {}  to : {} are min required interviewers coming : {}",
					data.getInterviewId(), data.getInterview().getEvaluationId(), data.getPartnerCompanyId(),
					data.getJobRoleId(), data.getInterviewStructureId(), data.getInterviewRound(),
					data.getBarRaiserUsedMarginPercentage(),
					data.getInterviewersId().size() > 0);
		}
	}
}
