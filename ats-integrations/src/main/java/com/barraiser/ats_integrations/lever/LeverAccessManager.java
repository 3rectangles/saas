/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.lever;

import com.barraiser.ats_integrations.config.LeverSecretFactory;
import com.barraiser.ats_integrations.dal.ATSAccessTokenDAO;
import com.barraiser.ats_integrations.dal.ATSAccessTokenRepository;
import com.barraiser.ats_integrations.lever.requests.LeverAccessTokenRequestBody;
import com.barraiser.ats_integrations.lever.requests.LeverRefreshAccessTokenRequestBody;
import com.barraiser.ats_integrations.lever.responses.LeverAccessTokenResponseBody;
import com.barraiser.ats_integrations.lever.responses.LeverRefreshAccessTokenResponseBody;
import com.barraiser.common.ats_integrations.ATSProvider;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@Log4j2
@AllArgsConstructor
public class LeverAccessManager {
	private final LeverAccessTokenClient leverAccessTokenClient;
	private final ATSAccessTokenRepository atsAccessTokenRepository;
	private final LeverSecretFactory leverSecretFactory;

	public void requestRefreshToken(
			String oneTimeAuthorizationCode,
			String partnerId) throws Exception {
		log.info(String.format(
				"Requesting refresh token from lever for partnerId : %s",
				partnerId));

		final LeverSecret leverSecret = this.leverSecretFactory.getLeverSecret();

		final LeverAccessTokenRequestBody request = LeverAccessTokenRequestBody
				.builder()
				.clientId(leverSecret.getClientId())
				.clientSecret(leverSecret.getClientSecret())
				.grantType("authorization_code")
				.code(oneTimeAuthorizationCode)
				.redirectUri(leverSecret.getRedirectUri())
				.build();

		LeverAccessTokenResponseBody response = null;
		try {
			response = this.leverAccessTokenClient
					.requestAccessToken(request)
					.getBody();
		} catch (Exception exception) {
			log.warn("Unable to request access token from lever : ", exception);
			throw exception;
		}

		Optional<ATSAccessTokenDAO> atsAccessTokenDAO = this.atsAccessTokenRepository
				.findByPartnerIdAndAtsProvider(
						partnerId,
						ATSProvider.LEVER.getValue());

		atsAccessTokenDAO.ifPresent(this.atsAccessTokenRepository::delete);

		this.saveRefreshTokenToDatabase(
				response,
				partnerId);
	}

	private void saveRefreshTokenToDatabase(
			final LeverAccessTokenResponseBody response,
			final String partnerId) {
		log.info(String.format(
				"Saving refresh token of partnerId %s into database",
				partnerId));

		final ATSAccessTokenDAO atsAccessTokenDAO = ATSAccessTokenDAO
				.builder()
				.id(UUID.randomUUID().toString())
				.partnerId(partnerId)
				.token(response.getRefreshToken())
				.tokenType(response.getTokenType())
				.atsProvider(ATSProvider.LEVER.getValue())
				.build();

		this.atsAccessTokenRepository
				.save(atsAccessTokenDAO);
	}

	private String getAccessToken(final String partnerId) throws Exception {
		log.info("Fetching lever access token for partnerId : " + partnerId);

		final ATSAccessTokenDAO atsAccessTokenDAO = this.atsAccessTokenRepository
				.findByPartnerIdAndAtsProvider(
						partnerId,
						ATSProvider.LEVER.getValue())
				.get();

		final LeverSecret leverSecret = this.leverSecretFactory.getLeverSecret();

		final LeverRefreshAccessTokenRequestBody requestBody = LeverRefreshAccessTokenRequestBody
				.builder()
				.clientId(leverSecret.getClientId())
				.clientSecret(leverSecret.getClientSecret())
				.grantType("refresh_token")
				.refreshToken(atsAccessTokenDAO.getToken())
				.build();

		LeverRefreshAccessTokenResponseBody responseBody = null;

		try {
			responseBody = this.leverAccessTokenClient
					.refreshAccessToken(requestBody)
					.getBody();
		} catch (Exception exception) {
			log.warn("Unable to fetch refreshed access token from lever : ", exception);
			throw exception;
		}

		this.saveNewRefreshTokenToDatabase(
				atsAccessTokenDAO,
				responseBody);

		return responseBody.getAccessToken();
	}

	private void saveNewRefreshTokenToDatabase(
			final ATSAccessTokenDAO ATSAccessTokenDAO,
			final LeverRefreshAccessTokenResponseBody responseBody) {
		final ATSAccessTokenDAO updatedATSAccessTokenDAO = ATSAccessTokenDAO
				.toBuilder()
				.token(responseBody.getRefreshToken())
				.tokenType(responseBody.getTokenType())
				.build();

		this.atsAccessTokenRepository.save(updatedATSAccessTokenDAO);
	}

	public String getAuthorization(String partnerId) throws Exception {
		final String accessToken = this.getAccessToken(partnerId);
		final ATSAccessTokenDAO ATSAccessTokenDAO = this.atsAccessTokenRepository
				.findByPartnerIdAndAtsProvider(
						partnerId,
						ATSProvider.LEVER.getValue())
				.get();

		return String.format(
				"%s %s",
				ATSAccessTokenDAO.getTokenType(),
				accessToken);
	}
}
