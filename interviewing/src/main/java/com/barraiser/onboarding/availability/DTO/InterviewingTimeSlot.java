/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.availability.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

@ToString
@SuperBuilder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InterviewingTimeSlot {

	private Long id;

	private String userId;

	private Long startTimeEpoch;

	private Long endTimeEpoch;

	private Integer maxInterviews;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || this.getClass() != o.getClass())
			return false;
		InterviewingTimeSlot that = (InterviewingTimeSlot) o;
		return Objects.equals(this.id, that.id) && Objects.equals(this.userId, that.userId)
				&& Objects.equals(this.startTimeEpoch, that.startTimeEpoch)
				&& Objects.equals(this.endTimeEpoch, that.endTimeEpoch)
				&& Objects.equals(this.maxInterviews, that.maxInterviews);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.id, this.userId, this.startTimeEpoch, this.endTimeEpoch, this.maxInterviews);
	}
}
