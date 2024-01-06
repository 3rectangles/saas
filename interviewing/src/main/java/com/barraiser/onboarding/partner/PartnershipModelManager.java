/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.partner;

import com.barraiser.onboarding.dal.PartnerCompanyRepository;
import com.barraiser.onboarding.dal.PartnershipModelRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Component
public class PartnershipModelManager {

	private final PartnerCompanyRepository partnerCompanyRepository;
	private final PartnershipModelRepository partnershipModelRepository;

	public List<String> getAllEnabledFeatures(final String partnerId) {
		final String partnershipModel = this.partnerCompanyRepository.findById(partnerId).get().getPartnershipModelId();
		final List<String> enabledFeatures = this.partnershipModelRepository.findById(partnershipModel).get()
				.getEnabledFeatures();
		return enabledFeatures != null ? enabledFeatures : List.of();
	}
}
