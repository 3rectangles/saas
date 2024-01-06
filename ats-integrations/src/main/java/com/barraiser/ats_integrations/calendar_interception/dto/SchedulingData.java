/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.calendar_interception.dto;

import com.barraiser.common.ats_integrations.ATSProvider;
import com.barraiser.common.graphql.types.Partner;
import com.barraiser.commons.dto.ats.enums.ATSAggregator;
import com.barraiser.commons.eventing.schema.commons.calendar.BRCalendarEvent;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SchedulingData {
	BRCalendarEvent brCalendarEvent;

	Boolean isCalendarInterceptionEnabled;

	ATSProvider atsProvider;

	String partnerId;

	String interviewerId; // set in InterviewerCreationProcessor

	String brInterviewId; // set in InterviewLifecycleManagementProcessor

	String interviewerEmailId;

	List<String> pocEmails;

	Long interviewStart;

	Long interviewEnd;

	String interviewTimezone;

	String atsEvaluationId; // set in processor

	String brEvaluationId; // set in processor

	String atsJobRoleId;

	String brJobRoleId;

	String atsInterviewId;

	String atsInterviewStructureId;

	String brInterviewStructureId;

	String atsExpertInterviewLandingPageLink;

	String atsMeetingLink;

	String originalInviteBody;

	String originalInviteEventId; // set in processor

	Map<String, String> inviteVariableValueMapping;

	Map<String, String> replacementInviteVariableValueMapping; // variables that are to be injected in the replacement
	// invite
	ATSAggregator atsAggregator;

	Double interviewDuration;

	CandidateDetails candidateDetails;

	String brCandidateId;

	String remoteData;

	String atsInterviewFeedbackLink;

	List<String> interviewAttendeeEmails;
}
