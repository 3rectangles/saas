/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ATSJobPostingToBRJobRoleRepository
		extends JpaRepository<ATSJobPostingToBRJobRoleDAO, String> {
	List<ATSJobPostingToBRJobRoleDAO> findAllByAtsJobPostingIdAndAtsProvider(
			final String atsJobPostingId,
			final String atsProvider);

	List<ATSJobPostingToBRJobRoleDAO> findAllByPartnerIdAndAtsProvider(
			final String jobRoleId,
			final String atsProvider);

	Optional<ATSJobPostingToBRJobRoleDAO> findByAtsJobPostingIdAndAtsProvider(
			final String atsJobPostingId,
			final String atsProvider);

	Optional<ATSJobPostingToBRJobRoleDAO> findByAtsJobPostingId(final String atsJobPostingId);

	Optional<ATSJobPostingToBRJobRoleDAO> findByBrJobRoleId(String jobRoleId);

	void deleteByBrJobRoleId(String jobRoleId);

	List<ATSJobPostingToBRJobRoleDAO> findAllByPartnerId(final String partnerId);

	List<ATSJobPostingToBRJobRoleDAO> findAllByBrJobRoleIdIn(Set<String> jobRoleId);

}
