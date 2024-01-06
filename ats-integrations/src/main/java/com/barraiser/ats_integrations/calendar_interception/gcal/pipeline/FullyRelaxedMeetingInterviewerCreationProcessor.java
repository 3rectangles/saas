/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.calendar_interception.gcal.pipeline;

import com.barraiser.ats_integrations.calendar_interception.SchedulingProcessing;
import com.barraiser.ats_integrations.calendar_interception.dto.SchedulingData;
import com.barraiser.ats_integrations.common.client.PartnerRepFeignClient;
import com.barraiser.common.graphql.input.PartnerAccessInput;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@AllArgsConstructor
@Component
public class FullyRelaxedMeetingInterviewerCreationProcessor implements SchedulingProcessing {

	private final PartnerRepFeignClient partnerRepFeignClient;

	@Override
	public void process(SchedulingData data) throws IOException {
		final String interviewerId = this.createInterviewer(data.getPartnerId(), data.getInterviewerEmailId());
		data.setInterviewerId(interviewerId);

		if (data.getInterviewAttendeeEmails() != null) {
			data.getInterviewAttendeeEmails().stream().forEach(attendeeEmail -> {
				this.createInterviewer(data.getPartnerId(), attendeeEmail);
			});
		}
	}

	private String createInterviewer(final String partnerId, final String interviewerEmailId) {

		// TODO:
		// Role should also be sent in input
		return this.partnerRepFeignClient.addInterviewer(PartnerAccessInput.builder()
				.firstName("INTERVIEWER")
				.lastName("")
				.email(interviewerEmailId)
				.partnerId(partnerId)
				.build()).getBody();
	}

}
