/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.pricing;

import com.barraiser.common.dal.Money;
import com.barraiser.pricing.dal.InterviewCostDetailsDAO;
import com.barraiser.pricing.dal.InterviewCostDetailsRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class InterviewCostDetailsServiceTest {
	@Mock
	private CurrencyUtil currencyUtil;

	@Mock
	private InterviewCostDetailsRepository interviewCostDetailsRepository;

	@InjectMocks
	private InterviewCostDetailsService interviewCostDetailsService;

	@Test
	public void shouldReturnNullIfNoPricePresent() {
		when(this.interviewCostDetailsRepository
				.findByInterviewIdAndRescheduleCountAndExpertId("1", 0, "e1")).thenReturn(Optional.empty());
		final Money price = this.interviewCostDetailsService.getPriceToBePaidToExpert("1", 0, "e1", 60L);
		assertNull(price);
	}

	@Test
	public void shouldReturnMaxPriceOfExpert() {
		when(this.interviewCostDetailsRepository
				.findByInterviewIdAndRescheduleCountAndExpertId("1", 0, "e1")).thenReturn(Optional.of(
						InterviewCostDetailsDAO.builder()
								.interviewId("i1")
								.expertMinPricePerHour(300D)
								.interviewCost(Money.builder().value(90D).currency("SGD").build())
								.usedMargin(50D)
								.expertCostPerHour(Money.builder().value(400D).currency("INR").build())
								.build()));
		when(this.currencyUtil.convertToINR(90D, "SGD")).thenReturn(4500D);
		when(this.currencyUtil.convertToINR(400D, "INR")).thenReturn(400D);
		when(this.currencyUtil.convertToINR(300D, "INR")).thenReturn(300D);
		when(this.currencyUtil.convertINRToCurrency(4500D, "INR")).thenReturn(4500D);
		when(this.currencyUtil.convertINRToCurrency(400D, "INR")).thenReturn(400D);
		final Money price = this.interviewCostDetailsService.getPriceToBePaidToExpert("1", 0, "e1", 60L);
		assertEquals(price.getValue(), (Double) 400D);
		assertEquals(price.getCurrency(), "INR");
	}

	@Test
	public void shouldReturnCostOfInterview() {
		when(this.interviewCostDetailsRepository
				.findByInterviewIdAndRescheduleCountAndExpertId("1", 0, "e1")).thenReturn(Optional.of(
						InterviewCostDetailsDAO.builder()
								.interviewId("i1")
								.expertMinPricePerHour(300D)
								.interviewCost(Money.builder().value(350D).currency("INR").build())
								.usedMargin(0D)
								.expertCostPerHour(Money.builder().value(400D).currency("INR").build())
								.build()));
		when(this.currencyUtil.convertToINR(350D, "INR")).thenReturn(350D);
		when(this.currencyUtil.convertToINR(400D, "INR")).thenReturn(400D);
		when(this.currencyUtil.convertToINR(300D, "INR")).thenReturn(300D);
		when(this.currencyUtil.convertINRToCurrency(350D, "INR")).thenReturn(350D);
		when(this.currencyUtil.convertINRToCurrency(400D, "INR")).thenReturn(400D);
		final Money price = this.interviewCostDetailsService.getPriceToBePaidToExpert("1", 0, "e1", 60L);
		assertEquals(price.getValue(), (Double) 350D);
		assertEquals(price.getCurrency(), "INR");
	}

	@Test
	public void shouldReturnMinPriceIfUsedMarginIsLessThanConfiguredMargin() {
		when(this.interviewCostDetailsRepository
				.findByInterviewIdAndRescheduleCountAndExpertId("1", 0, "e1")).thenReturn(Optional.of(
						InterviewCostDetailsDAO.builder()
								.interviewId("i1")
								.expertMinPricePerHour(2300D)
								.interviewCost(Money.builder().value(3000D).currency("INR").build())
								.usedMargin(20D)
								.configuredMargin(40D)
								.expertCostPerHour(Money.builder().value(2600D).currency("INR").build())
								.build()));

		when(this.currencyUtil.convertToINR(2300D, "INR")).thenReturn(2300D);
		when(this.currencyUtil.convertToINR(3000D, "INR")).thenReturn(3000D);
		when(this.currencyUtil.convertToINR(2400D, "INR")).thenReturn(2400D);
		when(this.currencyUtil.convertToINR(2600D, "INR")).thenReturn(2600D);
		when(this.currencyUtil.convertToINR(1800D, "INR")).thenReturn(1800D);
		when(this.currencyUtil.convertINRToCurrency(2300D, "INR")).thenReturn(2300D);
		final Money price = this.interviewCostDetailsService.getPriceToBePaidToExpert("1", 0, "e1", 60L);
		assertEquals(price.getValue(), (Double) 2300D);
		assertEquals(price.getCurrency(), "INR");
	}

	@Test
	public void shouldReturnInterviewCostIfUsedMarginIsEqualToConfiguredMarginAndInterviewCostLessThanExpertMaxPrice() {
		when(this.interviewCostDetailsRepository
				.findByInterviewIdAndRescheduleCountAndExpertId("1", 0, "e1")).thenReturn(Optional.of(
						InterviewCostDetailsDAO.builder()
								.interviewId("i1")
								.expertMinPricePerHour(1700D)
								.interviewCost(Money.builder().value(3000D).currency("INR").build())
								.usedMargin(40D)
								.configuredMargin(40D)
								.expertCostPerHour(Money.builder().value(2600D).currency("INR").build())
								.build()));

		when(this.currencyUtil.convertToINR(1700D, "INR")).thenReturn(1700D);
		when(this.currencyUtil.convertToINR(3000D, "INR")).thenReturn(3000D);
		when(this.currencyUtil.convertToINR(2400D, "INR")).thenReturn(2400D);
		when(this.currencyUtil.convertToINR(2600D, "INR")).thenReturn(2600D);
		when(this.currencyUtil.convertToINR(1800D, "INR")).thenReturn(1800D);
		when(this.currencyUtil.convertINRToCurrency(1800D, "INR")).thenReturn(1800D);
		final Money price = this.interviewCostDetailsService.getPriceToBePaidToExpert("1", 0, "e1", 60L);
		assertEquals(price.getValue(), (Double) 1800D);
		assertEquals(price.getCurrency(), "INR");
	}

	@Test
	public void shouldReturnExpertMaxPriceIfUsedMarginIsEqualToConfiguredMarginAndInterviewCostGreaterThanExpertMaxPrice() {
		when(this.interviewCostDetailsRepository
				.findByInterviewIdAndRescheduleCountAndExpertId("1", 0, "e1")).thenReturn(Optional.of(
						InterviewCostDetailsDAO.builder()
								.interviewId("i1")
								.expertMinPricePerHour(1700D)
								.interviewCost(Money.builder().value(3000D).currency("INR").build())
								.usedMargin(40D)
								.configuredMargin(40D)
								.expertCostPerHour(Money.builder().value(1750D).currency("INR").build())
								.build()));

		when(this.currencyUtil.convertToINR(1700D, "INR")).thenReturn(1700D);
		when(this.currencyUtil.convertToINR(3000D, "INR")).thenReturn(3000D);
		when(this.currencyUtil.convertToINR(2400D, "INR")).thenReturn(2400D);
		when(this.currencyUtil.convertToINR(1750D, "INR")).thenReturn(1750D);
		when(this.currencyUtil.convertToINR(1800D, "INR")).thenReturn(1800D);
		when(this.currencyUtil.convertINRToCurrency(1750D, "INR")).thenReturn(1750D);
		final Money price = this.interviewCostDetailsService.getPriceToBePaidToExpert("1", 0, "e1", 60L);
		assertEquals(price.getValue(), (Double) 1750D);
		assertEquals(price.getCurrency(), "INR");
	}

	@Test
	public void shouldReturnInterviewCostIfConfiguredMarginIsNullAndInterviewCostIsLessThanExpertMaxPrice() {
		when(this.interviewCostDetailsRepository
				.findByInterviewIdAndRescheduleCountAndExpertId("1", 0, "e1")).thenReturn(Optional.of(
						InterviewCostDetailsDAO.builder()
								.interviewId("i1")
								.expertMinPricePerHour(1700D)
								.interviewCost(Money.builder().value(3000D).currency("INR").build())
								.usedMargin(40D)
								.expertCostPerHour(Money.builder().value(2500D).currency("INR").build())
								.build()));

		when(this.currencyUtil.convertToINR(1700D, "INR")).thenReturn(1700D);
		when(this.currencyUtil.convertToINR(3000D, "INR")).thenReturn(3000D);
		when(this.currencyUtil.convertToINR(2400D, "INR")).thenReturn(2400D);
		when(this.currencyUtil.convertToINR(2500D, "INR")).thenReturn(2500D);
		when(this.currencyUtil.convertToINR(1800D, "INR")).thenReturn(1800D);
		when(this.currencyUtil.convertINRToCurrency(1800D, "INR")).thenReturn(1800D);
		final Money price = this.interviewCostDetailsService.getPriceToBePaidToExpert("1", 0, "e1", 60L);
		assertEquals(price.getValue(), (Double) 1800D);
		assertEquals(price.getCurrency(), "INR");
	}

	@Test
	public void shouldReturnExpertMaxPriceIfConfiguredMarginIsNullAndInterviewCostIsGreaterThanExpertMaxPrice() {
		when(this.interviewCostDetailsRepository
				.findByInterviewIdAndRescheduleCountAndExpertId("1", 0, "e1")).thenReturn(Optional.of(
						InterviewCostDetailsDAO.builder()
								.interviewId("i1")
								.expertMinPricePerHour(1700D)
								.interviewCost(Money.builder().value(3000D).currency("INR").build())
								.usedMargin(40D)
								.expertCostPerHour(Money.builder().value(1750D).currency("INR").build())
								.build()));

		when(this.currencyUtil.convertToINR(1700D, "INR")).thenReturn(1700D);
		when(this.currencyUtil.convertToINR(3000D, "INR")).thenReturn(3000D);
		when(this.currencyUtil.convertToINR(2400D, "INR")).thenReturn(2400D);
		when(this.currencyUtil.convertToINR(1750D, "INR")).thenReturn(1750D);
		when(this.currencyUtil.convertToINR(1800D, "INR")).thenReturn(1800D);
		when(this.currencyUtil.convertINRToCurrency(1750D, "INR")).thenReturn(1750D);
		final Money price = this.interviewCostDetailsService.getPriceToBePaidToExpert("1", 0, "e1", 60L);
		assertEquals(price.getValue(), (Double) 1750D);
		assertEquals(price.getCurrency(), "INR");
	}
}
