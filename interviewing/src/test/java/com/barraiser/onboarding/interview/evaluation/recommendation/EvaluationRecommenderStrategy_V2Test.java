/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.recommendation;

import com.barraiser.common.graphql.types.EvaluationRecommendation;
import com.barraiser.common.graphql.types.RecommendationType;
import com.barraiser.onboarding.config.DynamicAppConfigProperties;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.evaluation.enums.InterviewProcessType;
import com.barraiser.onboarding.interview.evaluation.scores.BgsScoreFetcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EvaluationRecommenderStrategy_V2Test {

	@Mock
	private InterViewRepository interViewRepository;

	@Mock
	private EvaluationRepository evaluationRepository;

	@Mock
	private JobRoleToInterviewStructureRepository jobRoleToInterviewStructureRepository;

	@Mock
	private BgsScoreFetcher bgsScoreFetcher;

	@InjectMocks
	private EvaluationRecommenderStrategy_V2 evaluationRecommenderStrategy_V2;

	@Mock
	private EvaluationRecommendationRepository evaluationRecommendationRepository;

	@Mock
	private DynamicAppConfigProperties appConfigProperties;

	@Test
	public void shouldProduceEvaluationRecommendation() {
		final String evaluationId = "1";

		final EvaluationDAO evaluationDAO = EvaluationDAO
				.builder()
				.id("1")
				.jobRoleId("1")
				.jobRoleVersion(0)
				.status("Done")
				.build();

		final List<JobRoleToInterviewStructureDAO> jobRoleToInterviewStructureDAOList = List.of(
				JobRoleToInterviewStructureDAO
						.builder()
						.id("2")
						.jobRoleId("1")
						.jobRoleVersion(0)
						.jobRoleVersion(0)
						.interviewRound("PEER")
						.orderIndex(1)
						.acceptanceCutoffScore(498)
						.recommendationScore(300)
						.build(),
				JobRoleToInterviewStructureDAO
						.builder()
						.id("1")
						.jobRoleId("1")
						.jobRoleVersion(0)
						.interviewRound("PEER")
						.orderIndex(0)
						.acceptanceCutoffScore(499)
						.recommendationScore(200)
						.build(),
				JobRoleToInterviewStructureDAO
						.builder()
						.id("3")
						.jobRoleId("1")
						.jobRoleVersion(0)
						.interviewRound("INTERNAL")
						.orderIndex(2)
						.acceptanceCutoffScore(499)
						.build(),
				JobRoleToInterviewStructureDAO
						.builder()
						.id("4")
						.jobRoleId("1")
						.jobRoleVersion(0)
						.interviewRound("INTERNAL")
						.orderIndex(3)
						.acceptanceCutoffScore(499)
						.build());

		final List<InterviewDAO> interviewDAOS = List.of(
				InterviewDAO
						.builder()
						.id("0")
						.evaluationId(evaluationId)
						.status("Done")
						.interviewRound("PEER")
						.build(),
				InterviewDAO
						.builder()
						.id("1")
						.evaluationId(evaluationId)
						.status("Done")
						.interviewRound("PEER")
						.build(),
				InterviewDAO
						.builder()
						.id("2")
						.evaluationId(evaluationId)
						.status("Done")
						.interviewRound("INTERNAL")
						.build(),
				InterviewDAO
						.builder()
						.id("3")
						.evaluationId(evaluationId)
						.status("Done")
						.interviewRound("INTERNAL")
						.build());

		Integer bgs = 200;
		when(this.bgsScoreFetcher
				.getBgsScoreForEvaluationBasedOnInterviewProcessType(evaluationDAO.getId(),
						InterviewProcessType.BARRAISER))
								.thenReturn(bgs);

		when(this.evaluationRepository.findById(evaluationId))
				.thenReturn(Optional.of(evaluationDAO));

		when(this.jobRoleToInterviewStructureRepository
				.findAllByJobRoleIdAndJobRoleVersion(
						evaluationDAO.getJobRoleId(),
						evaluationDAO.getJobRoleVersion()))
								.thenReturn(jobRoleToInterviewStructureDAOList);

		when(this.interViewRepository
				.findAllByEvaluationId(evaluationDAO.getId()))
						.thenReturn(interviewDAOS);
		when(this.evaluationRecommendationRepository
				.findByEvaluationIdAndRecommendationAlgoVersion(evaluationId, "2")).thenReturn(Optional.empty());

		final EvaluationRecommendation recommendation = this.evaluationRecommenderStrategy_V2
				.getRecommendation(evaluationId);

		assertNotNull(recommendation);
	}

	@Test
	public void shouldReturnNullIfOnlyInternalRoundsAreDone() {
		final String evaluationId = "1";

		final EvaluationDAO evaluationDAO = EvaluationDAO
				.builder()
				.id("1")
				.jobRoleId("1")
				.jobRoleVersion(0)
				.status("Done")
				.build();

		final List<JobRoleToInterviewStructureDAO> jobRoleToInterviewStructureDAOList = List.of(
				JobRoleToInterviewStructureDAO
						.builder()
						.id("3")
						.jobRoleId("1")
						.jobRoleVersion(0)
						.interviewRound("INTERNAL")
						.orderIndex(1)
						.acceptanceCutoffScore(499)
						.build(),
				JobRoleToInterviewStructureDAO
						.builder()
						.id("2")
						.jobRoleId("1")
						.jobRoleVersion(0)
						.interviewRound("INTERNAL")
						.orderIndex(2)
						.acceptanceCutoffScore(499)
						.build(),
				JobRoleToInterviewStructureDAO
						.builder()
						.id("1")
						.jobRoleId("1")
						.jobRoleVersion(0)
						.interviewRound("PEER")
						.orderIndex(0)
						.acceptanceCutoffScore(499)
						.build());

		final List<InterviewDAO> interviewDAOS = List.of(
				InterviewDAO
						.builder()
						.id("2")
						.evaluationId(evaluationId)
						.status("Done")
						.interviewRound("INTERNAL")
						.build(),
				InterviewDAO
						.builder()
						.id("3")
						.evaluationId(evaluationId)
						.status("Done")
						.interviewRound("INTERNAL")
						.build(),
				InterviewDAO
						.builder()
						.id("1")
						.evaluationId(evaluationId)
						.status("pending_scheduling")
						.interviewRound("PEER")
						.build());

		when(this.evaluationRepository.findById(evaluationId))
				.thenReturn(Optional.of(evaluationDAO));

		when(this.jobRoleToInterviewStructureRepository
				.findAllByJobRoleIdAndJobRoleVersion(
						evaluationDAO.getJobRoleId(),
						evaluationDAO.getJobRoleVersion()))
								.thenReturn(jobRoleToInterviewStructureDAOList);

		when(this.interViewRepository
				.findAllByEvaluationId(evaluationDAO.getId()))
						.thenReturn(interviewDAOS);

		when(this.evaluationRecommendationRepository
				.findByEvaluationIdAndRecommendationAlgoVersion(evaluationId, "2")).thenReturn(Optional.empty());

		final EvaluationRecommendation recommendation = this.evaluationRecommenderStrategy_V2
				.getRecommendation(evaluationId);

		assertNull(recommendation);
	}

	@Test
	public void shouldReturnNullIfOnlyInternalRoundsAreConfiguredInJR() {
		final String evaluationId = "1";

		final EvaluationDAO evaluationDAO = EvaluationDAO
				.builder()
				.id("1")
				.jobRoleId("1")
				.jobRoleVersion(0)
				.status("Done")
				.build();

		final List<JobRoleToInterviewStructureDAO> jobRoleToInterviewStructureDAOList = List.of(
				JobRoleToInterviewStructureDAO
						.builder()
						.id("3")
						.jobRoleId("1")
						.jobRoleVersion(0)
						.interviewRound("INTERNAL")
						.orderIndex(1)
						.acceptanceCutoffScore(499)
						.build(),
				JobRoleToInterviewStructureDAO
						.builder()
						.id("2")
						.jobRoleId("1")
						.jobRoleVersion(0)
						.interviewRound("INTERNAL")
						.orderIndex(2)
						.acceptanceCutoffScore(499)
						.build(),
				JobRoleToInterviewStructureDAO
						.builder()
						.id("1")
						.jobRoleId("1")
						.jobRoleVersion(0)
						.interviewRound("INTERNAL")
						.orderIndex(0)
						.acceptanceCutoffScore(499)
						.build());

		final List<InterviewDAO> interviewDAOS = List.of(
				InterviewDAO
						.builder()
						.id("2")
						.evaluationId(evaluationId)
						.status("Done")
						.interviewRound("INTERNAL")
						.build(),
				InterviewDAO
						.builder()
						.id("3")
						.evaluationId(evaluationId)
						.status("Done")
						.interviewRound("INTERNAL")
						.build(),
				InterviewDAO
						.builder()
						.id("1")
						.evaluationId(evaluationId)
						.status("pending_scheduling")
						.interviewRound("INTERNAL")
						.build());

		when(this.evaluationRepository.findById(evaluationId))
				.thenReturn(Optional.of(evaluationDAO));

		when(this.jobRoleToInterviewStructureRepository
				.findAllByJobRoleIdAndJobRoleVersion(
						evaluationDAO.getJobRoleId(),
						evaluationDAO.getJobRoleVersion()))
								.thenReturn(jobRoleToInterviewStructureDAOList);

		when(this.interViewRepository
				.findAllByEvaluationId(evaluationDAO.getId()))
						.thenReturn(interviewDAOS);

		when(this.evaluationRecommendationRepository
				.findByEvaluationIdAndRecommendationAlgoVersion(evaluationId, "2")).thenReturn(Optional.empty());

		final EvaluationRecommendation recommendation = this.evaluationRecommenderStrategy_V2
				.getRecommendation(evaluationId);

		assertNull(recommendation);
	}

	@Test
	public void shouldReturnDefaultCutOffScoreIfCutOffScoreNullForHighestOrderIndex() {
		final String evaluationId = "1";

		final EvaluationDAO evaluationDAO = EvaluationDAO
				.builder()
				.id("1")
				.jobRoleId("1")
				.jobRoleVersion(0)
				.status("Done")
				.build();

		final List<JobRoleToInterviewStructureDAO> jobRoleToInterviewStructureDAOList = List.of(
				JobRoleToInterviewStructureDAO
						.builder()
						.id("2")
						.jobRoleId("1")
						.jobRoleVersion(0)
						.jobRoleVersion(0)
						.interviewRound("PEER")
						.orderIndex(1)
						.build(),
				JobRoleToInterviewStructureDAO
						.builder()
						.id("1")
						.jobRoleId("1")
						.jobRoleVersion(0)
						.interviewRound("PEER")
						.orderIndex(0)
						.acceptanceCutoffScore(499)
						.recommendationScore(200)
						.build());

		final List<InterviewDAO> interviewDAOS = List.of(
				InterviewDAO
						.builder()
						.id("0")
						.evaluationId(evaluationId)
						.status("Done")
						.interviewRound("PEER")
						.build(),
				InterviewDAO
						.builder()
						.id("1")
						.evaluationId(evaluationId)
						.status("Done")
						.interviewRound("PEER")
						.build());

		when(this.evaluationRepository.findById(evaluationId))
				.thenReturn(Optional.of(evaluationDAO));

		when(this.jobRoleToInterviewStructureRepository
				.findAllByJobRoleIdAndJobRoleVersion(
						evaluationDAO.getJobRoleId(),
						evaluationDAO.getJobRoleVersion()))
								.thenReturn(jobRoleToInterviewStructureDAOList);

		when(this.interViewRepository
				.findAllByEvaluationId(evaluationDAO.getId()))
						.thenReturn(interviewDAOS);

		when(this.evaluationRecommendationRepository
				.findByEvaluationIdAndRecommendationAlgoVersion(evaluationId, "2")).thenReturn(Optional.empty());

		when(this.appConfigProperties.getString("default-cutoff-score-for-evaluation-recommendation-v2"))
				.thenReturn("450");

		final EvaluationRecommendation recommendation = this.evaluationRecommenderStrategy_V2
				.getRecommendation(evaluationId);

		assertEquals(RecommendationType.NOT_RECOMMENDED, recommendation.getRecommendationType());
	}

	@Test
	public void shouldReturnNullBecauseBarRaiserInterviewNotDone() {
		final String evaluationId = "1";

		final EvaluationDAO evaluationDAO = EvaluationDAO
				.builder()
				.id("1")
				.jobRoleId("1")
				.jobRoleVersion(0)
				.status("Done")
				.build();

		final List<JobRoleToInterviewStructureDAO> jobRoleToInterviewStructureDAOList = List.of(
				JobRoleToInterviewStructureDAO
						.builder()
						.id("2")
						.jobRoleId("1")
						.jobRoleVersion(0)
						.jobRoleVersion(0)
						.interviewRound("PEER")
						.orderIndex(1)
						.acceptanceCutoffScore(498)
						.recommendationScore(300)
						.build(),
				JobRoleToInterviewStructureDAO
						.builder()
						.id("1")
						.jobRoleId("1")
						.jobRoleVersion(0)
						.interviewRound("PEER")
						.orderIndex(0)
						.acceptanceCutoffScore(499)
						.recommendationScore(200)
						.build(),
				JobRoleToInterviewStructureDAO
						.builder()
						.id("3")
						.jobRoleId("1")
						.jobRoleVersion(0)
						.interviewRound("INTERNAL")
						.orderIndex(2)
						.acceptanceCutoffScore(499)
						.build(),
				JobRoleToInterviewStructureDAO
						.builder()
						.id("4")
						.jobRoleId("1")
						.jobRoleVersion(0)
						.interviewRound("INTERNAL")
						.orderIndex(3)
						.acceptanceCutoffScore(499)
						.build());

		final List<InterviewDAO> interviewDAOS = List.of(
				InterviewDAO
						.builder()
						.id("0")
						.evaluationId(evaluationId)
						.status("Done")
						.interviewRound("PEER")
						.build(),
				InterviewDAO
						.builder()
						.id("1")
						.evaluationId(evaluationId)
						.status("pending_scheduling")
						.interviewRound("PEER")
						.build(),
				InterviewDAO
						.builder()
						.id("2")
						.evaluationId(evaluationId)
						.status("Done")
						.interviewRound("INTERNAL")
						.build(),
				InterviewDAO
						.builder()
						.id("3")
						.evaluationId(evaluationId)
						.status("Done")
						.interviewRound("INTERNAL")
						.build());

		when(this.evaluationRepository.findById(evaluationId))
				.thenReturn(Optional.of(evaluationDAO));

		when(this.jobRoleToInterviewStructureRepository
				.findAllByJobRoleIdAndJobRoleVersion(
						evaluationDAO.getJobRoleId(),
						evaluationDAO.getJobRoleVersion()))
								.thenReturn(jobRoleToInterviewStructureDAOList);

		when(this.interViewRepository
				.findAllByEvaluationId(evaluationDAO.getId()))
						.thenReturn(interviewDAOS);

		when(this.evaluationRecommendationRepository
				.findByEvaluationIdAndRecommendationAlgoVersion(evaluationId, "2")).thenReturn(Optional.empty());

		final EvaluationRecommendation recommendation = this.evaluationRecommenderStrategy_V2
				.getRecommendation(evaluationId);

		assertNull(recommendation);
	}

	@Test
	public void shouldReturnNullBecauseEvaluationNotDone() {
		final String evaluationId = "1";

		final EvaluationDAO evaluationDAO = EvaluationDAO
				.builder()
				.id("1")
				.jobRoleId("1")
				.jobRoleVersion(0)
				.status("pending")
				.build();

		final List<JobRoleToInterviewStructureDAO> jobRoleToInterviewStructureDAOList = List.of(
				JobRoleToInterviewStructureDAO
						.builder()
						.id("2")
						.jobRoleId("1")
						.jobRoleVersion(0)
						.jobRoleVersion(0)
						.interviewRound("PEER")
						.orderIndex(1)
						.acceptanceCutoffScore(498)
						.recommendationScore(300)
						.build(),
				JobRoleToInterviewStructureDAO
						.builder()
						.id("1")
						.jobRoleId("1")
						.jobRoleVersion(0)
						.interviewRound("PEER")
						.orderIndex(0)
						.acceptanceCutoffScore(499)
						.recommendationScore(200)
						.build(),
				JobRoleToInterviewStructureDAO
						.builder()
						.id("3")
						.jobRoleId("1")
						.jobRoleVersion(0)
						.interviewRound("INTERNAL")
						.orderIndex(2)
						.acceptanceCutoffScore(499)
						.build(),
				JobRoleToInterviewStructureDAO
						.builder()
						.id("4")
						.jobRoleId("1")
						.jobRoleVersion(0)
						.interviewRound("INTERNAL")
						.orderIndex(3)
						.acceptanceCutoffScore(499)
						.build());

		when(this.evaluationRepository.findById(evaluationId))
				.thenReturn(Optional.of(evaluationDAO));

		when(this.jobRoleToInterviewStructureRepository
				.findAllByJobRoleIdAndJobRoleVersion(
						evaluationDAO.getJobRoleId(),
						evaluationDAO.getJobRoleVersion()))
								.thenReturn(jobRoleToInterviewStructureDAOList);

		when(this.evaluationRecommendationRepository
				.findByEvaluationIdAndRecommendationAlgoVersion(evaluationId, "2")).thenReturn(Optional.empty());

		final EvaluationRecommendation recommendation = this.evaluationRecommenderStrategy_V2
				.getRecommendation(evaluationId);

		assertNull(recommendation);
	}

	@Test
	public void shouldReturnStronglyRecommended() {
		final EvaluationDAO evaluationDAO = EvaluationDAO.builder().id("1").status("Done").build();
		final JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO = JobRoleToInterviewStructureDAO.builder()
				.id("1").acceptanceCutoffScore(500).recommendationScore(100).build();
		Integer bgs = 580;
		when(this.bgsScoreFetcher.getBgsScoreForEvaluationBasedOnInterviewProcessType(evaluationDAO.getId(),
				InterviewProcessType.BARRAISER)).thenReturn(bgs);
		final EvaluationRecommendation recommendationType = this.evaluationRecommenderStrategy_V2
				.getRecommendation(evaluationDAO, jobRoleToInterviewStructureDAO);
		assertEquals(RecommendationType.STRONGLY_RECOMMENDED, recommendationType.getRecommendationType());
		assertEquals(100, recommendationType.getScreeningCutOff().intValue());

	}

	@Test
	public void shouldReturnRecommended() {
		final EvaluationDAO evaluationDAO = EvaluationDAO.builder().id("1").status("Done").build();
		final JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO = JobRoleToInterviewStructureDAO.builder()
				.id("1").acceptanceCutoffScore(500).rejectionCutoffScore(200).recommendationScore(450).build();
		Integer bgs = 463;
		when(this.bgsScoreFetcher.getBgsScoreForEvaluationBasedOnInterviewProcessType(evaluationDAO.getId(),
				InterviewProcessType.BARRAISER)).thenReturn(bgs);
		final EvaluationRecommendation recommendationType = this.evaluationRecommenderStrategy_V2
				.getRecommendation(evaluationDAO, jobRoleToInterviewStructureDAO);
		assertEquals(RecommendationType.RECOMMENDED, recommendationType.getRecommendationType());
		assertEquals(450, recommendationType.getScreeningCutOff().intValue());
	}

	@Test
	public void shouldReturnRequiresApproval() {
		final EvaluationDAO evaluationDAO = EvaluationDAO.builder().id("1").status("Done").build();
		final JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO = JobRoleToInterviewStructureDAO.builder()
				.id("1").acceptanceCutoffScore(500).recommendationScore(100).build();
		Integer bgs = 100;
		when(this.bgsScoreFetcher.getBgsScoreForEvaluationBasedOnInterviewProcessType(evaluationDAO.getId(),
				InterviewProcessType.BARRAISER)).thenReturn(bgs);
		final EvaluationRecommendation recommendationType = this.evaluationRecommenderStrategy_V2
				.getRecommendation(evaluationDAO, jobRoleToInterviewStructureDAO);
		assertEquals(RecommendationType.REQUIRES_FURTHER_REVIEW, recommendationType.getRecommendationType());
		assertEquals(100, recommendationType.getScreeningCutOff().intValue());
	}

	@Test
	public void shouldReturnRejectedIfCandidateIsRejected() {
		final EvaluationDAO evaluationDAO = EvaluationDAO.builder().id("1").status("Done").build();
		final JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO = JobRoleToInterviewStructureDAO.builder()
				.id("1").acceptanceCutoffScore(500).recommendationScore(200).build();
		Integer bgs = 100;
		when(this.bgsScoreFetcher.getBgsScoreForEvaluationBasedOnInterviewProcessType(evaluationDAO.getId(),
				InterviewProcessType.BARRAISER)).thenReturn(bgs);
		final EvaluationRecommendation recommendationType = this.evaluationRecommenderStrategy_V2
				.getRecommendation(evaluationDAO, jobRoleToInterviewStructureDAO);
		assertEquals(RecommendationType.NOT_RECOMMENDED, recommendationType.getRecommendationType());
		assertEquals(200, recommendationType.getScreeningCutOff().intValue());
	}

}
