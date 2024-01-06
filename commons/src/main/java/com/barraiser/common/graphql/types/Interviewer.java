/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.types;

import com.barraiser.common.dal.Money;
import lombok.*;

import java.util.List;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Interviewer {
	private String id;
	private String initials;
	private String designation;
	private String currentCompanyName;
	private Company currentCompany;
	private List<String> lastCompanyNames;
	private List<Company> lastCompanies;
	private List<String> skills;
	private String almaMater;
	private List<String> achievements;
	private Double cost;
	private List<Slot> availability;
	private Integer workExperienceInMonths;
	private Integer slotsBooked;
	private Integer workExperienceInMonthsRelative;
	private Double averageProficiencyInSkills;
	@Builder.Default
	private final Boolean recommended = Boolean.FALSE;
	private UserDetails userDetails;
	private String pan;
	private String bankAccount;
	private String offerLetter;
	private List<Skill> specificSkills;
	private List<String> expertDomains;
	private List<String> peerDomains;
	private String timezone;
	private Long totalInterviewsCompleted;
	private Money totalEarningTillDate;
	private String tenantId;
	private Money minPrice;
}
