/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.jobRoleManagement.UserManagement;

import com.barraiser.common.utilities.SetOperationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * NOTE: Lot of reusable code for HM & Recruiter addition but keeping seperate
 * for readability.
 */

@Log4j2
@RequiredArgsConstructor
@Component
public class RecruitersInfoSaver {

	private final JobRoleUserManagementUtil jobRoleUserManagementUtil;
	private static final String RECRUITER_ROLE_ID = "RECRUITER";

	public List<String> manageRecruitersInfo(final String jobRoleId, final Set<String> allRecruitersBrIdsFromInput) {

		final Set<String> allExistingRecruitersBrIdsForJobRole = new HashSet<>(
				this.jobRoleUserManagementUtil.getAllUserWithARole(jobRoleId, RECRUITER_ROLE_ID));

		final Set<String> brPartnerRepIdsToBeAddedAsRecruiters = SetOperationUtils
				.findDifference(allRecruitersBrIdsFromInput, allExistingRecruitersBrIdsForJobRole);
		final Set<String> brPartnerRepIdsToRemovedAsRecruiters = SetOperationUtils
				.findDifference(allExistingRecruitersBrIdsForJobRole, allRecruitersBrIdsFromInput);

		// Adding as Recruiters
		for (final String partnerRepId : brPartnerRepIdsToBeAddedAsRecruiters) {
			try {
				this.jobRoleUserManagementUtil.grantRole(jobRoleId, partnerRepId, RECRUITER_ROLE_ID);
			} catch (Exception e) {
				log.error("There was an error ADDING Recruiter  : {} for job role : {} ", partnerRepId, jobRoleId);
				throw e;
			}

		}

		// Removing as Recruiters
		for (final String partnerRepId : brPartnerRepIdsToRemovedAsRecruiters) {
			try {
				this.jobRoleUserManagementUtil.dissociateRole(jobRoleId, partnerRepId, RECRUITER_ROLE_ID);
			} catch (Exception e) {
				log.error("There was an error REMOVING Recruiter  : {} for job role : {} ", partnerRepId, jobRoleId);
				throw e;
			}
		}

		final Set<String> unionOfRecruiters = SetOperationUtils.findUnion(allExistingRecruitersBrIdsForJobRole,
				brPartnerRepIdsToBeAddedAsRecruiters);
		unionOfRecruiters.removeAll(brPartnerRepIdsToRemovedAsRecruiters);

		return new ArrayList<>(unionOfRecruiters);

	}

}
