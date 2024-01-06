/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.pricing;

import com.barraiser.common.enums.PricingType;
import com.barraiser.pricing.dal.*;
import com.barraiser.pricing.pojo.PartnerPricingInputData;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ContractualPricingService {
	private final ContractualPricingConfigRepository contractualPricingConfigRepository;

	public ContractualPricingConfigDAO getActiveContractualPricing(final String partnerId) {
		final List<ContractualPricingConfigDAO> contractualPricingConfigDAOs = this
				.getActiveContractualPricingForPartners(Arrays.asList(partnerId));
		return contractualPricingConfigDAOs.stream().findFirst()
				.orElse(null);
	}

	public List<ContractualPricingConfigDAO> getActiveContractualPricingForPartners(final List<String> partnerIds) {
		final List<ContractualPricingConfigDAO> contractualPricingConfigDAOs = this.contractualPricingConfigRepository
				.findAllByPartnerIdInOrderByCreatedOnDesc(partnerIds);

		final List<ContractualPricingConfigDAO> applicableContractualPricingForPartners = contractualPricingConfigDAOs
				.stream()
				.filter(x -> PricingUtils.isCurrentlyActive(x.getApplicableFrom(), x.getApplicableTill()))
				.collect(Collectors.toList());

		final List<ContractualPricingConfigDAO> contractualPricingConfigDAOS = new ArrayList<>();
		for (final String partnerId : partnerIds) {
			final Optional<ContractualPricingConfigDAO> contractualPricingConfigDAO = applicableContractualPricingForPartners
					.stream()
					.filter(x -> x.getPartnerId().equals(partnerId)).findFirst();
			contractualPricingConfigDAO.ifPresent(contractualPricingConfigDAOS::add);
		}
		return contractualPricingConfigDAOS;
	}

	public void addContractualPricingConfig(final String partnerId,
			final List<PartnerPricingInputData> partnerPricingInputDataList, final String createdBy) {
		final List<ContractualPricingConfigDAO> contractualPricingConfigDAOs = new ArrayList<>();
		for (final PartnerPricingInputData partnerPricingInputData : partnerPricingInputDataList) {
			contractualPricingConfigDAOs.add(
					ContractualPricingConfigDAO.builder()
							.id(UUID.randomUUID().toString())
							.partnerId(partnerId)
							.pricingType(partnerPricingInputData.getPricingType())
							.defaultMargin(partnerPricingInputData.getDefaultMargin())
							.price(PricingType.FLAT_RATE_BASED.equals(partnerPricingInputData.getPricingType())
									? partnerPricingInputData.getFlatRateBasedPricing().getPrice()
									: null)
							.applicableFrom(partnerPricingInputData.getApplicableFrom() == null ? null
									: Instant.ofEpochSecond(partnerPricingInputData.getApplicableFrom()))
							.applicableTill(partnerPricingInputData.getApplicableTill() == null ? null
									: Instant.ofEpochSecond(partnerPricingInputData.getApplicableTill()))
							.shouldBeConsideredForBilling(
									partnerPricingInputData.getShouldPricingBeConsideredInBilling())
							.createdBy(createdBy)
							.build());
		}
		this.contractualPricingConfigRepository.saveAll(contractualPricingConfigDAOs);
	}

	public Map<String, ContractualPricingConfigDAO> getActivePricingForPartners(final List<String> partnerIds) {
		final Specification<ContractualPricingConfigDAO> specification = Specification
				.where(ContractualPricingSpecifications.isActive());
		final List<ContractualPricingConfigDAO> contractualPricingConfigDAOs = this.contractualPricingConfigRepository
				.findAll(specification)
				.stream()
				.sorted(Comparator.comparing(ContractualPricingConfigDAO::getCreatedOn).reversed())
				.collect(Collectors.toList());

		final Map<String, ContractualPricingConfigDAO> partnerPricingMapping = new HashMap<>();
		for (final String partnerId : partnerIds) {
			final Optional<ContractualPricingConfigDAO> contractualPricingConfigDAO = contractualPricingConfigDAOs
					.stream().filter(x -> partnerId.equals(x.getPartnerId())).findFirst();
			contractualPricingConfigDAO.ifPresent(pricingConfigDAO -> partnerPricingMapping.put(partnerId,
					pricingConfigDAO));
		}
		return partnerPricingMapping;
	}
}
