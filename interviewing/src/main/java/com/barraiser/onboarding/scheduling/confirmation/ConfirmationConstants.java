/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.confirmation;

public class ConfirmationConstants {

	public static final String GET_INTERVIEW_INFORMATION = "get-interview-information-test-v2";
	public static final String GET_INTERVIEW_CONFIRMATION_STATUS = "get-interview-conf-status-before-test-v2";
	public static final String CALL_CANDIDATE_FOR_CONFIRMATION_MANUALLY = "call-candidate-for-confirmation-manually-v2";
	public static final String CALL_CANDIDATE_FOR_CONFIRMATION_PRIORITY_FLAG = "call_candidate_for_confirmation";
	public static final String GET_INTERVIEW_STATUS_FOR_REMINDER = "get-interview-status-for-reminder-v2";

	public static final String DYNAMO_TIME_TO_WAIT_PER_TURN = "interview-confirmation-candidate-wait-time-per-turn";
	public static final String DYNAMO_INTERVIEW_REMINDER_WAIT_TIME = "interview-reminder-candidate-wait-time";
	public static final String DYNAMO_NON_OPERATIONAL_TIME_START = "interview-confirmation-non-operational-time-start-hour";
	public static final String DYNAMO_NON_OPERATIONAL_TIME_END = "interview-confirmation-non-operational-time-end-hour";
	public static final String DYNAMO_TIME_BEFORE_INTERVIEW_FOR_PATH_ONE = "interview-confirmation-time-before-interview-for-path-one";
	public static final String DYNAMO_TIME_BEFORE_INTERVIEW_FOR_PATH_TWO = "interview-confirmation-time-before-interview-for-path-two";

	public static final String LIFECYCLE_PARAM_KEY_WORKFLOW_PATH = "workflowPath";
	public static final String LIFECYCLE_PARAM_KEY_TIMESTAMP_TO_WAIT_UNTIL = "timestampToWaitUntil";
	public static final String LIFECYCLE_PARAM_KEY_WORKFLOW_TURN = "workflowTurn";

	public static final String WORKFLOW_PATH_ONE = "1";
	public static final String WORKFLOW_PATH_TWO = "2";
	public static final int WORKFLOW_TURN_FOR_PATH_ONE = 1;
	public static final int WORKFLOW_TURN_FOR_PATH_TWO = 4;

	public static final Integer SECONDS_IN_A_MINUTE = 60;
	public static final Integer MINUTES_IN_AN_HOUR = 60;
	public static final String TIMEZONE_UTC = "UTC";
	public static final String TIMEZONE_ASIA_KOLKATA = "Asia/Kolkata";
	public static final int WORKFLOW_TURN_FOR_REMINDER_FLOW = 7;

}
