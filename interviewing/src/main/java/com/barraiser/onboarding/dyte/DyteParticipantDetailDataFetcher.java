/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dyte;

import com.barraiser.common.graphql.types.DyteMeetingDetail;
import com.barraiser.common.graphql.types.DyteParticipantDetail;
import com.barraiser.onboarding.auth.AuthenticationException;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.DyteParticipantDAO;
import com.barraiser.onboarding.dal.DyteParticipantRepository;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.MultiParentTypeDataFetcher;
import com.barraiser.commons.auth.UserRole;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
@Log4j2
public class DyteParticipantDetailDataFetcher implements MultiParentTypeDataFetcher {
	private final DyteParticipantRepository dyteParticipantRepository;
	private final GraphQLUtil graphQLUtil;

	@Override

	public List<List<String>> typeNameMap() {
		return List.of(List.of("DyteMeetingDetail", "participantMeetingDetails"));
	}

	@Override
	public Object get(DataFetchingEnvironment environment) throws Exception {
		final DyteMeetingDetail meeting = environment.getSource();
		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);

		if (authenticatedUser == null) {
			throw new AuthenticationException("No authenticated user found");
		}

		final List<DyteParticipantDAO> participants = this.getAllParticipantsForMeeting(meeting.getMeetingId());

		if (participants.size() == 0) {
			log.warn("No participants added to the meeting");
			return null;
		}

		final DyteParticipantDetail participantDetail = this.getParticipantDetails(authenticatedUser, participants);

		return DataFetcherResult.newResult()
				.data(participantDetail)
				.build();
	}

	private DyteParticipantDetail getParticipantDetails(final AuthenticatedUser authenticatedUser,
			final List<DyteParticipantDAO> participants) {
		final DyteParticipantDetail participantDetails;
		if (authenticatedUser.getRoles().contains(UserRole.ADMIN)
				&& this.isAdminForInterview(authenticatedUser, participants)) {
			participantDetails = this.getParticipantDetailsForRole(participants, UserRole.ADMIN);
		} else if (authenticatedUser.getRoles().contains(UserRole.TAGGING_AGENT)
				&& this.isTaggingAgentForInterview(authenticatedUser, participants)) {
			participantDetails = this.getParticipantDetailsForRole(participants, UserRole.TAGGING_AGENT);
		} else {
			participantDetails = this.getParticipantDetailsForParticipant(authenticatedUser.getUserName(),
					participants);
		}
		return participantDetails;
	}

	public List<DyteParticipantDAO> getAllParticipantsForMeeting(final String meetingId) {
		return this.dyteParticipantRepository.findAllByMeetingId(meetingId);
	}

	private DyteParticipantDetail getParticipantDetailsForRole(final List<DyteParticipantDAO> participants,
			final UserRole role) {
		final DyteParticipantDAO participantDAO = participants.stream().filter(
				p -> p.getParticipantMeetingRole().equals(role.getRole())).collect(Collectors.toList()).get(0);

		return this.toParticipantDetail(participantDAO);
	}

	private DyteParticipantDetail getParticipantDetailsForParticipant(final String participantId,
			final List<DyteParticipantDAO> participants) {
		Map<String, DyteParticipantDAO> idParticipantMapping = participants.stream()
				.collect(Collectors.toMap(DyteParticipantDAO::getParticipantId, Function.identity()));
		return this.toParticipantDetail(idParticipantMapping.get(participantId));
	}

	private DyteParticipantDetail toParticipantDetail(final DyteParticipantDAO participantDAO) {
		return DyteParticipantDetail.builder()
				.id(participantDAO.getParticipantId())
				.role(participantDAO.getParticipantMeetingRole())
				.authToken(participantDAO.getAuthToken())
				.build();
	}

	private Boolean isTaggingAgentForInterview(final AuthenticatedUser authenticatedUser,
			final List<DyteParticipantDAO> participants) {

		for (final DyteParticipantDAO participant : participants) {
			if (authenticatedUser.getUserName().equals(participant.getParticipantId())
					&& !UserRole.TAGGING_AGENT.getRole().equals(participant.getParticipantMeetingRole())) {
				return Boolean.FALSE;
			}
		}

		return Boolean.TRUE;
	}

	private Boolean isAdminForInterview(final AuthenticatedUser authenticatedUser,
			final List<DyteParticipantDAO> participants) {

		for (final DyteParticipantDAO participant : participants) {
			if (authenticatedUser.getUserName().equals(participant.getParticipantId())
					&& !UserRole.ADMIN.getRole().equals(participant.getParticipantMeetingRole())) {
				return Boolean.FALSE;
			}
		}
		return Boolean.TRUE;
	}

}
