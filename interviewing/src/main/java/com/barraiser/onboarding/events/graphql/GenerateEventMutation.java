/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.events.graphql;

import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.EventParser;
import com.barraiser.commons.eventing.EventTypeMapper;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.conversationmessage.ConversationMessage;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.generateinterviewstructureevent.GenerateInterviewStructureEvent;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.interviewend.InterviewEnd;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.redoEvent.RedoInterviewEvent;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.sendSchedulingLinkEvent.SendSchedulingLinkEvent;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.sendinterviewerfeedback.SendInterviewerFeedback;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.userclick.UserClick;
import com.barraiser.onboarding.config.DynamicAppConfigProperties;
import com.barraiser.onboarding.events.InterviewingEventProducer;
import com.barraiser.onboarding.events.graphql.input.EventInput;
import com.barraiser.onboarding.graphql.GraphQLMutation;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class GenerateEventMutation implements GraphQLMutation {
	private final InterviewingEventProducer eventProducer;
	private final GraphQLUtil graphQLUtil;
	private final EventTypeMapper eventTypeMapper;
	private final EventParser eventParser = new EventParser();
	private final DynamicAppConfigProperties dynamicAppConfigProperties;
	private final String SUPPORTED_EVENTS_FOR_GENERATION_KEY = "supported-events-for-generation";

	@Override
	public String name() {
		return "generateEvent";
	}

	@Override
	public Object get(final DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
		final EventInput input = this.graphQLUtil.getInput(dataFetchingEnvironment, EventInput.class);

		final Object payload = this.checkIfEventTypeIsAllowedAndGetPayload(input);

		this.pushEvent(
				input.getEventType(),
				payload);

		return true;
	}

	private Object checkIfEventTypeIsAllowedAndGetPayload(final EventInput input) throws IOException {
		final List<String> allowedEventTypes = dynamicAppConfigProperties
				.getListOfString(SUPPORTED_EVENTS_FOR_GENERATION_KEY);
		final Class<?> eventTypeClass = this.eventTypeMapper.getEventTypeClass(input.getEventType());
		if (eventTypeClass == null || !allowedEventTypes.contains(input.getEventType())) {
			throw new IllegalArgumentException("You cannot send event of type " + input.getEventType());
		}
		return this.eventParser.parseEventPayload(input.getEvent(), eventTypeClass);
	}

	public void pushEvent(final String eventType, final Object payload)
			throws Exception {
		final Event<?> event = Event.builder()
				.eventType(eventType)
				.payload(payload)
				.build();

		this.eventProducer.pushEvent(event);
	}
}
