/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.pricing.validators;

import com.barraiser.common.enums.RoundType;
import com.barraiser.common.graphql.types.FieldValidationResult;
import com.barraiser.common.graphql.types.WorkExperienceBasedPricing;
import com.barraiser.pricing.pojo.PartnerPricingInputData;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@AllArgsConstructor
public class WorkExperienceBasedInputValidator implements PartnerPricingInputValidator {
	public static final String ERROR_MESSAGE_FOR_WORK_EXPERIENCE_MISSING = "Work experience in months missing";
	public static final String FIELD_TAG_FOR_WORK_EXPERIENCE_BASED_PRICING = "WorkExperienceBasedPricing";
	public static final String ERROR_MESSAGE_FOR_INVALID_WORK_EXPERIENCE_RANGE = "lower bound greater than upper bound in work experience";
	public static final String ERROR_MESSAGE_FOR_PRICE_DETAILS_NOT_PRESENT = "Price should be present for work experience based pricing";
	public static final String FIELD_TAG_FOR_PRICE_DETAILS = "Price";
	public static final String ERROR_MESSAGE_FOR_AMOUNT_NOT_PRESENT = "Value should be present for work experience based pricing";
	public static final String FIELD_TAG_FOR_AMOUNT = "Value";
	public static final String ERROR_MESSAGE_FOR_INVALID_INTERVIEW_ROUND = "Not a valid interview round";
	public static final String FIELD_TAG_FOR_INTERVIEW_ROUND = "InterviewRound";
	public static final String ERROR_MESSAGE_FOR_OVERLAPPING_WORK_EXPERIENCE = "Overlapping work experience";

	@Override
	public List<FieldValidationResult> validate(final List<PartnerPricingInputData> input) {
		return this.validateWorkExperienceBasedPricing(input);
	}

	private List<FieldValidationResult> validateWorkExperienceBasedPricing(
			final List<PartnerPricingInputData> partnerPricingInputList) {
		final ArrayList<FieldValidationResult> errors = new ArrayList<>();

		for (final PartnerPricingInputData input : partnerPricingInputList) {
			final List<WorkExperienceBasedPricing> workExperienceBasedPricingList = this
					.getWorkExperienceBasedPricing(input);
			for (int i = 0; i < workExperienceBasedPricingList.size(); i++) {
				final WorkExperienceBasedPricing workExperienceBasedPricing = workExperienceBasedPricingList.get(i);
				final FieldValidationResult error = this
						.validateInvalidRangeForWorkExperience(workExperienceBasedPricing);
				if (error != null) {
					errors.add(error);
				}
				errors.addAll(this.validatePricingDetails(workExperienceBasedPricing));
				errors.addAll(this.validateInterviewRoundType(workExperienceBasedPricing));
				if (errors.size() == 0) {
					errors.addAll(this.validateOverlappingWorkExperienceBasedPricing(i, workExperienceBasedPricing,
							workExperienceBasedPricingList));
				}
			}
		}
		return errors;
	}

	private List<WorkExperienceBasedPricing> getWorkExperienceBasedPricing(final PartnerPricingInputData input) {
		final List<WorkExperienceBasedPricing> workExperienceBasedPricingList = new ArrayList<>();
		if (input.getWorkExperienceBasedPricing() != null) {
			for (WorkExperienceBasedPricing workExperienceBasedPricing : input.getWorkExperienceBasedPricing()) {
				if (workExperienceBasedPricing.getWorkExperienceInMonthsUpperBound() == null) {
					workExperienceBasedPricing = workExperienceBasedPricing.toBuilder()
							.workExperienceInMonthsUpperBound(Integer.MAX_VALUE).build();
					workExperienceBasedPricingList.add(workExperienceBasedPricing);
				}
			}
		}
		return workExperienceBasedPricingList;
	}

