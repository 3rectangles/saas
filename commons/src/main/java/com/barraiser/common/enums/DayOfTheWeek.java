/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.enums;

import java.util.NoSuchElementException;

public enum DayOfTheWeek {

	MON("MONDAY"),

	TUE("TUESDAY"),

	WED("WEDNESDAY"),

	THU("THURSDAY"),

	FRI("FRIDAY"),

	SAT("SATURDAY"),

	SUN("SUNDAY");

	private final String dayOfTheWeek;

	DayOfTheWeek(final String dayOfTheWeek) {
		this.dayOfTheWeek = dayOfTheWeek;
	}

	public String getValue() {
		return this.dayOfTheWeek;
	}

	public static DayOfTheWeek fromString(String dayOfTheWeek) {
		for (DayOfTheWeek day : values()) {
			if (day.getValue().equals(dayOfTheWeek)) {
				return day;
			}
		}
		throw new NoSuchElementException("Element with value " + dayOfTheWeek + " has not been found");
	}
}
