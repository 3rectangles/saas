/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.BaseModel;
import com.barraiser.common.enums.DayOfTheWeek;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@Table(name = "recurring_availability")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RecurringAvailabilityDAO extends BaseModel {
	@Id
	private String id;

	@Column(name = "user_id")
	private String userId;

	@Enumerated(EnumType.STRING)
	@Column(name = "day_of_the_week")
	private DayOfTheWeek dayOfTheWeek;

	// Unit : Seconds from the start of the day
	@Column(name = "slot_start_time")
	private Integer slotStartTime;

	// Unit : Seconds from the start of the day
	@Column(name = "slot_end_time")
	private Integer slotEndTime;

	@Column(name = "timezone")
	private String timezone;

	@Column(name = "max_interviews_in_slot")
	private Integer maximumNumberOfInterviewsInSlot;

	@Column(name = "is_available")
	private Boolean isAvailable;
}
