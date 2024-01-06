/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.partner.partnerPricing;

import com.barraiser.common.DTO.pricing.PartnerPricingRequestDTO;
import com.barraiser.common.DTO.pricing.PartnerPricingResponseDTO;
import com.barraiser.common.graphql.types.PartnerPricing;
import com.barraiser.onboarding.graphql.DataLoaderFactory;
import lombok.AllArgsConstructor;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Component
@AllArgsConstructor
public class PartnerPricingDataLoaderFactory implements DataLoaderFactory<String, PartnerPricing> {
	public static final String DATA_LOADER_NAME = "partner-pricing-dataLoader";
	private static final Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	private final PricingServiceClient pricingServiceClient;

	@Override
	public String dataLoaderName() {
		return DATA_LOADER_NAME;
	}

	@Override
	public DataLoader<String, PartnerPricing> getDataLoader() {
		return DataLoader.newMappedDataLoader(
				(Set<String> partners) -> CompletableFuture.supplyAsync(
						() -> getData(partners), executor));
	}

	@Override
	public Map<String, PartnerPricing> getData(final Set<String> partnerIds) {
		final List<PartnerPricingResponseDTO> result = this.pricingServiceClient.getActivePricingForPartners(
				PartnerPricingRequestDTO.builder().partnerIds(new ArrayList<>(partnerIds)).build()).getBody();
		final Map<String, PartnerPricing> partnerPricingMapping = new HashMap<>();
		result.forEach(x -> partnerPricingMapping.put(x.getPartnerId(),
				PartnerPricing.builder().pricingType(x.getPricingType()).build()));
		return partnerPricingMapping;
	}
}
