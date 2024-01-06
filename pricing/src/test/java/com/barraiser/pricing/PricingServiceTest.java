/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.pricing;

import com.barraiser.common.dal.Money;
import com.barraiser.common.enums.PricingType;
import com.barraiser.common.enums.RoundType;
import com.barraiser.common.model.InterviewPriceResponseDTO;
import com.barraiser.pricing.dal.ContractualPricingConfigDAO;
import com.barraiser.pricing.dal.JobRoleBasedPricingDAO;
import com.barraiser.pricing.pojo.InterviewPriceData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PricingServiceTest {
	@InjectMocks
	private PricingService pricingService;
	@Spy
	private ObjectMapper objectMapper;
	@Mock
	private ContractualPricingService contractualPricingService;
	@Mock
	private JobRoleBasedPricingService jobRoleBasedPricingService;
	@Mock
	private WorkExperienceBasedPricingService workExperienceBasedPricingService;

	// should return default price and margin if no pricing present
	@Test
	public void shouldReturnDefaultScenario1() throws JsonProcessingException {
		when(this.jobRoleBasedPricingService.getActiveJobRoleBasedPricing("j1",
				"i1"))
						.thenReturn(null);
		when(this.contractualPricingService.getActiveContractualPricing("p1"))
				.thenReturn(null);
		final InterviewPriceResponseDTO interviewPriceResponseDTO = this.pricingService.getInterviewPrice("p1", "j1",
				"i1", 25, RoundType.EXPERT, 60L);
		assertEquals((Double) 3000D,
				interviewPriceResponseDTO.getMaximumInterviewPrice().getValue());
		assertEquals((Double) 40D,
				interviewPriceResponseDTO.getBarRaiserMarginPercentage());
		assertEquals("INR",
				interviewPriceResponseDTO.getMaximumInterviewPrice().getCurrency());
		assertEquals(PricingType.FLAT_RATE_BASED,
				interviewPriceResponseDTO.getPricingType());
		assertTrue(interviewPriceResponseDTO.getIsDefaultInterviewPrice());
	}

	// -----------------------FLAT RATE BASED PRICING TEST
	// CASES-------------------------------//

	// should return default price and default margin for flat rate based pricing
	// if price and margin not present
	@Test
	public void shouldReturnPriceAndMarginScenario2() throws JsonProcessingException {
		when(this.jobRoleBasedPricingService.getActiveJobRoleBasedPricing("j1",
				"i1"))
						.thenReturn(null);
		when(this.contractualPricingService.getActiveContractualPricing("p1"))
				.thenReturn(ContractualPricingConfigDAO.builder().pricingType(PricingType.FLAT_RATE_BASED)
						.build());
		final InterviewPriceResponseDTO interviewPriceResponseDTO = this.pricingService.getInterviewPrice("p1", "j1",
				"i1", 25, RoundType.EXPERT, 60L);
		assertEquals((Double) 3000D,
				interviewPriceResponseDTO.getMaximumInterviewPrice().getValue());
		assertEquals((Double) 40D,
				interviewPriceResponseDTO.getBarRaiserMarginPercentage());
		assertEquals("INR",
				interviewPriceResponseDTO.getMaximumInterviewPrice().getCurrency());
		assertEquals(PricingType.FLAT_RATE_BASED,
				interviewPriceResponseDTO.getPricingType());
		assertTrue(interviewPriceResponseDTO.getIsDefaultInterviewPrice());
	}

	// should return price and default margin for flat rate based pricing
	// if price is present and margin not present
	@Test
	public void shouldReturnPriceAndMarginScenario3() throws JsonProcessingException {
		when(this.jobRoleBasedPricingService.getActiveJobRoleBasedPricing("j1",
				"i1"))
						.thenReturn(null);
		when(this.contractualPricingService.getActiveContractualPricing("p1"))
				.thenReturn(ContractualPricingConfigDAO.builder().pricingType(PricingType.FLAT_RATE_BASED)
						.price(Money.builder().value(10000D).currency("SG").build())
						.build());
		final InterviewPriceResponseDTO interviewPriceResponseDTO = this.pricingService.getInterviewPrice("p1", "j1",
				"i1", 25, RoundType.EXPERT, 60L);
		assertEquals((Double) 10000D,
				interviewPriceResponseDTO.getMaximumInterviewPrice().getValue());
		assertEquals((Double) 40D,
				interviewPriceResponseDTO.getBarRaiserMarginPercentage());
		assertEquals("SG",
				interviewPriceResponseDTO.getMaximumInterviewPrice().getCurrency());
		assertEquals(PricingType.FLAT_RATE_BASED,
				interviewPriceResponseDTO.getPricingType());
		assertEquals(null, interviewPriceResponseDTO.getIsDefaultInterviewPrice());
	}

	// should return price and margin for flat rate based pricing
	// if both are present
	@Test
	public void shouldReturnPriceAndMarginScenario4() throws JsonProcessingException {
		when(this.jobRoleBasedPricingService.getActiveJobRoleBasedPricing("j1",
				"i1"))
						.thenReturn(null);
		when(this.contractualPricingService.getActiveContractualPricing("p1"))
				.thenReturn(ContractualPricingConfigDAO.builder().pricingType(PricingType.FLAT_RATE_BASED)
						.price(Money.builder().value(10000D).currency("SG").build())
						.defaultMargin(10D)
						.build());
		final InterviewPriceResponseDTO interviewPriceResponseDTO = this.pricingService.getInterviewPrice("p1", "j1",
				"i1", 25, RoundType.EXPERT, 60L);
		assertEquals((Double) 10000D,
				interviewPriceResponseDTO.getMaximumInterviewPrice().getValue());
		assertEquals((Double) 10D,
				interviewPriceResponseDTO.getBarRaiserMarginPercentage());
		assertEquals("SG",
				interviewPriceResponseDTO.getMaximumInterviewPrice().getCurrency());
		assertEquals(PricingType.FLAT_RATE_BASED,
				interviewPriceResponseDTO.getPricingType());
		assertEquals(null, interviewPriceResponseDTO.getIsDefaultInterviewPrice());
	}

	// should return default price and margin from flat rate based pricing
	// if price is not present but margin is present
	@Test
	public void shouldReturnPriceAndMarginScenario5() throws JsonProcessingException {
		when(this.jobRoleBasedPricingService.getActiveJobRoleBasedPricing("j1",
				"i1"))
						.thenReturn(null);
		when(this.contractualPricingService.getActiveContractualPricing("p1"))
				.thenReturn(ContractualPricingConfigDAO.builder().pricingType(PricingType.FLAT_RATE_BASED)
						.defaultMargin(10D)
						.build());
		final InterviewPriceResponseDTO interviewPriceResponseDTO = this.pricingService.getInterviewPrice("p1", "j1",
				"i1", 25, RoundType.EXPERT, 60L);
		assertEquals((Double) 3000D,
				interviewPriceResponseDTO.getMaximumInterviewPrice().getValue());
		assertEquals((Double) 10D,
				interviewPriceResponseDTO.getBarRaiserMarginPercentage());
		assertEquals("INR",
				interviewPriceResponseDTO.getMaximumInterviewPrice().getCurrency());
		assertEquals(PricingType.FLAT_RATE_BASED,
				interviewPriceResponseDTO.getPricingType());
		assertTrue(interviewPriceResponseDTO.getIsDefaultInterviewPrice());
	}

	// should return price from flat rate based pricing and margin from job role
	// based pricing
	// if margin is present in job role based pricing
	@Test
	public void shouldReturnPriceAndMarginScenario6() throws JsonProcessingException {
		when(this.jobRoleBasedPricingService.getActiveJobRoleBasedPricing("j1",
				"i1"))
						.thenReturn(JobRoleBasedPricingDAO.builder()
								.price(Money.builder().value(4000D).currency("SK").build())
								.margin(15D).build());
		when(this.contractualPricingService.getActiveContractualPricing("p1"))
				.thenReturn(ContractualPricingConfigDAO.builder().pricingType(PricingType.FLAT_RATE_BASED)
						.price(Money.builder().value(10000D).currency("SG").build())
						.defaultMargin(10D)
						.build());
		final InterviewPriceResponseDTO interviewPriceResponseDTO = this.pricingService.getInterviewPrice("p1", "j1",
				"i1", 25, RoundType.EXPERT, 60L);
		assertEquals((Double) 10000D,
				interviewPriceResponseDTO.getMaximumInterviewPrice().getValue());
		assertEquals((Double) 15D,
				interviewPriceResponseDTO.getBarRaiserMarginPercentage());
		assertEquals("SG",
				interviewPriceResponseDTO.getMaximumInterviewPrice().getCurrency());
		assertEquals(PricingType.FLAT_RATE_BASED,
				interviewPriceResponseDTO.getPricingType());
		assertEquals(null, interviewPriceResponseDTO.getIsDefaultInterviewPrice());
	}

	// -----------------------WORK EXPERIENCE BASED PRICING TEST
	// CASES-------------------------------//

	// should return default price and margin for work experience based pricing
	// if price and margin both are not present
	@Test
	public void shouldReturnPriceAndMarginScenario7() throws JsonProcessingException {
		when(this.jobRoleBasedPricingService.getActiveJobRoleBasedPricing("j1",
				"i1"))
						.thenReturn(JobRoleBasedPricingDAO.builder().build());
		when(this.contractualPricingService.getActiveContractualPricing("p1"))
				.thenReturn(ContractualPricingConfigDAO.builder().pricingType(PricingType.WORK_EXPERIENCE_BASED)
						.build());
		final InterviewPriceResponseDTO interviewPriceResponseDTO = this.pricingService.getInterviewPrice("p1", "j1",
				"i1", 25, RoundType.EXPERT, 60L);
		assertEquals((Double) 3000D,
				interviewPriceResponseDTO.getMaximumInterviewPrice().getValue());
		assertEquals((Double) 40D,
				interviewPriceResponseDTO.getBarRaiserMarginPercentage());
		assertEquals("INR",
				interviewPriceResponseDTO.getMaximumInterviewPrice().getCurrency());
		assertEquals(PricingType.FLAT_RATE_BASED,
				interviewPriceResponseDTO.getPricingType());
		assertTrue(interviewPriceResponseDTO.getIsDefaultInterviewPrice());
	}

	// should return price and default margin for work experience based pricing
	// if price is present but margin is not present
	@Test
	public void shouldReturnPriceAndMarginScenario8() throws JsonProcessingException {
		when(this.jobRoleBasedPricingService.getActiveJobRoleBasedPricing("j1",
				"i1"))
						.thenReturn(JobRoleBasedPricingDAO.builder().build());
		when(this.contractualPricingService.getActiveContractualPricing("p1"))
				.thenReturn(ContractualPricingConfigDAO.builder().pricingType(PricingType.WORK_EXPERIENCE_BASED)
						.build());
		when(this.workExperienceBasedPricingService.getInterviewPriceBasedOnWorkExperience("p1", 25, RoundType.EXPERT))
				.thenReturn(InterviewPriceData.builder()
						.maximumInterviewPrice(Money.builder().value(10000D).currency("SK").build())
						.pricingType(PricingType.WORK_EXPERIENCE_BASED)
						.build());
		final InterviewPriceResponseDTO interviewPriceResponseDTO = this.pricingService.getInterviewPrice("p1", "j1",
				"i1", 25, RoundType.EXPERT, 60L);
		assertEquals((Double) 10000D,
				interviewPriceResponseDTO.getMaximumInterviewPrice().getValue());
		assertEquals((Double) 40D,
				interviewPriceResponseDTO.getBarRaiserMarginPercentage());
		assertEquals("SK",
				interviewPriceResponseDTO.getMaximumInterviewPrice().getCurrency());
		assertEquals(PricingType.WORK_EXPERIENCE_BASED,
				interviewPriceResponseDTO.getPricingType());
		assertEquals(null, interviewPriceResponseDTO.getIsDefaultInterviewPrice());
	}

	// should return default price and margin for work experience based pricing
	// if price is not present but margin is present
	@Test
	public void shouldReturnPriceAndMarginScenario9() throws JsonProcessingException {
		when(this.jobRoleBasedPricingService.getActiveJobRoleBasedPricing("j1",
				"i1"))
						.thenReturn(JobRoleBasedPricingDAO.builder().build());
		when(this.contractualPricingService.getActiveContractualPricing("p1"))
				.thenReturn(ContractualPricingConfigDAO.builder().pricingType(PricingType.WORK_EXPERIENCE_BASED)
						.defaultMargin(25D)
						.build());
		when(this.workExperienceBasedPricingService.getInterviewPriceBasedOnWorkExperience("p1", 25, RoundType.EXPERT))
				.thenReturn(InterviewPriceData.builder()
						.pricingType(PricingType.WORK_EXPERIENCE_BASED)
						.build());
		final InterviewPriceResponseDTO interviewPriceResponseDTO = this.pricingService.getInterviewPrice("p1", "j1",
				"i1", 25, RoundType.EXPERT, 60L);
		assertEquals((Double) 3000D,
				interviewPriceResponseDTO.getMaximumInterviewPrice().getValue());
		assertEquals((Double) 25D,
				interviewPriceResponseDTO.getBarRaiserMarginPercentage());
		assertEquals("INR",
				interviewPriceResponseDTO.getMaximumInterviewPrice().getCurrency());
		assertEquals(PricingType.FLAT_RATE_BASED,
				interviewPriceResponseDTO.getPricingType());
		assertTrue(interviewPriceResponseDTO.getIsDefaultInterviewPrice());
	}

	// should return actual price and margin for work experience based pricing
	// if both are present
	@Test
	public void shouldReturnPriceAndMarginScenario10() throws JsonProcessingException {
		when(this.jobRoleBasedPricingService.getActiveJobRoleBasedPricing("j1",
				"i1"))
						.thenReturn(JobRoleBasedPricingDAO.builder().build());
		when(this.contractualPricingService.getActiveContractualPricing("p1"))
				.thenReturn(ContractualPricingConfigDAO.builder().pricingType(PricingType.WORK_EXPERIENCE_BASED)
						.defaultMargin(25D)
						.build());
		when(this.workExperienceBasedPricingService.getInterviewPriceBasedOnWorkExperience("p1", 25, RoundType.EXPERT))
				.thenReturn(InterviewPriceData.builder()
						.pricingType(PricingType.WORK_EXPERIENCE_BASED)
						.maximumInterviewPrice(Money.builder().value(10000D).currency("SK").build())
						.build());
		final InterviewPriceResponseDTO interviewPriceResponseDTO = this.pricingService.getInterviewPrice("p1", "j1",
				"i1", 25, RoundType.EXPERT, 60L);
		assertEquals((Double) 10000D,
				interviewPriceResponseDTO.getMaximumInterviewPrice().getValue());
		assertEquals((Double) 25D,
				interviewPriceResponseDTO.getBarRaiserMarginPercentage());
		assertEquals("SK",
				interviewPriceResponseDTO.getMaximumInterviewPrice().getCurrency());
		assertEquals(PricingType.WORK_EXPERIENCE_BASED,
				interviewPriceResponseDTO.getPricingType());
		assertEquals(null, interviewPriceResponseDTO.getIsDefaultInterviewPrice());
	}

	// should return price from work experience based pricing and margin from job
	// role based pricing
	// if margin is present in job role based pricing
	@Test
	public void shouldReturnPriceAndMarginScenario11() throws JsonProcessingException {
		when(this.jobRoleBasedPricingService.getActiveJobRoleBasedPricing("j1",
				"i1"))
						.thenReturn(JobRoleBasedPricingDAO.builder().margin(60D).build());
		when(this.contractualPricingService.getActiveContractualPricing("p1"))
				.thenReturn(ContractualPricingConfigDAO.builder().pricingType(PricingType.WORK_EXPERIENCE_BASED)
						.defaultMargin(25D)
						.build());
		when(this.workExperienceBasedPricingService.getInterviewPriceBasedOnWorkExperience("p1", 25, RoundType.EXPERT))
				.thenReturn(InterviewPriceData.builder()
						.pricingType(PricingType.WORK_EXPERIENCE_BASED)
						.maximumInterviewPrice(Money.builder().value(10000D).currency("SK").build())
						.build());
		final InterviewPriceResponseDTO interviewPriceResponseDTO = this.pricingService.getInterviewPrice("p1", "j1",
				"i1", 25, RoundType.EXPERT, 60L);
		assertEquals((Double) 10000D,
				interviewPriceResponseDTO.getMaximumInterviewPrice().getValue());
		assertEquals((Double) 60D,
				interviewPriceResponseDTO.getBarRaiserMarginPercentage());
		assertEquals("SK",
				interviewPriceResponseDTO.getMaximumInterviewPrice().getCurrency());
		assertEquals(PricingType.WORK_EXPERIENCE_BASED,
				interviewPriceResponseDTO.getPricingType());
		assertEquals(null, interviewPriceResponseDTO.getIsDefaultInterviewPrice());
	}

	// -----------------------JOB ROLE BASED PRICING TEST
	// CASES-------------------------------//

	// should return default price and margin for job role based pricing
	// if both are not present
	@Test
	public void shouldReturnPriceAndMarginScenario12() throws JsonProcessingException {
		when(this.jobRoleBasedPricingService.getActiveJobRoleBasedPricing("j1",
				"i1"))
						.thenReturn(JobRoleBasedPricingDAO.builder().build());
		when(this.contractualPricingService.getActiveContractualPricing("p1"))
				.thenReturn(ContractualPricingConfigDAO.builder().pricingType(PricingType.JOB_ROLE_BASED)
						.build());
		final InterviewPriceResponseDTO interviewPriceResponseDTO = this.pricingService.getInterviewPrice("p1", "j1",
				"i1", 25, RoundType.EXPERT, 60L);
		assertEquals((Double) 3000D,
				interviewPriceResponseDTO.getMaximumInterviewPrice().getValue());
		assertEquals((Double) 40D,
				interviewPriceResponseDTO.getBarRaiserMarginPercentage());
		assertEquals("INR",
				interviewPriceResponseDTO.getMaximumInterviewPrice().getCurrency());
		assertEquals(PricingType.FLAT_RATE_BASED,
				interviewPriceResponseDTO.getPricingType());
		assertTrue(interviewPriceResponseDTO.getIsDefaultInterviewPrice());
	}

	// should return price and default margin for job role based pricing
	// if price is present but margin is not present
	@Test
	public void shouldReturnPriceAndMarginScenario13() throws JsonProcessingException {
		when(this.jobRoleBasedPricingService.getActiveJobRoleBasedPricing("j1",
				"i1"))
						.thenReturn(JobRoleBasedPricingDAO.builder()
								.price(Money.builder().value(7000D).currency("SK").build()).build());
		when(this.contractualPricingService.getActiveContractualPricing("p1"))
				.thenReturn(ContractualPricingConfigDAO.builder().pricingType(PricingType.JOB_ROLE_BASED)
						.build());
		final InterviewPriceResponseDTO interviewPriceResponseDTO = this.pricingService.getInterviewPrice("p1", "j1",
				"i1", 25, RoundType.EXPERT, 60L);
		assertEquals((Double) 7000D,
				interviewPriceResponseDTO.getMaximumInterviewPrice().getValue());
		assertEquals((Double) 40D,
				interviewPriceResponseDTO.getBarRaiserMarginPercentage());
		assertEquals("SK",
				interviewPriceResponseDTO.getMaximumInterviewPrice().getCurrency());
		assertEquals(PricingType.JOB_ROLE_BASED,
				interviewPriceResponseDTO.getPricingType());
		assertEquals(null, interviewPriceResponseDTO.getIsDefaultInterviewPrice());
	}

	// should return default price but actual margin for job role based pricing
	// if price is not present but margin is present
	@Test
	public void shouldReturnPriceAndMarginScenario14() throws JsonProcessingException {
		when(this.jobRoleBasedPricingService.getActiveJobRoleBasedPricing("j1",
				"i1"))
						.thenReturn(JobRoleBasedPricingDAO.builder().margin(40D).build());
		when(this.contractualPricingService.getActiveContractualPricing("p1"))
				.thenReturn(ContractualPricingConfigDAO.builder().pricingType(PricingType.JOB_ROLE_BASED)
						.build());
		final InterviewPriceResponseDTO interviewPriceResponseDTO = this.pricingService.getInterviewPrice("p1", "j1",
				"i1", 25, RoundType.EXPERT, 60L);
		assertEquals((Double) 3000D,
				interviewPriceResponseDTO.getMaximumInterviewPrice().getValue());
		assertEquals((Double) 40D,
				interviewPriceResponseDTO.getBarRaiserMarginPercentage());
		assertEquals("INR",
				interviewPriceResponseDTO.getMaximumInterviewPrice().getCurrency());
		assertEquals(PricingType.FLAT_RATE_BASED,
				interviewPriceResponseDTO.getPricingType());
		assertTrue(interviewPriceResponseDTO.getIsDefaultInterviewPrice());
	}

	// should return actual price and margin for job role based pricing
	// if both are present
	@Test
	public void shouldReturnPriceAndMarginScenario15() throws JsonProcessingException {
		when(this.jobRoleBasedPricingService.getActiveJobRoleBasedPricing("j1",
				"i1"))
						.thenReturn(JobRoleBasedPricingDAO.builder().margin(40D)
								.price(Money.builder().value(7000D).currency("SK").build()).build());
		when(this.contractualPricingService.getActiveContractualPricing("p1"))
				.thenReturn(ContractualPricingConfigDAO.builder().pricingType(PricingType.JOB_ROLE_BASED)
						.build());
		final InterviewPriceResponseDTO interviewPriceResponseDTO = this.pricingService.getInterviewPrice("p1", "j1",
				"i1", 25, RoundType.EXPERT, 60L);
		assertEquals((Double) 7000D,
				interviewPriceResponseDTO.getMaximumInterviewPrice().getValue());
		assertEquals((Double) 40D,
				interviewPriceResponseDTO.getBarRaiserMarginPercentage());
		assertEquals("SK",
				interviewPriceResponseDTO.getMaximumInterviewPrice().getCurrency());
		assertEquals(PricingType.JOB_ROLE_BASED,
				interviewPriceResponseDTO.getPricingType());
		assertEquals(null, interviewPriceResponseDTO.getIsDefaultInterviewPrice());
	}

	// should return actual price and margin for job role based pricing
	// though default margin is present in contractual pricing config
	@Test
	public void shouldReturnPriceAndMarginScenario16() throws JsonProcessingException {
		when(this.jobRoleBasedPricingService.getActiveJobRoleBasedPricing("j1",
				"i1"))
						.thenReturn(JobRoleBasedPricingDAO.builder().margin(40D)
								.price(Money.builder().value(7000D).currency("SK").build()).build());
		when(this.contractualPricingService.getActiveContractualPricing("p1"))
				.thenReturn(ContractualPricingConfigDAO.builder().pricingType(PricingType.JOB_ROLE_BASED)
						.defaultMargin(10D)
						.build());
		final InterviewPriceResponseDTO interviewPriceResponseDTO = this.pricingService.getInterviewPrice("p1", "j1",
				"i1", 25, RoundType.EXPERT, 60L);
		assertEquals((Double) 7000D,
				interviewPriceResponseDTO.getMaximumInterviewPrice().getValue());
		assertEquals((Double) 40D,
				interviewPriceResponseDTO.getBarRaiserMarginPercentage());
		assertEquals("SK",
				interviewPriceResponseDTO.getMaximumInterviewPrice().getCurrency());
		assertEquals(PricingType.JOB_ROLE_BASED,
				interviewPriceResponseDTO.getPricingType());
		assertEquals(null, interviewPriceResponseDTO.getIsDefaultInterviewPrice());
	}

	// should return default price and margin for job role based pricing
	// if job role based pricing is null
	@Test
	public void shouldReturnPriceAndMarginScenario17() throws JsonProcessingException {
		when(this.jobRoleBasedPricingService.getActiveJobRoleBasedPricing("j1",
				"i1"))
						.thenReturn(null);
		when(this.contractualPricingService.getActiveContractualPricing("p1"))
				.thenReturn(ContractualPricingConfigDAO.builder().pricingType(PricingType.JOB_ROLE_BASED)
						.build());
		final InterviewPriceResponseDTO interviewPriceResponseDTO = this.pricingService.getInterviewPrice("p1", "j1",
				"i1", 25, RoundType.EXPERT, 60L);
		assertEquals((Double) 3000D,
				interviewPriceResponseDTO.getMaximumInterviewPrice().getValue());
		assertEquals((Double) 40D,
				interviewPriceResponseDTO.getBarRaiserMarginPercentage());
		assertEquals("INR",
				interviewPriceResponseDTO.getMaximumInterviewPrice().getCurrency());
		assertEquals(PricingType.FLAT_RATE_BASED,
				interviewPriceResponseDTO.getPricingType());
		assertTrue(interviewPriceResponseDTO.getIsDefaultInterviewPrice());
	}

	// should return default because active contractual pricing is job role based
	// but job role based is not present
	// but work experience based is present

	@Test
	public void shouldReturnActualPricing() throws JsonProcessingException {
		when(this.jobRoleBasedPricingService.getActiveJobRoleBasedPricing("j1",
				"i1"))
						.thenReturn(null);
		when(this.contractualPricingService.getActiveContractualPricing("p1"))
				.thenReturn(ContractualPricingConfigDAO.builder().pricingType(PricingType.JOB_ROLE_BASED)
						.build());
		final InterviewPriceResponseDTO interviewPriceResponseDTO = this.pricingService.getInterviewPrice("p1", "j1",
				"i1", 25, RoundType.EXPERT, 60L);
		assertEquals((Double) 3000D,
				interviewPriceResponseDTO.getMaximumInterviewPrice().getValue());
		assertEquals((Double) 40D,
				interviewPriceResponseDTO.getBarRaiserMarginPercentage());
		assertEquals("INR",
				interviewPriceResponseDTO.getMaximumInterviewPrice().getCurrency());
		assertEquals(PricingType.FLAT_RATE_BASED,
				interviewPriceResponseDTO.getPricingType());
		assertTrue(interviewPriceResponseDTO.getIsDefaultInterviewPrice());
	}

	// should return default because active contractual pricing is job role based
	// but job role based is not present
	// but work experience based is present
	// but should consider duration of interview
	@Test
	public void shouldReturnPricingForInterviewDurationGreaterThanAnHour() throws JsonProcessingException {
		when(this.jobRoleBasedPricingService.getActiveJobRoleBasedPricing("j1",
				"i1"))
						.thenReturn(null);
		when(this.contractualPricingService.getActiveContractualPricing("p1"))
				.thenReturn(ContractualPricingConfigDAO.builder().pricingType(PricingType.JOB_ROLE_BASED)
						.build());
		final InterviewPriceResponseDTO interviewPriceResponseDTO = this.pricingService.getInterviewPrice("p1", "j1",
				"i1", 25, RoundType.EXPERT, 90L);
		assertEquals((Double) 4500D,
				interviewPriceResponseDTO.getMaximumInterviewPrice().getValue());
		assertEquals((Double) 40D,
				interviewPriceResponseDTO.getBarRaiserMarginPercentage());
		assertEquals("INR",
				interviewPriceResponseDTO.getMaximumInterviewPrice().getCurrency());
		assertEquals(PricingType.FLAT_RATE_BASED,
				interviewPriceResponseDTO.getPricingType());
		assertTrue(interviewPriceResponseDTO.getIsDefaultInterviewPrice());
	}

}
