/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.calendar.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CalendarEvent {
	String eventId;
	String accountId;
}
