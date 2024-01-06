/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.match_interviewers;

import com.amazonaws.services.dynamodbv2.xspec.B;
import com.barraiser.common.dal.Money;
import com.barraiser.common.enums.RoundType;
import com.barraiser.common.model.InterviewPriceResponseDTO;
import com.barraiser.onboarding.common.TestingUtil;
import com.barraiser.onboarding.expert.CostUtil;
import com.barraiser.onboarding.partner.partnerPricing.PricingServiceClient;
import com.barraiser.onboarding.scheduling.match_interviewers.data.FilterExpertsBasedOnMaxCostTestData;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.ExpertCostForSchedulingFeatureToggleManager;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.InterviewerData;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.MatchInterviewersData;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.MaxCostBasedFilteringProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MaxCostBasedFilteringProcessorTest {
	@InjectMocks
	private MaxCostBasedFilteringProcessor maxCostBasedFilteringProcessor;
	@Spy
	private ObjectMapper objectMapper;
	@InjectMocks
	private TestingUtil testingUtil;
	@Mock
	private PricingServiceClient pricingServiceClient;
	@Mock
	private CostUtil costUtil;
	@Mock
	private ExpertCostForSchedulingFeatureToggleManager expertCostForSchedulingFeatureToggleManager;

	/**
	 * Standard case.
	 * The cost and margin are fetched from pricing service
	 * and not passed in input data object.
	 */
	@Test
	public void shouldFilterExpertsHavingCostGreaterThanMaxCost_scenario1() throws IOException {
		final FilterExpertsBasedOnMaxCostTestData testData = this.testingUtil.getTestingData(
				"src/test/resources/json_data_files/FilterForMaxCostTestData_scenario1.json",
				FilterExpertsBasedOnMaxCostTestData.class);
		final MatchInterviewersData data = new MatchInterviewersData();
		data.setPartnerCompanyId("p1");
		data.setJobRoleId("j1");
		data.setInterviewStructureId("i1");
		data.setInterviewId("interview-id1");
		data.setWorkExperienceOfIntervieweeInMonths(25);
		data.setInterviewRound("PEER");
		data.setInterviewers(testData.getInterviewers());
		data.setDurationOfInterview(60L);
		final InterviewPriceResponseDTO interviewPriceResponseDTO = InterviewPriceResponseDTO.builder()
				.maximumInterviewPrice(Money.builder().value(4000D).currency("INR").build())
				.barRaiserMarginPercentage(25D)
				.build();
		when(this.pricingServiceClient.getInterviewPrice(data.getPartnerCompanyId(), data.getJobRoleId(),
				data.getInterviewStructureId(), data.getWorkExperienceOfIntervieweeInMonths(),
				RoundType.fromString(data.getInterviewRound()), data.getDurationOfInterview()))
						.thenReturn(ResponseEntity.ok().body(interviewPriceResponseDTO));
		when(this.costUtil.convertToINR(3000D, "INR")).thenReturn(3000D);
		when(this.expertCostForSchedulingFeatureToggleManager.isFeatureToggleOn("interview-id1"))
				.thenReturn(Boolean.TRUE);
		this.maxCostBasedFilteringProcessor.process(data);
		final List<InterviewerData> filteredList = data.getInterviewers();
		final List<InterviewerData> expectedList = testData.getFilteredInterviewers();
		assertEquals(
				expectedList.stream().map(InterviewerData::getId).collect(Collectors.toList()),
				filteredList.stream().map(InterviewerData::getId).collect(Collectors.toList()));
	}

	/**
	 * the cost and margin passed in the input data are considered.
	 * Ex: When constructing list of eligible experts for overbooking , the cost and
	 * margin
	 * of the scheduled interview (scheduled with duplicate expert) will be passed
	 * here.
	 * Now incase the pricing of the interview changes from pricing service (lets
	 * say it was flat pricing
	 * and it has been modified)
	 * But still we want eligible experts at the same cost and margin at which
	 * duplicate expert was picked.
	 */
	@Test
	public void shouldFilterExpertsHavingCostGreaterThanMaxCost_scenario2() throws IOException {
		final FilterExpertsBasedOnMaxCostTestData testData = this.testingUtil.getTestingData(
				"src/test/resources/json_data_files/FilterForMaxCostTestData_scenario2.json",
				FilterExpertsBasedOnMaxCostTestData.class);
		final MatchInterviewersData data = new MatchInterviewersData();
		data.setPartnerCompanyId("p1");
		data.setJobRoleId("j1");
		data.setInterviewStructureId("i1");
		data.setInterviewId("interview-id1");
		data.setWorkExperienceOfIntervieweeInMonths(25);
		data.setInterviewRound("PEER");
		data.setInterviewers(testData.getInterviewers());
		data.setDurationOfInterview(60L);
		data.setBarRaiserConfiguredMarginPercentage(25D);
		data.setBarRaiserUsedMarginPercentage(25D);
		data.setInterviewCost(Money.builder().value(4000D).currency("INR").build());

		final InterviewPriceResponseDTO interviewPriceResponseDTO = InterviewPriceResponseDTO.builder()
				.maximumInterviewPrice(Money.builder().value(3000D).currency("INR").build())
				.barRaiserMarginPercentage(25D)
				.build();
		when(this.pricingServiceClient.getInterviewPrice(data.getPartnerCompanyId(), data.getJobRoleId(),
				data.getInterviewStructureId(), data.getWorkExperienceOfIntervieweeInMonths(),
				RoundType.fromString(data.getInterviewRound()), data.getDurationOfInterview()))
						.thenReturn(ResponseEntity.ok().body(interviewPriceResponseDTO));
		when(this.costUtil.convertToINR(3000D, "INR")).thenReturn(3000D);
		when(this.expertCostForSchedulingFeatureToggleManager.isFeatureToggleOn("interview-id1"))
				.thenReturn(Boolean.TRUE);
		this.maxCostBasedFilteringProcessor.process(data);
		final List<InterviewerData> filteredList = data.getInterviewers();
		final List<InterviewerData> expectedList = testData.getFilteredInterviewers();
		assertEquals(
				expectedList.stream().map(InterviewerData::getId).collect(Collectors.toList()),
				filteredList.stream().map(InterviewerData::getId).collect(Collectors.toList()));
	}

	/**
	 * Same as previous scenario 2 just that filtered experts are different
	 *
	 * @throws IOException
	 */
	@Test
	public void shouldFilterExpertsHavingCostGreaterThanMaxCost_scenario3() throws IOException {
		final FilterExpertsBasedOnMaxCostTestData testData = this.testingUtil.getTestingData(
				"src/test/resources/json_data_files/FilterForMaxCostTestData_scenario3.json",
				FilterExpertsBasedOnMaxCostTestData.class);
		final MatchInterviewersData data = new MatchInterviewersData();
		data.setPartnerCompanyId("p1");
		data.setJobRoleId("j1");
		data.setInterviewStructureId("i1");
		data.setInterviewId("interview-id1");
		data.setWorkExperienceOfIntervieweeInMonths(25);
		data.setInterviewRound("PEER");
		data.setInterviewers(testData.getInterviewers());
		data.setDurationOfInterview(60L);
		data.setBarRaiserConfiguredMarginPercentage(10D);
		data.setBarRaiserUsedMarginPercentage(10D);
		data.setInterviewCost(Money.builder().value(2500D).currency("INR").build());
		data.setIsFallbackEnabled(Boolean.FALSE);

		final InterviewPriceResponseDTO interviewPriceResponseDTO = InterviewPriceResponseDTO.builder()
				.maximumInterviewPrice(Money.builder().value(3000D).currency("INR").build())
				.barRaiserMarginPercentage(25D)
				.build();
		when(this.pricingServiceClient.getInterviewPrice(data.getPartnerCompanyId(), data.getJobRoleId(),
				data.getInterviewStructureId(), data.getWorkExperienceOfIntervieweeInMonths(),
				RoundType.fromString(data.getInterviewRound()), data.getDurationOfInterview()))
						.thenReturn(ResponseEntity.ok().body(interviewPriceResponseDTO));
		when(this.costUtil.convertToINR(2250D, "INR")).thenReturn(2250D);
		when(this.expertCostForSchedulingFeatureToggleManager.isFeatureToggleOn("interview-id1"))
				.thenReturn(Boolean.TRUE);
		this.maxCostBasedFilteringProcessor.process(data);
		final List<InterviewerData> filteredList = data.getInterviewers();
		final List<InterviewerData> expectedList = testData.getFilteredInterviewers();
		assertEquals(
				expectedList.stream().map(InterviewerData::getId).collect(Collectors.toList()),
				filteredList.stream().map(InterviewerData::getId).collect(Collectors.toList()));

		assertEquals(
				2500D, data.getInterviewCost().getValue().doubleValue(), 0.0);

		assertEquals(10D, data.getBarRaiserUsedMarginPercentage().doubleValue(), 0.0);
		assertEquals(10D, data.getBarRaiserConfiguredMarginPercentage().doubleValue(), 0.0);
		assertEquals(Boolean.FALSE, data.getIsFallbackEnabled());
	}

	@Test
	public void shouldFilterAllExperts() throws IOException {
		final FilterExpertsBasedOnMaxCostTestData testData = this.testingUtil.getTestingData(
				"src/test/resources/json_data_files/FilterAllForMaxCostTestData.json",
				FilterExpertsBasedOnMaxCostTestData.class);
		final MatchInterviewersData data = new MatchInterviewersData();
		data.setPartnerCompanyId("p1");
		data.setJobRoleId("j1");
		data.setInterviewStructureId("i1");
		data.setWorkExperienceOfIntervieweeInMonths(25);
		data.setInterviewId("interview-id1");
		data.setInterviewRound("PEER");
		data.setInterviewers(testData.getInterviewers());
		data.setDurationOfInterview(60L);
		final InterviewPriceResponseDTO interviewPriceResponseDTO = InterviewPriceResponseDTO.builder()
				.maximumInterviewPrice(Money.builder().value(4000D).currency("INR").build())
				.barRaiserMarginPercentage(25D)
				.build();
		when(this.pricingServiceClient.getInterviewPrice(data.getPartnerCompanyId(), data.getJobRoleId(),
				data.getInterviewStructureId(), data.getWorkExperienceOfIntervieweeInMonths(),
				RoundType.fromString(data.getInterviewRound()), data.getDurationOfInterview()))
						.thenReturn(ResponseEntity.ok().body(interviewPriceResponseDTO));
		when(this.costUtil.convertToINR(3000D, "INR")).thenReturn(3000D);
		when(this.expertCostForSchedulingFeatureToggleManager.isFeatureToggleOn("interview-id1"))
				.thenReturn(Boolean.TRUE);
		this.maxCostBasedFilteringProcessor.process(data);
		final List<InterviewerData> filteredList = data.getInterviewers();
		final List<InterviewerData> expectedList = testData.getFilteredInterviewers();
		assertEquals(
				expectedList.stream().map(InterviewerData::getId).collect(Collectors.toList()),
				filteredList.stream().map(InterviewerData::getId).collect(Collectors.toList()));
	}

	@Test
	public void shouldNotFilterExpertsIfFeatureToggleIsOff() throws IOException {
		final FilterExpertsBasedOnMaxCostTestData testData = this.testingUtil.getTestingData(
				"src/test/resources/json_data_files/FilterNoneForMaxCostTestData.json",
				FilterExpertsBasedOnMaxCostTestData.class);
		final MatchInterviewersData data = new MatchInterviewersData();
		data.setPartnerCompanyId("p1");
		data.setJobRoleId("j1");
		data.setInterviewStructureId("i1");
		data.setWorkExperienceOfIntervieweeInMonths(25);
		data.setInterviewId("interview-id1");
		data.setInterviewRound("PEER");
		data.setDurationOfInterview(60L);
		data.setInterviewers(testData.getInterviewers());
		when(this.expertCostForSchedulingFeatureToggleManager.isFeatureToggleOn("interview-id1"))
				.thenReturn(Boolean.FALSE);
		this.maxCostBasedFilteringProcessor.process(data);
		final List<InterviewerData> filteredList = data.getInterviewers();
		final List<InterviewerData> expectedList = testData.getFilteredInterviewers();
		assertEquals(
				expectedList.stream().map(InterviewerData::getId).collect(Collectors.toList()),
				filteredList.stream().map(InterviewerData::getId).collect(Collectors.toList()));
	}

}
