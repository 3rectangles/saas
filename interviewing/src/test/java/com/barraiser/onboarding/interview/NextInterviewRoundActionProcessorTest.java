/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.EvaluationDAO;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.JobRoleToInterviewStructureDAO;
import com.barraiser.onboarding.dal.JobRoleToInterviewStructureRepository;
import com.barraiser.onboarding.interview.evaluation.scores.BgsScoreFetcher;
import com.barraiser.onboarding.interview.ruleEngine.InterviewRoundClearanceRuleChecker;
import com.barraiser.onboarding.interview.status.EvaluationStatusManager;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NextInterviewRoundActionProcessorTest {

	@Mock
	private InterviewApprovalProcessor interviewApprovalProcessor;
	@Mock
	private BgsScoreFetcher bgsScoreFetcher;
	@Mock
	private InterviewRejectionProcessor interviewRejectionProcessor;
	@Mock
	private InterviewUtil interviewUtil;
	@Mock
	private JobRoleToInterviewStructureRepository jobRoleToInterviewStructureRepository;
	@Mock
	private InterviewRoundClearanceRuleChecker interviewRuleManager;

	@InjectMocks
	private NextInterviewRoundActionProcessor nextInterviewRoundActionProcessor;

	@Test
	public void autoApprovalForOldJobRole() throws ParseException {
		InterviewDAO interview = InterviewDAO.builder().id("1").interviewStructureId("1").build();
		EvaluationDAO evaluationDAO = EvaluationDAO.builder().id("1").jobRoleId("1").jobRoleVersion(0).build();
		when(this.interviewUtil.getEvaluationForInterview("1")).thenReturn(evaluationDAO);
		when(this.jobRoleToInterviewStructureRepository.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId(
				evaluationDAO.getJobRoleId(), evaluationDAO.getJobRoleVersion(), interview.getInterviewStructureId()))
						.thenReturn(
								Optional.of(JobRoleToInterviewStructureDAO.builder().id("1").acceptanceCutoffScore(500)
										.rejectionCutoffScore(100).build()));
		when(this.bgsScoreFetcher.getBgsScoreForInterview(interview.getId())).thenReturn(600);
		this.nextInterviewRoundActionProcessor.takeActionForNextRound(interview);
		verify(this.interviewApprovalProcessor, times(1)).approveInterview(interview.getId(),
				EvaluationStatusManager.BARRAISER_PARTNER_ID);
	}

	@Test
	public void autoRejectionForOldJobRole() throws ParseException {
		InterviewDAO interview = InterviewDAO.builder().id("1").interviewStructureId("1").build();
		AuthenticatedUser user = AuthenticatedUser.builder().userName("BarRaiser").build();
		EvaluationDAO evaluationDAO = EvaluationDAO.builder().id("1").jobRoleId("1").jobRoleVersion(0).build();
		when(this.interviewUtil.getEvaluationForInterview("1")).thenReturn(evaluationDAO);
		when(this.jobRoleToInterviewStructureRepository.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId(
				evaluationDAO.getJobRoleId(), evaluationDAO.getJobRoleVersion(), interview.getInterviewStructureId()))
						.thenReturn(
								Optional.of(JobRoleToInterviewStructureDAO.builder().id("1").acceptanceCutoffScore(500)
										.rejectionCutoffScore(100).build()));
		when(this.bgsScoreFetcher.getBgsScoreForInterview(interview.getId())).thenReturn(50);
		this.nextInterviewRoundActionProcessor.takeActionForNextRound(interview);
		verify(this.interviewRejectionProcessor, times(1)).rejectInterview(any(), any(), any());
	}

	@Test
	public void clientRequiresApprovalForOldJobRole() throws ParseException {
		InterviewDAO interview = InterviewDAO.builder().id("1").interviewStructureId("1").build();
		EvaluationDAO evaluationDAO = EvaluationDAO.builder().id("1").jobRoleId("1").jobRoleVersion(0).build();
		when(this.interviewUtil.getEvaluationForInterview("1")).thenReturn(evaluationDAO);
		when(this.jobRoleToInterviewStructureRepository.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId(
				evaluationDAO.getJobRoleId(), evaluationDAO.getJobRoleVersion(), interview.getInterviewStructureId()))
						.thenReturn(
								Optional.of(JobRoleToInterviewStructureDAO.builder().id("1").acceptanceCutoffScore(500)
										.rejectionCutoffScore(100).build()));
		when(this.bgsScoreFetcher.getBgsScoreForInterview(interview.getId())).thenReturn(300);
		this.nextInterviewRoundActionProcessor.takeActionForNextRound(interview);
		verify(this.interviewApprovalProcessor, times(1)).takeActionForApprovalRequiredFromClient(any());
	}

	@Test
	public void bothCriteriaAreNullAndManualFlagIsTrue() throws ParseException {
		InterviewDAO interview = InterviewDAO.builder().id("1").interviewStructureId("1").build();
		EvaluationDAO evaluationDAO = EvaluationDAO.builder().id("1").jobRoleId("1").jobRoleVersion(0).build();
		when(this.interviewUtil.getEvaluationForInterview("1")).thenReturn(evaluationDAO);
		when(this.jobRoleToInterviewStructureRepository.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId(
				evaluationDAO.getJobRoleId(), evaluationDAO.getJobRoleVersion(), interview.getInterviewStructureId()))
						.thenReturn(Optional.of(JobRoleToInterviewStructureDAO.builder().id("1").approvalRuleId(null)
								.rejectionRuleId(null).build()));
		when(interviewUtil.isManualActionRequiredFlagEnabled(any())).thenReturn(true);
		this.nextInterviewRoundActionProcessor.takeActionForNextRound(interview);
		verify(this.interviewApprovalProcessor, times(1)).takeActionForApprovalRequiredFromClient(any());
	}

	@Test
	public void shouldApproveWhenApprovalCriteriaMeetsRejectionCriteriaIsNULLAndManualFlagIsTrue()
			throws ParseException {
		InterviewDAO interview = InterviewDAO.builder().id("1").interviewStructureId("1").build();
		EvaluationDAO evaluationDAO = EvaluationDAO.builder().id("1").jobRoleId("1").jobRoleVersion(0).build();
		JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO = JobRoleToInterviewStructureDAO.builder().id("1")
				.approvalRuleId("1").rejectionRuleId(null).isManualActionForRemainingCases(true).build();
		when(this.interviewUtil.getEvaluationForInterview("1")).thenReturn(evaluationDAO);
		when(this.jobRoleToInterviewStructureRepository.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId(
				evaluationDAO.getJobRoleId(), evaluationDAO.getJobRoleVersion(), interview.getInterviewStructureId()))
						.thenReturn(Optional.of(jobRoleToInterviewStructureDAO));
		when(interviewRuleManager.isInterviewApproved(jobRoleToInterviewStructureDAO, interview)).thenReturn(true);

		this.nextInterviewRoundActionProcessor.takeActionForNextRound(interview);
		verify(this.interviewApprovalProcessor, times(1)).approveInterview(any(), any());
	}

	@Test
	public void shouldHandleManuallyWhenApprovalCriteriaDoesntMeetsRejectionCriteriaIsNULLAndManualFlagIsTrue()
			throws ParseException {
		InterviewDAO interview = InterviewDAO.builder().id("1").interviewStructureId("1").build();
		EvaluationDAO evaluationDAO = EvaluationDAO.builder().id("1").jobRoleId("1").jobRoleVersion(0).build();
		JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO = JobRoleToInterviewStructureDAO.builder().id("1")
				.approvalRuleId("1").rejectionRuleId(null).isManualActionForRemainingCases(true).build();
		when(this.interviewUtil.getEvaluationForInterview("1")).thenReturn(evaluationDAO);
		when(this.jobRoleToInterviewStructureRepository.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId(
				evaluationDAO.getJobRoleId(), evaluationDAO.getJobRoleVersion(), interview.getInterviewStructureId()))
						.thenReturn(Optional.of(jobRoleToInterviewStructureDAO));
		when(interviewRuleManager.isInterviewApproved(jobRoleToInterviewStructureDAO, interview)).thenReturn(false);
		when(interviewUtil.isManualActionRequiredFlagEnabled(any()))
				.thenReturn(jobRoleToInterviewStructureDAO.getIsManualActionForRemainingCases());

		this.nextInterviewRoundActionProcessor.takeActionForNextRound(interview);
		verify(this.interviewApprovalProcessor, times(1)).takeActionForApprovalRequiredFromClient(evaluationDAO);
	}

	@Test
	public void shouldRejectWhenRejectionCriteriaMeetsApprovalCriteriaIsNULLAndManualFlagIsTrue()
			throws ParseException {
		InterviewDAO interview = InterviewDAO.builder().id("1").interviewStructureId("1").build();
		EvaluationDAO evaluationDAO = EvaluationDAO.builder().id("1").jobRoleId("1").jobRoleVersion(0).build();
		JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO = JobRoleToInterviewStructureDAO.builder().id("1")
				.approvalRuleId(null).rejectionRuleId("1").isManualActionForRemainingCases(true).build();
		when(this.interviewUtil.getEvaluationForInterview("1")).thenReturn(evaluationDAO);
		when(this.jobRoleToInterviewStructureRepository.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId(
				evaluationDAO.getJobRoleId(), evaluationDAO.getJobRoleVersion(), interview.getInterviewStructureId()))
						.thenReturn(Optional.of(jobRoleToInterviewStructureDAO));
		when(interviewRuleManager.isInterviewRejected(jobRoleToInterviewStructureDAO, interview)).thenReturn(true);

		this.nextInterviewRoundActionProcessor.takeActionForNextRound(interview);
		verify(this.interviewRejectionProcessor, times(1)).rejectInterview(any(), any(), any());
	}

	@Test
	public void shouldHandleManuallyWhenRejectionCriteriaDoesntMeetsApprovalCriteriaIsNULLAndManualFlagIsTrue()
			throws ParseException {
		InterviewDAO interview = InterviewDAO.builder().id("1").interviewStructureId("1").build();
		EvaluationDAO evaluationDAO = EvaluationDAO.builder().id("1").jobRoleId("1").jobRoleVersion(0).build();
		JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO = JobRoleToInterviewStructureDAO.builder().id("1")
				.approvalRuleId("1").rejectionRuleId(null).isManualActionForRemainingCases(true).build();
		when(this.interviewUtil.getEvaluationForInterview("1")).thenReturn(evaluationDAO);
		when(this.jobRoleToInterviewStructureRepository.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId(
				evaluationDAO.getJobRoleId(), evaluationDAO.getJobRoleVersion(), interview.getInterviewStructureId()))
						.thenReturn(Optional.of(jobRoleToInterviewStructureDAO));
		when(interviewRuleManager.isInterviewRejected(jobRoleToInterviewStructureDAO, interview)).thenReturn(false);
		when(interviewUtil.isManualActionRequiredFlagEnabled(any()))
				.thenReturn(jobRoleToInterviewStructureDAO.getIsManualActionForRemainingCases());

		this.nextInterviewRoundActionProcessor.takeActionForNextRound(interview);
		verify(this.interviewApprovalProcessor, times(1)).takeActionForApprovalRequiredFromClient(evaluationDAO);
	}

	@Test
	public void shouldHandleManuallyWhenBothCriteriaExistsAndBothMeets() throws ParseException {
		InterviewDAO interview = InterviewDAO.builder().id("1").interviewStructureId("1").build();
		EvaluationDAO evaluationDAO = EvaluationDAO.builder().id("1").jobRoleId("1").jobRoleVersion(0).build();
		JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO = JobRoleToInterviewStructureDAO.builder().id("1")
				.approvalRuleId("1").rejectionRuleId("2").isManualActionForRemainingCases(true).build();
		when(this.interviewUtil.getEvaluationForInterview("1")).thenReturn(evaluationDAO);
		when(this.jobRoleToInterviewStructureRepository.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId(
				evaluationDAO.getJobRoleId(), evaluationDAO.getJobRoleVersion(), interview.getInterviewStructureId()))
						.thenReturn(Optional.of(jobRoleToInterviewStructureDAO));
		when(interviewRuleManager.isInterviewApproved(jobRoleToInterviewStructureDAO, interview)).thenReturn(true);
		when(interviewRuleManager.isInterviewRejected(jobRoleToInterviewStructureDAO, interview)).thenReturn(true);

		this.nextInterviewRoundActionProcessor.takeActionForNextRound(interview);
		verify(this.interviewApprovalProcessor, times(1)).takeActionForApprovalRequiredFromClient(evaluationDAO);
	}

	@Test
	public void shouldHandleManuallyWhenBothCriteriaExistsAndBothDoesntMeets() throws ParseException {
		InterviewDAO interview = InterviewDAO.builder().id("1").interviewStructureId("1").build();
		EvaluationDAO evaluationDAO = EvaluationDAO.builder().id("1").jobRoleId("1").jobRoleVersion(0).build();
		JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO = JobRoleToInterviewStructureDAO.builder().id("1")
				.approvalRuleId("1").rejectionRuleId("2").isManualActionForRemainingCases(true).build();
		when(this.interviewUtil.getEvaluationForInterview("1")).thenReturn(evaluationDAO);
		when(this.jobRoleToInterviewStructureRepository.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId(
				evaluationDAO.getJobRoleId(), evaluationDAO.getJobRoleVersion(), interview.getInterviewStructureId()))
						.thenReturn(Optional.of(jobRoleToInterviewStructureDAO));
		when(interviewRuleManager.isInterviewApproved(jobRoleToInterviewStructureDAO, interview)).thenReturn(false);
		when(interviewRuleManager.isInterviewRejected(jobRoleToInterviewStructureDAO, interview)).thenReturn(false);
		when(interviewUtil.isManualActionRequiredFlagEnabled(any()))
				.thenReturn(jobRoleToInterviewStructureDAO.getIsManualActionForRemainingCases());

		this.nextInterviewRoundActionProcessor.takeActionForNextRound(interview);
		verify(this.interviewApprovalProcessor, times(1)).takeActionForApprovalRequiredFromClient(evaluationDAO);
	}

	@Test
	public void shouldApproveWhenBothCriteriaExistsAndOnlyApprovalCriteriaMeets() throws ParseException {
		InterviewDAO interview = InterviewDAO.builder().id("1").interviewStructureId("1").build();
		EvaluationDAO evaluationDAO = EvaluationDAO.builder().id("1").jobRoleId("1").jobRoleVersion(0).build();
		JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO = JobRoleToInterviewStructureDAO.builder().id("1")
				.approvalRuleId("1").rejectionRuleId("2").isManualActionForRemainingCases(true).build();
		when(this.interviewUtil.getEvaluationForInterview("1")).thenReturn(evaluationDAO);
		when(this.jobRoleToInterviewStructureRepository.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId(
				evaluationDAO.getJobRoleId(), evaluationDAO.getJobRoleVersion(), interview.getInterviewStructureId()))
						.thenReturn(Optional.of(jobRoleToInterviewStructureDAO));
		when(interviewRuleManager.isInterviewApproved(jobRoleToInterviewStructureDAO, interview)).thenReturn(true);
		when(interviewRuleManager.isInterviewRejected(jobRoleToInterviewStructureDAO, interview)).thenReturn(false);

		this.nextInterviewRoundActionProcessor.takeActionForNextRound(interview);
		verify(this.interviewApprovalProcessor, times(1)).approveInterview(any(), any());
	}

	@Test
	public void shouldRejectWhenBothCriteriaExistsAndOnlyRejectionCriteriaMeets() throws ParseException {
		InterviewDAO interview = InterviewDAO.builder().id("1").interviewStructureId("1").build();
		EvaluationDAO evaluationDAO = EvaluationDAO.builder().id("1").jobRoleId("1").jobRoleVersion(0).build();
		JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO = JobRoleToInterviewStructureDAO.builder().id("1")
				.approvalRuleId("1").rejectionRuleId("2").isManualActionForRemainingCases(true).build();
		when(this.interviewUtil.getEvaluationForInterview("1")).thenReturn(evaluationDAO);
		when(this.jobRoleToInterviewStructureRepository.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId(
				evaluationDAO.getJobRoleId(), evaluationDAO.getJobRoleVersion(), interview.getInterviewStructureId()))
						.thenReturn(Optional.of(jobRoleToInterviewStructureDAO));
		when(interviewRuleManager.isInterviewApproved(jobRoleToInterviewStructureDAO, interview)).thenReturn(false);
		when(interviewRuleManager.isInterviewRejected(jobRoleToInterviewStructureDAO, interview)).thenReturn(true);

		this.nextInterviewRoundActionProcessor.takeActionForNextRound(interview);
		verify(this.interviewRejectionProcessor, times(1)).rejectInterview(any(), any(), any());
	}

	@Test
	public void shouldAutomaticApproveWhenRejectionCriteriaExistsApprovalDoesntExistsAndManualFlagIsFalse()
			throws ParseException {
		InterviewDAO interview = InterviewDAO.builder().id("1").interviewStructureId("1").build();
		EvaluationDAO evaluationDAO = EvaluationDAO.builder().id("1").jobRoleId("1").jobRoleVersion(0).build();
		JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO = JobRoleToInterviewStructureDAO.builder().id("1")
				.approvalRuleId(null).rejectionRuleId("2").isManualActionForRemainingCases(false).build();
		when(this.interviewUtil.getEvaluationForInterview("1")).thenReturn(evaluationDAO);
		when(this.jobRoleToInterviewStructureRepository.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId(
				evaluationDAO.getJobRoleId(), evaluationDAO.getJobRoleVersion(), interview.getInterviewStructureId()))
						.thenReturn(Optional.of(jobRoleToInterviewStructureDAO));
		when(interviewRuleManager.isInterviewRejected(jobRoleToInterviewStructureDAO, interview)).thenReturn(false);
		when(interviewUtil.isManualActionRequiredFlagEnabled(any()))
				.thenReturn(jobRoleToInterviewStructureDAO.getIsManualActionForRemainingCases());

		this.nextInterviewRoundActionProcessor.takeActionForNextRound(interview);
		verify(this.interviewApprovalProcessor, times(1)).approveInterview(any(), any());
	}

	@Test
	public void shouldAutomaticRejectWhenApprovalCriteriaExistsRejectionDoesntExistsAndManualFlagIsFalse()
			throws ParseException {
		InterviewDAO interview = InterviewDAO.builder().id("1").interviewStructureId("1").build();
		EvaluationDAO evaluationDAO = EvaluationDAO.builder().id("1").jobRoleId("1").jobRoleVersion(0).build();
		JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO = JobRoleToInterviewStructureDAO.builder().id("1")
				.approvalRuleId("1").rejectionRuleId(null).isManualActionForRemainingCases(false).build();
		when(this.interviewUtil.getEvaluationForInterview("1")).thenReturn(evaluationDAO);
		when(this.jobRoleToInterviewStructureRepository.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId(
				evaluationDAO.getJobRoleId(), evaluationDAO.getJobRoleVersion(), interview.getInterviewStructureId()))
						.thenReturn(Optional.of(jobRoleToInterviewStructureDAO));
		when(interviewRuleManager.isInterviewRejected(jobRoleToInterviewStructureDAO, interview)).thenReturn(false);
		when(interviewUtil.isManualActionRequiredFlagEnabled(any()))
				.thenReturn(jobRoleToInterviewStructureDAO.getIsManualActionForRemainingCases());

		this.nextInterviewRoundActionProcessor.takeActionForNextRound(interview);
		verify(this.interviewRejectionProcessor, times(1)).rejectInterview(any(), any(), any());
	}

}
