/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.onboarding.dal.InterviewStructureDAO;
import com.barraiser.onboarding.dal.InterviewStructureRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InterviewStructureManagerTest {
	@InjectMocks
	private InterviewStructureManager interviewStructureManager;
	@Spy
	private ObjectMapper objectMapper;
	@Mock
	private InterviewStructureRepository interviewStructureRepository;

	@Test
	public void shouldNotRequireTaggingAgentIfVersion1() {
		when(this.interviewStructureRepository.findById("1")).thenReturn(Optional.of(InterviewStructureDAO.builder()
				.interviewFlow(
						"{\"version\":\"1\",\"sections\":[{\"skill\":{\"id\":\"INTRODUCTION\",\"name\":\"Introduction\"},\"duration\":600,\"isEvaluative\":false,\"guidelines\":\"<p>Pleaseaskname</p>\"},{\"skill\":{\"id\":\"baeee505-744d-49a4-94f5-a93e6a69c201\",\"name\":\"Java\",\"parentSkillId\":null},\"duration\":1500,\"isEvaluative\":true,\"guidelines\":\"<liclass=\\\"\\\">abscskdv</li><liclass=\\\"\\\">sdv</li><liclass=\\\"\\\">sdv</li><li>cs</li>\",\"questions\":[{\"name\":\"Overall\",\"weightage\":\"MODERATE\",\"type\":\"EVALUATION_METRICS\",\"rating\":7,\"feedback\":\"<liclass=\\\"br-feedback-thumbs-up-list-item\\\">sdvsdv</li>\"},{\"name\":\"Somemetric\",\"weightage\":\"HARD\",\"type\":\"EVALUATION_METRICS\",\"rating\":5,\"feedback\":\"<liclass=\\\"br-feedback-thumbs-up-list-item\\\">sdvsdv</li>\"},{\"name\":\"q1\",\"weightage\":\"EASY\",\"type\":\"MUST_ASK_QUESTIONS\",\"rating\":5,\"feedback\":\"<liclass=\\\"br-feedback-thumbs-up-list-item\\\">feedback1</li>\"},{\"name\":\"q2\",\"weightage\":\"MODERATE\",\"type\":\"MUST_ASK_QUESTIONS\",\"rating\":0}],\"sampleProblems\":[\"sample1\",\"sample2\"]}],\"overallFeedback\":{\"softSkills\":[{\"id\":\"ca56a190-32d5-4ea4-864c-b11cf5b69e77\",\"name\":\"Communication\",\"weightage\":\"MODERATE\",\"rating\":4},{\"id\":\"5411a339-ebb5-4971-8a57-4c385db786d6\",\"name\":\"Enthusiasm\",\"weightage\":\"MODERATE\",\"rating\":3}],\"overallRating\":5,\"strengths\":\"<liclass=\\\"br-feedback-thumbs-up-list-item\\\">adv</li><liclass=\\\"br-feedback-thumbs-up-list-item\\\">sdv</li><liclass=\\\"br-feedback-thumbs-up-list-item\\\">sdv</li>\",\"areasOfImprovement\":\"<liclass=\\\"br-feedback-thumbs-down-list-item\\\">sdv</li><liclass=\\\"br-feedback-thumbs-down-list-item\\\">sdv</li><liclass=\\\"br-feedback-thumbs-down-list-item\\\">sdv</li>\"}}")
				.build()));
		final Boolean isTaggingAgentNeeded = this.interviewStructureManager.isTaggingAgentRequired("1");
		assertFalse(isTaggingAgentNeeded);
	}

}
