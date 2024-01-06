/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.AuditableBaseModel;
import com.barraiser.onboarding.audit.AuditListener;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vladmihalcea.hibernate.type.array.ListArrayType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;

@JsonIgnoreProperties(ignoreUnknown = true)
@EntityListeners(AuditListener.class)
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Table(name = "interview")
@TypeDef(name = "list-array", typeClass = ListArrayType.class)
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class InterviewDAO extends AuditableBaseModel {
	@Id
	private String id;

	@Column(name = "interviewer_id")
	private String interviewerId;

	@Column(name = "interviewee_id")
	private String intervieweeId;

	/**
	 * INTRODUCTORY, PEER, EXPERT
	 */
	@Column(name = "interview_round")
	private String interviewRound;

	@Column(name = "submitted_code_link")
	private String submittedCodeLink;

	@Column(name = "start_date")
	private Long startDate;

	@Column(name = "video_start_time")
	private Long videoStartTime;

	@Column(name = "video_end_time")
	private Long videoEndTime;

	@Column(name = "interview_start_time")
	private Long interviewStartTime;

	@Column(name = "end_date")
	private Long endDate;

	@Column(name = "actual_end_date")
	private Long actualEndDate;

	@Column(name = "last_question_end")
	private Long lastQuestionEnd;

	private String status;

	@Column(name = "youtubeLink")
	private String youtubeLink;

	@Column(name = "ops_rep")
	private String opsRep;

	@Column(name = "zoom_link")
	private String zoomLink;

	@Column(name = "feedback_status")
	private String feedbackStatus;

	@Column(name = "question_tagging_status")
	private String questionTaggingStatus;

	@Column(name = "video_link")
	private String videoLink;

	private Double rating;
	private Double duration;

	@Column(name = "remarks")
	private String remarks;

	@Column(name = "evaluation_id")
	private String evaluationId;

	@Column(name = "tagging_agent")
	private String taggingAgent;

	@Column(name = "cancellation_time")
	private String cancellationTime;

	@Column(name = "cancellation_reason")
	private String cancellationReason;

	@Column(name = "zoom_account_email")
	private String zoomAccountEmail;

	@Column(name = "zoom_end_time")
	private Long zoomEndTime;

	@Column(name = "cancellation_reason_id")
	private String cancellationReasonId;

	@Column(name = "expert_feedback_submission_time")
	private Long expertFeedbackSubmissionTime;

	@Column(name = "feedback_submission_time")
	private Long feedbackSubmissionTime;

	@Column(name = "is_rescheduled")
	private Boolean isRescheduled;

	@Column(name = "is_bad_quality")
	private Boolean isBadQuality;

	@Column(name = "rescheduled_from")
	private String rescheduledFrom;

	@Column(name = "scheduling_platform")
	private String schedulingPlatform;

	@Column(name = "interview_structure_id")
	private String interviewStructureId;

	@Column(name = "audio_link")
	private String audioLink;

	// TODO: need to remove (check interview cost)
	@Column(name = "actual_start_date")
	private Long actualStartDate;

	@Column(name = "duplicate_reason")
	private String duplicateReason;

	@Version
	private Long version;

	@Column(name = "reschedule_count")
	private Integer rescheduleCount;

	@Column(name = "interviewee_timezone")
	private String intervieweeTimezone;

	@Column(name = "partner_id")
	private String partnerId;

	@Column(name = "is_pending_scheduling")
	private Boolean isPendingScheduling;

	@Column(name = "reopening_reason_id")
	private String reopeningReasonId;

	@Column(name = "poc_email")
	private String pocEmail;

	@Column(name = "redo_reason_id")
	private String redoReasonId;

	private String meetingLink;

	private Boolean isTaggingAgentNeeded;

	@Column(name = "are_highlights_complete")
	private Boolean areHighlightsComplete;

	@Column(name = "ats_interview_feedback_link")
	private String atsInterviewFeedbackLink;
}
