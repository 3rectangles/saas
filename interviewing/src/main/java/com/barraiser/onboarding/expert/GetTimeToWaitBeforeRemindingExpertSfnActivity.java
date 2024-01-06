/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.expert;

import com.barraiser.common.utilities.DateUtils;
import com.barraiser.onboarding.config.DynamicAppConfigProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class GetTimeToWaitBeforeRemindingExpertSfnActivity implements ExpertReminderSfnActivity {
	private static final String DYNAMO_TIME_TO_WAIT_BEFORE_REMINDING_EXPERT_FOR_INTERVIEW_IN_MINUTES = "wait-time-before-reminding-expert-for-interview";

	private final ObjectMapper objectMapper;
	private final DynamicAppConfigProperties appConfigProperties;
	private final DateUtils dateUtils;

	@Override
	public String name() {
		return "get-time-to-wait-before-reminding-expert";
	}

	@Override
	public ExpertReminderData process(final String input) throws Exception {
		final ExpertReminderData data = this.objectMapper.readValue(input, ExpertReminderData.class);
		data.setTimestampToWaitBeforeRemindingExpert(
				this.dateUtils.getFormattedDateString(
						data.getInterviewDAO().getStartDate()
								- (long) this.appConfigProperties.getInt(
										DYNAMO_TIME_TO_WAIT_BEFORE_REMINDING_EXPERT_FOR_INTERVIEW_IN_MINUTES)
										* 60,
						DateUtils.TIMEZONE_UTC,
						DateUtils.DATEFORMAT_ISO_8601));
		data.setDurationBeforeInterviewInMinutesToSendReminder(this.appConfigProperties.getInt(
				DYNAMO_TIME_TO_WAIT_BEFORE_REMINDING_EXPERT_FOR_INTERVIEW_IN_MINUTES));
		return data;
	}
}
