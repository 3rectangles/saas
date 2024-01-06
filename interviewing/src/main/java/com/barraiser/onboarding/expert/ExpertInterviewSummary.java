/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.expert;

import lombok.*;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ExpertInterviewSummary {
	private Integer numberOfInterviewsTaken;
	private Double utilisation;
}
