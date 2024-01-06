/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.pricing.validators;

import com.barraiser.common.enums.PricingType;
import com.barraiser.common.graphql.types.FieldValidationResult;
import com.barraiser.pricing.pojo.PartnerPricingInputData;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class FlatRateBasedPricingInputValidator implements PartnerPricingInputValidator {
	public static final String ERROR_MESSAGE_FOR_FLAT_RATE_BASED_PRICING_NOT_PRESENT = "flat rate based pricing object cannot be null";
	public static final String FIELD_TAG = "FlatRateBasedPricing";
	public static final String ERROR_MESSAGE_FOR_FLAT_RATE_BASED_PRICING_IS_PRESENT = "flat rate based pricing object cannot be present if pricing type is not Flat Rate based";

	private final PricingDetailsValidator pricingDetailsValidator;

	@Override
	public List<FieldValidationResult> validate(List<PartnerPricingInputData> input) {
		return this.validateFlatRateBasedPricing(input);
	}

	private List<FieldValidationResult> validateFlatRateBasedPricing(
			final List<PartnerPricingInputData> partnerPricingInputDataList) {
		final ArrayList<FieldValidationResult> errors = new ArrayList<>();
		for (final PartnerPricingInputData input : partnerPricingInputDataList) {
			if (PricingType.FLAT_RATE_BASED.equals(input.getPricingType())) {
				if (input.getFlatRateBasedPricing() == null) {
					errors.add(FieldValidationResult.builder()
							.fieldTag(FIELD_TAG)
							.message(ERROR_MESSAGE_FOR_FLAT_RATE_BASED_PRICING_NOT_PRESENT)
							.build());
				} else {
					errors.addAll(this.pricingDetailsValidator.validate(input.getFlatRateBasedPricing().getPrice()));
				}
			} else {
				if (input.getFlatRateBasedPricing() != null) {
					errors.add(FieldValidationResult.builder()
							.fieldTag(FIELD_TAG)
							.message(ERROR_MESSAGE_FOR_FLAT_RATE_BASED_PRICING_IS_PRESENT)
							.build());
				}
			}
		}
		return errors;
	}
}
