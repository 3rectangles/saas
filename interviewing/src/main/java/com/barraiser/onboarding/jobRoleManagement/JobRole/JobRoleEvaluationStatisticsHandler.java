/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.jobRoleManagement.JobRole;

import com.barraiser.common.dal.VersionedEntityId;
import com.barraiser.onboarding.dal.JobRoleDAO;
import com.barraiser.onboarding.dal.JobRoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JobRoleEvaluationStatisticsHandler {
	private final JobRoleRepository jobRoleRepository;

	public void addActiveCandidateCount(final String jobRoleId, final Integer version) {
		JobRoleDAO jobRoleDAO = this.jobRoleRepository.findByEntityId(
				new VersionedEntityId(jobRoleId, version)).get();
		Long value = jobRoleDAO.getActiveCandidatesCountAggregate();
		if (value == null)
			value = (long) 1;
		else
			value++;
		this.jobRoleRepository.save(jobRoleDAO.toBuilder()
				.activeCandidatesCountAggregate(value)
				.build());
	}

	public void removeActiveCandidateCount(final String jobRoleId, final Integer version) {
		JobRoleDAO jobRoleDAO = this.jobRoleRepository.findByEntityId(new VersionedEntityId(jobRoleId, version)).get();

		this.jobRoleRepository.save(jobRoleDAO.toBuilder()
				.activeCandidatesCountAggregate(jobRoleDAO.getActiveCandidatesCountAggregate() - 1)
				.build());
	}

}
