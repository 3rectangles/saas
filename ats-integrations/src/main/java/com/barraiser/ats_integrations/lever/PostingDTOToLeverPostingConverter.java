/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.lever;

import com.barraiser.ats_integrations.dal.ATSJobPostingToBRJobRoleDAO;
import com.barraiser.ats_integrations.lever.DTO.PostingDTO;
import com.barraiser.common.graphql.types.LeverPosting;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Log4j2
@AllArgsConstructor
public class PostingDTOToLeverPostingConverter {
	public LeverPosting getLeverPostingFromPostingDTO(
			final PostingDTO postingDTO,
			final List<ATSJobPostingToBRJobRoleDAO> atsJobPostingToBRJobRoleDAOList) {

		List<String> jobRoleIds = new ArrayList<>();
		for (ATSJobPostingToBRJobRoleDAO atsJobPostingToBRJobRoleDAO : atsJobPostingToBRJobRoleDAOList) {
			jobRoleIds.add(atsJobPostingToBRJobRoleDAO.getBrJobRoleId());
		}

		return LeverPosting
				.builder()
				.id(postingDTO.getId())
				.name(postingDTO.getText())
				.jobRoleIds(jobRoleIds)
				.build();
	}
}
