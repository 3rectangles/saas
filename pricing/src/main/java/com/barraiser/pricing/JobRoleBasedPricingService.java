/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.pricing;

import com.barraiser.common.DTO.pricing.JobRoleBasedPricingUpdationResult;
import com.barraiser.common.enums.PricingType;
import com.barraiser.common.graphql.types.JobRoleBasedPricing;
import com.barraiser.common.graphql.types.ValidationResult;
import com.barraiser.common.DTO.pricing.JobRoleBasedPricingRequestDTO;
import com.barraiser.pricing.dal.ContractualPricingConfigDAO;
import com.barraiser.pricing.dal.JobRoleBasedPricingDAO;
import com.barraiser.pricing.dal.JobRoleBasedPricingRepository;
import com.barraiser.pricing.validators.JobRoleBasedPricingUpdationValidator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class JobRoleBasedPricingService {
	private final JobRoleBasedPricingRepository jobRoleBasedPricingRepository;
	private final JobRoleBasedPricingUpdationValidator jobRoleBasedPricingUpdationValidator;
	private final ObjectMapper objectMapper;
	private final ContractualPricingService contractualPricingService;

	public JobRoleBasedPricingDAO getActiveJobRoleBasedPricing(final String jobRoleId,
			final String interviewStructureId) {
		final List<JobRoleBasedPricingDAO> jobRoleBasedPricingDAOs = this.jobRoleBasedPricingRepository
				.findAllByJobRoleIdAndInterviewStructureIdOrderByCreatedOnDesc(
						jobRoleId, interviewStructureId);
		final List<JobRoleBasedPricingDAO> applicableJobRoleBasedPricingDAOs = jobRoleBasedPricingDAOs.stream()
				.filter(x -> PricingUtils.isCurrentlyActive(x.getApplicableFrom(), x.getApplicableTill()))
				.collect(Collectors.toList());

		return applicableJobRoleBasedPricingDAOs.stream().findFirst()
				.orElse(null);
	}

	@Transactional
	public JobRoleBasedPricingUpdationResult updateJobRoleBasedPricing(
			final List<JobRoleBasedPricing> jobRoleBasedPricings, final String createdBy) {
		final Map<String, ContractualPricingConfigDAO> partnerToActivePricingMapping = this.contractualPricingService
				.getActivePricingForPartners(
						jobRoleBasedPricings.stream().map(JobRoleBasedPricing::getPartnerId).distinct()
								.collect(Collectors.toList()));
		final ValidationResult jobRoleBasedPricingUpdationValidationResult = this.jobRoleBasedPricingUpdationValidator
				.validate(jobRoleBasedPricings, partnerToActivePricingMapping);

		if (jobRoleBasedPricingUpdationValidationResult.getFieldErrors().size() > 0) {
			return JobRoleBasedPricingUpdationResult.builder()
					.validationResult(jobRoleBasedPricingUpdationValidationResult).build();
		}
		final Instant instantOfCreation = Instant.now();
		final List<JobRoleBasedPricingDAO> updatedJobRoleBasedPricingDAOs = new ArrayList<>();
		jobRoleBasedPricings.stream().forEach(
				x -> {
					final ContractualPricingConfigDAO contractualPricingConfigDAO = partnerToActivePricingMapping
							.get(x.getPartnerId());
					updatedJobRoleBasedPricingDAOs.add(JobRoleBasedPricingDAO.builder()
							.id(UUID.randomUUID().toString())
							.jobRoleId(x.getJobRoleId())
							.interviewStructureId(x.getInterviewStructureId())
							.price(PricingType.JOB_ROLE_BASED.equals(contractualPricingConfigDAO.getPricingType())
									? x.getPrice()
									: null)
							.margin(x.getMargin())
							.applicableFrom(instantOfCreation)
							.createdBy(createdBy)
							.build());
				});
		this.jobRoleBasedPricingRepository.saveAll(updatedJobRoleBasedPricingDAOs);
		return JobRoleBasedPricingUpdationResult.builder().build();
	}

	public List<JobRoleBasedPricingDAO> getJobRoleBasedPricing(
			final List<JobRoleBasedPricingRequestDTO> jobRoleBasedPricings) {
		final List<String> jobRoleIds = jobRoleBasedPricings.stream().map(JobRoleBasedPricingRequestDTO::getJobRoleId)
				.collect(Collectors.toList());
		final List<JobRoleBasedPricingDAO> jobRoleBasedPricingDAOs = this.jobRoleBasedPricingRepository
				.findAllByJobRoleIdInOrderByCreatedOnDesc(jobRoleIds);
		final List<JobRoleBasedPricingDAO> applicableJobRoleBasedPricingDAOs = jobRoleBasedPricingDAOs.stream()
				.filter(x -> PricingUtils.isCurrentlyActive(x.getApplicableFrom(), x.getApplicableTill()))
				.collect(Collectors.toList());
		final List<JobRoleBasedPricingDAO> filteredJobRoleBasedPricingDAOs = new ArrayList<>();
		for (final JobRoleBasedPricingRequestDTO jobRoleBasedPricingData : jobRoleBasedPricings) {
			for (final String interviewStructureId : jobRoleBasedPricingData.getInterviewStructureIds()) {
				final Optional<JobRoleBasedPricingDAO> jobRoleBasedPricingDAO = applicableJobRoleBasedPricingDAOs
						.stream()
						.filter(
								x -> x.getJobRoleId().equals(jobRoleBasedPricingData.getJobRoleId())
										&& x.getInterviewStructureId()
												.equals(interviewStructureId))
						.findFirst();
				jobRoleBasedPricingDAO.ifPresent(filteredJobRoleBasedPricingDAOs::add);
			}
		}
		return filteredJobRoleBasedPricingDAOs;
	}
}
