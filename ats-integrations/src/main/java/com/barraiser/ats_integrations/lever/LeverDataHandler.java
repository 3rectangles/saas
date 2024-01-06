/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.lever;

import com.barraiser.ats_integrations.dal.ATSJobPostingToBRJobRoleDAO;
import com.barraiser.ats_integrations.dal.ATSJobPostingToBRJobRoleRepository;
import com.barraiser.ats_integrations.dal.ATSAccessTokenDAO;
import com.barraiser.ats_integrations.dal.ATSAccessTokenRepository;
import com.barraiser.ats_integrations.lever.DTO.PostingDTO;
import com.barraiser.common.ats_integrations.ATSProvider;
import com.barraiser.common.graphql.input.GetLeverDataInput;
import com.barraiser.common.graphql.types.Lever;
import com.barraiser.common.graphql.types.LeverPosting;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Log4j2
@AllArgsConstructor
public class LeverDataHandler {
	private final ATSAccessTokenRepository atsAccessTokenRepository;
	private final LeverPostingHandler leverPostingHandler;
	private final ATSJobPostingToBRJobRoleRepository atsJobPostingToBRJobRoleRepository;
	private final PostingDTOToLeverPostingConverter postingDTOToLeverPostingConverter;

	public Lever getLeverData(final GetLeverDataInput input) throws Exception {
		Optional<ATSAccessTokenDAO> leverRefreshTokenDAOOptional = this.atsAccessTokenRepository
				.findByPartnerIdAndAtsProvider(
						input.getPartnerId(),
						ATSProvider.LEVER.getValue());

		Lever result;
		if (leverRefreshTokenDAOOptional.isPresent()) {
			result = Lever
					.builder()
					.isLeverIntegrated(true)
					.postings(this.getLeverPostings(input.getPartnerId()))
					.build();
		} else {
			result = Lever
					.builder()
					.isLeverIntegrated(false)
					.postings(List.of())
					.build();
		}

		return result;
	}

	private List<LeverPosting> getLeverPostings(final String partnerId) throws Exception {
		final List<PostingDTO> postingDTOs = this.leverPostingHandler
				.getLeverPostings(partnerId);

		final List<LeverPosting> leverPostings = new ArrayList<>();
		for (PostingDTO postingDTO : postingDTOs) {
			final List<ATSJobPostingToBRJobRoleDAO> atsJobPostingToBRJobRoleDAOList = this.atsJobPostingToBRJobRoleRepository
					.findAllByAtsJobPostingIdAndAtsProvider(
							postingDTO.getId(),
							ATSProvider.LEVER
									.getValue());

			leverPostings.add(
					this.postingDTOToLeverPostingConverter
							.getLeverPostingFromPostingDTO(postingDTO, atsJobPostingToBRJobRoleDAOList));
		}

		return leverPostings;
	}
}
