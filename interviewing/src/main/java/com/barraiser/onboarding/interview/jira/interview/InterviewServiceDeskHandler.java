/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.jira.interview;

import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.*;
import com.barraiser.onboarding.scheduling.cancellation.StartCancellationStepFunctionProcessor;
import com.barraiser.onboarding.interview.jira.JiraEventHandler;
import com.barraiser.onboarding.interview.jira.JiraUtil;
import com.barraiser.onboarding.interview.jira.client.JiraClient;
import com.barraiser.onboarding.interview.jira.dto.*;
import com.barraiser.onboarding.payment.expert.InterviewServiceDeskProcessingData;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

/**
 * Created for Interview Issue in Jira Service Desk (Evaluations Board) => proxy
 * for Interview Issue
 * in CP board
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class InterviewServiceDeskHandler implements JiraEventHandler {
	public static final String JIRA_ISSUE_TYPE_ID_INTERVIEW_SERVICE_DESK = "10103";
	public static final String ACTION_TAKEN_BY_SYSTEM = "SYSTEM";

	private final JiraUtil jiraUtil;
	private final JiraUUIDRepository jiraUUIDRepository;
	private final EvaluationRepository evaluationRepository;
	private final InterviewProcessQualityRepository interviewProcessQualityRepository;
	private final JiraClient jiraClient;
	private final JobRoleToInterviewStructureRepository jobRoleToInterviewStructureRepository;
	private final StartCancellationStepFunctionProcessor startCancellationStepFunctionProcessor;
	private final InterviewEventGenerator interviewEventGenerator;
	private final InterviewService interviewService;
	private final CommentUtil commentUtil;

	@Override
	public String projectId() {
		return JIRA_ISSUE_TYPE_ID_INTERVIEW_SERVICE_DESK;
	}

	@Override
	public void handleEvent(final JiraEvent event) throws Exception {
		if (JiraConstants.JIRA_COMMENTS_STATUS.contains(event.getBody().getWebhookEvent())) {
			this.commentUtil.actionCommentToDB(event, event.getBody().getWebhookEvent());
			return;
		}
		final InterviewServiceDeskIssue issue = this.jiraClient.getInterviewServiceDeskIssue(event.getIssue());
		final InterviewServiceDeskProcessingData data = new InterviewServiceDeskProcessingData();
		final String interviewId = this.getIdForInterview(issue);
		if (interviewId == null) {
			return;
		}

		final InterviewServiceDeskIssue.Fields fields = issue.getFields();
		final String statusFromEvent = fields.getStatus() == null ? null : fields.getStatus().getName();
		final Optional<EvaluationDAO> evaluation = this.getEvaluation(issue, interviewId);
		if (evaluation.isEmpty()) {
			return;
		}
		final InterviewDAO savedInterview = this.interviewService
				.findById(interviewId);

		final String statusFromDB = savedInterview.getStatus();
		InterviewDAO interview = this.updateFieldsToBeSynced(savedInterview, fields, statusFromEvent);
		if (this.isInterviewCancelledInCurrentSync(statusFromDB, statusFromEvent)) {
			interview = this.captureInterviewCancellationTime(interview, fields);
		}
		interview = this.interviewService.save(interview);
		data.setInterview(interview);
		if (this.isInterviewCancelledInCurrentSync(statusFromDB, statusFromEvent)) {
			if (this.doesInterviewNeedCancellationProcessing(statusFromDB, statusFromEvent)) {
				this.startCancellationStepFunctionProcessor.process(data);
				this.interviewEventGenerator.sendInterviewCancellationEvent(interview);
			} else {
				interview = this.interviewService
						.save(interview.toBuilder().status(InterviewStatus.CANCELLATION_DONE.getValue())
								.isPendingScheduling(Boolean.FALSE)
								.build());
			}
		}
		this.updateQualityOfInterview(interviewId, fields);
	}

	private String getIdForInterview(final InterviewServiceDeskIssue interview) {
		String interviewId = interview.getFields().getEntityId();
		final String rescheduleCountFromJira = interview.getFields().getRescheduleCount();
		final Integer rescheduleCount = rescheduleCountFromJira == null ? null
				: Integer.parseInt(rescheduleCountFromJira);
		if (interviewId.contains("unlinked")) {
			return null;
		}

		if (Strings.isBlank(interviewId)) {
			interviewId = this.jiraUtil.getOrCreateIdAgainstJira(interview.getKey()).getUuid();
		}
		final InterviewDAO interviewDAO = this.interviewService.findById(interviewId);
		if (rescheduleCount != null && !interviewDAO.getRescheduleCount().equals(rescheduleCount)) {
			return null;
		}
		return interviewId;
	}

	private boolean doesInterviewNeedCancellationProcessing(
			final String statusFromDB, final String statusFromEvent) {
		log.info(
				"The previous interview status was {}, Current interview Status is {}",
				statusFromDB,
				statusFromEvent);

		// If it is scheduled only then it needs cancellation processing like slot
		// cancellation,
		// zoom canellation etc.
		if (InterviewStatus.fromString(statusFromDB).isScheduled()) {
			return true;
		}
		return false;
	}

	private boolean isInterviewCancelledInCurrentSync(
			final String statusFromDB, final String statusFromEvent) {

		if (!InterviewStatus.CANCELLATION_DONE.getValue().equalsIgnoreCase(statusFromDB)
				&& InterviewStatus.CANCELLATION_DONE.getValue().equalsIgnoreCase(statusFromEvent)) {
			return true;
		}

		return false;
	}

	private String getParentUUID(final InterviewServiceDeskIssue issue, final String jiraUuid) {

		final EvaluationServiceDeskIssue linkedEvaluation = !issue.getFields().getLinkedEvaluations().isEmpty()
				? issue.getFields().getLinkedEvaluations().get(0).getInwardIssue()
				: null;
		if (linkedEvaluation == null || linkedEvaluation.getKey() == null) {
			return null;
		}

		final String parentJiraId = linkedEvaluation.getKey();

		final Optional<JiraUUIDDAO> parentJiraUUID = this.jiraUUIDRepository.findByJira(parentJiraId);

		this.evaluationRepository
				.findById(parentJiraUUID.get().getUuid())
				.orElseThrow(
						() -> new RuntimeException(
								String.format(
										"No evaluation exist in database for interview id:"
												+ " %s",
										jiraUuid)));

		// Its highly unlikely that parent does not exisevaluatt and hence the
		// optional.get()
		// throwing exception would not be
		// the worst thing in the world.
		return parentJiraUUID.get().getUuid();
	}

	public void checkIfInterviewStructureIsPartOfJobRole(
			final String interviewStructureId,
			final String jobRoleId,
			final Integer jobRoleVersion) {
		this.jobRoleToInterviewStructureRepository
				.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId(
						jobRoleId, jobRoleVersion, interviewStructureId)
				.orElseThrow(
						() -> new IllegalArgumentException(
								"interview structure "
										+ interviewStructureId
										+ " not part of job role "
										+ jobRoleId));
	}

	private String getInterviewErrorFromJiraEvent(final InterviewServiceDeskIssue.Fields fields) {

		if (fields.getInterviewErrorReported() != null) {
			return fields.getInterviewErrorReported().stream()
					.map(IdValueField::getValue)
					.map(String::toLowerCase)
					.sorted()
					.collect(Collectors.joining(","));
		}

		return "";
	}

	private void updateQualityOfInterview(final String interviewId, final InterviewServiceDeskIssue.Fields fields) {
		final Integer taggingQuality = fields.getTaggingQuality() == null
				? null
				: Integer.parseInt(fields.getTaggingQuality().getValue());

		final InterviewProcessQualityDAO interviewProcessQualityDAO = this.interviewProcessQualityRepository
				.findByInterviewId(interviewId)
				.orElse(
						InterviewProcessQualityDAO.builder()
								.id(UUID.randomUUID().toString())
								.interviewId(interviewId)
								.build());

		this.interviewProcessQualityRepository.save(
				interviewProcessQualityDAO.toBuilder()
						.taggingQuality(taggingQuality)
						.interviewErrorReported(this.getInterviewErrorFromJiraEvent(fields))
						.errorDescription(fields.getErrorDescription())
						.build());
	}

	private InterviewDAO captureInterviewCancellationTime(final InterviewDAO interview,
			final InterviewServiceDeskIssue.Fields fields) {

		final String cancellationReasonId = fields.getCancellationReason() == null
				? null
				: this.jiraUtil.getIdFromString(fields.getCancellationReason().getValue());
		final Long cancellationTime = fields.getCancellationTime() == null
				? null
				: fields.getCancellationTime().toEpochSecond();
		return interview.toBuilder()
				.cancellationTime(
						cancellationTime == null
								? String.valueOf(Instant.now().getEpochSecond())
								: String.valueOf(cancellationTime))
				.cancellationReasonId(cancellationReasonId)
				.build();
	}

	private InterviewDAO updateFieldsToBeSynced(InterviewDAO interviewDAO,
			final InterviewServiceDeskIssue.Fields fields, final String statusFromEvent) {
		final String opsRep = fields.getAssignee() != null ? fields.getAssignee().getDisplayName() : null;
		final Boolean isRescheduled = !(fields.getIsRescheduled() == null || fields.getIsRescheduled().isEmpty());
		final String taggingAgent = fields.getTaggingAgent() == null ? null
				: this.jiraUtil.getIdFromString(fields.getTaggingAgent().getValue());
		if (InterviewStatus.PENDING_INTERVIEWING.getValue().equals(statusFromEvent)) {
			interviewDAO = interviewDAO.toBuilder().status(statusFromEvent).build();
		}

		return interviewDAO.toBuilder()
				.opsRep(opsRep)
				.operatedBy(ACTION_TAKEN_BY_SYSTEM)
				.isRescheduled(isRescheduled)
				.taggingAgent(taggingAgent)
				.rescheduledFrom(fields.getRescheduledFrom())
				.build();
	}

	private Optional<EvaluationDAO> getEvaluation(final InterviewServiceDeskIssue issue, final String interviewId) {
		final String parentUUID = this.getParentUUID(issue, interviewId);
		final Optional<EvaluationDAO> evaluation = parentUUID != null
				? this.evaluationRepository.findById(parentUUID)
				: Optional.empty();

		return evaluation;
	}
}
