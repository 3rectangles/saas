/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.pricing;

import com.barraiser.common.DTO.pricing.AddPricingConfigResult;
import com.barraiser.common.enums.PricingType;
import com.barraiser.common.graphql.types.ValidationResult;
import com.barraiser.pricing.pojo.PartnerPricingInputData;
import com.barraiser.pricing.validators.AddPartnerPricingInputValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class AddPartnerPricingService {
	private final AddPartnerPricingInputValidator addPartnerPricingInputValidator;
	private final ContractualPricingService contractualPricingService;
	private final WorkExperienceBasedPricingService workExperienceBasedPricingService;

	@Transactional
	public AddPricingConfigResult add(final String partnerId,
			final List<PartnerPricingInputData> partnerPricingInputDataList, final String createdBy) {
		final ValidationResult validationResult = this.addPartnerPricingInputValidator
				.validate(partnerPricingInputDataList);
		if (validationResult.getFieldErrors().size() > 0) {
			return AddPricingConfigResult.builder().validationResult(validationResult).build();
		}
		this.contractualPricingService.addContractualPricingConfig(partnerId, partnerPricingInputDataList, createdBy);
		this.workExperienceBasedPricingService.addWorkExperienceBasedPricing(partnerId,
				partnerPricingInputDataList.stream()
						.filter(x -> PricingType.WORK_EXPERIENCE_BASED.equals(x.getPricingType()))
						.collect(Collectors.toList()),
				createdBy);
		return AddPricingConfigResult.builder().build();
	}
}
