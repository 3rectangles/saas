/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation.expertReassignment.sfn_activities;

import com.barraiser.commons.auth.UserRole;
import com.barraiser.onboarding.dal.DyteMeetingDAO;
import com.barraiser.onboarding.dal.DyteMeetingRepository;
import com.barraiser.onboarding.dyte.DyteMeetingManager;
import com.barraiser.onboarding.dyte.DyteUtils;
import com.barraiser.onboarding.dyte.dto.ParticipantAdditionData;
import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.DTO.ExpertAllocatorData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class UpdateDyteMeetingProcessor implements ExpertAllocatorSfnActivity {
	public static final String UPDATE_DYTE_MEETING_ACTIVITY_NAME = "update-dyte-meeting";
	private final DyteMeetingRepository dyteMeetingRepository;
	private final DyteMeetingManager dyteMeetingManager;
	private final ObjectMapper objectMapper;

	@Override
	public String name() {
		return UPDATE_DYTE_MEETING_ACTIVITY_NAME;
	}

	@Override
	public ExpertAllocatorData process(final String input) throws Exception {
		final ExpertAllocatorData data = objectMapper.readValue(input, ExpertAllocatorData.class);
		final DyteMeetingDAO dyteMeetingDAO = this.dyteMeetingRepository
				.findByInterviewIdAndRescheduleCount(data.getInterviewId(), data.getInterview().getRescheduleCount());
		if (dyteMeetingDAO == null) {
			return data;
		}
		if (this.dyteMeetingManager.isParticipantPresentInMeeting(dyteMeetingDAO.getMeetingId(),
				data.getInterviewerId())) {
			return data;
		}
		this.dyteMeetingManager.addParticipantToMeeting(ParticipantAdditionData.builder()
				.meetingId(dyteMeetingDAO.getMeetingId())
				.participantId(data.getInterviewerId())
				.partcipantName(DyteUtils.DYTE_EXPERT_PARTICIPANT_NAME)
				.role(UserRole.EXPERT.getRole())
				.isHost(Boolean.FALSE)
				.participantPresetName(DyteUtils.DYTE_EXPERT_PRESET)
				.build());
		return data;
	}
}
