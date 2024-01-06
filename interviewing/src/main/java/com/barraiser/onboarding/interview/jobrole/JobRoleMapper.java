/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.jobrole;

import com.barraiser.common.graphql.types.JobRole;
import com.barraiser.onboarding.dal.JobRoleDAO;
import com.barraiser.onboarding.dal.PartnerRepsMapper;
import com.barraiser.onboarding.dal.StatusMapper;
import com.barraiser.onboarding.interview.LocationMapper;
import com.barraiser.onboarding.interview.TeamMapper;
import com.barraiser.onboarding.jobRoleManagement.JobRole.search.dal.JobRoleSearchDAO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JobRoleMapper {
	private LocationMapper locationMapper;
	private TeamMapper teamMapper;
	private PartnerRepsMapper partnerRepsMapper;
	private StatusMapper statusMapper;

	public JobRole toJobRole(final JobRoleDAO jobRoleDAO) {
		return JobRole.builder()
				.id(jobRoleDAO.getEntityId().getId())
				.version(jobRoleDAO.getEntityId().getVersion())
				.category(jobRoleDAO.getCategory())
				.companyId(jobRoleDAO.getCompanyId())
				.maxExp(jobRoleDAO.getMaxExp())
				.minExp(jobRoleDAO.getMinExp())
				.domainId(jobRoleDAO.getDomainId())
				.evaluationProcessType(jobRoleDAO.getEvaluationProcessType())
				.internalDisplayName(jobRoleDAO.getInternalDisplayName())
				.candidateDisplayName(jobRoleDAO.getCandidateDisplayName())
				.jdLink(jobRoleDAO.getJdLink())
				.isDeprecated(jobRoleDAO.getDeprecatedOn() != null)
				.deprecatedOn(
						jobRoleDAO.getDeprecatedOn() == null ? null : jobRoleDAO.getDeprecatedOn().getEpochSecond())
				.cutOffScore(jobRoleDAO.getCutOffScore())
				.countryCode(jobRoleDAO.getCountryCode())
				.timezone(jobRoleDAO.getTimezone())
				.eligibleCountriesOfExperts(jobRoleDAO.getEligibleCountriesOfExperts())
				.defaultPocEmail(jobRoleDAO.getDefaultPocEmail())
				.isDraft(Boolean.TRUE.equals(jobRoleDAO.getIsDraft()))
				.atsStatus(this.statusMapper.toStatusTypeFromId(jobRoleDAO.getAtsStatus()))
				.extFullSync(jobRoleDAO.getExtFullSync())
				.extFullSyncStatus(jobRoleDAO.getExtFullSyncStatus())
				.locations(this.locationMapper.toLocations(jobRoleDAO.getLocations()))
				.teams(this.teamMapper.toTeams(jobRoleDAO.getTeams()))
				.brStatus(this.statusMapper.toStatusTypes(jobRoleDAO.getBrStatus()))
				.hiringManagers(this.partnerRepsMapper.toPartnerRepDetailsList(jobRoleDAO.getHiringManagers(),
						jobRoleDAO.getPartnerId()))
				.recruiters(this.partnerRepsMapper.toPartnerRepDetailsList(jobRoleDAO.getRecruiters(),
						jobRoleDAO.getPartnerId()))
				.hiringTeamMembers(this.partnerRepsMapper.toPartnerRepDetailsList(jobRoleDAO.getHiringTeamMembers(),
						jobRoleDAO.getPartnerId()))
				.creationSource(jobRoleDAO.getCreationSource())
				.creationSourceMeta(jobRoleDAO.getCreationMeta())
				.build();
	}

	public JobRoleSearchDAO toJobRoleSearchDAO(final JobRoleDAO jobRoleDAO) {
		return JobRoleSearchDAO.builder()
				.partnerId(jobRoleDAO.getPartnerId())
				.jobRoleId(jobRoleDAO.getEntityId().getId())
				.version(jobRoleDAO.getEntityId().getVersion())
				.domainId(jobRoleDAO.getDomainId())
				.deprecatedOn(jobRoleDAO.getDeprecatedOn())
				.internalDisplayName(jobRoleDAO.getInternalDisplayName())
				.candidateDisplayName(jobRoleDAO.getCandidateDisplayName())
				.isDraft(jobRoleDAO.getIsDraft())
				.build();
	}
}
