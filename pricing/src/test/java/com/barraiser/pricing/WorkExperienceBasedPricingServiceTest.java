/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.pricing;

import com.barraiser.common.dal.Money;
import com.barraiser.common.enums.RoundType;
import com.barraiser.pricing.dal.WorkExperienceBasedPricingDAO;
import com.barraiser.pricing.dal.WorkExperienceBasedPricingRepository;
import com.barraiser.pricing.pojo.InterviewPriceData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class WorkExperienceBasedPricingServiceTest {
	@InjectMocks
	private WorkExperienceBasedPricingService workExperienceBasedPricingService;
	@Mock
	private WorkExperienceBasedPricingRepository workExperienceBasedPricingRepository;
	@Mock
	private ObjectMapper objectMapper;

	@Test
	public void shouldReturnPricingBasedOnWorkExperience() throws JsonProcessingException {
		when(this.workExperienceBasedPricingRepository
				.findAllByPartnerIdAndRoundTypeAndWorkExperienceLowerBoundLessThanEqualOrderByCreatedOnDesc(
						"p1",
						RoundType.EXPERT,
						25))
								.thenReturn(
										List.of(
												WorkExperienceBasedPricingDAO.builder()
														.price(Money.builder().value(1000D).currency("INR").build())
														.applicableFrom(null)
														.applicableTill(Instant.ofEpochSecond(1625486895)).build(),
												WorkExperienceBasedPricingDAO.builder()
														.price(Money.builder().value(2000D).currency("USD").build())
														.applicableFrom(Instant.ofEpochSecond(1625486895))
														.applicableTill(Instant.ofEpochSecond(1688558895)).build(),
												WorkExperienceBasedPricingDAO.builder()
														.price(Money.builder().value(3000D).currency("INR").build())
														.applicableFrom(Instant.ofEpochSecond(1688558895))
														.applicableTill(null).build()));
		final InterviewPriceData interviewPriceData = this.workExperienceBasedPricingService
				.getInterviewPriceBasedOnWorkExperience("p1", 25, RoundType.EXPERT);
		assertEquals((Double) 2000D, interviewPriceData.getMaximumInterviewPrice().getValue());
		assertEquals("USD", interviewPriceData.getMaximumInterviewPrice().getCurrency());
	}

	@Test
	public void shouldReturnNullIfNoActiveWorkExperienceBasedPricingPresent() throws JsonProcessingException {
		when(this.workExperienceBasedPricingRepository
				.findAllByPartnerIdAndRoundTypeAndWorkExperienceLowerBoundLessThanEqualOrderByCreatedOnDesc(
						"p1",
						RoundType.EXPERT,
						25))
								.thenReturn(
										List.of(
												WorkExperienceBasedPricingDAO.builder()
														.price(Money.builder().value(1000D).currency("INR").build())
														.applicableFrom(null)
														.applicableTill(Instant.ofEpochSecond(1625486895)).build(),
												WorkExperienceBasedPricingDAO.builder()
														.price(Money.builder().value(3000D).currency("INR").build())
														.applicableFrom(Instant.ofEpochSecond(1688558895))
														.applicableTill(null).build()));
		final InterviewPriceData interviewPriceData = this.workExperienceBasedPricingService
				.getInterviewPriceBasedOnWorkExperience("p1", 25, RoundType.EXPERT);
		assertEquals(null, interviewPriceData);
	}

	@Test
	public void shouldReturnNullIfNoWorkExperienceBasedPricingPresent() throws JsonProcessingException {
		when(this.workExperienceBasedPricingRepository
				.findAllByPartnerIdAndRoundTypeAndWorkExperienceLowerBoundLessThanEqualOrderByCreatedOnDesc(
						"p1",
						RoundType.EXPERT,
						25))
								.thenReturn(
										List.of());
		final InterviewPriceData interviewPriceData = this.workExperienceBasedPricingService
				.getInterviewPriceBasedOnWorkExperience("p1", 25, RoundType.EXPERT);
		assertEquals(null, interviewPriceData);
	}

	// work experience upper bound
	@Test
	public void shouldReturnActiveWorkExperienceBasedPricingWhenWorkExperienceUpperBoundIsNull()
			throws JsonProcessingException {
		when(this.workExperienceBasedPricingRepository
				.findAllByPartnerIdAndRoundTypeAndWorkExperienceLowerBoundLessThanEqualOrderByCreatedOnDesc(
						"p1",
						RoundType.EXPERT,
						25))
								.thenReturn(
										List.of(
												WorkExperienceBasedPricingDAO.builder()
														.price(Money.builder().value(1000D).currency("INR").build())
														.workExperienceLowerBound(12)
														.workExperienceUpperBound(25)
														.applicableFrom(null)
														.applicableTill(Instant.ofEpochSecond(1625486895)).build(),
												WorkExperienceBasedPricingDAO.builder()
														.price(Money.builder().value(2000D).currency("USD").build())
														.applicableFrom(Instant.ofEpochSecond(1625486895))
														.workExperienceLowerBound(25)
														.workExperienceUpperBound(null)
														.applicableTill(Instant.ofEpochSecond(1688558895)).build(),
												WorkExperienceBasedPricingDAO.builder()
														.price(Money.builder().value(4000D).currency("USD").build())
														.applicableFrom(Instant.ofEpochSecond(1625486895))
														.workExperienceLowerBound(10)
														.workExperienceUpperBound(12)
														.applicableTill(Instant.ofEpochSecond(1688558895)).build(),
												WorkExperienceBasedPricingDAO.builder()
														.price(Money.builder().value(3000D).currency("INR").build())
														.workExperienceLowerBound(0)
														.workExperienceUpperBound(10)
														.applicableFrom(Instant.ofEpochSecond(1688558895))
														.applicableTill(null).build()));
		final InterviewPriceData interviewPriceData = this.workExperienceBasedPricingService
				.getInterviewPriceBasedOnWorkExperience("p1", 25, RoundType.EXPERT);
		assertEquals((Double) 2000D, interviewPriceData.getMaximumInterviewPrice().getValue());
		assertEquals("USD", interviewPriceData.getMaximumInterviewPrice().getCurrency());
	}

	@Test
	public void shouldReturnActiveWorkExperienceBasedPricingWhenWorkExperienceUpperBoundIsGreater()
			throws JsonProcessingException {
		when(this.workExperienceBasedPricingRepository
				.findAllByPartnerIdAndRoundTypeAndWorkExperienceLowerBoundLessThanEqualOrderByCreatedOnDesc(
						"p1",
						RoundType.EXPERT,
						25))
								.thenReturn(
										List.of(
												WorkExperienceBasedPricingDAO.builder()
														.price(Money.builder().value(1000D).currency("INR").build())
														.workExperienceLowerBound(12)
														.workExperienceUpperBound(25)
														.applicableFrom(null)
														.applicableTill(Instant.ofEpochSecond(1625486895)).build(),
												WorkExperienceBasedPricingDAO.builder()
														.price(Money.builder().value(2000D).currency("USD").build())
														.applicableFrom(Instant.ofEpochSecond(1625486895))
														.workExperienceLowerBound(25)
														.workExperienceUpperBound(28)
														.applicableTill(Instant.ofEpochSecond(1688558895)).build(),
												WorkExperienceBasedPricingDAO.builder()
														.price(Money.builder().value(4000D).currency("USD").build())
														.applicableFrom(Instant.ofEpochSecond(1625486895))
														.workExperienceLowerBound(10)
														.workExperienceUpperBound(12)
														.applicableTill(Instant.ofEpochSecond(1688558895)).build(),
												WorkExperienceBasedPricingDAO.builder()
														.price(Money.builder().value(3000D).currency("INR").build())
														.workExperienceLowerBound(0)
														.workExperienceUpperBound(10)
														.applicableFrom(Instant.ofEpochSecond(1688558895))
														.applicableTill(null).build()));
		final InterviewPriceData interviewPriceData = this.workExperienceBasedPricingService
				.getInterviewPriceBasedOnWorkExperience("p1", 25, RoundType.EXPERT);
		assertEquals((Double) 2000D, interviewPriceData.getMaximumInterviewPrice().getValue());
		assertEquals("USD", interviewPriceData.getMaximumInterviewPrice().getCurrency());
	}

	@Test
	public void shouldReturnNullIfNoActiveWorkExperienceBasedPricingHasUpperBoundGreaterThanCandidateWorkExperience()
			throws JsonProcessingException {
		when(this.workExperienceBasedPricingRepository
				.findAllByPartnerIdAndRoundTypeAndWorkExperienceLowerBoundLessThanEqualOrderByCreatedOnDesc(
						"p1",
						RoundType.EXPERT,
						25))
								.thenReturn(
										List.of(
												WorkExperienceBasedPricingDAO.builder()
														.price(Money.builder().value(1000D).currency("INR").build())
														.workExperienceLowerBound(12)
														.workExperienceUpperBound(25)
														.applicableFrom(null)
														.applicableTill(Instant.ofEpochSecond(1625486895)).build(),
												WorkExperienceBasedPricingDAO.builder()
														.price(Money.builder().value(2000D).currency("USD").build())
														.applicableFrom(null)
														.workExperienceLowerBound(25)
														.workExperienceUpperBound(28)
														.applicableTill(Instant.ofEpochSecond(1625486895)).build(),
												WorkExperienceBasedPricingDAO.builder()
														.price(Money.builder().value(4000D).currency("USD").build())
														.applicableFrom(Instant.ofEpochSecond(1625486895))
														.workExperienceLowerBound(10)
														.workExperienceUpperBound(12)
														.applicableTill(Instant.ofEpochSecond(1688558895)).build(),
												WorkExperienceBasedPricingDAO.builder()
														.price(Money.builder().value(3000D).currency("INR").build())
														.workExperienceLowerBound(0)
														.workExperienceUpperBound(10)
														.applicableFrom(Instant.ofEpochSecond(1688558895))
														.applicableTill(null).build()));
		final InterviewPriceData interviewPriceData = this.workExperienceBasedPricingService
				.getInterviewPriceBasedOnWorkExperience("p1", 25, RoundType.EXPERT);
		assertEquals(null, interviewPriceData);
	}

	@Test
	public void shouldReturnNullIfWorkExperienceIsNotInAnyGivenRange() throws JsonProcessingException {
		when(this.workExperienceBasedPricingRepository
				.findAllByPartnerIdAndRoundTypeAndWorkExperienceLowerBoundLessThanEqualOrderByCreatedOnDesc(
						"p1",
						RoundType.EXPERT,
						25))
								.thenReturn(
										List.of(
												WorkExperienceBasedPricingDAO.builder()
														.price(Money.builder().value(1000D).currency("INR").build())
														.workExperienceLowerBound(12)
														.workExperienceUpperBound(25)
														.applicableFrom(null)
														.applicableTill(Instant.ofEpochSecond(1625486895)).build(),
												WorkExperienceBasedPricingDAO.builder()
														.price(Money.builder().value(4000D).currency("USD").build())
														.applicableFrom(Instant.ofEpochSecond(1625486895))
														.workExperienceLowerBound(10)
														.workExperienceUpperBound(12)
														.applicableTill(Instant.ofEpochSecond(1688558895)).build(),
												WorkExperienceBasedPricingDAO.builder()
														.price(Money.builder().value(3000D).currency("INR").build())
														.workExperienceLowerBound(0)
														.workExperienceUpperBound(10)
														.applicableFrom(Instant.ofEpochSecond(1688558895))
														.applicableTill(null).build()));
		final InterviewPriceData interviewPriceData = this.workExperienceBasedPricingService
				.getInterviewPriceBasedOnWorkExperience("p1", 25, RoundType.EXPERT);
		assertEquals(null, interviewPriceData);
	}
}
