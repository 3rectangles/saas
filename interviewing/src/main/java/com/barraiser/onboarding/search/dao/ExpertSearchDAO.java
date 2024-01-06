/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.search.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ExpertSearchDAO {
	@JsonProperty("user_id")
	private String userId;
	/**
	 * Roles for which the interviwer can take interviews
	 */

	@JsonProperty("peer_domains")
	private List<String> peerDomains;
	@JsonProperty("expert_domains")
	private List<String> expertDomains;
	@JsonProperty("phone")
	private String phone;
	@JsonProperty("linkedin_profile")
	private String linkedInprofile;
	@JsonProperty("cost_per_hour")
	private Double costPerHour;
	@JsonProperty("currency")
	private String currency;
	@JsonProperty("designation")
	private String designation;
	@JsonProperty("email")
	private String email;
	@JsonProperty("work_experience_in_month")
	private Integer workExperienceInMonths;
	@JsonProperty("category")
	private String category;
	@JsonProperty("introductory")
	private Boolean introductory;
	@JsonProperty("is_active")
	private Boolean isActive;
	@JsonProperty("current_company_id")
	private String currentCompanyId;
	@JsonProperty("last_companies_id")
	private List<String> lastCompaniesId;
	/**
	 * List of skills
	 */
	@JsonProperty("skills")
	private List<String> skills;
	@JsonProperty("is_under_training")
	private Boolean isUnderTraining;

	@JsonProperty("companies_for_which_expert_can_take_interview")
	private List<String> companiesForWhichExpertCanTakeInterview;

	@JsonProperty("multiplier")
	private Double multiplier;

	@JsonProperty("is_demo_eligible")
	private Boolean isDemoEligible;

	@JsonProperty("countries_for_which_expert_can_take_interviews")
	private List<String> countriesForWhichExpertCanTakeInterviews;
	@JsonProperty("min_cost_per_hour")
	private Double minCostPerHour;
}
