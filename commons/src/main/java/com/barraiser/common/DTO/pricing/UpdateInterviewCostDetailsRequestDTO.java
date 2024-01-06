/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.DTO.pricing;

import com.barraiser.common.dal.Money;
import com.barraiser.common.enums.RoundType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpdateInterviewCostDetailsRequestDTO {
	private String interviewId;
	private Integer rescheduleCount;
	private String expertId;
	private Money expertCostPerHour;
	private String jobRoleId;
	private String interviewStructureId;
	private Integer workExperienceOfCandidateInMonths;
	private RoundType roundType;
	private Long durationOfInterview;
	private Double minPriceOfExpertPerHour;
	private Money interviewPrice;
	private Double usedMargin;
	private Double configuredMargin;
}
