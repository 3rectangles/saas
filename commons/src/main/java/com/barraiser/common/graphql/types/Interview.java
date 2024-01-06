/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.types;

import com.barraiser.common.enums.MeetingPlatform;
import com.barraiser.common.model.Media;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Interview {

	private String id;
	private Integer roundNumber;
	private String interviewerId;
	private Interviewer interviewer;
	private String intervieweeId;
	private String interviewRound;
	private Long startDate;
	private Long videoStartTime;
	private Long endDate;
	private Interviewee interviewee;
	private String jobRoleId;

	@JsonSerialize(as = Long.class)
	private Long cancellationTime;

	private Long actualEndDate;
	private Long scheduledStartDate;
	private Long expertScheduledStartDate;
	private Long scheduledEndDate;
	private Long lastQuestionEnd;
	private String status;
	private String youtubeLink;
	private String interviewStructureId;
	private String opsRep;
	private String zoomLink;
	private String feedbackStatus;
	private String questionTaggingStatus;
	private String videoLink;
	private Double rating;
	private Long durationInMinutes;
	private String remarks;
	private String interviewStructureLink;
	private String evaluationId;
	private Integer jobRoleVersion;
	private String taggingAgent;
	private JobRole jobRole;
	private String cancellationReasonId;
	private String submittedCodeLink;
	private Instant createdOn;
	private String date;
	private String scoringAlgoVersion;
	private List<InterviewChangeHistory> interviewTimeLine;
	private Integer orderIndex;
	private Boolean isRescheduled;
	private String audioLink;
	private List<Media> media;
	private Boolean requiresApproval;
	private StatusType interviewStatus;
	private Evaluation evaluation;
	private InterviewRoundTypeConfiguration roundTypeConfiguration;
	private RoundLevelInterviewStructure roundLevelInterviewStructure;
	private Integer rescheduleCount;
	private String intervieweeTimezone;
	private String partnerId;
	private String message;
	private Long updatedOn;
	private Boolean isRedoEligible;
	private String redoReasonId;
	private String meetingLink;
	private Boolean areHighlightsComplete;
	private MeetingPlatform meetingPlatform;
	private String atsInterviewFeedbackLink;
}
