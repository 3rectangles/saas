/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.barraiser.onboarding.common.TestingUtil;
import com.barraiser.onboarding.dal.EvaluationDAO;
import com.barraiser.onboarding.dal.EvaluationRepository;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.JobRoleToInterviewStructureDAO;
import com.barraiser.onboarding.dal.JobRoleToInterviewStructureRepository;
import com.barraiser.onboarding.interview.EvaluationUtil;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.InterviewUtil;
import com.barraiser.onboarding.interview.status.EvaluationStatusManager;
import com.barraiser.onboarding.scheduling.match_interviewers.data.UpdateEvaluationSearchHandlerTestData;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.log4j.Log4j2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@Log4j2
@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
class UpdateEvaluationSearchHandlerTest {

	@Mock
	private EvaluationRepository evaluationRepository;
	@Mock
	private InterViewRepository interViewRepository;
	@Spy
	private ObjectMapper objectMapper;
	@InjectMocks
	private TestingUtil testingUtil;
	@InjectMocks
	private EvaluationUtil evaluationUtil;
	@Mock
	private InterviewUtil interviewUtil;
	@Mock
	private JobRoleToInterviewStructureRepository jobRoleToInterviewStructureRepository;
	@Mock
	private EvaluationStatusManager evaluationStatusManager;

	@BeforeEach
	public void setup() throws IOException {
		final UpdateEvaluationSearchHandlerTestData testingData = this.testingUtil.getTestingData(
				"src/test/resources/json_data_files/UpdateEvaluationSearchHandlerTestData.json",
				UpdateEvaluationSearchHandlerTestData.class);

		Map<String, EvaluationDAO> evaluationDAOMap = testingData.getEvaluations();
		when(this.evaluationRepository.findById(anyString()))
				.then(
						(invocationOnMock -> Optional.ofNullable(
								evaluationDAOMap.get(invocationOnMock.getArgument(0)))));

		Map<String, InterviewDAO> interviewDAOMap = testingData.getInterviews();
		when(this.interViewRepository.findAllByEvaluationId(anyString()))
				.then(
						(invocationOnMock -> new ArrayList<>(
								Arrays.asList(
										interviewDAOMap.get(
												invocationOnMock.getArgument(0))))));

		Map<String, JobRoleToInterviewStructureDAO> jobRoleToInterviewStructureDAOMap = testingData
				.getJobRoleToInterviewStructures();
		when(this.evaluationStatusManager.populateStatus(any())).then(returnsFirstArg());
		when(this.jobRoleToInterviewStructureRepository
				.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId(
						anyString(), any(), anyString()))
								.then(
										(invocationOnMock -> Optional.ofNullable(
												jobRoleToInterviewStructureDAOMap.get(
														invocationOnMock.getArgument(0)))));
	}

	@Test
	void check_if_the_flag_is_set_correctly_test() {

		EvaluationDAO evaluationDAO = this.evaluationRepository.findById("e1").get();

		assertEquals(
				Boolean.FALSE,
				this.evaluationUtil.checkIfEvaluationIsPendingApproval(evaluationDAO),
				"Check if the flag is set correctly");
	}
}
