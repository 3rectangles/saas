/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.communication;

import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.EventListener;
import com.barraiser.commons.eventing.EventTypeMapper;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.question_tagging_submission_event.QuestionTaggingSubmissionEvent;
import com.barraiser.commons.eventing.schema.zoom.zoom_recording_stopped_event.ZoomRecordingStoppedEvent;
import com.barraiser.onboarding.dal.DyteMeetingDAO;
import com.barraiser.onboarding.dal.DyteMeetingRepository;
import com.barraiser.onboarding.dal.QuestionRepository;
import com.barraiser.onboarding.dyte.DyteUtils;
import com.barraiser.onboarding.events.InterviewingConsumer;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.interview.InterviewUtil;
import com.barraiser.onboarding.interview.jira.JiraWorkflowManager;
import com.barraiser.onboarding.interview.jira.dto.InterviewServiceDeskIssue;
import com.barraiser.onboarding.interview.jira.dto.JiraCommentDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@Log4j2
@AllArgsConstructor
public class PostInterviewConductionCommunicationService implements EventListener<InterviewingConsumer> {

	private final String TAGGING_COMPLETED_BEFORE_RECORDING_COMMENT_BODY = "Hi team,\n" +
			"The recording is not complete whereas tagging agent has submitted the questions. Kindly check the interview.";
	private final String INTERVIEW_FINISHED_EARLY_RECORDING_ENDED_FIRST_COMMENT_BODY = "Hi Team,\n" +
			"Interview has ended early. Recording has ended. Kindly check the interview.";
	private final String INTERVIEW_FINISHED_EARLY_TAGGING_COMPLETED_FIRST_COMMENT_BODY = "Hi Team,\n" +
			"Interview has ended early. Tagging agent has submitted the sheet. Kindly check the interview.";
	private final String PRIORITY_FLAG_TO_NOTIFY_HOSTING_TEAM = "hosting_team_alert";

	private final EventTypeMapper eventTypeMapper;
	private final ObjectMapper objectMapper;
	private final InterViewRepository interViewRepository;
	private final JiraWorkflowManager jiraWorkflowManager;
	private final InterviewUtil interviewUtil;
	private final QuestionRepository questionRepository;

	@Override
	public List<Class> eventsToListen() {
		return List.of(QuestionTaggingSubmissionEvent.class, ZoomRecordingStoppedEvent.class);
	}

	@Override
	public void handleEvent(Event event) throws Exception {
		if (event.getEventType().equals(this.eventTypeMapper.getEventTypeName(QuestionTaggingSubmissionEvent.class))) {
			this.handleQuestionTaggingSubmittedEvent(event);
		} else {
			this.handleZoomRecordingStoppedEvent(event);
		}
	}

	private void handleQuestionTaggingSubmittedEvent(final Event event) {
		final QuestionTaggingSubmissionEvent questionTaggingSubmissionEvent = this.objectMapper
				.convertValue(event.getPayload(), QuestionTaggingSubmissionEvent.class);
		final InterviewDAO interview = this.interViewRepository
				.findById(questionTaggingSubmissionEvent.getInterview().getId()).get();
		if (!this.isInterviewRecordingComplete(interview)) {
			this.communicateOnJira(interview.getId(), this.TAGGING_COMPLETED_BEFORE_RECORDING_COMMENT_BODY);
			final Long duration = event.getTimestamp() - interview.getStartDate();
			if (this.isDurationLessThanExpected(interview, duration)) {
				this.communicateOnJira(interview.getId(),
						this.INTERVIEW_FINISHED_EARLY_TAGGING_COMPLETED_FIRST_COMMENT_BODY);
			}
		}
	}

	private void handleZoomRecordingStoppedEvent(final Event event) {
		final ZoomRecordingStoppedEvent zoomRecordingStoppedEvent = this.objectMapper.convertValue(event.getPayload(),
				ZoomRecordingStoppedEvent.class);
		final Long meetingId = zoomRecordingStoppedEvent.getPayload().getObject().getId();
		final InterviewDAO interview = this.interviewUtil.getInterviewFromZoomMeetingId(meetingId.toString());
		if (interview == null) {
			return;
		}
		final Long recordingEndTime = zoomRecordingStoppedEvent.getPayload().getObject().getRecordingFile()
				.getRecordingEnd().toInstant().getEpochSecond();
		final Long duration = recordingEndTime - interview.getStartDate();
		if (this.isDurationLessThanExpected(interview, duration) && !this.isQuestionTaggingComplete(interview)) {
			this.communicateOnJira(interview.getId(), this.INTERVIEW_FINISHED_EARLY_RECORDING_ENDED_FIRST_COMMENT_BODY);
		}
	}

	private Boolean isInterviewRecordingComplete(final InterviewDAO interview) {
		return interview.getVideoEndTime() != null;
	}

	private Boolean isDurationLessThanExpected(final InterviewDAO interview, final Long actualDuration) {
		final Long expectedDuration = interview.getEndDate() - interview.getStartDate();
		return expectedDuration / 2 >= actualDuration;
	}

	private Boolean isQuestionTaggingComplete(final InterviewDAO interview) {
		return this.questionRepository.findAllByInterviewId(interview.getId()).size() > 0;
	}

	private void communicateOnJira(final String interviewId, final String msg) {
		this.jiraWorkflowManager.addCommentInJira(interviewId, JiraCommentDTO.builder().body(msg).build());
		this.setPriorityFlagOnJira(interviewId);
	}

	private void setPriorityFlagOnJira(final String interviewId) {
		final InterviewServiceDeskIssue currentIssue = this.jiraWorkflowManager.getInterviewIssue(interviewId);
		final List<String> priorityFlags = Objects.requireNonNullElse(currentIssue.getFields().getPriorityFlags(),
				new ArrayList<>());
		if (!priorityFlags.contains(this.PRIORITY_FLAG_TO_NOTIFY_HOSTING_TEAM)) {
			priorityFlags.add(this.PRIORITY_FLAG_TO_NOTIFY_HOSTING_TEAM);
		}
		final InterviewServiceDeskIssue.Fields updatedFields = InterviewServiceDeskIssue.Fields.builder()
				.priorityFlags(priorityFlags)
				.build();

		this.jiraWorkflowManager.setInterviewFieldsInJira(interviewId, updatedFields);
	}
}
