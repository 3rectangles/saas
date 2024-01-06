/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.types;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Reason {
	private String id;
	private String type;
	private String reason;
	private String displayText;
	private String customerDisplayableReason;
	private Boolean nonReschedulableReason;
	private String description;
}
