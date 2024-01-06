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
public class PassthroughResponse {
	private String remoteResponse;
	private String remoteStatus;
	private String inputPath;
}
