/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.pricing;

import com.barraiser.common.dal.Money;
import com.barraiser.pricing.dal.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ContractualPricingServiceTest {
	@InjectMocks
	private ContractualPricingService contractualPricingService;
	@Mock
	private ContractualPricingConfigRepository contractualPricingConfigRepository;

	@Test
	public void shouldReturnCurrentActiveContractualPricing() {
		when(this.contractualPricingConfigRepository.findAllByPartnerIdInOrderByCreatedOnDesc(Arrays.asList("p1")))
				.thenReturn(
						List.of(
								ContractualPricingConfigDAO.builder()
										.price(Money.builder().value(1000D).currency("INR").build())
										.partnerId("p1")
										.applicableFrom(null).applicableTill(Instant.ofEpochSecond(1625486895)).build(),
								ContractualPricingConfigDAO.builder()
										.price(Money.builder().value(2000D).currency("USD").build())
										.partnerId("p1")
										.applicableFrom(Instant.ofEpochSecond(1625486895))
										.applicableTill(Instant.ofEpochSecond(1688558895)).build(),
								ContractualPricingConfigDAO.builder()
										.price(Money.builder().value(3000D).currency("INR").build())
										.applicableFrom(Instant.ofEpochSecond(1688558895)).applicableTill(null)
										.partnerId("p1")
										.build()));
		final ContractualPricingConfigDAO contractualPricingConfigDAO = this.contractualPricingService
				.getActiveContractualPricing("p1");
		assertEquals((Double) 2000D, contractualPricingConfigDAO.getPrice().getValue());
		assertEquals("USD", contractualPricingConfigDAO.getPrice().getCurrency());
	}

	@Test
	public void shouldReturnNullIfNoActiveContractualPricing() {
		when(this.contractualPricingConfigRepository.findAllByPartnerIdInOrderByCreatedOnDesc(Arrays.asList("p1")))
				.thenReturn(
						List.of(
								ContractualPricingConfigDAO.builder()
										.price(Money.builder().value(1000D).currency("INR").build())
										.partnerId("p1")
										.applicableFrom(null).applicableTill(Instant.ofEpochSecond(1625486895)).build(),
								ContractualPricingConfigDAO.builder()
										.price(Money.builder().value(3000D).currency("INR").build())
										.applicableFrom(Instant.ofEpochSecond(1688558895)).applicableTill(null)
										.partnerId("p1")
										.build()));
		final ContractualPricingConfigDAO contractualPricingConfigDAO = this.contractualPricingService
				.getActiveContractualPricing("p1");
		assertEquals(null, contractualPricingConfigDAO);
	}

	@Test
	public void shouldReturnNullIfNoContractualPricing() {
		when(this.contractualPricingConfigRepository.findAllByPartnerIdInOrderByCreatedOnDesc(Arrays.asList("p1")))
				.thenReturn(
						List.of());
		final ContractualPricingConfigDAO contractualPricingConfigDAO = this.contractualPricingService
				.getActiveContractualPricing("p1");
		assertEquals(null, contractualPricingConfigDAO);
	}

}
