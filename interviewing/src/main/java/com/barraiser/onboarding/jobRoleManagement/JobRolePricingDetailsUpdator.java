/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.jobRoleManagement;

import com.barraiser.common.DTO.pricing.JobRoleBasedPricingUpdationRequestDTO;
import com.barraiser.common.DTO.pricing.JobRoleBasedPricingUpdationResult;
import com.barraiser.common.dal.Money;
import com.barraiser.common.graphql.input.InterviewStructureInput;
import com.barraiser.common.graphql.types.JobRoleBasedPricing;
import com.barraiser.onboarding.interview.PartnerConfigManager;
import com.barraiser.onboarding.partner.partnerPricing.PricingServiceClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class JobRolePricingDetailsUpdator {
	private final PricingServiceClient pricingServiceClient;
	private final PartnerConfigManager partnerConfigManager;
	private final ObjectMapper objectMapper;

	public JobRoleBasedPricingUpdationResult update(final List<JobRoleBasedPricing> jobRoleBasedPricings,
			final String userId) {
		final List<JobRoleBasedPricing> jobRoleBasedPricingList = jobRoleBasedPricings.stream()
				.map(x -> x.toBuilder()
						.partnerId(this.partnerConfigManager.getPartnerCompanyForJobRole(x.getJobRoleId()).getId())
						.build())
				.collect(Collectors.toList());
		return this.updateJobRoleBasedPricing(jobRoleBasedPricingList, userId);
	}

	public JobRoleBasedPricingUpdationResult update(final String jobRoleId,
			final List<InterviewStructureInput> interviewStructures, final String companyId, final String createdBy) {
		final List<JobRoleBasedPricing> jobRoleBasedPricings = interviewStructures.stream()
				.filter(x -> x.getMargin() != null || x.getPrice() != null)
				.map(x -> JobRoleBasedPricing.builder()
						.jobRoleId(jobRoleId)
						.interviewStructureId(x.getId())
						.price(x.getPrice())
						.margin(x.getMargin())
						.partnerId(this.partnerConfigManager.getPartnerIdFromCompanyId(companyId))
						.build())
				.collect(Collectors.toList());
		return this.updateJobRoleBasedPricing(jobRoleBasedPricings, createdBy);
	}

	private JobRoleBasedPricingUpdationResult updateJobRoleBasedPricing(
			final List<JobRoleBasedPricing> jobRoleBasedPricings, final String createdBy) {
		final JobRoleBasedPricingUpdationRequestDTO jobRoleBasedPricingUpdationRequestDTO = JobRoleBasedPricingUpdationRequestDTO
				.builder()
				.jobRoleBasedPricingList(jobRoleBasedPricings)
				.createdBy(createdBy)
				.build();
		return this.pricingServiceClient.updateJobRoleBasedPricing(jobRoleBasedPricingUpdationRequestDTO);
	}
}
