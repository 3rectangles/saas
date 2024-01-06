/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FollowupQuestionDetectionRequest {
	private List<FollowupQuestionDetectionData> followupQuestionDetectionDataList;
}
