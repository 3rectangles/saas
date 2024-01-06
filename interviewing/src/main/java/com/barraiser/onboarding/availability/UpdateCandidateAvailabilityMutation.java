/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.availability;

import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.availabilitychangeevent.AvailabilityChangeEvent;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.availabilitychangeevent.SlotEvent;
import com.barraiser.onboarding.auth.AuthorizationResourceDTO;
import com.barraiser.onboarding.auth.Authorizer;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.InterviewStatus;
import com.barraiser.onboarding.events.InterviewingEventProducer;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.InterviewService;
import com.barraiser.onboarding.interview.auth.InterviewAuthorizer;
import com.barraiser.onboarding.interview.jira.JiraWorkflowManager;
import com.fasterxml.jackson.databind.ObjectMapper;

import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class UpdateCandidateAvailabilityMutation implements NamedDataFetcher {
	private final GraphQLUtil graphQLUtil;
	private final ObjectMapper objectMapper;
	private final CandidateAvailabilityManager candidateAvailabilityManager;
	private final InterviewingEventProducer eventProducer;
	private final Authorizer authorizer;
	private final JiraWorkflowManager jiraWorkflowManager;
	private final InterViewRepository interViewRepository;
	private final InterviewService interviewService;

	@Override
	public String name() {
		return "updateCandidateAvailability";
	}

	@Override
	public String type() {
		return MUTATION_TYPE;
	}

	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {
		final AuthenticatedUser user = this.graphQLUtil.getLoggedInUser(environment);
		final CandidateAvailabilityInput input = this.graphQLUtil.getInput(environment,
				CandidateAvailabilityInput.class);
		final AuthorizationResourceDTO authorizationResource = AuthorizationResourceDTO.builder()
				.type(InterviewAuthorizer.RESOURCE_TYPE)
				.resource(input.getInterviewId())
				.build();
		this.authorizer.can(
				user,
				InterviewAuthorizer.ACTION_READ_AND_WRITE_PREFERRED_SLOTS,
				authorizationResource);
		final List<AvailabilitySlot> slots = input.getInputSlots().stream()
				.map(x -> this.objectMapper.convertValue(x, AvailabilitySlot.class))
				.collect(Collectors.toList());
		this.candidateAvailabilityManager.updateSlots(
				user.getUserName(), input.getInterviewId(), slots);
		this.transitionStatus(
				input.getInterviewId(), user.getUserName());
		this.sendUpdateAvailabilityEvent(user.getUserName(), input.getInterviewId(), slots);
		return DataFetcherResult.newResult().data(true).build();
	}

	private void sendUpdateAvailabilityEvent(
			final String userId, final String interviewId, final List<AvailabilitySlot> slots)
			throws Exception {
		final Event<AvailabilityChangeEvent> event = new Event<>();
		event.setPayload(
				new AvailabilityChangeEvent()
						.contextId(interviewId)
						.contextType("INTERVIEW")
						.userId(userId)
						.operation("UPDATE")
						.slots(
								slots.stream()
										.map(
												x -> this.objectMapper.convertValue(
														x, SlotEvent.class))
										.collect(Collectors.toList())));
		this.eventProducer.pushEvent(event);
	}

	private void transitionStatus(final String interviewId, final String userId) {
		InterviewDAO interviewDAO = this.interViewRepository.findById(interviewId).get();
		if (InterviewStatus.PENDING_SCHEDULING.getValue().equals(interviewDAO.getStatus())) {
			interviewDAO = interviewDAO.toBuilder().status(InterviewStatus.SLOT_REQUESTED_BY_CANDIDATE.getValue())
					.build();
			interviewDAO = this.interviewService.save(interviewDAO, userId);
			this.jiraWorkflowManager.transitionJiraStatus(interviewId,
					InterviewStatus.SLOT_REQUESTED_BY_CANDIDATE.getValue());
		}
	}
}
