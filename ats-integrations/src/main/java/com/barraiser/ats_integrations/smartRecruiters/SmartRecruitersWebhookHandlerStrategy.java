/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.smartRecruiters;

import com.barraiser.ats_integrations.common.ATSWebhookHandlerStrategy;
import com.barraiser.ats_integrations.dal.PartnerATSIntegrationDAO;
import com.barraiser.ats_integrations.smartRecruiters.DTO.WebhookSubscriptionRequestDTO;
import com.barraiser.ats_integrations.smartRecruiters.DTO.WebhookSubscriptionResponseDTO;
import com.barraiser.common.ats_integrations.ATSProvider;
import com.barraiser.common.graphql.input.ActivateATSWebhookInput;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Component
@Log4j2
@AllArgsConstructor
public class SmartRecruitersWebhookHandlerStrategy implements ATSWebhookHandlerStrategy {
	private static final String WEBHOOK_BASE_URL = "https://api.barraiser.com/smart-recruiters-webhook?partner_id=%s";
	private static final String WEBHOOK_EVENT = "application.status.updated";
	private static final String SUPPORT_EMAIL_ADDRESS = "tech@barraiser.com";
	private static final String HEADER = "header";
	private static final String HEADER_NAME = "X-Token";

	private final SmartRecruitersClient smartRecruitersClient;
	private final SmartRecruitersAccessManager smartRecruitersAccessManager;

	@Override
	public String getATSProvider() {
		return ATSProvider.SMART_RECRUITERS.getValue();
	}

	@Override
	public void activateATSWebhook(final PartnerATSIntegrationDAO partnerATSIntegrationDAO) {
		final WebhookSubscriptionRequestDTO requestDTO = this
				.getWebhookSubscriptionRequestDTO(partnerATSIntegrationDAO);

		final WebhookSubscriptionResponseDTO responseDTO = this.subscribeWebhook(
				partnerATSIntegrationDAO,
				requestDTO);

		this.activateWebhook(
				partnerATSIntegrationDAO,
				responseDTO);
	}

	private WebhookSubscriptionRequestDTO getWebhookSubscriptionRequestDTO(
			final PartnerATSIntegrationDAO partnerATSIntegrationDAO) {
		return WebhookSubscriptionRequestDTO
				.builder()
				.callbackUrl(this.getWebhookURL(partnerATSIntegrationDAO.getPartnerId()))
				.events(List.of(WEBHOOK_EVENT))
				.alertingEmailAddress(SUPPORT_EMAIL_ADDRESS)
				.callbackAuthentication(
						WebhookSubscriptionRequestDTO.CallbackAuthentication
								.builder()
								.type(HEADER)
								.headerName(HEADER_NAME)
								.headerValue(this.generateKey())
								.build())
				.build();
	}

	private WebhookSubscriptionResponseDTO subscribeWebhook(
			final PartnerATSIntegrationDAO partnerATSIntegrationDAO,
			final WebhookSubscriptionRequestDTO requestDTO) {
		try {
			final String apiKey = this.smartRecruitersAccessManager
					.getApiKey(partnerATSIntegrationDAO);

			return this.smartRecruitersClient
					.subscribeWebhook(apiKey, requestDTO)
					.getBody();
		} catch (Exception exception) {
			log.error(
					String.format(
							"Unable to subscribe SR webhook for partnerId:%s",
							partnerATSIntegrationDAO.getPartnerId()),
					exception);

			throw exception;
		}
	}

	private void activateWebhook(final PartnerATSIntegrationDAO partnerATSIntegrationDAO,
			final WebhookSubscriptionResponseDTO responseDTO) {
		try {
			final String apiKey = this.smartRecruitersAccessManager
					.getApiKey(partnerATSIntegrationDAO);

			this.smartRecruitersClient
					.activateWebhookSubscription(
							apiKey,
							responseDTO.getId());
		} catch (Exception exception) {
			log.error(
					String.format(
							"Unable to activate SR webhook for partnerId:%s",
							partnerATSIntegrationDAO.getPartnerId()),
					exception);

			throw exception;
		}
	}

	private String getWebhookURL(final String partnerId) {
		return String.format(WEBHOOK_BASE_URL, partnerId);
	}

	private String generateKey() {
		return Base64
				.getEncoder()
				.encodeToString(UUID
						.randomUUID()
						.toString()
						.getBytes());
	}
}
