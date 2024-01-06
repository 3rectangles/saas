/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.jobRoleManagement.UserManagement;

import com.barraiser.common.utilities.SetOperationUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.*;

@Log4j2
@RequiredArgsConstructor
@Component
public class HiringManagerInfoSaver {

	private final JobRoleUserManagementUtil jobRoleUserManagementUtil;
	private static final String HIRING_MANAGER_ROLE_ID = "HIRING_MANAGER";

	public List<String> manageHiringManagersInfo(final String jobRoleId, final Set<String> hiringManagerInputUserIds) {

		final Set<String> allExistingHiringManagerIdsForJobRole = new HashSet<>(
				this.jobRoleUserManagementUtil.getAllUserWithARole(jobRoleId, HIRING_MANAGER_ROLE_ID));

		final Set<String> brPartnerRepIdsToBeAddedAsHMs = SetOperationUtils
				.findDifference(hiringManagerInputUserIds, allExistingHiringManagerIdsForJobRole);
		final Set<String> brPartnerRepIdsToRemovedAsHMs = SetOperationUtils
				.findDifference(allExistingHiringManagerIdsForJobRole, hiringManagerInputUserIds);

		// TBD : Can add per HM exception if needed.
		// Adding as HMs
		for (final String partnerRepId : brPartnerRepIdsToBeAddedAsHMs) {
			try {
				this.jobRoleUserManagementUtil.grantRole(jobRoleId, partnerRepId, HIRING_MANAGER_ROLE_ID);
			} catch (Exception e) {
				log.error("There was an error ADDING Hiring Manager  : {} for job role : {} ", partnerRepId,
						jobRoleId);
				throw e;

			}
		}
		// Removing as HMs
		for (final String partnerRepId : brPartnerRepIdsToRemovedAsHMs) {
			try {
				this.jobRoleUserManagementUtil.dissociateRole(jobRoleId, partnerRepId, HIRING_MANAGER_ROLE_ID);
			} catch (Exception e) {
				log.error("There was an error REMOVING Hiring Manager  : {} for job role : {} ", partnerRepId,
						jobRoleId);
				throw e;
			}
		}

		final Set<String> unionOfHMs = SetOperationUtils.findUnion(allExistingHiringManagerIdsForJobRole,
				brPartnerRepIdsToBeAddedAsHMs);
		unionOfHMs.removeAll(brPartnerRepIdsToRemovedAsHMs);

		return new ArrayList<>(unionOfHMs);

	}

}
