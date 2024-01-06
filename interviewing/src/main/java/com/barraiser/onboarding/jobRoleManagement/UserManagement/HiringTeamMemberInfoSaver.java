/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.jobRoleManagement.UserManagement;

import com.barraiser.common.utilities.SetOperationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Log4j2
@RequiredArgsConstructor
@Component
public class HiringTeamMemberInfoSaver {

	private final JobRoleUserManagementUtil jobRoleUserManagementUtil;
	private static final String HIRING_TEAM_MEMBER_ROLE_ID = "HIRING_TEAM_MEMBER";

	public List<String> manageHiringTeamMemberInfo(final String jobRoleId,
			final Set<String> hiringTeamMemberInputUserIds) {

		final Set<String> allExistingHiringTeamMemberIdsForJobRole = new HashSet<>(
				this.jobRoleUserManagementUtil.getAllUserWithARole(jobRoleId, HIRING_TEAM_MEMBER_ROLE_ID));

		final Set<String> brPartnerRepIdsToBeAddedAsHiringTeamMembers = SetOperationUtils
				.findDifference(hiringTeamMemberInputUserIds, allExistingHiringTeamMemberIdsForJobRole);
		final Set<String> brPartnerRepIdsToRemovedAsHiringTeamMembers = SetOperationUtils
				.findDifference(allExistingHiringTeamMemberIdsForJobRole, hiringTeamMemberInputUserIds);

		// TBD : Can add per HM exception if needed.
		// Adding as HMs
		for (final String partnerRepId : brPartnerRepIdsToBeAddedAsHiringTeamMembers) {
			try {
				this.jobRoleUserManagementUtil.grantRole(jobRoleId, partnerRepId, HIRING_TEAM_MEMBER_ROLE_ID);
			} catch (Exception e) {
				log.error("There was an error ADDING Hiring Team Member  : {} for job role : {} ", partnerRepId,
						jobRoleId);
				throw e;

			}
		}
		// Removing as HMs
		for (final String partnerRepId : brPartnerRepIdsToRemovedAsHiringTeamMembers) {
			try {
				this.jobRoleUserManagementUtil.dissociateRole(jobRoleId, partnerRepId, HIRING_TEAM_MEMBER_ROLE_ID);
			} catch (Exception e) {
				log.error("There was an error REMOVING Hiring Team Member  : {} for job role : {} ", partnerRepId,
						jobRoleId);
				throw e;
			}
		}

		final Set<String> unionOfHMs = SetOperationUtils.findUnion(allExistingHiringTeamMemberIdsForJobRole,
				brPartnerRepIdsToBeAddedAsHiringTeamMembers);
		unionOfHMs.removeAll(brPartnerRepIdsToRemovedAsHiringTeamMembers);

		return new ArrayList<>(unionOfHMs);

	}

}
