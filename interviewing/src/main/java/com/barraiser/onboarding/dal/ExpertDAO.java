/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "expert")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ExpertDAO extends BaseModel {
	@Id
	private String id;

	@Column(name = "cost")
	private Double baseCost;

	private String currency;

	private Boolean isActive;

	private String opsRep;

	@Column(name = "pan")
	private String pan;

	@Column(name = "bank_account")
	private String bankAccount;

	@Column(name = "resume_received_date")
	private Instant resumeReceivedDate;

	@Column(name = "offer_letter")
	private String offerLetter;

	@Type(type = "list-array")
	@Column(columnDefinition = "text[]", name = "expert_domains")
	private List<String> expertDomains;

	@Type(type = "list-array")
	@Column(columnDefinition = "text[]", name = "peer_domains")
	private List<String> peerDomains;

	private Double multiplier;

	@Column(name = "cost_logic")
	private String costLogic;

	@Column(name = "cancellation_logic")
	private String cancellationLogic;

	@Column(name = "earning_for_financial_year_2020_21")
	private Double earningForFinancialYear2020And2021;

	@Column(name = "ifsc")
	private String IFSC;

	@Column(name = "interviewer_referrer")
	private String interviewerReferrer;

	@Column(name = "consultancy_referrer")
	private String consultancyReferrer;

	@Column(name = "reachout_channel")
	private String reachoutChannel;

	@Column(name = "is_under_training")
	private Boolean isUnderTraining;

	@Type(type = "list-array")
	@Column(columnDefinition = "text[]", name = "companies_for_which_expert_can_take_interview")
	private List<String> companiesForWhichExpertCanTakeInterview;

	@Column(name = "gap_between_interviews")
	private Long gapBetweenInterviews;

	private String duplicatedFrom;

	private Long totalInterviewsCompleted;

	@Column(name = "is_demo_eligible")
	private Boolean isDemoEligible;

	@Column(name = "tenant_id")
	private String tenantId;

	@Type(type = "list-array")
	@Column(columnDefinition = "text[]", name = "countries_for_which_expert_can_take_interview")
	private List<String> countriesForWhichExpertCanTakeInterviews;

	@Column(name = "willing_to_switch_video_on")
	private Boolean willingToSwitchVideoOn;

	@Column(name = "min_price")
	private Double minPrice;
}
