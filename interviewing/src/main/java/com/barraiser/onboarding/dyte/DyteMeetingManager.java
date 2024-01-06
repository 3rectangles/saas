/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dyte;

import com.barraiser.onboarding.dal.DyteMeetingDAO;
import com.barraiser.onboarding.dal.DyteMeetingRepository;
import com.barraiser.onboarding.dal.DyteParticipantDAO;
import com.barraiser.onboarding.dal.DyteParticipantRepository;
import com.barraiser.onboarding.dyte.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Log4j2
@RequiredArgsConstructor
public class DyteMeetingManager {

	private final DyteClient dyteClient;
	private final DyteMeetingRepository dyteMeetingRepository;
	private final DyteParticipantRepository dyteParticipantRepository;

	private final static String BARRAISER_ORGANIZATION_ID = "aba5491a-b910-41eb-9168-8416465a7181";
	private final static String FALLBACK_PRESET = "fallback_preset";
	private final static String JOINING_URL = "https://barraiser.dyte.live/meeting/stage/%s?authToken=%s";

	public DyteCreateMeetingResponseDTO scheduleMeeting(final String topic) {

		final DyteCreateMeetingResponseDTO createMeetingResponse = this.dyteClient.createMeeting(
				BARRAISER_ORGANIZATION_ID,
				DyteCreateMeetingRequestDTO.builder()
						.title(topic)
						.presetName(FALLBACK_PRESET)
						.recordOnStart(Boolean.TRUE)
						.authorization(DyteCreateMeetingRequestDTO.Authorization.builder()
								.closed(Boolean.TRUE)
								.waitingRoom(Boolean.FALSE)
								.build())
						.build());

		return createMeetingResponse;
	}

	public DyteAddParticipantResponseDTO addParticipantToMeeting(
			final ParticipantAdditionData participantAdditionData) {

		final DyteAddParticipantResponseDTO participantAdditionResponse = this.dyteClient.addParticipant(
				BARRAISER_ORGANIZATION_ID,
				participantAdditionData.getMeetingId(),
				DyteAddParticipantRequestDTO.builder()
						.clientSpecificId(UUID.randomUUID().toString())
						.presetName(participantAdditionData.getParticipantPresetName())
						.userDetails(DyteAddParticipantRequestDTO.UserDetails.builder()
								.name(participantAdditionData.getPartcipantName()).build())
						.build());

		this.dyteParticipantRepository.save(
				DyteParticipantDAO.builder()
						.id(UUID.randomUUID().toString())
						.meetingId(participantAdditionData.getMeetingId())
						.participantId(participantAdditionData.getParticipantId())
						.participantMeetingRole(participantAdditionData.getRole())
						.authToken(participantAdditionResponse.getData().getAuthResponse().getAuthToken())
						.build());

		return participantAdditionResponse;
	}

	public Boolean isParticipantPresentInMeeting(final String meetingId, final String participantId) {
		final DyteParticipantDAO participantDAO = this.dyteParticipantRepository
				.findByMeetingIdAndParticipantId(meetingId, participantId);
		return participantDAO != null;
	}

	public String getParticipantJoiningLinkForInterview(final String participantId, final String interviewId,
			final Integer rescheduleCount) {
		final DyteMeetingDAO meetingDAO = this.dyteMeetingRepository.findByInterviewIdAndRescheduleCount(interviewId,
				rescheduleCount);
		if (meetingDAO == null) {
			return null;
		}
		final DyteParticipantDAO participantDAO = this.dyteParticipantRepository
				.findByMeetingIdAndParticipantId(meetingDAO.getMeetingId(), participantId);
		return String.format(JOINING_URL, meetingDAO.getRoomName(), participantDAO.getAuthToken());
	}
}
