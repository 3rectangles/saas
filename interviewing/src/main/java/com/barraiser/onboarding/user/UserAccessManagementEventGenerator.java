/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.user;

import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.usergrantedaccess.CommunicationDetailsEvent;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.usergrantedaccess.Resource;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.usergrantedaccess.User;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.usergrantedaccess.UserGrantedAccess;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.events.InterviewingEventProducer;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Log4j2
@Component
@AllArgsConstructor
public class UserAccessManagementEventGenerator {
	private final InterviewingEventProducer eventProducer;
	private final UserDetailsRepository userDetailsRepository;
	private final CompanyManager companyManager;

	private final static String COMPONENT_NAME_PARTNER_PORTAL = "PARTNER_PORTAL";
	private final static String PARTNER_PORTAL_URL_EXPRESSION = "https://app.barraiser.com/customer/%s";

	/**
	 * When a partner rep is granted access
	 */
	public void sendUserAccessGrantedEvent(final String partnerRepId, final String partnerId,
			final String accessGrantorId) {

		final UserDetailsDAO partnerRep = this.userDetailsRepository.findById(partnerRepId).get();
		final UserDetailsDAO accessGrantor = this.userDetailsRepository.findById(accessGrantorId).get();

		final Event<UserGrantedAccess> event = new Event<>();
		event.setPayload(
				new UserGrantedAccess()
						.partnerId(partnerId)
						.user(new User()
								.id(partnerRep.getId())
								.firstName(partnerRep.getFirstName())
								.lastName(partnerRep.getLastName())
								.email(partnerRep.getEmail()))
						.resource(new Resource()
								.parentComponent(COMPONENT_NAME_PARTNER_PORTAL)
								.resourceUrl(String.format(PARTNER_PORTAL_URL_EXPRESSION,
										partnerId)))
						.grantedBy(new User()
								.id(accessGrantor.getId())
								.firstName(accessGrantor.getFirstName())
								.lastName(accessGrantor.getLastName())
								.email(accessGrantor.getEmail()))
						.grantedAtTime(Long.valueOf(Instant.now().getEpochSecond()).intValue())
						.communicationDetails(new CommunicationDetailsEvent().userId(partnerRep.getId())));

		try {
			this.eventProducer.pushEvent(event);
		} catch (final Exception err) {
			log.error(err);
		}
	}

}
