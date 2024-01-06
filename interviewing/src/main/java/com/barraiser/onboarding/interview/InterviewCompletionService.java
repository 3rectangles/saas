/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.amazonaws.services.sqs.AmazonSQS;
import com.barraiser.onboarding.common.Constants;
import com.barraiser.onboarding.common.StaticAppConfigValues;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.InterviewStatus;
import com.barraiser.onboarding.interview.evaluation.BgsDataGenerator;
import com.barraiser.onboarding.interview.jira.JiraWorkflowManager;
import com.barraiser.onboarding.interview.jira.interview.InterviewEventGenerator;
import com.barraiser.onboarding.interview.pojo.PostInterviewCompletionNoteData;
import com.barraiser.onboarding.payment.expert.InterviewConcludedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@RequiredArgsConstructor
@Component
public class InterviewCompletionService {
	private final InterviewService interviewService;
	private final InterviewEventGenerator interviewEventGenerator;
	private final JiraWorkflowManager jiraWorkflowManager;
	private final AmazonSQS amazonSQS;
	private final StaticAppConfigValues staticAppConfigValues;
	private final ObjectMapper objectMapper;
	private final BgsDataGenerator bgsDataGenerator;
	private final AtsInterviewManager atsInterviewManager;
	private final InterviewUtil interviewUtil;

	public InterviewDAO finishInterview(InterviewDAO interviewDAO) throws JsonProcessingException {
		if (!InterviewStatus.DONE.getValue().equals(interviewDAO.getStatus())) {
			this.bgsDataGenerator.generateBgsDataForInterview(interviewDAO);
			interviewDAO = interviewDAO.toBuilder().status(InterviewStatus.DONE.getValue()).build();
			this.interviewService.save(interviewDAO);
			this.interviewEventGenerator.sendInterviewCompletionEvent(interviewDAO);
			this.jiraWorkflowManager.transitionJiraStatus(interviewDAO.getId(), InterviewStatus.DONE.getValue());
			this.calculateCostForDoneInterview(interviewDAO);

			// Only for Saas Interviews
			if (interviewUtil.isSaasInterview(interviewDAO.getInterviewRound())) {
				this.atsInterviewManager.postInterviewCompletionNote(PostInterviewCompletionNoteData.builder()
						.interviewId(interviewDAO.getId())
						.interviewRound(interviewDAO.getInterviewRound())
						.evaluationId(interviewDAO.getEvaluationId())
						.partnerId(interviewDAO.getPartnerId())
						.interviewerId(interviewDAO.getInterviewerId())
						.build());
			}
		}
		return interviewDAO;
	}

	public void calculateCostForDoneInterview(final InterviewDAO interviewDAO) throws JsonProcessingException {
		if (!Constants.ROUND_TYPE_INTERNAL.equals(interviewDAO.getInterviewRound())
				&& interviewDAO.getDuplicateReason() == null) {
			final InterviewConcludedEvent event = InterviewConcludedEvent.builder()
					.interviewId(interviewDAO.getId())
					.interviewStatus(InterviewStatus.DONE.getValue())
					.rescheduleCount(interviewDAO.getRescheduleCount())
					.interviewerId(interviewDAO.getInterviewerId())
					.interviewStartDate(interviewDAO.getStartDate())
					.feedbackSubmissionTime(interviewDAO.getExpertFeedbackSubmissionTime() != null
							? interviewDAO.getExpertFeedbackSubmissionTime()
							: interviewDAO.getFeedbackSubmissionTime())
					.build();
			this.amazonSQS.sendMessage(
					this.staticAppConfigValues.getExpertPaymentCalculationEventQueueUrl(),
					this.objectMapper.writeValueAsString(event));
		}
	}
}
