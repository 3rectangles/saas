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
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Table(name = "webex_meeting")
public class WebexMeetingDAO extends BaseModel {
	@Id
	private String id;

	@Column(name = "interview_id")
	private String interviewId;

	@Column(name = "reschedule_count")
	private Integer rescheduleCount;

	@Column(name = "meeting_number")
	private String meetingNumber;

	@Column(name = "join_link")
	private String joinLink;

	@Column(name = "password")
	private String password;

	@Column(name = "start_date")
	private Long startDate;

	@Column(name = "end_date")
	private Long endDate;
}
