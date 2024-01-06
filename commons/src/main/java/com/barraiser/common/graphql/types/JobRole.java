/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.types;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class JobRole {

	private String id;
	private Integer version;
	private String internalDisplayName;
	private String candidateDisplayName;
	private String domainId;
	private String companyId;
	private String category;
	private Integer minExp;
	private Integer maxExp;
	private Company company;
	private Domain domain;
	private String remarks;
	private String evaluationProcessType;
	private List<RoundLevelInterviewStructure> roundLevelInterviewStructure;
	private List<SkillWeightage> skillWeightages;
	private Integer cutOffScore;
	private String jdLink;
	private EvaluationStatistics evaluationStatistics;
	private Long deprecatedOn;
	private Boolean isDeprecated;
	private String timezone;
	private String countryCode;
	private List<String> eligibleCountriesOfExperts;
	private String defaultPocEmail;
	private Boolean isDraft;
	private List<Location> locations;
	private List<Team> teams;
	private List<PartnerRepDetails> hiringManagers;
	private List<PartnerRepDetails> hiringTeamMembers;
	private List<PartnerRepDetails> recruiters;

	// -- TEMP CHANGE : Adding these fields so they can be returned back from FE
	// incase of job role tool , so no changes are lost on updates for now.
	private String atsId;
	private StatusType atsStatus;
	private Boolean extFullSync;
	private String extFullSyncStatus;
	private List<StatusType> brStatus;
	private String creationSource;
	private String creationSourceMeta;
}
