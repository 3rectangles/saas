/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ATSEvaluationDetailsDTO {
	private String BREvaluationId;

	private String ATSEvaluationId;

	private String ATSRemoteData;

	private String partnerId;
}
