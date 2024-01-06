/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.jobrole;

import com.barraiser.common.dal.VersionedEntityId;
import com.barraiser.onboarding.dal.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.barraiser.onboarding.common.Constants.*;

@Component
@AllArgsConstructor
public class JobRoleStatusManager {

	private final JobRoleToInterviewStructureRepository jobRoleToInterviewStructureRepository;
	private final InterviewStructureRepository interviewStructureRepository;

	public List<String> getBrStatus(final JobRoleDAO jobRoleDAO) {
		String brStatus;

		if (!this.isATSJobRole(jobRoleDAO)) {
			if (Boolean.TRUE.equals(jobRoleDAO.getIsDraft())) {
				brStatus = JOBROLE_DRAFT_STATUS_ID;
			} else if (jobRoleDAO.getDeprecatedOn() != null) {
				brStatus = JOBROLE_INACTIVE_STATUS_ID;
			} else
				brStatus = JOBROLE_ACTIVE_STATUS_ID;
			return List.of(brStatus);
		} else {
			// Checking if Intelligence enabled
			if (jobRoleDAO.getExtFullSync()) {
				if (this.isStatusAllInterviewsStructured(jobRoleDAO.getEntityId()))
					return List.of(JOBROLE_INTERVIEWS_STRUCTURED_STATUS_ID);
			}
		}

		return jobRoleDAO.getBrStatus();
	}

	private Boolean isATSJobRole(final JobRoleDAO jobRoleDAO) {
		return jobRoleDAO.getAtsStatus() != null;
	}

	private Boolean isStatusAllInterviewsStructured(final VersionedEntityId jobRoleId) {
		final List<JobRoleToInterviewStructureDAO> jobRoleToInterviewStructureDAOs = this.jobRoleToInterviewStructureRepository
				.findAllByJobRoleIdAndJobRoleVersion(jobRoleId.getId(), jobRoleId.getVersion());

		Integer structuredRoundsCount = 0;

		for (JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO : jobRoleToInterviewStructureDAOs) {
			InterviewStructureDAO interviewStructureDAO = interviewStructureRepository
					.findById(jobRoleToInterviewStructureDAO.getInterviewStructureId()).get();
			if (interviewStructureDAO.getInterviewFlow() != null)
				structuredRoundsCount++;
		}

		if (structuredRoundsCount.equals(jobRoleToInterviewStructureDAOs.size()))
			return Boolean.TRUE;

		return Boolean.FALSE;
	}
}
