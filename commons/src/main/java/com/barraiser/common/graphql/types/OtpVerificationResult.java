/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.types;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class OtpVerificationResult {
	private boolean verified;
	private boolean authenticated;
	private Boolean isLoginBlocked;
	private Integer accountLockoutPeriodInMinutes;
	private Integer remainingLoginAttempts;
	private Integer maximumAllowedLoginAttempts;
}
