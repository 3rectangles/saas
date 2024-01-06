/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.types.InterviewerFeedbackInput;
import com.barraiser.common.graphql.types.UserDetails;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.sendinterviewerfeedback.EmailCommunicationDetailsEvent;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.sendinterviewerfeedback.InterviewDetails;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.sendinterviewerfeedback.SendInterviewerFeedback;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.sendinterviewerfeedback.User;
import com.barraiser.onboarding.events.InterviewingEventProducer;
import com.barraiser.onboarding.user.UserInformationManagementHelper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;

@Log4j2
@Component
@AllArgsConstructor
public class SendInterviewerFeedbackEventGenerator {
	private final InterviewingEventProducer eventProducer;
	private final UserInformationManagementHelper userInformationManagementHelper;
	private final InterViewRepository interViewRepository;

	public void sendInterviewerFeedbackEvent(InterviewerFeedbackInput input, AuthenticatedUser authenticatedUser) {

		final UserDetails sender = this.userInformationManagementHelper
				.getUserDetailsById(authenticatedUser.getUserName());

		Event<SendInterviewerFeedback> event = new Event<>();

		List<String> receiverIds = new ArrayList<>();
		receiverIds.add(input.getInterviewerId());

		List<String> ccUserIds = input.getCcUserList();
		ccUserIds.add(authenticatedUser.getUserName());

		event.setPayload(
				new SendInterviewerFeedback()
						.sender(
								new User()
										.id(sender.getId())
										.email(sender.getEmail())
										.firstName(sender.getFirstName())
										.lastName(sender.getLastName()))
						.feedback(
								input.getFeedback())
						.partnerId(this.getPartnerId(input.getInterviewId())) // TODO:Remove partnerId check for event
																				// generation
						.interview(
								new InterviewDetails().id(input.getInterviewId()))
						.sentAtTime(
								Long.valueOf(Instant.now().getEpochSecond()).intValue())
						.emailCommunicationDetails(
								new EmailCommunicationDetailsEvent()
										.toUserIds(receiverIds)
										.ccUserIds(ccUserIds)));

		// TODO: this needs to be removed.
		event.setSource("com.barraiser.integration");
		event.setEventType("SendInterviewerFeedback");

		try {
			this.eventProducer.pushEvent(event);
		} catch (final Exception err) {
			log.error("ERROR" + err);
		}
	}

	private String getPartnerId(final String interviewId) {
		return this.interViewRepository.findById(interviewId).get().getPartnerId();
	}

}
