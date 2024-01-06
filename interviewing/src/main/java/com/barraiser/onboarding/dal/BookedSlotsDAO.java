/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.BaseModel;
import com.barraiser.onboarding.availability.enums.BookingSource;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "booked_slot")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class BookedSlotsDAO extends BaseModel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String userId;

	private Long startDate;

	private Long endDate;

	private String bookedBy;

	private Long ttl;

	private Long buffer;

	private Instant deletedOn;

	@Column(name = "google_cal_id")
	private String googleCalId;

	@Column(name = "email")
	private String email;

	@Enumerated(EnumType.STRING)
	private BookingSource source;
}
