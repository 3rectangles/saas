/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.pricing;

import com.barraiser.common.enums.PricingType;
import com.barraiser.common.enums.RoundType;
import com.barraiser.common.graphql.types.WorkExperienceBasedPricing;
import com.barraiser.pricing.dal.WorkExperienceBasedPricingDAO;
import com.barraiser.pricing.dal.WorkExperienceBasedPricingRepository;
import com.barraiser.pricing.pojo.InterviewPriceData;
import com.barraiser.pricing.pojo.PartnerPricingInputData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class WorkExperienceBasedPricingService {
	private final WorkExperienceBasedPricingRepository workExperienceBasedPricingRepository;
	private final ObjectMapper objectMapper;

	private WorkExperienceBasedPricingDAO getActiveWorkExperienceBasedPricing(final String partnerId,
			final Integer workExperienceOfCandidateInMonths, final RoundType roundType) {
		final List<WorkExperienceBasedPricingDAO> workExperienceBasedPricingDAOs = this.workExperienceBasedPricingRepository
				.findAllByPartnerIdAndRoundTypeAndWorkExperienceLowerBoundLessThanEqualOrderByCreatedOnDesc(
						partnerId, roundType, workExperienceOfCandidateInMonths);
		final List<WorkExperienceBasedPricingDAO> filteredWorkExperienceBasedPricingDAOs = this
				.filterPricingBasedOnWorkExperienceUpperBound(workExperienceBasedPricingDAOs,
						workExperienceOfCandidateInMonths);
		final List<WorkExperienceBasedPricingDAO> applicableWorkExperienceBasedPricingDAOs = filteredWorkExperienceBasedPricingDAOs
				.stream()
				.filter(x -> PricingUtils.isCurrentlyActive(x.getApplicableFrom(), x.getApplicableTill()))
				.collect(Collectors.toList());
		return applicableWorkExperienceBasedPricingDAOs.stream().findFirst()
				.orElse(null);
	}

	public InterviewPriceData getInterviewPriceBasedOnWorkExperience(final String partnerId,
			final Integer workExperienceOfCandidateInMonths, final RoundType roundType) throws JsonProcessingException {
		final WorkExperienceBasedPricingDAO workExperienceBasedPricingDAO = this
				.getActiveWorkExperienceBasedPricing(partnerId, workExperienceOfCandidateInMonths, roundType);
		if (workExperienceBasedPricingDAO != null && workExperienceBasedPricingDAO.getPrice() != null) {
			return InterviewPriceData.builder()
					.maximumInterviewPrice(workExperienceBasedPricingDAO.getPrice())
					.pricingType(PricingType.WORK_EXPERIENCE_BASED)
					.pricingSpecific(this.objectMapper.writeValueAsString(workExperienceBasedPricingDAO))
					.build();
		}
		return null;
	}

	private List<WorkExperienceBasedPricingDAO> filterPricingBasedOnWorkExperienceUpperBound(
			final List<WorkExperienceBasedPricingDAO> workExperienceBasedPricingDAOs,
			final Integer workExperienceOfCanddiateInMonths) {
		return workExperienceBasedPricingDAOs.stream()
				.filter(x -> x.getWorkExperienceUpperBound() == null
						|| x.getWorkExperienceUpperBound() > workExperienceOfCanddiateInMonths)
				.collect(Collectors.toList());
	}

	public void addWorkExperienceBasedPricing(final String partnerId,
			final List<PartnerPricingInputData> partnerPricingInputDataList, final String createdBy) {
		final List<WorkExperienceBasedPricingDAO> workExperienceBasedPricingDAOs = new ArrayList<>();
		for (final PartnerPricingInputData partnerPricingInputData : partnerPricingInputDataList) {
			for (final WorkExperienceBasedPricing workExperienceBasedPricing : partnerPricingInputData
					.getWorkExperienceBasedPricing())
				workExperienceBasedPricingDAOs.add(
						WorkExperienceBasedPricingDAO.builder()
								.id(UUID.randomUUID().toString())
								.partnerId(partnerId)
								.workExperienceUpperBound(
										workExperienceBasedPricing.getWorkExperienceInMonthsUpperBound())
								.workExperienceLowerBound(
										workExperienceBasedPricing.getWorkExperienceInMonthsLowerBound())
								.applicableFrom(partnerPricingInputData.getApplicableFrom() == null ? null
										: Instant.ofEpochSecond(partnerPricingInputData.getApplicableFrom()))
								.applicableTill(partnerPricingInputData.getApplicableTill() == null ? null
										: Instant.ofEpochSecond(partnerPricingInputData.getApplicableTill()))
								.price(workExperienceBasedPricing.getPrice())
								.roundType(workExperienceBasedPricing.getRoundType())
								.createdBy(createdBy)
								.build());
		}
		this.workExperienceBasedPricingRepository.saveAll(workExperienceBasedPricingDAOs);
	}
}
