/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.followup;

public class FollowUpConstants {

	public static final String GET_FOLLOW_STATUS_AND_SET_WAIT_TIME = "candidate-followup-for-scheduling-get-followup-status-and-set-wait-time";
	public static final String ADD_EXPIRY_MESSAGE_FOR_PARTNER = "candidate-followup-for-scheduling-add-expiry-message";
	public static final String GET_IVR_RESPONSE_STATUS = "candidate-followup-for-scheduling-get-ivr-response-status";
	public static final String CALL_CANDIDATE_MANUALLY = "candidate-followup-for-scheduling-call-manually-and-update-flag-in-jira";
	public static final String UPDATE_FOLLOW_UP_DATE_ON_JIRA = "candidate-followup-for-scheduling-update-follow-up-time-on-jira";

	public static final String DYNAMO_TIME_TO_WAIT_PER_TURN = "candidate-followup-for-scheduling-wait-time-per-turn";
	public static final String DYNAMO_NON_OPERATIONAL_TIME_START = "candidate-followup-for-scheduling-non-operational-time-start-hour";
	public static final String DYNAMO_NON_OPERATIONAL_TIME_END = "candidate-followup-for-scheduling-non-operational-time-end-hour";
	public static final String DYNAMO_FOLLOW_UP_EXPIRY_TIME_IN_MINUTES = "candidate-followup-for-scheduling-expiry-time-in-minutes";

	public static final Integer SECONDS_IN_A_MINUTE = 60;
	public static final Integer MINUTES_IN_AN_HOUR = 60;
	public static final String TIMEZONE_UTC = "UTC";
	public static final String TIMEZONE_ASIA_KOLKATA = "Asia/Kolkata";

	public static final String CALL_CANDIDATE_FOR_SCHEDULING = "2_call_the_Candidate";

	public static final String FLOW_TERMINATE_DUE_TO_STATUS_CHANGE = "FLOW_TERMINATE_DUE_TO_STATUS_CHANGE";
	public static final String FLOW_TERMINATE_DUE_TO_EXPIRY = "FLOW_TERMINATE_DUE_TO_EXPIRY";
	public static final String FLOW_CONTINUE = "FLOW_CONTINUE";

}
