/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.input;

import com.barraiser.common.graphql.UserDetailsInput;
import com.barraiser.commons.dto.jobRoleManagement.ATSPartnerRepInfo;
import com.barraiser.commons.dto.jobRoleManagement.Location;
import com.barraiser.commons.dto.jobRoleManagement.Team;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * TODO: The input object created for ATS Job role creation and job role
 * creation via our tools is different for now
 * Can be merged. Or renamed.
 * <p>
 * Initial plan was to merge them as they would use common interfaces.
 * Can us it once we move to common interfaces
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class JobRoleInput {
	private String partnerId;
	private String id;
	private Integer version;
	private String companyId;
	private String domainId;
	private String category;
	private String internalDisplayName;
	private String candidateDisplayName;
	private String remarks;
	private String evaluationProcessType;
	private List<SkillWeightageInput> skillWeightages;
	private List<InterviewStructureInput> interviewStructures;
	private String jdLink;
	private String jdDocumentId;
	private Boolean checkSpecificSkillExpertAvailability;
	private Integer cutOffScore;
	private String countryCode;
	private List<String> eligibleCountriesOfExperts;
	private String timezone;
	private String defaultPocEmail;
	private String atsId;
	private Boolean isDraft;
	private String creationSource;
	private String creationSourceMeta;
	private List<String> hiringManagers;
	private List<String> recruiters;
	private List<String> hiringTeamMembers;
	private List<String> teams;
	private List<String> locations;

	// TEMP CHANGE: Fields added to input of job role tool , as we send them from BE
	// during job role fetch and get them here on save as
	// Job rool tool as been made in a way that it assumes all data will come from
	// FE
	private Boolean extFullSync;
	private String extFullSyncStatus;
	private String atsStatusId;
	private String brStatusId;
}