	private FieldValidationResult validateInvalidRangeForWorkExperience(
			final WorkExperienceBasedPricing workExperienceBasedPricing) {
		if (workExperienceBasedPricing.getWorkExperienceInMonthsLowerBound() == null
				|| workExperienceBasedPricing.getWorkExperienceInMonthsUpperBound() == null) {
			return FieldValidationResult.builder()
					.fieldTag(FIELD_TAG_FOR_WORK_EXPERIENCE_BASED_PRICING)
					.message(ERROR_MESSAGE_FOR_WORK_EXPERIENCE_MISSING)
					.build();
		}

		if (workExperienceBasedPricing.getWorkExperienceInMonthsLowerBound() >= workExperienceBasedPricing
				.getWorkExperienceInMonthsUpperBound()) {
			return FieldValidationResult.builder()
					.fieldTag(FIELD_TAG_FOR_WORK_EXPERIENCE_BASED_PRICING)
					.message(ERROR_MESSAGE_FOR_INVALID_WORK_EXPERIENCE_RANGE)
					.build();
		}
		return null;
	}

	private List<FieldValidationResult> validatePricingDetails(
			final WorkExperienceBasedPricing workExperienceBasedPricing) {
		final ArrayList<FieldValidationResult> errors = new ArrayList<>();
		if (workExperienceBasedPricing.getPrice() == null) {
			errors.add(FieldValidationResult.builder()
					.fieldTag(FIELD_TAG_FOR_PRICE_DETAILS)
					.message(ERROR_MESSAGE_FOR_PRICE_DETAILS_NOT_PRESENT)
					.build());
		} else if (workExperienceBasedPricing.getPrice() != null
				&& workExperienceBasedPricing.getPrice().getValue() == null) {
			errors.add(FieldValidationResult.builder()
					.fieldTag(FIELD_TAG_FOR_AMOUNT)
					.message(ERROR_MESSAGE_FOR_AMOUNT_NOT_PRESENT)
					.build());
		}
		return errors;
	}

	private List<FieldValidationResult> validateInterviewRoundType(
			final WorkExperienceBasedPricing workExperienceBasedPricing) {
		final ArrayList<FieldValidationResult> errors = new ArrayList<>();
		try {
			final RoundType roundType = workExperienceBasedPricing.getRoundType();
		} catch (final NoSuchElementException e) {
			errors.add(FieldValidationResult.builder()
					.fieldTag(FIELD_TAG_FOR_INTERVIEW_ROUND)
					.message(ERROR_MESSAGE_FOR_INVALID_INTERVIEW_ROUND)
					.build());
		}
		return errors;
	}

	private List<FieldValidationResult> validateOverlappingWorkExperienceBasedPricing(final Integer index,
			final WorkExperienceBasedPricing workExperienceBasedPricing1,
			final List<WorkExperienceBasedPricing> workExperienceBasedPricingList) {
		final ArrayList<FieldValidationResult> errors = new ArrayList<>();
		for (int j = index + 1; j < workExperienceBasedPricingList.size(); j++) {
			final WorkExperienceBasedPricing workExperienceBasedPricing2 = workExperienceBasedPricingList.get(j);
			if (this.isWorkExperienceBasedPricingOverlapping(workExperienceBasedPricing1,
					workExperienceBasedPricing2)) {
				errors.add(FieldValidationResult.builder()
						.fieldTag(FIELD_TAG_FOR_WORK_EXPERIENCE_BASED_PRICING)
						.message(ERROR_MESSAGE_FOR_OVERLAPPING_WORK_EXPERIENCE)
						.build());
			}
		}
		return errors;
	}

	private Boolean isWorkExperienceBasedPricingOverlapping(
			final WorkExperienceBasedPricing workExperienceBasedPricing1,
			final WorkExperienceBasedPricing workExperienceBasedPricing2) {
		return workExperienceBasedPricing1.getWorkExperienceInMonthsUpperBound() > workExperienceBasedPricing2
				.getWorkExperienceInMonthsLowerBound()
				&& workExperienceBasedPricing1
						.getWorkExperienceInMonthsLowerBound() < workExperienceBasedPricing2
								.getWorkExperienceInMonthsUpperBound()
				&&
				workExperienceBasedPricing1.getRoundType().equals(workExperienceBasedPricing2.getRoundType());
	}
}
