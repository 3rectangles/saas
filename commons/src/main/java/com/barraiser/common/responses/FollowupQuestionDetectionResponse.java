/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@SuperBuilder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FollowupQuestionDetectionResponse {
	private Map<String, Boolean> questionIdToIsFollowUpMap;
}
