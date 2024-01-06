/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.pricing;

import java.time.Instant;

public class PricingUtils {

	public static Boolean isCurrentlyActive(Instant applicableFrom, Instant applicableTill) {
		applicableFrom = applicableFrom == null ? Instant.MIN : applicableFrom;
		applicableTill = applicableTill == null ? Instant.MAX : applicableTill;
		return applicableFrom.compareTo(Instant.now()) <= 0 &&
				applicableTill.compareTo(Instant.now()) > 0;
	}
}
