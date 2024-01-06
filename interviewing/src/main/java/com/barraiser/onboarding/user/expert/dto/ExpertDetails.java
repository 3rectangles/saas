/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.user.expert.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ExpertDetails {

	private String id;

	private String category;

	private String email;

	private String phone;

	private String whatsappNumber;

	private String designation;

	private String currentCompanyId;

	private List<String> lastCompaniesId;

	private Integer workExperienceInMonths;

	private Boolean isActive;

	private String opsRep;

	private Instant resumeReceivedDate;

	private String offerLetter;

	private List<String> expertDomains;

	private List<String> peerDomains;

	private Boolean isDemoEligible;

	private String interviewerReferrer;

	private String consultancyReferrer;

	private String reachoutChannel;

	private Boolean isUnderTraining;

	private List<String> companiesForWhichExpertCanTakeInterview;

	private List<String> countriesForWhichExpertCanTakeInterview;

	private Long gapBetweenInterviews;

	private String duplicatedFrom;

	private PayoutDetails payoutDetails;
	private String tenantId;

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder(toBuilder = true)
	public static class PayoutDetails {
		private Double baseCost;

		private String currency;

		private String pan;

		private String bankAccount;

		private String IFSC;

		private Double multiplier;

		private String costLogic;

		private String cancellationLogic;

		private Double minCostPerHour;
	}
}
