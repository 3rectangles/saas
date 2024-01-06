/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.pricing;

import com.barraiser.common.dal.Money;
import com.barraiser.pricing.dal.JobRoleBasedPricingDAO;
import com.barraiser.pricing.dal.JobRoleBasedPricingRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Instant;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class JobRoleBasedPricingServiceTest {
	@InjectMocks
	private JobRoleBasedPricingService jobRoleBasedPricingService;
	@Mock
	private JobRoleBasedPricingRepository jobRoleBasedPricingRepository;

	@Test
	public void shouldReturnJobRoleBasedPricing() {
		when(this.jobRoleBasedPricingRepository.findAllByJobRoleIdAndInterviewStructureIdOrderByCreatedOnDesc("j1",
				"i1"))
						.thenReturn(
								List.of(
										JobRoleBasedPricingDAO.builder()
												.price(Money.builder().value(1000D).currency("INR").build()).margin(15D)
												.applicableFrom(null).applicableTill(Instant.ofEpochSecond(1625486895))
												.build(),
										JobRoleBasedPricingDAO.builder()
												.price(Money.builder().value(2000D).currency("USD").build()).margin(16D)
												.applicableFrom(Instant.ofEpochSecond(1625486895))
												.applicableTill(Instant.ofEpochSecond(1688558895)).build(),
										JobRoleBasedPricingDAO.builder()
												.price(Money.builder().value(3000D).currency("INR").build()).margin(17D)
												.applicableFrom(Instant.ofEpochSecond(1688558895)).applicableTill(null)
												.build()));
		final JobRoleBasedPricingDAO jobRoleBasedPricingDAO = this.jobRoleBasedPricingService
				.getActiveJobRoleBasedPricing("j1", "i1");
		assertEquals((Double) 2000D, jobRoleBasedPricingDAO.getPrice().getValue());
		assertEquals("USD", jobRoleBasedPricingDAO.getPrice().getCurrency());
		assertEquals((Double) 16D, jobRoleBasedPricingDAO.getMargin());
	}

	@Test
	public void shouldReturnNullIfNoActiveJobRoleBasedPricingFound() {
		when(this.jobRoleBasedPricingRepository.findAllByJobRoleIdAndInterviewStructureIdOrderByCreatedOnDesc("j1",
				"i1"))
						.thenReturn(
								List.of(
										JobRoleBasedPricingDAO.builder()
												.price(Money.builder().value(1000D).currency("INR").build()).margin(15D)
												.applicableFrom(null).applicableTill(Instant.ofEpochSecond(1625486895))
												.build(),
										JobRoleBasedPricingDAO.builder()
												.price(Money.builder().value(3000D).currency("INR").build()).margin(17D)
												.applicableFrom(Instant.ofEpochSecond(1688558895)).applicableTill(null)
												.build()));
		final JobRoleBasedPricingDAO jobRoleBasedPricingDAO = this.jobRoleBasedPricingService
				.getActiveJobRoleBasedPricing("j1", "i1");
		assertEquals(null, jobRoleBasedPricingDAO);
	}

	@Test
	public void shouldReturnNullIfNoJobRoleBasedPricingFound() {
		when(this.jobRoleBasedPricingRepository.findAllByJobRoleIdAndInterviewStructureIdOrderByCreatedOnDesc("j1",
				"i1"))
						.thenReturn(List.of());
		final JobRoleBasedPricingDAO jobRoleBasedPricingDAO = this.jobRoleBasedPricingService
				.getActiveJobRoleBasedPricing("j1", "i1");
		assertEquals(null, jobRoleBasedPricingDAO);
	}

	@Test(expected = NullPointerException.class)
	public void shouldThrowExceptionWhenFetchAndGivenJobRoleIdIsDifferent() {
		final JobRoleBasedPricingDAO jobRoleBasedPricingDAO = this.jobRoleBasedPricingService
				.getActiveJobRoleBasedPricing("j2", "i1");
		assertEquals((Double) 2000D, jobRoleBasedPricingDAO.getPrice().getValue());
	}
}
