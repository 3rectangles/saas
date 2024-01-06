/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.pricing.validators;

import com.barraiser.common.graphql.types.FieldValidationResult;
import com.barraiser.pricing.pojo.PartnerPricingInputData;

import java.util.List;

public interface PartnerPricingInputValidator {
	List<FieldValidationResult> validate(final List<PartnerPricingInputData> input);
}
