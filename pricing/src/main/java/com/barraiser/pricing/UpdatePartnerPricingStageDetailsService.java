/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.pricing;

import com.barraiser.common.graphql.types.PartnerPricingStageUpdationResult;
import com.barraiser.common.graphql.types.ValidationResult;
import com.barraiser.pricing.dal.PartnerConfigDAO;
import com.barraiser.pricing.dal.PartnerConfigRepository;
import com.barraiser.pricing.pojo.PartnerPricingStageData;
import com.barraiser.pricing.validators.UpdatePartnerPricingStageDetailsValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.UUID;

@Component
@AllArgsConstructor
public class UpdatePartnerPricingStageDetailsService {
	private final UpdatePartnerPricingStageDetailsValidator updatePartnerPricingStageDetailsValidator;
	private final PartnerConfigRepository partnerConfigRepository;

	@Transactional
	public PartnerPricingStageUpdationResult update(final String partnerId,
			final PartnerPricingStageData partnerPricingStageData, final String createdBy) {
		final ValidationResult validationResult = this.updatePartnerPricingStageDetailsValidator
				.validate(partnerPricingStageData);
		if (validationResult.getFieldErrors().size() > 0) {
			return PartnerPricingStageUpdationResult.builder().validationResult(validationResult).build();
		}
		this.partnerConfigRepository.save(
				PartnerConfigDAO.builder()
						.id(UUID.randomUUID().toString())
						.partnerId(partnerId)
						.createdBy(createdBy)
						.stage(partnerPricingStageData.getPricingStage())
						.numberOfInterviewsForDemo(partnerPricingStageData.getNumberOfInterviewsForDemo())
						.applicableFrom(partnerPricingStageData.getApplicableFrom() == null ? null
								: Instant.ofEpochSecond(partnerPricingStageData.getApplicableFrom()))
						.applicableTill(partnerPricingStageData.getApplicableTill() == null ? null
								: Instant.ofEpochSecond(partnerPricingStageData.getApplicableTill()))
						.build());
		return PartnerPricingStageUpdationResult.builder().build();
	}
}
