/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.common;

import java.util.HashMap;
import java.util.Map;

public class Constants {

	public static final String AWS_RESOURCE_ARN_PREFIX = "arn:aws:states:ap-south-1:969111487786";

	public static final String STATE_MACHINE_ARN = AWS_RESOURCE_ARN_PREFIX + ":stateMachine:%s-%s";

	public static final String PROCESS_TYPE_INTERVIEW = "Interview";

	public static final String USER_TYPE_EXPERT = "EXPERT";

	public static final String CANCELLATION_TYPE_CANDIDATE_AND_EXPERT = "CANDIDATE_EXPERT";
	public static final String CANCELLATION_REASON_ID_FOR_CANDIDATE_AND_EXPERT_DID_NOT_JOIN = "81";
	public static final String CANDIDATE_REASON_FOR_INTERVIEW_CANCELLED_BY_EXPERT_AND_CANDIDATE = "The candidate did not join the interview";
	public static final String CANDIDATE_CANCELLATION_TYPE_FOR_INTERVIEW_CANCELLED_BY_EXPERT_AND_CANDIDATE = "CANDIDATE";

	public static final String OVERALL_FEEDBACK_TYPE_STRENGTH = "OVERALL_STRENGTH";
	public static final String OVERALL_FEEDBACK_TYPE_AREAS_OF_IMPROVEMENT = "OVERALL_AREAS_OF_IMPROVEMENT";

	/**
	 * Module names like evaluation, feedback, interview scheduling etc
	 */
	public static final String EVALUATION_MODULE = "evaluation";

	public static final String SOFT_SKILL_ID = "52";

	public static final String OTHERS_SKILL_ID = "161";

	public static final String ROUND_TYPE_INTERNAL = "INTERNAL";

	public static final String ROUND_NOT_REQUIRED_REASON_ID = "33";

	public static final String CANCELLED_VIA_IVR_REASON_ID = "34";

	public static final String MONITORING_SLACK_CHANNEL = "#monitoring";

	public static final Long BUFFER_TA_ALLOCATION_PER_SLOT = 300L;

	public static final String GENERIC_EMAIL_TEMPLATE = "generic_template";

	public static final long SECONDS_IN_HOUR = 3600L;

	public static final long SECONDS_IN_MINUTE = 60L;

	public static Integer MAX_ES_RECORD_FETCH_SIZE = 10000;

	public static final Map<Integer, Long> mapTaSlotsWait = new HashMap<>();

	static {
		for (int hourOfTheDay = 0; hourOfTheDay < 24; hourOfTheDay++) {
			if (hourOfTheDay < 7) {
				mapTaSlotsWait.putIfAbsent(hourOfTheDay, BUFFER_TA_ALLOCATION_PER_SLOT);
			} else {
				mapTaSlotsWait.put(hourOfTheDay, mapTaSlotsWait.get(hourOfTheDay - 1) + BUFFER_TA_ALLOCATION_PER_SLOT);
			}
		}
	}

	public static final String OVERALL_FEEDBACK_TYPE_SOFT_SKILLS = "OVERALL_SOFT_SKILLS";

	public static final String JOBROLE_DRAFT_STATUS_ID = "1ee7832a-f552-4960-a2ae-0c931ae0b004";
	public static final String JOBROLE_ACTIVE_STATUS_ID = "b6da1cbb-f7e9-4925-a0a4-351cc8ec7e5b";
	public static final String JOBROLE_INACTIVE_STATUS_ID = "4fa008df-28f5-48ac-a465-180d60360a91";

	public static final String JOBROLE_INTELLIGENCE_DISABLED_STATUS_ID = "d53840f1-f03f-44df-a172-b27df1fdbe89";
	public static final String JOBROLE_INTELLIGENCE_ENABLE_INTERVIEWS_NOT_STRUCTURED_STATUS_ID = "d58298c8-47cf-49cc-a2a4-7175f22dd62b";
	public static final String JOBROLE_INTERVIEWS_STRUCTURED_STATUS_ID = "ec347550-8ff7-4086-b6c2-27ba06ed1039";

	public static final String PARTNERSHIP_MODEL_SAAS = "pure_saas";

	public static final String SAAS_TRIAL_PARTNERSHIP_MODEL_ID = "saas_trial";

	public static final String CONTEXT_KEY_USER_ID = "userId";
	public static final String CONTEXT_KEY_USER_AGENT = "userAgent";
	public static final String CONTEXT_KEY_SOURCE_IP = "sourceIp";
}
