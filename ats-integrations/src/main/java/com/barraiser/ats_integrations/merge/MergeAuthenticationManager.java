/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.merge;

import com.barraiser.ats_integrations.merge.DTO.MergeAccountTokenResponseDTO;
import com.barraiser.ats_integrations.merge.DTO.MergeLinkTokenRequestDTO;
import com.barraiser.ats_integrations.merge.DTO.MergeLinkTokenResponseDTO;
import com.barraiser.common.graphql.input.GetMergeLinkTokenInput;
import com.barraiser.common.graphql.input.SaveMergeAccountTokenInput;
import com.barraiser.common.graphql.types.MergeLinkToken;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
@AllArgsConstructor
public class MergeAuthenticationManager {
	private static final String ATS = "ats";

	private final MergeAuthenticationClient mergeAuthenticationClient;
	private final MergeAccessManager mergeAccessManager;

	public MergeLinkToken getMergeLinkToken(final GetMergeLinkTokenInput input)
			throws Exception {
		log.info(String.format(
				"Fetching merge link token for partnerId:%s",
				input.getPartnerId()));

		final MergeLinkTokenRequestDTO requestDTO = this.getMergeLinkTokenRequest(input);

		final MergeLinkTokenResponseDTO response = this.mergeAuthenticationClient
				.createLinkToken(
						this.mergeAccessManager
								.getAuthorizationHeader(),
						requestDTO)
				.getBody();

		return MergeLinkToken
				.builder()
				.linkToken(response.getLinkToken())
				.build();
	}

	public void saveMergeAccountToken(final SaveMergeAccountTokenInput input)
			throws Exception {
		log.info(String.format(
				"Saving merge account token for partnerId:%s",
				input.getPartnerId()));

		final MergeAccountTokenResponseDTO response = this.mergeAuthenticationClient
				.getAccountToken(
						this.mergeAccessManager
								.getAuthorizationHeader(),
						input.getPublicToken())
				.getBody();

		this.mergeAccessManager.storeAccountToken(
				input.getPartnerId(),
				response.getAccountToken());
	}

	private MergeLinkTokenRequestDTO getMergeLinkTokenRequest(final GetMergeLinkTokenInput input) {
		return MergeLinkTokenRequestDTO
				.builder()
				.endUserOriginId(input.getPartnerId())
				.endUserOrganizationName(input.getCompanyName())
				.endUserEmailAddress(input.getEmailAddress())
				.categories(List.of(ATS))
				.build();
	}
}
