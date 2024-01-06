/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.pricing.validators;

import com.barraiser.common.graphql.types.FieldValidationResult;
import com.barraiser.common.graphql.types.ValidationResult;
import com.barraiser.pricing.pojo.PartnerPricingInputData;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class AddPartnerPricingInputValidator {
	public static final String FIELD_TAG_FOR_APPLICABILITY_PERIOD = "ApplicableFrom";
	public static final String ERROR_MESSAGE_FOR_INVALID_APPLICABILITY_PERIOD = "applicable from is greater than applicable till";

	private final List<PartnerPricingInputValidator> partnerPricingUpdationValidators;

	public ValidationResult validate(final List<PartnerPricingInputData> partnerPricingInputDataList) {
		final List<FieldValidationResult> errors = new ArrayList<>();
		errors.addAll(this.validateForOverlappingApplicabilityPeriod(partnerPricingInputDataList));
		for (final PartnerPricingInputValidator partnerPricingUpdationValidator : this.partnerPricingUpdationValidators) {
			errors.addAll(partnerPricingUpdationValidator.validate(partnerPricingInputDataList));
		}
		final ValidationResult validationResult = new ValidationResult();
		validationResult.setFieldErrors(errors);
		return validationResult;
	}

	private List<FieldValidationResult> validateForOverlappingApplicabilityPeriod(
			List<PartnerPricingInputData> partnerPricingInputDataList) {
		final List<FieldValidationResult> errors = new ArrayList<>();

		partnerPricingInputDataList = partnerPricingInputDataList.stream()
				.map(x -> x.toBuilder().applicableFrom(
						x.getApplicableFrom() == null ? Integer.MIN_VALUE : x.getApplicableFrom())
						.applicableTill(x.getApplicableTill() == null ? Integer.MAX_VALUE : x.getApplicableTill())
						.build())
				.collect(Collectors.toList());
		for (int i = 0; i < partnerPricingInputDataList.size(); i++) {
			final PartnerPricingInputData partnerPricingInputData1 = partnerPricingInputDataList.get(i);
			if (partnerPricingInputData1.getApplicableFrom()
					.compareTo(partnerPricingInputData1.getApplicableTill()) > 0) {
				errors.add(FieldValidationResult.builder()
						.message(ERROR_MESSAGE_FOR_INVALID_APPLICABILITY_PERIOD)
						.fieldTag(FIELD_TAG_FOR_APPLICABILITY_PERIOD)
						.build());
			}
		}
		return errors;
	}
}
