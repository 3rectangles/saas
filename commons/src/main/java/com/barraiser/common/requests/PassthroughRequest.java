/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PassthroughRequest {
	private String requestMethod;
	private String requestPath;
	private String requestBody;
}
