/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.sfn;

import com.barraiser.onboarding.common.StaticAppConfigValues;
import com.barraiser.onboarding.communication.channels.email.EmailService;
import com.barraiser.onboarding.communication.channels.slack.SlackService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Log4j2
@AllArgsConstructor
@Component
public class StepFunctionUtil {

	@Qualifier("applicationEnvironment")
	private final String applicationEnvironmet;
	private final StaticAppConfigValues staticAppConfigValues;
	private final EmailService emailService;
	private final SlackService slackService;

	public static final String CANCELLATION_FLOW_FAILURE_EMAIL_NOTIFICATION_PREFIX = "Cancellation Flow Failure for interview : ";
	public static final String FAILURE_EMAIL_NOTIFICATION_TEMPLATE = "step_function_step_failure";
	public static final String SCHEDULING_FLOW_FAILURE_EMAIL_NOTIFICATION_PREFIX = "Scheduling Flow Failure for interview : ";
	public static final String TA_SCHEDULING_FLOW_FAILURE_EMAIL_NOTIFICATION_PREFIX = "TA Scheduling Flow Failure for interview : ";
	public static final String CONFIRMATION_FLOW_FAILURE_EMAIL_NOTIFICATION_PREFIX = "Confirmation Flow Failure for interview : ";
	public static final String EXPERT_ALLOCATION_FLOW_FAILURE_EMAIL_NOTIFICATION_PREFIX = "Expert Allocation Flow Failure for interview : ";
	public static final String EXPERT_DEALLOCATION_FLOW_FAILURE_EMAIL_NOTIFICATION_PREFIX = "Expert DeAllocation Flow Failure for interview : ";
	public static final String EXPERT_REASSIGNMENT_FLOW_FAILURE_EMAIL_NOTIFICATION_PREFIX = "Expert Reassignment Flow Failure for interview : ";

	public void sendStepFailureEmail(final String interviewId, final String activityName, final Exception exception,
			final String flowNotificationPrefix) throws Exception {

		final Map<String, String> emailData = this.getStepFailureEmailData(interviewId, activityName, exception);

		final String fromEmail = this.staticAppConfigValues.getInterviewLifecycleInformationEmail();
		final String toEmail = this.staticAppConfigValues.getInterviewNotificationEmail();
		final String subject = this.applicationEnvironmet + ":" + flowNotificationPrefix;

		if (Boolean.getBoolean(this.staticAppConfigValues.getMonitoringEnabled())) {
			this.emailService.sendEmail(fromEmail, subject, FAILURE_EMAIL_NOTIFICATION_TEMPLATE, List.of(toEmail),
					emailData, null);
		}
	}

	public Map<String, String> getStepFailureEmailData(final String interviewId, final String activityName,
			final Exception exception) {
		final Map<String, String> data = Map.of(
				"interview_id", "" + interviewId,
				"activity_name", "" + activityName,
				"error_message", "" + ExceptionUtils.getStackTrace(exception));

		return data;
	}

	public void sendStepFailureSlackMessage(final String interviewId, final String activityName,
			final Exception exception, final String flowNotificationPrefix) throws Exception {
		final String subject = this.applicationEnvironmet + ":" + flowNotificationPrefix;
		final String body = "interview_id : " + interviewId + "\n" + "activity_name : " + activityName + "\n"
				+ ExceptionUtils.getStackTrace(exception);

		if (Boolean.getBoolean(this.staticAppConfigValues.getMonitoringEnabled())) {
			this.slackService.sendMessage(subject, body, this.staticAppConfigValues.getMonitoringSlackChannel());
		}
	}

}
