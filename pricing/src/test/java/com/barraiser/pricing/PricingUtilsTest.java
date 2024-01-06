/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.pricing;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Instant;

import static graphql.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

@RunWith(MockitoJUnitRunner.class)
public class PricingUtilsTest {

	@Test
	public void shouldReturnTrueIfBothNull() {
		assertTrue(PricingUtils.isCurrentlyActive(null, null));
	}

	@Test
	public void shouldReturnTrueIfApplicableFromIsNullAndApplicableTillIsInFuture() {
		assertTrue(PricingUtils.isCurrentlyActive(null, Instant.ofEpochSecond(2856254895L)));
	}

	@Test
	public void shouldReturnTrueIfApplicableFromIsInPastAndApplicableTillIsNull() {
		assertTrue(PricingUtils.isCurrentlyActive(Instant.ofEpochSecond(1625486895L), null));
	}

	@Test
	public void shouldReturnTrueIfApplicableFromIsInPastAndApplicableTillIsInFuture() {
		assertTrue(
				PricingUtils.isCurrentlyActive(Instant.ofEpochSecond(1625486895L), Instant.ofEpochSecond(2856254895L)));
	}

	@Test
	public void shouldReturnFalseIfApplicableFromIsInPastAndApplicableTillIsInPast() {
		assertFalse(
				PricingUtils.isCurrentlyActive(Instant.ofEpochSecond(1625486895L), Instant.ofEpochSecond(1628185095L)));
	}

	@Test
	public void shouldReturnFalseIfApplicableFromIsInFutureAndApplicableTillIsInFuture() {
		assertFalse(
				PricingUtils.isCurrentlyActive(Instant.ofEpochSecond(2827330695L), Instant.ofEpochSecond(2856254895L)));
	}

	@Test
	public void shouldReturnFalseIfApplicableFromIsNullAndApplicableTillIsInPast() {
		assertFalse(
				PricingUtils.isCurrentlyActive(null, Instant.ofEpochSecond(1625486895L)));
	}

	@Test
	public void shouldReturnFalseIfApplicableFromIsInFutureAndApplicableTillIsNull() {
		assertFalse(
				PricingUtils.isCurrentlyActive(Instant.ofEpochSecond(2827330695L), null));
	}
}
