/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.evaluation.scores.BgsScoreFetcher;
import com.barraiser.onboarding.interview.ruleEngine.InterviewRoundClearanceRuleChecker;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InterviewUtilTest {
	@Mock
	private InterViewRepository interViewRepository;
	@InjectMocks
	private InterviewUtil interviewUtil;
	@Mock
	private JobRoleToInterviewStructureRepository jobRoleToInterviewStructureRepository;
	@Mock
	private EvaluationRepository evaluationRepository;

	@Mock
	private BgsScoreFetcher bgsScoreFetcher;
	@Mock
	private InterviewRoundClearanceRuleChecker interviewRoundClearanceRuleChecker;

	@Test
	public void shouldBeNextRoundIndependentForOldJobRoleTool() {
		when(this.jobRoleToInterviewStructureRepository.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId("1", 1,
				"1")).thenReturn(
						Optional.of(JobRoleToInterviewStructureDAO.builder().jobRoleId("1").jobRoleVersion(1)
								.interviewStructureId("1").acceptanceCutoffScore(null).rejectionCutoffScore(null)
								.isManualActionForRemainingCases(null).build()));
		boolean isNextRoundDependent = this.interviewUtil.isNextRoundCreationDependent("1", 1, "1");
		assertFalse(isNextRoundDependent);

	}

	@Test
	public void shouldBeNextRoundIndependentForNewJobRoleTool() {
		when(this.jobRoleToInterviewStructureRepository.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId("1", 1,
				"1")).thenReturn(
						Optional.of(JobRoleToInterviewStructureDAO.builder().jobRoleId("1").jobRoleVersion(1)
								.interviewStructureId("1").approvalRuleId(null).rejectionRuleId(null)
								.isManualActionForRemainingCases(false).build()));
		boolean isNextRoundDependent = this.interviewUtil.isNextRoundCreationDependent("1", 1, "1");
		assertFalse(isNextRoundDependent);

	}

	@Test
	public void shouldBeNextRoundDependentForOldJobRoleTool() {
		// These cases are possible for old job role tool
		when(this.jobRoleToInterviewStructureRepository.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId("1", 1,
				"1")).thenReturn(
						Optional.of(JobRoleToInterviewStructureDAO.builder().jobRoleId("1").jobRoleVersion(1)
								.interviewStructureId("1").acceptanceCutoffScore(500).rejectionCutoffScore(200)
								.isManualActionForRemainingCases(null).build()));
		when(this.jobRoleToInterviewStructureRepository.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId("1", 1,
				"2")).thenReturn(
						Optional.of(JobRoleToInterviewStructureDAO.builder().jobRoleId("1").jobRoleVersion(1)
								.interviewStructureId("1").acceptanceCutoffScore(500).rejectionCutoffScore(200)
								.isManualActionForRemainingCases(null).build()));
		when(this.jobRoleToInterviewStructureRepository.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId("1", 1,
				"3")).thenReturn(
						Optional.of(JobRoleToInterviewStructureDAO.builder().jobRoleId("1").jobRoleVersion(1)
								.interviewStructureId("1").acceptanceCutoffScore(500).rejectionCutoffScore(200)
								.isManualActionForRemainingCases(true).build()));
		boolean isNextRoundDependentCase1 = this.interviewUtil.isNextRoundCreationDependent("1", 1, "1");
		boolean isNextRoundDependentCase2 = this.interviewUtil.isNextRoundCreationDependent("1", 1, "2");
		boolean isNextRoundDependentCase3 = this.interviewUtil.isNextRoundCreationDependent("1", 1, "3");
		assertTrue(isNextRoundDependentCase1);
		assertTrue(isNextRoundDependentCase2);
		assertTrue(isNextRoundDependentCase3);

	}

	@Test
	public void shouldBeNextRoundDependentForNewJobRoleTool() {
		// These cases are possible for old job role tool
		when(this.jobRoleToInterviewStructureRepository.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId("1", 1,
				"1")).thenReturn(
						Optional.of(JobRoleToInterviewStructureDAO.builder().jobRoleId("1").jobRoleVersion(1)
								.interviewStructureId("1").approvalRuleId(null).rejectionRuleId(null)
								.isManualActionForRemainingCases(true).build()));
		when(this.jobRoleToInterviewStructureRepository.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId("1", 1,
				"2")).thenReturn(
						Optional.of(JobRoleToInterviewStructureDAO.builder().jobRoleId("1").jobRoleVersion(1)
								.interviewStructureId("1").approvalRuleId("123").rejectionRuleId(null)
								.isManualActionForRemainingCases(true).build()));
		when(this.jobRoleToInterviewStructureRepository.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId("1", 1,
				"3")).thenReturn(
						Optional.of(JobRoleToInterviewStructureDAO.builder().jobRoleId("1").jobRoleVersion(1)
								.interviewStructureId("1").approvalRuleId(null).rejectionRuleId("2343")
								.isManualActionForRemainingCases(true).build()));
		when(this.jobRoleToInterviewStructureRepository.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId("1", 1,
				"4")).thenReturn(
						Optional.of(JobRoleToInterviewStructureDAO.builder().jobRoleId("1").jobRoleVersion(1)
								.interviewStructureId("1").approvalRuleId("123").rejectionRuleId("2343")
								.isManualActionForRemainingCases(true).build()));
		when(this.jobRoleToInterviewStructureRepository.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId("1", 1,
				"5")).thenReturn(
						Optional.of(JobRoleToInterviewStructureDAO.builder().jobRoleId("1").jobRoleVersion(1)
								.interviewStructureId("1").approvalRuleId("123").rejectionRuleId(null)
								.isManualActionForRemainingCases(false).build()));
		when(this.jobRoleToInterviewStructureRepository.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId("1", 1,
				"6")).thenReturn(
						Optional.of(JobRoleToInterviewStructureDAO.builder().jobRoleId("1").jobRoleVersion(1)
								.interviewStructureId("1").approvalRuleId(null).rejectionRuleId("2343")
								.isManualActionForRemainingCases(false).build()));
		boolean isNextRoundDependentCase1 = this.interviewUtil.isNextRoundCreationDependent("1", 1, "1");
		boolean isNextRoundDependentCase2 = this.interviewUtil.isNextRoundCreationDependent("1", 1, "2");
		boolean isNextRoundDependentCase3 = this.interviewUtil.isNextRoundCreationDependent("1", 1, "3");
		boolean isNextRoundDependentCase4 = this.interviewUtil.isNextRoundCreationDependent("1", 1, "4");
		boolean isNextRoundDependentCase5 = this.interviewUtil.isNextRoundCreationDependent("1", 1, "5");
		boolean isNextRoundDependentCase6 = this.interviewUtil.isNextRoundCreationDependent("1", 1, "6");
		assertTrue(isNextRoundDependentCase1);
		assertTrue(isNextRoundDependentCase2);
		assertTrue(isNextRoundDependentCase3);
		assertTrue(isNextRoundDependentCase4);
		assertTrue(isNextRoundDependentCase5);
		assertTrue(isNextRoundDependentCase6);

	}

	@Test
	public void doesInterviewRequiresApprovalFalseIfInterviewNotDone() {
		InterviewDAO interview = InterviewDAO.builder().id("1").status("pending_scheduling").build();
		JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO = JobRoleToInterviewStructureDAO.builder()
				.jobRoleId("1").jobRoleVersion(1).interviewStructureId("1").build();
		boolean doesInterviewRequiresApproval = this.interviewUtil.doesInterviewRequireApproval(interview,
				jobRoleToInterviewStructureDAO);
		assertFalse(doesInterviewRequiresApproval);
	}

	@Test
	public void doesInterviewRequiresApprovalFalseIfEvaluationDone() {
		when(this.evaluationRepository.findById("1"))
				.thenReturn(Optional.of(EvaluationDAO.builder().id("1").status("Done").build()));

		InterviewDAO interview = InterviewDAO.builder().id("1").evaluationId("1").status("Done").build();
		JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO = JobRoleToInterviewStructureDAO.builder()
				.jobRoleId("1").jobRoleVersion(1).interviewStructureId("1").build();
		boolean doesInterviewRequiresApproval = this.interviewUtil.doesInterviewRequireApproval(interview,
				jobRoleToInterviewStructureDAO);
		assertFalse(doesInterviewRequiresApproval);
	}

	@Test
	public void doesInterviewRequiresApprovalTrueForOldJobRole() {
		InterviewDAO interview1 = InterviewDAO.builder().id("1").evaluationId("1").status("Done")
				.interviewStructureId("1").build();
		JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO1 = JobRoleToInterviewStructureDAO.builder()
				.jobRoleId("1").jobRoleVersion(1).interviewStructureId("1").orderIndex(0).acceptanceCutoffScore(300)
				.rejectionCutoffScore(100).build();

		when(this.evaluationRepository.findById("1"))
				.thenReturn(Optional.of(EvaluationDAO.builder().id("1").status("status").build()));
		when(this.bgsScoreFetcher.getBgsScoreForInterview("1")).thenReturn(200);
		when(this.jobRoleToInterviewStructureRepository
				.findTopByJobRoleIdAndJobRoleVersionAndOrderIndexGreaterThanOrderByOrderIndexAsc("1", 1, 0)).thenReturn(
						Optional.of(JobRoleToInterviewStructureDAO.builder().jobRoleId("1").jobRoleVersion(1)
								.interviewStructureId("2").orderIndex(1).build()));
		when(this.jobRoleToInterviewStructureRepository.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId("1", 1,
				"1")).thenReturn(
						Optional.of(jobRoleToInterviewStructureDAO1));
		when(this.interViewRepository.findAllByEvaluationIdAndInterviewStructureId("1", "2")).thenReturn(
				List.of());

		boolean doesInterviewRequiresApproval = this.interviewUtil.doesInterviewRequireApproval(interview1,
				jobRoleToInterviewStructureDAO1);
		assertTrue(doesInterviewRequiresApproval);
	}

	@Test
	public void doesInterviewRequiresApprovalFalseForOldJobRoleInCaseNextRoundIsAlreadyPresent() {
		InterviewDAO interview1 = InterviewDAO.builder().id("1").evaluationId("1").status("Done")
				.interviewStructureId("1").build();
		JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO1 = JobRoleToInterviewStructureDAO.builder()
				.jobRoleId("1").jobRoleVersion(1).interviewStructureId("1").orderIndex(0).acceptanceCutoffScore(300)
				.rejectionCutoffScore(100).build();

		when(this.evaluationRepository.findById("1"))
				.thenReturn(Optional.of(EvaluationDAO.builder().id("1").status("status").build()));
		when(this.bgsScoreFetcher.getBgsScoreForInterview("1")).thenReturn(200);
		when(this.jobRoleToInterviewStructureRepository
				.findTopByJobRoleIdAndJobRoleVersionAndOrderIndexGreaterThanOrderByOrderIndexAsc("1", 1, 0)).thenReturn(
						Optional.of(JobRoleToInterviewStructureDAO.builder().jobRoleId("1").jobRoleVersion(1)
								.interviewStructureId("2").orderIndex(1).build()));
		when(this.jobRoleToInterviewStructureRepository.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId("1", 1,
				"1")).thenReturn(
						Optional.of(jobRoleToInterviewStructureDAO1));
		when(this.interViewRepository.findAllByEvaluationIdAndInterviewStructureId("1", "2")).thenReturn(
				List.of(InterviewDAO.builder().id("2").evaluationId("1").status("pending_scheduling")
						.interviewStructureId("2").build()));

		boolean doesInterviewRequiresApproval = this.interviewUtil.doesInterviewRequireApproval(interview1,
				jobRoleToInterviewStructureDAO1);
		assertFalse(doesInterviewRequiresApproval);
	}

	@Test
	public void doesInterviewRequiresApprovalFalseForBgsScoreGreaterThanCutOff() {
		InterviewDAO interview1 = InterviewDAO.builder().id("1").evaluationId("1").status("Done")
				.interviewStructureId("1").build();
		JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO1 = JobRoleToInterviewStructureDAO.builder()
				.jobRoleId("1").jobRoleVersion(1).interviewStructureId("1").orderIndex(0).acceptanceCutoffScore(300)
				.rejectionCutoffScore(100).build();
		when(this.evaluationRepository.findById("1"))
				.thenReturn(Optional.of(EvaluationDAO.builder().id("1").status("status").build()));
		when(this.bgsScoreFetcher.getBgsScoreForInterview("1")).thenReturn(700);

		when(this.jobRoleToInterviewStructureRepository.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId("1", 1,
				"1")).thenReturn(
						Optional.of(jobRoleToInterviewStructureDAO1));

		boolean doesInterviewRequiresApproval = this.interviewUtil.doesInterviewRequireApproval(interview1,
				jobRoleToInterviewStructureDAO1);
		assertFalse(doesInterviewRequiresApproval);
	}

	@Test
	public void doesInterviewRequiresApprovalFalseForBgsScoreLessThanThreshold() {
		InterviewDAO interview1 = InterviewDAO.builder().id("1").evaluationId("1").status("Done")
				.interviewStructureId("1").build();
		JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO1 = JobRoleToInterviewStructureDAO.builder()
				.jobRoleId("1").jobRoleVersion(1).interviewStructureId("1").orderIndex(0).acceptanceCutoffScore(300)
				.rejectionCutoffScore(100).build();
		when(this.evaluationRepository.findById("1"))
				.thenReturn(Optional.of(EvaluationDAO.builder().id("1").status("status").build()));
		when(this.bgsScoreFetcher.getBgsScoreForInterview("1")).thenReturn(50);

		when(this.jobRoleToInterviewStructureRepository.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId("1", 1,
				"1")).thenReturn(
						Optional.of(jobRoleToInterviewStructureDAO1));

		boolean doesInterviewRequiresApproval = this.interviewUtil.doesInterviewRequireApproval(interview1,
				jobRoleToInterviewStructureDAO1);
		assertFalse(doesInterviewRequiresApproval);
	}

	@Test
	public void doesInterviewRequiresApprovalFalseForBgsScoreLiesBetweenCutOffAndThreshold() {
		InterviewDAO interview1 = InterviewDAO.builder().id("1").evaluationId("1").status("Done")
				.interviewStructureId("1").build();
		JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO1 = JobRoleToInterviewStructureDAO.builder()
				.jobRoleId("1").jobRoleVersion(1).interviewStructureId("1").orderIndex(0).acceptanceCutoffScore(300)
				.rejectionCutoffScore(100).build();
		when(this.evaluationRepository.findById("1"))
				.thenReturn(Optional.of(EvaluationDAO.builder().id("1").status("status").build()));
		when(this.bgsScoreFetcher.getBgsScoreForInterview("1")).thenReturn(200);
		when(this.jobRoleToInterviewStructureRepository
				.findTopByJobRoleIdAndJobRoleVersionAndOrderIndexGreaterThanOrderByOrderIndexAsc("1", 1, 0)).thenReturn(
						Optional.of(JobRoleToInterviewStructureDAO.builder().jobRoleId("1").jobRoleVersion(1)
								.interviewStructureId("2").orderIndex(1).build()));
		when(this.jobRoleToInterviewStructureRepository.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId("1", 1,
				"1")).thenReturn(
						Optional.of(jobRoleToInterviewStructureDAO1));
		when(this.interViewRepository.findAllByEvaluationIdAndInterviewStructureId("1", "2")).thenReturn(
				List.of());

		boolean doesInterviewRequiresApproval = this.interviewUtil.doesInterviewRequireApproval(interview1,
				jobRoleToInterviewStructureDAO1);
		assertTrue(doesInterviewRequiresApproval);
	}

	@Test
	public void doesInterviewRequiresApprovalForNewJobRoleWhereOnlyManualFlagIsTrueAndCriteriaAreNull() {
		InterviewDAO interview1 = InterviewDAO.builder().id("1").evaluationId("1").status("Done")
				.interviewStructureId("1").build();
		JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO1 = JobRoleToInterviewStructureDAO.builder()
				.jobRoleId("1").jobRoleVersion(1).interviewStructureId("1").orderIndex(0).approvalRuleId(null)
				.rejectionRuleId(null).isManualActionForRemainingCases(true).build();
		when(this.evaluationRepository.findById("1"))
				.thenReturn(Optional.of(EvaluationDAO.builder().id("1").status("status").build()));
		when(this.bgsScoreFetcher.getBgsScoreForInterview("1")).thenReturn(200);
		when(this.jobRoleToInterviewStructureRepository
				.findTopByJobRoleIdAndJobRoleVersionAndOrderIndexGreaterThanOrderByOrderIndexAsc("1", 1, 0)).thenReturn(
						Optional.of(JobRoleToInterviewStructureDAO.builder().jobRoleId("1").jobRoleVersion(1)
								.interviewStructureId("2").orderIndex(1).build()));
		when(this.jobRoleToInterviewStructureRepository.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId("1", 1,
				"1")).thenReturn(
						Optional.of(jobRoleToInterviewStructureDAO1));
		when(this.interViewRepository.findAllByEvaluationIdAndInterviewStructureId("1", "2")).thenReturn(
				List.of());

		boolean doesInterviewRequiresApproval = this.interviewUtil.doesInterviewRequireApproval(interview1,
				jobRoleToInterviewStructureDAO1);
		assertTrue(doesInterviewRequiresApproval);
	}

	@Test
	public void doesInterviewRequiresApprovalWhenApprovalIsNotNullRejectionIsNULLAndManualFlagIsTrueAndApprovalCriteriaMeets()
			throws ParseException {
		InterviewDAO interview1 = InterviewDAO.builder().id("1").evaluationId("1").status("Done")
				.interviewStructureId("1").build();
		JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO1 = JobRoleToInterviewStructureDAO.builder()
				.jobRoleId("1").jobRoleVersion(1).interviewStructureId("1").orderIndex(0).approvalRuleId("ruleId1")
				.rejectionRuleId(null).isManualActionForRemainingCases(true).build();
		when(this.evaluationRepository.findById("1"))
				.thenReturn(Optional.of(EvaluationDAO.builder().id("1").status("status").build()));
		when(this.jobRoleToInterviewStructureRepository.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId("1", 1,
				"1")).thenReturn(
						Optional.of(jobRoleToInterviewStructureDAO1));
		when(this.interviewRoundClearanceRuleChecker.isInterviewApproved(jobRoleToInterviewStructureDAO1, interview1))
				.thenReturn(true);
		boolean doesInterviewRequiresApproval = this.interviewUtil.doesInterviewRequireApproval(interview1,
				jobRoleToInterviewStructureDAO1);
		assertFalse(doesInterviewRequiresApproval);
	}

	@Test
	public void doesInterviewRequiresApprovalWhenApprovalIsNotNullRejectionIsNULLAndManualFlagIsTrueAndApprovalCriteriaNotMeetsMeets()
			throws ParseException {
		InterviewDAO interview1 = InterviewDAO.builder().id("1").evaluationId("1").status("Done")
				.interviewStructureId("1").build();
		JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO1 = JobRoleToInterviewStructureDAO.builder()
				.jobRoleId("1").jobRoleVersion(1).interviewStructureId("1").orderIndex(0).approvalRuleId("ruleId1")
				.rejectionRuleId(null).isManualActionForRemainingCases(true).build();
		when(this.evaluationRepository.findById("1"))
				.thenReturn(Optional.of(EvaluationDAO.builder().id("1").status("status").build()));
		when(this.jobRoleToInterviewStructureRepository.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId("1", 1,
				"1")).thenReturn(
						Optional.of(jobRoleToInterviewStructureDAO1));
		when(this.interViewRepository.findAllByEvaluationIdAndInterviewStructureId("1", "2")).thenReturn(
				List.of());
		when(this.jobRoleToInterviewStructureRepository
				.findTopByJobRoleIdAndJobRoleVersionAndOrderIndexGreaterThanOrderByOrderIndexAsc("1", 1, 0)).thenReturn(
						Optional.of(JobRoleToInterviewStructureDAO.builder().jobRoleId("1").jobRoleVersion(1)
								.interviewStructureId("2").orderIndex(1).build()));
		when(this.interviewRoundClearanceRuleChecker.isInterviewApproved(jobRoleToInterviewStructureDAO1, interview1))
				.thenReturn(false);
		boolean doesInterviewRequiresApproval = this.interviewUtil.doesInterviewRequireApproval(interview1,
				jobRoleToInterviewStructureDAO1);
		assertTrue(doesInterviewRequiresApproval);
	}

	@Test
	public void doesInterviewRequiresApprovalWhenBothCriteriaAreNotNullAndOneOfThemMeets() throws ParseException {
		InterviewDAO interview1 = InterviewDAO.builder().id("1").evaluationId("1").status("Done")
				.interviewStructureId("1").build();
		JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO1 = JobRoleToInterviewStructureDAO.builder()
				.jobRoleId("1").jobRoleVersion(1).interviewStructureId("1").orderIndex(0).approvalRuleId("ruleId1")
				.rejectionRuleId("ruleId2").isManualActionForRemainingCases(true).build();
		when(this.evaluationRepository.findById("1"))
				.thenReturn(Optional.of(EvaluationDAO.builder().id("1").status("status").build()));
		when(this.jobRoleToInterviewStructureRepository.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId("1", 1,
				"1")).thenReturn(
						Optional.of(jobRoleToInterviewStructureDAO1));
		when(this.interviewRoundClearanceRuleChecker.isInterviewApproved(jobRoleToInterviewStructureDAO1, interview1))
				.thenReturn(true);
		when(this.interviewRoundClearanceRuleChecker.isInterviewRejected(jobRoleToInterviewStructureDAO1, interview1))
				.thenReturn(false);
		boolean doesInterviewRequiresApproval = this.interviewUtil.doesInterviewRequireApproval(interview1,
				jobRoleToInterviewStructureDAO1);
		assertFalse(doesInterviewRequiresApproval);
	}

	@Test
	public void doesInterviewRequiresApprovalWhenBothCriteriaAreNotNullAndBothOfCriteriaMeets() throws ParseException {
		InterviewDAO interview1 = InterviewDAO.builder().id("1").evaluationId("1").status("Done")
				.interviewStructureId("1").build();
		JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO1 = JobRoleToInterviewStructureDAO.builder()
				.jobRoleId("1").jobRoleVersion(1).interviewStructureId("1").orderIndex(0).approvalRuleId("ruleId1")
				.rejectionRuleId("ruleId2").isManualActionForRemainingCases(true).build();
		when(this.evaluationRepository.findById("1"))
				.thenReturn(Optional.of(EvaluationDAO.builder().id("1").status("status").build()));
		when(this.jobRoleToInterviewStructureRepository.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId("1", 1,
				"1")).thenReturn(
						Optional.of(jobRoleToInterviewStructureDAO1));
		when(this.interViewRepository.findAllByEvaluationIdAndInterviewStructureId("1", "2")).thenReturn(
				List.of());
		when(this.jobRoleToInterviewStructureRepository
				.findTopByJobRoleIdAndJobRoleVersionAndOrderIndexGreaterThanOrderByOrderIndexAsc("1", 1, 0)).thenReturn(
						Optional.of(JobRoleToInterviewStructureDAO.builder().jobRoleId("1").jobRoleVersion(1)
								.interviewStructureId("2").orderIndex(1).build()));
		when(this.interviewRoundClearanceRuleChecker.isInterviewApproved(jobRoleToInterviewStructureDAO1, interview1))
				.thenReturn(true);
		when(this.interviewRoundClearanceRuleChecker.isInterviewRejected(jobRoleToInterviewStructureDAO1, interview1))
				.thenReturn(true);
		boolean doesInterviewRequiresApproval = this.interviewUtil.doesInterviewRequireApproval(interview1,
				jobRoleToInterviewStructureDAO1);
		assertTrue(doesInterviewRequiresApproval);
	}

	@Test
	public void doesInterviewRequiresApprovalWhenBothCriteriaAreNotNullAndNoneOfCriteriaMeets() throws ParseException {
		InterviewDAO interview1 = InterviewDAO.builder().id("1").evaluationId("1").status("Done")
				.interviewStructureId("1").build();
		JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO1 = JobRoleToInterviewStructureDAO.builder()
				.jobRoleId("1").jobRoleVersion(1).interviewStructureId("1").orderIndex(0).approvalRuleId("ruleId1")
				.rejectionRuleId("ruleId2").isManualActionForRemainingCases(true).build();
		when(this.evaluationRepository.findById("1"))
				.thenReturn(Optional.of(EvaluationDAO.builder().id("1").status("status").build()));
		when(this.jobRoleToInterviewStructureRepository.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId("1", 1,
				"1")).thenReturn(
						Optional.of(jobRoleToInterviewStructureDAO1));
		when(this.interViewRepository.findAllByEvaluationIdAndInterviewStructureId("1", "2")).thenReturn(
				List.of());
		when(this.jobRoleToInterviewStructureRepository
				.findTopByJobRoleIdAndJobRoleVersionAndOrderIndexGreaterThanOrderByOrderIndexAsc("1", 1, 0)).thenReturn(
						Optional.of(JobRoleToInterviewStructureDAO.builder().jobRoleId("1").jobRoleVersion(1)
								.interviewStructureId("2").orderIndex(1).build()));
		when(this.interviewRoundClearanceRuleChecker.isInterviewApproved(jobRoleToInterviewStructureDAO1, interview1))
				.thenReturn(true);
		when(this.interviewRoundClearanceRuleChecker.isInterviewRejected(jobRoleToInterviewStructureDAO1, interview1))
				.thenReturn(true);
		boolean doesInterviewRequiresApproval = this.interviewUtil.doesInterviewRequireApproval(interview1,
				jobRoleToInterviewStructureDAO1);
		assertTrue(doesInterviewRequiresApproval);
	}

	@Test
	public void doesInterviewRequiresApprovalWhenAutoApprovalIsConfigured() throws ParseException {
		InterviewDAO interview1 = InterviewDAO.builder().id("1").evaluationId("1").status("Done")
				.interviewStructureId("1").build();
		JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO1 = JobRoleToInterviewStructureDAO.builder()
				.jobRoleId("1").jobRoleVersion(1).interviewStructureId("1").orderIndex(0).approvalRuleId(null)
				.rejectionRuleId("ruleId2").isManualActionForRemainingCases(false).build();
		when(this.evaluationRepository.findById("1"))
				.thenReturn(Optional.of(EvaluationDAO.builder().id("1").status("status").build()));
		when(this.jobRoleToInterviewStructureRepository.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId("1", 1,
				"1")).thenReturn(
						Optional.of(jobRoleToInterviewStructureDAO1));
		when(this.interviewRoundClearanceRuleChecker.isInterviewRejected(jobRoleToInterviewStructureDAO1, interview1))
				.thenReturn(false);
		boolean doesInterviewRequiresApproval = this.interviewUtil.doesInterviewRequireApproval(interview1,
				jobRoleToInterviewStructureDAO1);
		assertFalse(doesInterviewRequiresApproval);
	}

	@Test
	public void doesInterviewRequiresApprovalWhenAutoRejectionIsConfigured() throws ParseException {
		InterviewDAO interview1 = InterviewDAO.builder().id("1").evaluationId("1").status("Done")
				.interviewStructureId("1").build();
		JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO1 = JobRoleToInterviewStructureDAO.builder()
				.jobRoleId("1").jobRoleVersion(1).interviewStructureId("1").orderIndex(0).approvalRuleId("ruleId1")
				.rejectionRuleId(null).isManualActionForRemainingCases(false).build();
		when(this.evaluationRepository.findById("1"))
				.thenReturn(Optional.of(EvaluationDAO.builder().id("1").status("status").build()));
		when(this.jobRoleToInterviewStructureRepository.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId("1", 1,
				"1")).thenReturn(
						Optional.of(jobRoleToInterviewStructureDAO1));
		when(this.interviewRoundClearanceRuleChecker.isInterviewApproved(jobRoleToInterviewStructureDAO1, interview1))
				.thenReturn(false);
		boolean doesInterviewRequiresApproval = this.interviewUtil.doesInterviewRequireApproval(interview1,
				jobRoleToInterviewStructureDAO1);
		assertFalse(doesInterviewRequiresApproval);
	}

	@Test
	public void shouldReturnEmptyListIfNoOverlapOnRight() {
		when(this.interViewRepository.findAllByIntervieweeId("1")).thenReturn(
				List.of(InterviewDAO.builder().startDate(1637620200L).endDate(1637623800L).build()));
		final List<InterviewDAO> interviewDAOS = this.interviewUtil.getOverlappingInterviewsForCandidate("1",
				1637616600L, 1637620200L);
		assertEquals(0, interviewDAOS.size());
	}

	@Test
	public void shouldReturnEmptyListIfNoOverlapOnLeft() {
		when(this.interViewRepository.findAllByIntervieweeId("1")).thenReturn(
				List.of(InterviewDAO.builder().startDate(1637613000L).endDate(1637616600L).build()));
		final List<InterviewDAO> interviewDAOS = this.interviewUtil.getOverlappingInterviewsForCandidate("1",
				1637616600L, 1637620200L);
		assertEquals(0, interviewDAOS.size());
	}

	@Test
	public void shouldReturnEmptyListIfNoOverlappingInterviews() {
		when(this.interViewRepository.findAllByIntervieweeId("1")).thenReturn(List.of());
		final List<InterviewDAO> interviewDAOS = this.interviewUtil.getOverlappingInterviewsForCandidate("1",
				1637616600L, 1637620200L);
		assertEquals(0, interviewDAOS.size());
	}

	@Test
	public void shouldReturnListIfOverlapInBetween() {
		when(this.interViewRepository.findAllByIntervieweeId("1")).thenReturn(
				List.of(InterviewDAO.builder().startDate(1637614800L).endDate(1637618400L).build(),
						InterviewDAO.builder().startDate(1637620200L).endDate(1637623800L).build()));
		final List<InterviewDAO> interviewDAOS = this.interviewUtil.getOverlappingInterviewsForCandidate("1",
				1637613000L, 1637620200L);
		assertEquals(1, interviewDAOS.size());
		assertEquals(java.util.Optional.of(1637614800L), java.util.Optional.of(interviewDAOS.get(0).getStartDate()));
		assertEquals(java.util.Optional.of(1637618400L), java.util.Optional.of(interviewDAOS.get(0).getEndDate()));
	}

	@Test
	public void shouldReturnListIfCompleteOverlap() {
		when(this.interViewRepository.findAllByIntervieweeId("1")).thenReturn(
				List.of(InterviewDAO.builder().startDate(1637613000L).endDate(1637620200L).build(),
						InterviewDAO.builder().startDate(1637620200L).endDate(1637623800L).build()));
		final List<InterviewDAO> interviewDAOS = this.interviewUtil.getOverlappingInterviewsForCandidate("1",
				1637613000L, 1637620200L);
		assertEquals(1, interviewDAOS.size());
		assertEquals(java.util.Optional.of(1637613000L), java.util.Optional.of(interviewDAOS.get(0).getStartDate()));
		assertEquals(java.util.Optional.of(1637620200L), java.util.Optional.of(interviewDAOS.get(0).getEndDate()));
	}

	@Test
	public void shouldReturnListIfLeftOverlap() {
		when(this.interViewRepository.findAllByIntervieweeId("1")).thenReturn(
				List.of(InterviewDAO.builder().startDate(1637613000L).endDate(1637620200L).build(),
						InterviewDAO.builder().startDate(1637620200L).endDate(1637623800L).build()));
		final List<InterviewDAO> interviewDAOS = this.interviewUtil.getOverlappingInterviewsForCandidate("1",
				1637613000L, 1637620200L);
		assertEquals(1, interviewDAOS.size());
		assertEquals(java.util.Optional.of(1637613000L), java.util.Optional.of(interviewDAOS.get(0).getStartDate()));
		assertEquals(java.util.Optional.of(1637620200L), java.util.Optional.of(interviewDAOS.get(0).getEndDate()));
	}

	@Test
	public void shouldReturnListIfRightOverlap() {
		when(this.interViewRepository.findAllByIntervieweeId("1")).thenReturn(
				List.of(InterviewDAO.builder().startDate(1637614800L).endDate(1637618400L).build(),
						InterviewDAO.builder().startDate(1637615700L).endDate(1637619300L).build()));
		final List<InterviewDAO> interviewDAOS = this.interviewUtil.getOverlappingInterviewsForCandidate("1",
				1637613000L, 1637620200L);
		assertEquals(2, interviewDAOS.size());
		assertEquals(java.util.Optional.of(1637614800L), java.util.Optional.of(interviewDAOS.get(0).getStartDate()));
		assertEquals(java.util.Optional.of(1637618400L), java.util.Optional.of(interviewDAOS.get(0).getEndDate()));
		assertEquals(java.util.Optional.of(1637615700L), java.util.Optional.of(interviewDAOS.get(1).getStartDate()));
		assertEquals(java.util.Optional.of(1637619300L), java.util.Optional.of(interviewDAOS.get(1).getEndDate()));
	}

}
