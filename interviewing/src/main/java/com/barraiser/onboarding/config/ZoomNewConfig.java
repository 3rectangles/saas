/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.config;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ZoomNewConfig {
	// API credentials for generating signature for updated Zoom SDK
	private final String apiKey;

	private final String apiSecret;
}
