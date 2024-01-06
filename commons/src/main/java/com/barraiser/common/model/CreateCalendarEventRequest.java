/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateCalendarEventRequest {
	@NotNull
	private String entityId;

	@NotNull
	private String entityType;

	@NotNull
	private Integer entityRescheduleCount;

	@NotNull
	private String senderEmail;

	@NotEmpty
	private List<String> attendeeEmails;

	@NotNull
	private String summary;

	@NotNull
	private String description;

	@NotNull
	private String location;

	@NotNull
	private Long startTimeEpoch;

	@NotNull
	private Long endTimeEpoch;

	@NotNull
	private String timezone;

	@NotNull
	private String recipientId;
}
