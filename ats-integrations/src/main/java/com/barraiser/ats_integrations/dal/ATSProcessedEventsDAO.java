/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.dal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ats_processed_events")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ATSProcessedEventsDAO extends BaseModel {
	@Id
	private String id;

	@Column(name = "calendar_event_id")
	private String calendarEntityId;

	@Column(name = "calendar_event_start_time")
	private Long calendarEventStartTime;

	@Column(name = "calendar_event_end_time")
	private Long calendarEventEndTime;

	@Column(name = "interview_id")
	private String interviewId;
}
