/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.pricing.validators;

import com.barraiser.common.enums.PricingType;
import com.barraiser.common.graphql.types.FieldValidationResult;
import com.barraiser.common.graphql.types.JobRoleBasedPricing;
import com.barraiser.common.graphql.types.ValidationResult;
import com.barraiser.pricing.dal.ContractualPricingConfigDAO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@AllArgsConstructor
public class JobRoleBasedPricingUpdationValidator {
	public static final String ERROR_MESSAGE_FOR_OVERLAPPING_JOB_ROLE_BASED_PRICING = "either job role id or interview structure id or both are overlapping in input";
	public static final String FIELD_TAG = "JobRoleBasedPricing";
	public static final String ERROR_MESSAGE_FOR_NO_ACTIVE_PRICING = "no active pricing present";

	private final PricingDetailsValidator pricingDetailsValidator;

	public ValidationResult validate(final List<JobRoleBasedPricing> jobRoleBasedPricingList,
			final Map<String, ContractualPricingConfigDAO> partnerToActivePricingMapping) {
		final ValidationResult validationResult = new ValidationResult();
		final List<FieldValidationResult> errors = new ArrayList<>();
		for (final JobRoleBasedPricing jobRoleBasedPricing : jobRoleBasedPricingList) {
			if (partnerToActivePricingMapping.containsKey(jobRoleBasedPricing.getPartnerId())) {
				if (PricingType.JOB_ROLE_BASED.equals(
						partnerToActivePricingMapping.get(jobRoleBasedPricing.getPartnerId()).getPricingType())) {
					errors.addAll(this.pricingDetailsValidator.validate(jobRoleBasedPricing.getPrice()));
				}
				errors.addAll(this.validateMargin(jobRoleBasedPricing));
			} else {
				errors.add(FieldValidationResult.builder()
						.message(ERROR_MESSAGE_FOR_NO_ACTIVE_PRICING)
						.fieldTag(FIELD_TAG)
						.build());
			}
		}
		errors.addAll(this.validateOverlappingJobRoleBasedPricing(jobRoleBasedPricingList));
		validationResult.setFieldErrors(errors);
		return validationResult;
	}

	private List<FieldValidationResult> validateOverlappingJobRoleBasedPricing(
			final List<JobRoleBasedPricing> jobRoleBasedPricingList) {
		final List<FieldValidationResult> errors = new ArrayList<>();
		for (int i = 0; i < jobRoleBasedPricingList.size(); i++) {
			for (int j = i + 1; j < jobRoleBasedPricingList.size(); j++) {
				final JobRoleBasedPricing jobRoleBasedPricing1 = jobRoleBasedPricingList.get(i);
				final JobRoleBasedPricing jobRoleBasedPricing2 = jobRoleBasedPricingList.get(j);
				if (jobRoleBasedPricing1.getJobRoleId().equals(jobRoleBasedPricing2.getJobRoleId()) &&
						jobRoleBasedPricing1.getInterviewStructureId()
								.equals(jobRoleBasedPricing2.getInterviewStructureId())) {
					errors.add(FieldValidationResult.builder()
							.message(ERROR_MESSAGE_FOR_OVERLAPPING_JOB_ROLE_BASED_PRICING)
							.fieldTag(FIELD_TAG)
							.build());
				}
			}
		}
		return errors;
	}

	private List<FieldValidationResult> validateMargin(
			final JobRoleBasedPricing jobRoleBasedPricing) {
		final List<FieldValidationResult> errors = new ArrayList<>();
		if (jobRoleBasedPricing.getMargin() == null || jobRoleBasedPricing.getMargin() < 0) {
			errors.add(FieldValidationResult.builder()
					.message("margin should be present")
					.fieldTag("Margin")
					.build());
		}
		return errors;
	}
}
