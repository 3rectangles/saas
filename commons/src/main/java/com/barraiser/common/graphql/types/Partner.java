/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.types;

import com.barraiser.common.graphql.input.SearchInput;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Partner {
	private String id;
	private String companyId;
	private SearchInput evaluationsSearchQuery;
	private Company companyDetails;
	private Boolean isCandidateSchedulingEnabled;
	private PartnerInterviewSummary partnerInterviewSummary;
	private PartnerPricing pricing;
	private Boolean useATSFeedback;
	private String partnershipModelId;
}
