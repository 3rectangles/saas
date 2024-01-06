/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.BaseModel;
import com.barraiser.commons.auth.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "dyte_participant")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class DyteParticipantDAO extends BaseModel {
	@Id
	private String id;

	@Column(name = "meeting_id")
	private String meetingId;

	@Column(name = "participant_id")
	private String participantId;

	@Column(name = "participant_meeting_role")
	private String participantMeetingRole;

	@Column(name = "auth_token")
	private String authToken;
}
