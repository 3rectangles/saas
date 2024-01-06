/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dyte.dto;

import com.barraiser.onboarding.dal.InterviewDAO;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ParticipantAdditionData {

	private String meetingId;

	private String participantId; // barraiser user id

	private Boolean isHost;

	private String partcipantName;

	private String participantPresetName;

	private InterviewDAO interviewDAO;

	private String role;

}
