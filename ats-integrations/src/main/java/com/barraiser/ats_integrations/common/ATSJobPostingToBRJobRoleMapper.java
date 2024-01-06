/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.common;

import com.barraiser.ats_integrations.dal.ATSJobPostingToBRJobRoleDAO;
import com.barraiser.ats_integrations.dal.ATSJobPostingToBRJobRoleRepository;
import com.barraiser.common.graphql.input.MapAtsJobPostingToBRJobRoleInput;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Log4j2
@AllArgsConstructor
public class ATSJobPostingToBRJobRoleMapper {
	private final ATSJobPostingToBRJobRoleRepository atsJobPostingToBRJobRoleRepository;

	public void mapAtsJobPostingToBRJobRole(
			final MapAtsJobPostingToBRJobRoleInput input) {
		log.info(String.format(
				"Deleting old mapping for partnerId:%s atsProvider:%s",
				input.getPartnerId(),
				input.getAtsProvider()));

		this.deleteOldAtsJobPostingToBRJobRoleMapping(input);

		log.info(String.format(
				"Saving new mapping for partnerId:%s atsProvider:%s",
				input.getPartnerId(),
				input.getAtsProvider()));

		this.saveNewAtsJobPostingToBRJobRoleMapping(input);
	}

	private void deleteOldAtsJobPostingToBRJobRoleMapping(final MapAtsJobPostingToBRJobRoleInput input) {
		List<ATSJobPostingToBRJobRoleDAO> atsJobPostingToBRJobRoleDAOList = this.atsJobPostingToBRJobRoleRepository
				.findAllByPartnerIdAndAtsProvider(
						input.getPartnerId(),
						input.getAtsProvider());

		for (ATSJobPostingToBRJobRoleDAO atsJobPostingToBRJobRoleDAO : atsJobPostingToBRJobRoleDAOList) {
			this.atsJobPostingToBRJobRoleRepository
					.delete(atsJobPostingToBRJobRoleDAO);
		}
	}

	private void saveNewAtsJobPostingToBRJobRoleMapping(final MapAtsJobPostingToBRJobRoleInput input) {
		input.getJobPostingToBRJobRoleList()
				.forEach(atsJobPostingToJobRole -> {
					final ATSJobPostingToBRJobRoleDAO atsJobPostingToBRJobRoleDAO = ATSJobPostingToBRJobRoleDAO
							.builder()
							.id(UUID.randomUUID().toString())
							.partnerId(input.getPartnerId())
							.atsJobPostingId(atsJobPostingToJobRole.getAtsJobPostingId())
							.brJobRoleId(atsJobPostingToJobRole.getJobRoleId())
							.atsProvider(input.getAtsProvider())
							.build();

					this.atsJobPostingToBRJobRoleRepository
							.save(atsJobPostingToBRJobRoleDAO);
				});
	}
}
