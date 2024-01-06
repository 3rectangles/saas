/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.pricing;

import com.barraiser.common.DTO.pricing.*;
import com.barraiser.common.DTO.pricing.JobRoleBasedPricingRequestDTO;
import com.barraiser.common.DTO.pricing.JobRoleBasedPricingResponseDTO;
import com.barraiser.common.DTO.pricing.PartnerPricingRequestDTO;
import com.barraiser.common.DTO.pricing.PartnerPricingResponseDTO;
import com.barraiser.common.dal.Money;
import com.barraiser.common.enums.PricingType;
import com.barraiser.common.enums.RoundType;
import com.barraiser.common.graphql.types.PartnerPricingStageUpdationResult;
import com.barraiser.common.model.ScheduledInterviewCostDetailDTO;
import com.barraiser.pricing.dal.*;
import com.barraiser.pricing.pojo.PartnerPricingInputData;
import com.barraiser.pricing.pojo.PartnerPricingStageData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.barraiser.pricing.pojo.InterviewPriceData;
import com.barraiser.common.model.InterviewPriceResponseDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class PricingService {
	public static final Long minutesInAnHour = 60L;

	private final ObjectMapper objectMapper;
	private final ContractualPricingService contractualPricingService;
	private final WorkExperienceBasedPricingService workExperienceBasedPricingService;
	private final JobRoleBasedPricingService jobRoleBasedPricingService;
	private final AddPartnerPricingService addPartnerPricingService;
	private final UpdatePartnerPricingStageDetailsService updatePartnerPricingStageDetailsService;
	private final InterviewCostDetailsService interviewCostDetailsService;

	public InterviewPriceResponseDTO getInterviewPrice(final String partnerId, final String jobRoleId,
			final String interviewStructureId,
			final Integer workExperienceOfCandidateInMonths, final RoundType roundType,
			final Long durationOfInterviewInMinutes) throws JsonProcessingException {
		final JobRoleBasedPricingDAO jobRoleBasedPricingDAO = this.jobRoleBasedPricingService
				.getActiveJobRoleBasedPricing(jobRoleId, interviewStructureId);
		InterviewPriceData interviewPriceData = this.getActiveInterviewPricing(partnerId,
				workExperienceOfCandidateInMonths,
				roundType, jobRoleBasedPricingDAO);
		interviewPriceData = interviewPriceData.getMaximumInterviewPrice() != null ? interviewPriceData
				: this.getDefaultInterviewPrice(interviewPriceData);
		final Double margin = this.getMarginForInterview(interviewPriceData.getBarRaiserMarginPercentage(),
				jobRoleBasedPricingDAO);
		interviewPriceData = this.getInterviewPriceAccordingToDuration(interviewPriceData,
				durationOfInterviewInMinutes);
		return this.constructResponseData(interviewPriceData, margin);
	}

	private InterviewPriceData getDefaultInterviewPrice(final InterviewPriceData interviewPriceData) {
		return interviewPriceData.toBuilder()
				.maximumInterviewPrice(Money.builder().value(Constants.DEFAULT_INTERVIEW_PRICE)
						.currency(Constants.DEFAULT_CURRENCY).build())
				.pricingType(PricingType.FLAT_RATE_BASED)
				.isDefaultInterviewPrice(Boolean.TRUE)
				.build();
	}

	private Double getMarginForInterview(final Double priceLevelMargin,
			final JobRoleBasedPricingDAO jobRoleBasedPricingDAO) {
		return jobRoleBasedPricingDAO != null && jobRoleBasedPricingDAO.getMargin() != null
				? jobRoleBasedPricingDAO.getMargin()
				: priceLevelMargin != null ? priceLevelMargin : Constants.DEFAULT_MARGIN;
	}

	private InterviewPriceResponseDTO constructResponseData(final InterviewPriceData interviewPriceData,
			final Double margin) {
		return this.objectMapper.convertValue(interviewPriceData, InterviewPriceResponseDTO.class).toBuilder()
				.barRaiserMarginPercentage(margin)
				.build();
	}

	private InterviewPriceData createInterviewPriceDataObject(final Money price, final PricingType pricingType,
			final String pricingSpecific,
			final ContractualPricingConfigDAO contractualPricingConfigDAO) throws JsonProcessingException {
		return InterviewPriceData.builder()
				.maximumInterviewPrice(price)
				.pricingType(pricingType)
				.contractualPricingConfig(this.objectMapper.writeValueAsString(contractualPricingConfigDAO))
				.pricingSpecific(pricingSpecific)
				.build();
	}

	private InterviewPriceData getActiveInterviewPricing(final String partnerId,
			final Integer workExperienceOfCandidateInMonths,
			final RoundType roundType,
			final JobRoleBasedPricingDAO jobRoleBasedPricingDAO) throws JsonProcessingException {
		final ContractualPricingConfigDAO contractualPricingConfigDAO = this.contractualPricingService
				.getActiveContractualPricing(partnerId);
		if (contractualPricingConfigDAO == null) {
			return InterviewPriceData.builder().build();
		}
		InterviewPriceData interviewPriceData = null;
		if (PricingType.FLAT_RATE_BASED.equals(contractualPricingConfigDAO.getPricingType())
				&& contractualPricingConfigDAO.getPrice() != null) {
			interviewPriceData = this.createInterviewPriceDataObject(contractualPricingConfigDAO.getPrice(),
					PricingType.FLAT_RATE_BASED,
					this.objectMapper.writeValueAsString(contractualPricingConfigDAO),
					contractualPricingConfigDAO);
		} else if (PricingType.WORK_EXPERIENCE_BASED.equals(contractualPricingConfigDAO.getPricingType())) {
			interviewPriceData = this.workExperienceBasedPricingService
					.getInterviewPriceBasedOnWorkExperience(partnerId, workExperienceOfCandidateInMonths,
							roundType);
			interviewPriceData = interviewPriceData != null ? interviewPriceData.toBuilder()
					.contractualPricingConfig(
							this.objectMapper.writeValueAsString(contractualPricingConfigDAO))
					.build() : null;
		} else if (PricingType.JOB_ROLE_BASED.equals(contractualPricingConfigDAO.getPricingType()) &&
				jobRoleBasedPricingDAO != null && jobRoleBasedPricingDAO.getPrice() != null) {
			interviewPriceData = this.createInterviewPriceDataObject(jobRoleBasedPricingDAO.getPrice(),
					PricingType.JOB_ROLE_BASED,
					this.objectMapper.writeValueAsString(jobRoleBasedPricingDAO),
					contractualPricingConfigDAO);
		}
		interviewPriceData = interviewPriceData == null ? InterviewPriceData.builder().build() : interviewPriceData;
		return interviewPriceData.toBuilder()
				.barRaiserMarginPercentage(contractualPricingConfigDAO.getDefaultMargin()).build();
	}

	private InterviewPriceData getInterviewPriceAccordingToDuration(final InterviewPriceData interviewPriceData,
			final Long durationOfInterviewInMinutes) {
		final Double priceOfInterviewForAnHour = interviewPriceData.getMaximumInterviewPrice().getValue();
		return interviewPriceData.toBuilder()
				.maximumInterviewPrice(interviewPriceData.getMaximumInterviewPrice().toBuilder()
						.value(priceOfInterviewForAnHour * durationOfInterviewInMinutes / minutesInAnHour).build())
				.build();
	}

	public AddPricingConfigResult addPartnerPricing(final String partnerId,
			final AddPartnerPricingConfigRequestDTO addPartnerPricingConfigRequestDTO) {
		final List<PartnerPricingInputData> partnerPricingInputDataList = addPartnerPricingConfigRequestDTO
				.getPartnerPricingInputDTOList().stream()
				.map(x -> this.objectMapper.convertValue(x, PartnerPricingInputData.class))
				.collect(Collectors.toList());
		return this.addPartnerPricingService.add(partnerId, partnerPricingInputDataList,
				addPartnerPricingConfigRequestDTO.getCreatedBy());
	}

	public JobRoleBasedPricingUpdationResult updateJobRoleBasedPricing(
			final JobRoleBasedPricingUpdationRequestDTO jobRoleBasedPricingUpdationRequestDTO) {
		return this.jobRoleBasedPricingService.updateJobRoleBasedPricing(
				jobRoleBasedPricingUpdationRequestDTO.getJobRoleBasedPricingList(),
				jobRoleBasedPricingUpdationRequestDTO.getCreatedBy());
	}

	public PartnerPricingStageUpdationResult updatePricingStageDetails(final String partnerId,
			final PartnerPricingStageDetailsRequestDTO partnerPricingStageDetailsRequestDTO) {
		final PartnerPricingStageData partnerPricingStageData = this.objectMapper
				.convertValue(partnerPricingStageDetailsRequestDTO, PartnerPricingStageData.class);
		return this.updatePartnerPricingStageDetailsService.update(partnerId, partnerPricingStageData,
				partnerPricingStageDetailsRequestDTO.getCreatedBy());
	}

	public List<JobRoleBasedPricingResponseDTO> getJobRoleBasedPricing(
			final List<JobRoleBasedPricingRequestDTO> jobRoleBasedPricingRequestDTOList) {
		final List<JobRoleBasedPricingDAO> jobRoleBasedPricingDAOs = this.jobRoleBasedPricingService
				.getJobRoleBasedPricing(jobRoleBasedPricingRequestDTOList);
		final List<JobRoleBasedPricingResponseDTO> jobRoleBasedPricingResponseDTOs = new ArrayList<>();
		for (final JobRoleBasedPricingRequestDTO jobRoleBasedPricingRequestDTO : jobRoleBasedPricingRequestDTOList) {
			final List<JobRoleBasedPricingDAO> filteredJobRoleBasedPricingDAOs = jobRoleBasedPricingDAOs.stream()
					.filter(
							x -> x.getJobRoleId().equals(jobRoleBasedPricingRequestDTO.getJobRoleId()))
					.collect(Collectors.toList());
			if (filteredJobRoleBasedPricingDAOs.size() > 0) {
				final Map<String, JobRoleBasedPricingResponseDTO.InterviewStructurePricing> pricing = new HashMap<>();
				for (final JobRoleBasedPricingDAO jobRoleBasedPricingDAO : filteredJobRoleBasedPricingDAOs) {
					pricing.put(jobRoleBasedPricingDAO.getInterviewStructureId(),
							JobRoleBasedPricingResponseDTO.InterviewStructurePricing.builder()
									.price(jobRoleBasedPricingDAO.getPrice()).margin(jobRoleBasedPricingDAO.getMargin())
									.build());
				}
				final JobRoleBasedPricingResponseDTO jobRoleBasedPricingResponseDTO = JobRoleBasedPricingResponseDTO
						.builder().jobRoleId(jobRoleBasedPricingRequestDTO.getJobRoleId())
						.pricing(pricing)
						.build();
				jobRoleBasedPricingResponseDTOs.add(jobRoleBasedPricingResponseDTO);
			}
		}
		return jobRoleBasedPricingResponseDTOs;
	}

	public List<PartnerPricingResponseDTO> getActivePricingForPartners(
			final PartnerPricingRequestDTO partnerPricingRequestDTO) {
		final Map<String, ContractualPricingConfigDAO> partnerPricingMapping = this.contractualPricingService
				.getActivePricingForPartners(partnerPricingRequestDTO.getPartnerIds());
		final List<PartnerPricingResponseDTO> partnerPricingResponseDTOs = new ArrayList<>();
		partnerPricingMapping.forEach(
				(x, y) -> partnerPricingResponseDTOs
						.add(PartnerPricingResponseDTO.builder().partnerId(x).pricingType(y.getPricingType()).build()));
		return partnerPricingResponseDTOs;
	}

	public void calculateAndStoreInterviewCostDetails(final String partnerId,
			final UpdateInterviewCostDetailsRequestDTO updateInterviewCostDetailsRequestDTO)
			throws JsonProcessingException {
		final InterviewPriceResponseDTO interviewPriceResponseDTO = updateInterviewCostDetailsRequestDTO
				.getInterviewPrice() != null
				&& updateInterviewCostDetailsRequestDTO.getUsedMargin() != null
						? InterviewPriceResponseDTO.builder()
								.maximumInterviewPrice(updateInterviewCostDetailsRequestDTO.getInterviewPrice())
								.barRaiserMarginPercentage(updateInterviewCostDetailsRequestDTO.getUsedMargin()).build()
						: this.getInterviewPrice(partnerId,
								updateInterviewCostDetailsRequestDTO.getJobRoleId(),
								updateInterviewCostDetailsRequestDTO.getInterviewStructureId(),
								updateInterviewCostDetailsRequestDTO.getWorkExperienceOfCandidateInMonths(),
								updateInterviewCostDetailsRequestDTO.getRoundType(),
								updateInterviewCostDetailsRequestDTO.getDurationOfInterview());
		this.interviewCostDetailsService.saveInterviewCostDetails(updateInterviewCostDetailsRequestDTO.getInterviewId(),
				updateInterviewCostDetailsRequestDTO.getRescheduleCount(),
				interviewPriceResponseDTO, updateInterviewCostDetailsRequestDTO.getExpertId(),
				updateInterviewCostDetailsRequestDTO.getExpertCostPerHour(),
				updateInterviewCostDetailsRequestDTO.getMinPriceOfExpertPerHour(),
				updateInterviewCostDetailsRequestDTO.getConfiguredMargin());
	}

	public Money getPriceToBePaidToExpert(final String interviewId, final Integer rescheduleCount,
			final String expertId,
			final Long durationOfInterviewInMinutes) {
		return this.interviewCostDetailsService.getPriceToBePaidToExpert(interviewId, rescheduleCount, expertId,
				durationOfInterviewInMinutes);
	}

	public ScheduledInterviewCostDetailDTO getScheduledInterviewCostDetails(final String interviewId,
			final Integer rescheduleCount) {
		return this.interviewCostDetailsService.getScheduledInterviewCostDetails(interviewId, rescheduleCount);
	}

}
