/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.common;

import com.barraiser.ats_integrations.common.dto.ATSEvaluationDetailsDTO;
import com.barraiser.ats_integrations.dal.PartnerATSIntegrationDAO;
import com.barraiser.ats_integrations.dal.PartnerATSIntegrationRepository;
import com.barraiser.ats_integrations.merge.DTO.PassthroughInputDTO;
import com.barraiser.ats_integrations.merge.MergeATSClient;
import com.barraiser.ats_integrations.merge.MergeAccessManager;
import com.barraiser.commons.dto.ats.enums.ATSAggregator;
import com.barraiser.commons.dto.ats.enums.ATSProvider;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class MergeLeverCommunicationSender implements ATSCommunicationSender {

	private final MergeATSClient mergeATSClient;
	private final MergeAccessManager mergeAccessManager;
	private final PartnerATSIntegrationRepository partnerATSIntegrationRepository;

	private static final String POST_NOTE_ON_APPLICATION_PATH = "/opportunities/%s/notes/";
	private static final String APPLICATION_JSON_CONTENT_TYPE = "application/json";
	private static final String POST = "POST";
	private static final String MESSAGE_FORMAT = "{\"value\":\"%s\"}\"";

	@Override
	public ATSAggregator atsAggregator() {
		return ATSAggregator.MERGE;
	}

	@Override
	public ATSProvider atsProvider() {
		return ATSProvider.MERGE_LEVER;
	}

	@Override
	@SneakyThrows
	public void postNoteOnApplication(final String message, final ATSEvaluationDetailsDTO atsEvaluationDetailsDTO) {

		// TODO:Pass Secret Key and Account Token to this class and other such classes
		final PartnerATSIntegrationDAO partnerATSIntegration = this.partnerATSIntegrationRepository
				.findByPartnerIdAndAtsProvider(atsEvaluationDetailsDTO.getPartnerId(),
						ATSProvider.MERGE_LEVER.getValue())
				.get();

		final String authHeader = this.mergeAccessManager.getAuthorizationHeader();
		final String token = this.mergeAccessManager.getXAccountToken(partnerATSIntegration);

		mergeATSClient.callPassthrough(
				authHeader,
				token,
				PassthroughInputDTO.builder()
						.method(POST)
						.path(String.format(POST_NOTE_ON_APPLICATION_PATH, atsEvaluationDetailsDTO.getATSRemoteData()))
						.data(String.format(MESSAGE_FORMAT, message))
						.headers(PassthroughInputDTO.Headers.builder()
								.contentType(APPLICATION_JSON_CONTENT_TYPE)
								.build())
						.build());
	}
}
