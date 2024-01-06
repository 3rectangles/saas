/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.pricing.validators;

import com.barraiser.common.dal.Money;
import com.barraiser.common.graphql.types.FieldValidationResult;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class PricingDetailsValidator {
	public static final String ERROR_MESSAGE_FOR_PRICE_DETAILS_NOT_PRESENT = "Price should be present";
	public static final String FIELD_TAG_FOR_PRICE_DETAILS = "Price";
	public static final String ERROR_MESSAGE_FOR_AMOUNT_NOT_PRESENT = "Value should be present";
	public static final String FIELD_TAG_FOR_AMOUNT = "Value";
	public static final String ERROR_MESSAGE_FOR_CURRENCY_NOT_PRESENT = "Currency should be present";
	public static final String FIELD_TAG_FOR_CURRENCY = "Currency";

	public List<FieldValidationResult> validate(final Money price) {

		final List<FieldValidationResult> errors = new ArrayList<>();
		if (price == null) {
			errors.add(FieldValidationResult.builder()
					.fieldTag(FIELD_TAG_FOR_PRICE_DETAILS)
					.message(ERROR_MESSAGE_FOR_PRICE_DETAILS_NOT_PRESENT)
					.build());
		} else {
			if (price.getValue() == null || price.getValue() < 0) {
				errors.add(FieldValidationResult.builder()
						.fieldTag(FIELD_TAG_FOR_AMOUNT)
						.message(ERROR_MESSAGE_FOR_AMOUNT_NOT_PRESENT)
						.build());
			}
			if (price.getCurrency() == null) {
				errors.add(FieldValidationResult.builder()
						.fieldTag(FIELD_TAG_FOR_CURRENCY)
						.message(ERROR_MESSAGE_FOR_CURRENCY_NOT_PRESENT)
						.build());
			}
		}
		return errors;
	}
}
