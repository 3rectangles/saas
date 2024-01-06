/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.pricing.validators;

import com.barraiser.common.enums.PricingStage;
import com.barraiser.common.graphql.types.FieldValidationResult;
import com.barraiser.common.graphql.types.ValidationResult;
import com.barraiser.pricing.pojo.PartnerPricingStageData;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class UpdatePartnerPricingStageDetailsValidator {
	public static final String ERROR_MESSAGE_FOR_INVLAID_NUMBER_OF_DEMO_INTERVIEWS = "number of demo interviews should be present if stage is not contractual";
	public static final String FIELD_TAG_FOR_INVALID_NUMBER_OF_DEMO_INTERVIEWS = "NumberOfInterviewsForDemo";

	public ValidationResult validate(final PartnerPricingStageData data) {
		final ValidationResult validationResult = new ValidationResult();
		final List<FieldValidationResult> errors = new ArrayList<>();
		if (!PricingStage.CONTRACTUAL.equals(data.getPricingStage()) && data.getNumberOfInterviewsForDemo() == null) {
			errors.add(FieldValidationResult.builder()
					.message(ERROR_MESSAGE_FOR_INVLAID_NUMBER_OF_DEMO_INTERVIEWS)
					.fieldTag(FIELD_TAG_FOR_INVALID_NUMBER_OF_DEMO_INTERVIEWS)
					.build());
		}
		validationResult.setFieldErrors(errors);
		return validationResult;
	}
}
