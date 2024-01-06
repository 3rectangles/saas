/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "dyte_meeting")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class DyteMeetingDAO extends BaseModel {

	@Id
	@Column(name = "meeting_id")
	private String meetingId;

	@Column(name = "interview_id")
	private String interviewId;

	@Column(name = "reschedule_count")
	private Integer rescheduleCount;

	@Column(name = "room_name")
	private String roomName;
}