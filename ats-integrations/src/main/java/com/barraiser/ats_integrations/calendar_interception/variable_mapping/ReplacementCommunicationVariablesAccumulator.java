/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.calendar_interception.variable_mapping;

import com.barraiser.ats_integrations.calendar_interception.dto.SchedulingData;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ReplacementCommunicationVariablesAccumulator {

	private static final String INTERVIEW_JOINING_FALLBACK_LINK = "INTERVIEW_JOINING_FALLBACK_LINK";
	private static final String BR_INTERVIEW_FLOW_LINK = "BR_INTERVIEW_FLOW_LINK";
	private static final String BR_LAUNCH_ASSIST_PAGE = "BR_LAUNCH_ASSIST_PAGE";
	private static final String BR_INTERVIEW_FEEDBACK_LINK = "BR_INTERVIEW_FEEDBACK_LINK";
	private static final String ORIGINAL_EVENT = "ORIGINAL_EVENT";
	private static final String MEETING_LINK = "MEETING_LINK";

	public Map<String, String> getReplacementValues(final SchedulingData schedulingData) {
		final Map<String, String> replacementVariableValueMapping = new HashMap<>();

		// Also putting in all parsed variables incase we want to replace them back.
		replacementVariableValueMapping.put(INTERVIEW_JOINING_FALLBACK_LINK,
				schedulingData.getAtsMeetingLink());
		replacementVariableValueMapping.put(BR_INTERVIEW_FLOW_LINK, this.getInterviewFlowLink(schedulingData));
		replacementVariableValueMapping.put(BR_LAUNCH_ASSIST_PAGE, this.getLaunchAssistPageLink(schedulingData));
		replacementVariableValueMapping.put(BR_INTERVIEW_FEEDBACK_LINK,
				this.getBrInterviewFeedbackLink(schedulingData));
		replacementVariableValueMapping.put(ORIGINAL_EVENT,
				schedulingData.getBrCalendarEvent().getDescription() == null ? ""
						: schedulingData.getBrCalendarEvent().getDescription());
		replacementVariableValueMapping.put(MEETING_LINK, schedulingData.getAtsMeetingLink());

		return replacementVariableValueMapping;
	}

	private String getBrInterviewFeedbackLink(final SchedulingData schedulingData) {
		return "https://app.barraiser.com/interview-feedback/" + schedulingData.getBrInterviewId();
	}

	private String getInterviewFlowLink(final SchedulingData schedulingData) {
		if (schedulingData.getAtsInterviewStructureId() == null) {
			// Checking if Relaxed Interception or not
			return "https://app.barraiser.com/interview-landing/ec/" + schedulingData.getBrInterviewId();
		}

		return "https://app.barraiser.com/interview-landing/e/" + schedulingData.getBrInterviewId();
	}

	private String getLaunchAssistPageLink(final SchedulingData schedulingData) {
		return "https://app.barraiser.com/launch-assist/" + schedulingData.getBrInterviewId();
	}
}
