/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.jira.expert;

import com.barraiser.onboarding.common.TestingUtil;
import com.barraiser.onboarding.interview.jira.expert.util.ExpertMatchingUtil;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.MatchInterviewersData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.index.query.QueryBuilder;
import org.json.simple.*;
import org.json.simple.parser.*;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
public class ExpertElasticSearchAuthorizedGraphQLQueryManagerTest {
	@InjectMocks
	ExpertElasticSearchQueryManager expertElasticSearchQueryManager;
	@Mock
	private ExpertDataTransformer expertDataTransformer;
	@Mock
	private ExpertMatchingUtil expertMatchingUtil;
	@InjectMocks
	private TestingUtil testingUtil;
	@Spy
	private ObjectMapper objectMapper;

	@Test
	public void queryShouldBeSameAsCreatedUsingFilters() throws IOException {
		final JSONObject jsonObject = this.testingUtil.getTestingData(
				"src/test/resources/json_data_files/ExpertElasticSearchQueryManagerTestData.json",
				JSONObject.class);
		final String expectedQuery = this.objectMapper.writeValueAsString(jsonObject.toString()).replaceAll("\\\\", "")
				.replaceAll("\\s", "");
		final MatchInterviewersData data = MatchInterviewersData
				.builder()
				.candidateId("1")
				.hiringCompanyId("123")
				.category("A")
				.interviewRound("EXPERT")
				.domainId("ai")
				.workExperienceOfIntervieweeInMonths(22)
				.partnerCompanyId("p_123")
				.build();
		Integer experience = this.expertMatchingUtil.getRequiredWorkExperienceOfExpert(data.getCategory(),
				data.getWorkExperienceOfIntervieweeInMonths(), data.getInterviewRound(), data.getPartnerCompanyId());
		when(this.expertMatchingUtil.getRequiredWorkExperienceOfExpert(any(), any(), any(), any()))
				.thenReturn(experience);
		final QueryBuilder query = this.expertElasticSearchQueryManager.createQuery(data);
		final String actualQuery = "\"" + query.toString().replaceAll("\\s", "").replaceAll("\n", "") + "\"";
		assertEquals(expectedQuery, actualQuery);
	}

	@Test
	public void queryShouldNotBeSameIfCandidateIdIsDifferent() throws IOException, ParseException {
		final JSONObject jsonObject = this.testingUtil.getTestingData(
				"src/test/resources/json_data_files/ExpertElasticSearchQueryManagerTestData.json",
				JSONObject.class);
		final String expectedQuery = this.objectMapper.writeValueAsString(jsonObject.toString()).replaceAll("\\\\", "")
				.replaceAll("\\s", "");
		final MatchInterviewersData data = MatchInterviewersData.builder().candidateId("1")
				.hiringCompanyId("123")
				.category("A")
				.interviewRound("EXPERT")
				.workExperienceOfIntervieweeInMonths(22)
				.domainId("ai")
				.partnerCompanyId("p_123")
				.build();
		final Integer experience = this.expertMatchingUtil.getRequiredWorkExperienceOfExpert(data.getCategory(),
				data.getWorkExperienceOfIntervieweeInMonths(), data.getInterviewRound(), data.getPartnerCompanyId());
		when(this.expertMatchingUtil.getRequiredWorkExperienceOfExpert(any(), any(), any(), any()))
				.thenReturn(experience);
		final QueryBuilder query = this.expertElasticSearchQueryManager.createQuery(data);
		final String actualQuery = "\"" + query.toString().replaceAll("\\s", "").replaceAll("\n", "") + "\"";
		assertNotEquals(expectedQuery, actualQuery);
	}

	@Test
	public void queryShouldNotBeSameForDifferentData() {

		final MatchInterviewersData data1 = MatchInterviewersData.builder().candidateId("1")
				.hiringCompanyId("123")
				.category("A")
				.interviewRound("EXPERT")
				.domainId("ai")
				.workExperienceOfIntervieweeInMonths(22)
				.partnerCompanyId("p_123")
				.build();
		final MatchInterviewersData data2 = MatchInterviewersData.builder().candidateId("2").hiringCompanyId("122")
				.category("A").interviewRound("EXPERT").domainId("123").workExperienceOfIntervieweeInMonths(22).build();
		Integer experience = this.expertMatchingUtil.getRequiredWorkExperienceOfExpert(data1.getCategory(),
				data1.getWorkExperienceOfIntervieweeInMonths(), data1.getInterviewRound(), data1.getPartnerCompanyId());
		when(this.expertMatchingUtil.getRequiredWorkExperienceOfExpert(any(), any(), any(), any()))
				.thenReturn(experience);
		final QueryBuilder queryWithData1 = this.expertElasticSearchQueryManager.createQuery(data1);
		final QueryBuilder queryWithData2 = this.expertElasticSearchQueryManager.createQuery(data2);

		assertNotEquals(queryWithData1, queryWithData2);
	}
}
