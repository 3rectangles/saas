/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.match_interviewers;

import com.barraiser.onboarding.common.TestingUtil;
import com.barraiser.onboarding.dal.ExpertSkillsDAO;
import com.barraiser.onboarding.dal.ExpertSkillsRepository;
import com.barraiser.onboarding.dal.InterviewStructureSkillsRepository;
import com.barraiser.onboarding.scheduling.match_interviewers.data.SpecificSkillMatchingProcessorTestData;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.InterviewerData;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.MatchInterviewersData;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.SpecificSkillsMatchingProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SpecificSkillsMatchingProcessorTest {
	@Spy
	private ObjectMapper objectMapper;
	@Mock
	private InterviewStructureSkillsRepository interviewStructureSkillsRepository;
	@Mock
	private ExpertSkillsRepository expertSkillsRepository;
	@InjectMocks
	private SpecificSkillsMatchingProcessor specificSkillsMatchingProcessor;
	@InjectMocks
	private TestingUtil testingUtil;

	@Test
	public void shouldReturnOnlyInterviewersThatHaveSpecificSkills() throws IOException {
		final SpecificSkillMatchingProcessorTestData testData = testingUtil.getTestingData(
				"src/test/resources/json_data_files/SpecificSkillMatchingProcessorTestDataJson.json",
				SpecificSkillMatchingProcessorTestData.class);
		when(this.expertSkillsRepository.findAllByExpertIdInAndSkillIdIn(any(), any()))
				.thenReturn(testData.getExpertSkills());

		when(this.interviewStructureSkillsRepository.findAllByInterviewStructureIdAndIsSpecific("1", true))
				.thenReturn(testData.getSpecificSkills());
		MatchInterviewersData data = new MatchInterviewersData();
		data.setInterviewers(testData.getInterviewers());
		data.setInterviewStructureId("1");
		this.specificSkillsMatchingProcessor.process(data);
		assertEquals(testData.getResult().size(), data.getInterviewers().size());
		testData.getResult().forEach(x -> {
			final InterviewerData interviewer = data.getInterviewers().stream().filter(y -> y.getId().equals(x.getId()))
					.findFirst().get();
			final List<ExpertSkillsDAO> specificSkills = interviewer.getSpecificSkills();
			final List<ExpertSkillsDAO> expectedSpecificSkills = x.getSpecificSkills();
			assertEquals(x.getAverageProficiencyInSkills(), interviewer.getAverageProficiencyInSkills());
			specificSkills.forEach(y -> {
				final Optional<ExpertSkillsDAO> expectedSkill = expectedSpecificSkills.stream()
						.filter(z -> z.getSkillId().equals(y.getSkillId())).findFirst();
				assertTrue(expectedSkill.isPresent());
			});

		});
	}

}
