/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.lever;

import com.barraiser.ats_integrations.dal.ATSJobPostingToBRJobRoleDAO;
import com.barraiser.ats_integrations.dal.ATSJobPostingToBRJobRoleRepository;
import com.barraiser.common.ats_integrations.ATSProvider;
import com.barraiser.common.graphql.input.MapLeverPostingToBRJobRoleInput;
import com.barraiser.common.graphql.input.LeverPostingToJobRole;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Log4j2
@AllArgsConstructor
public class LeverPostingToBRJobRoleHandler {
	private final ATSJobPostingToBRJobRoleRepository atsJobPostingToBRJobRoleRepository;

	public void attachLeverPostingToBRJobRole(final MapLeverPostingToBRJobRoleInput input) {
		log.info(String.format(
				"Deleting old lever postings for partnerId : %s",
				input.getPartnerId()));

		this.deleteOldLeverPostingsForGivenPartnerId(input);

		log.info(String.format(
				"Saving new lever postings for partnerId %s",
				input.getPartnerId()));

		this.saveNewLeverPostingToBRJobRoleMapping(input);
	}

	private void deleteOldLeverPostingsForGivenPartnerId(final MapLeverPostingToBRJobRoleInput input) {
		List<ATSJobPostingToBRJobRoleDAO> atsJobPostingToBRJobRoleDAOList = this.atsJobPostingToBRJobRoleRepository
				.findAllByPartnerIdAndAtsProvider(
						input.getPartnerId(),
						ATSProvider.LEVER
								.getValue());

		for (ATSJobPostingToBRJobRoleDAO atsJobPostingToBRJobRoleDAO : atsJobPostingToBRJobRoleDAOList) {
			this.atsJobPostingToBRJobRoleRepository.delete(atsJobPostingToBRJobRoleDAO);
		}
	}

	private void saveNewLeverPostingToBRJobRoleMapping(final MapLeverPostingToBRJobRoleInput input) {
		for (LeverPostingToJobRole leverPostingToJobRole : input.getLeverPostingsToJobRoles()) {
			for (String jobRoleId : leverPostingToJobRole.getJobRoleIds()) {
				ATSJobPostingToBRJobRoleDAO atsJobPostingToBRJobRoleDAO = ATSJobPostingToBRJobRoleDAO
						.builder()
						.id(
								UUID
										.randomUUID()
										.toString())
						.partnerId(input.getPartnerId())
						.brJobRoleId(jobRoleId)
						.atsJobPostingId(leverPostingToJobRole.getLeverPostingId())
						.atsProvider(
								ATSProvider.LEVER
										.getValue())
						.build();

				this.atsJobPostingToBRJobRoleRepository
						.save(atsJobPostingToBRJobRoleDAO);
			}
		}
	}
}
