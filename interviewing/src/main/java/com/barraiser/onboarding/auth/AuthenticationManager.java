/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.auth;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import com.barraiser.onboarding.auth.apikey.ApiKeyDAO;
import com.barraiser.onboarding.auth.apikey.ApiKeyManager;
import com.barraiser.onboarding.auth.otp.OtpManager;
import com.barraiser.onboarding.auth.pojo.AuthTokens;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.auth.pojo.Constants;
import com.barraiser.onboarding.auth.sso.SSOAccountVerifier;
import com.barraiser.onboarding.common.StaticAppConfigValues;
import com.barraiser.onboarding.dal.OtpDAO;
import com.barraiser.onboarding.user.CognitoClientSecretRetriever;
import com.barraiser.onboarding.user.UserInformationManagementHelper;
import com.barraiser.commons.auth.UserRole;
import org.apache.logging.log4j.Logger;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.stream.Collectors;

import static com.barraiser.onboarding.auth.pojo.Constants.*;

@Component
public class AuthenticationManager {
	private static final Logger log = org.apache.logging.log4j.LogManager.getLogger(AuthenticationManager.class);
	private final AWSCognitoIdentityProvider awsCognitoIdentityProvider;
	private final JwtConsumer jwtConsumer;
	private final JwtConsumer jwtConsumerWithoutExpiration;
	private final StaticAppConfigValues staticAppConfigValues;
	private final CookieManager cookieManager;
	private final OtpManager otpManager;
	private final ApiKeyManager apikeyManager;
	private final UserInformationManagementHelper userInformationManagementHelper;
	private final SSOAccountVerifier ssoAccountVerifier;
	private final CognitoClientSecretRetriever cognitoClientSecret;

	/**
	 * The reason I have not used @{@link lombok.AllArgsConstructor}
	 * or @{@link lombok.RequiredArgsConstructor}
	 * here is that - @{@link Qualifier} annotation apparently does not work with
	 * lombok's @{@link lombok.AllArgsConstructor}
	 */
	public AuthenticationManager(final AWSCognitoIdentityProvider awsCognitoIdentityProvider,
			@Qualifier("jwtConsumer") final JwtConsumer jwtConsumer,
			@Qualifier("jwtConsumerWithoutExpirationTime") final JwtConsumer jwtConsumerWithoutExpiration,
			final StaticAppConfigValues staticAppConfigValues,
			final CookieManager cookieManager,
			final OtpManager otpManager,
			final ApiKeyManager apikeyManager,
			final UserInformationManagementHelper userInformationManagementHelper,
			final SSOAccountVerifier ssoAccountVerifier,
			final CognitoClientSecretRetriever cognitoClientSecretRetriever) {
		this.awsCognitoIdentityProvider = awsCognitoIdentityProvider;
		this.jwtConsumer = jwtConsumer;
		this.jwtConsumerWithoutExpiration = jwtConsumerWithoutExpiration;
		this.staticAppConfigValues = staticAppConfigValues;
		this.cookieManager = cookieManager;
		this.otpManager = otpManager;
		this.apikeyManager = apikeyManager;
		this.userInformationManagementHelper = userInformationManagementHelper;
		this.ssoAccountVerifier = ssoAccountVerifier;
		this.cognitoClientSecret = cognitoClientSecretRetriever;
	}

	public Optional<AuthenticatedUser> authenticateWitIdToken(final String idToken) throws InvalidJwtException {
		try {
			final Map<String, Object> claims = this.jwtConsumer.processToClaims(idToken).getClaimsMap();

			final String email = (String) claims.get(COGNITO_CLAIMS_KEY_EMAIL);
			final String userName = (String) claims.get(COGNITO_CLAIMS_KEY_USERNAME);
			final String phoneNumber = (String) claims.get(COGNITO_CLAIMS_KEY_PHONE_NUMBER);
			final boolean isEmailVerified = (Boolean) Optional.ofNullable(claims.get(COGNITO_CLAIMS_KEY_EMAIL_VERIFIED))
					.orElse(false);
			final List<String> roles = claims.get(COGNITO_GROUPS) == null ? List.of()
					: (List<String>) claims.get(COGNITO_GROUPS);
			final String partnerId = (String) claims.get(COGNITO_CLAIMS_KEY_CUSTOM_PARTNER_ID);
			return Optional.of(AuthenticatedUser.builder()
					.userName(userName)
					.email(email)
					.emailVerified(isEmailVerified)
					.phone(phoneNumber)
					.roles(roles.stream().map(UserRole::fromString).collect(Collectors.toList()))
					.partnerId(partnerId)
					.loginMethod("jwt")
					.build());
		} catch (final Exception ex) {
			log.warn(ex, ex);
			return Optional.empty();
		}
	}

	/**
	 * @param idToken
	 *            idToken is required as the client is configured with client secret
	 *            and secret hash calculation needs
	 *            a user name.
	 */
	public AuthenticationResultType authenticateWithRefreshToken(final String idToken, final String refreshToken)
			throws InvalidJwtException, IOException, GeneralSecurityException {

		final JwtClaims claims = this.jwtConsumerWithoutExpiration.processToClaims(idToken);
		final String userName = (String) claims.getClaimsMap().get(COGNITO_CLAIMS_KEY_USERNAME);

		this.verifySSOAccount(userName);

		final Map<String, String> params = new HashMap<>();
		params.put("REFRESH_TOKEN", refreshToken);
		params.put("SECRET_HASH", this.calculateSecretHash(this.staticAppConfigValues.getCognitoBackendClientId(),
				this.cognitoClientSecret.retrieve(),
				userName));

		final AdminInitiateAuthResult result = this.awsCognitoIdentityProvider
				.adminInitiateAuth(new AdminInitiateAuthRequest()
						.withAuthFlow(AuthFlowType.REFRESH_TOKEN_AUTH)
						.withAuthParameters(params)
						.withUserPoolId(this.staticAppConfigValues.getUserPoolId())
						.withClientId(this.staticAppConfigValues.getCognitoBackendClientId()));
		return result.getAuthenticationResult();
	}

	private void verifySSOAccount(final String userId) throws IOException, GeneralSecurityException {
		final Map<String, String> userAttributes = this.userInformationManagementHelper.getUserAttributes(userId);
		if (userAttributes.containsKey("custom:sso_source")) {
			log.info("verifying sso account for user : " + userId);
			this.ssoAccountVerifier.verify(userAttributes.get("custom:sso_source"),
					userAttributes.get("custom:sso_refresh_token"));
		}
	}

	public List<ResponseCookie> authenticateEmailViaSSO(final String email) {
		return this.authenticateWithOtpWithoutClearingSSODetails(email, "");
	}

	public List<ResponseCookie> authenticateWithOtp(final String email, final String otp) {
		final List<ResponseCookie> cookies = this.authenticateWithOtpWithoutClearingSSODetails(email, otp);
		final String userId = this.userInformationManagementHelper.findUserByEmail(email).get();
		this.ssoAccountVerifier.clearSSOAccountDetails(userId);
		return cookies;
	}

	public List<ResponseCookie> authenticateWithOtpWithoutClearingSSODetails(final String email, final String otp) {
		final Map<String, String> params = new HashMap<>();
		params.put("USERNAME", email);
		params.put("SECRET_HASH", this.calculateSecretHash(this.staticAppConfigValues.getCognitoBackendClientId(),
				this.cognitoClientSecret.retrieve(), email));

		log.info("Initiating auth for {} with otp {}", email, otp);
		final InitiateAuthResult response = this.awsCognitoIdentityProvider.initiateAuth(new InitiateAuthRequest()
				.withAuthFlow(AuthFlowType.CUSTOM_AUTH)
				.withClientId(this.staticAppConfigValues.getCognitoBackendClientId())
				.withAuthParameters(params));

		params.put("ANSWER", otp);

		final AuthenticationResultType authResult;
		if (Constants.CUSTOM_AUTH_CHALLENGE.equals(response.getChallengeName())) {
			log.info("Custom challenge received for {}", email);
			final RespondToAuthChallengeResult challengeResponse = this.awsCognitoIdentityProvider
					.respondToAuthChallenge(new RespondToAuthChallengeRequest()
							.withClientId(this.staticAppConfigValues.getCognitoBackendClientId())
							.withChallengeName(Constants.CUSTOM_AUTH_CHALLENGE)
							.withSession(response.getSession())
							.withChallengeResponses(params)
							.withClientMetadata(params));
			authResult = challengeResponse.getAuthenticationResult();
		} else {
			authResult = response.getAuthenticationResult();
			log.info("Received auth result: TokenType: {}", authResult.getTokenType());
		}

		return this.getCookies(authResult);
	}

	public Optional<AuthenticatedUser> authenticateWithApiKey(final String apiKey) {
		final Optional<ApiKeyDAO> apiKeyObj = this.apikeyManager.getApiKey(apiKey);

		return apiKeyObj.map(apiKeyDAO -> AuthenticatedUser.builder()
				.loginMethod("apikey")
				.userName(apiKeyDAO.getUserId())
				.partnerId(apiKeyDAO.getPartnerId())
				.roles(apiKeyDAO.getRoles()
						.stream()
						.map(UserRole::fromString)
						.collect(Collectors.toList()))
				.build());
	}

	private String calculateSecretHash(final String userPoolClientId,
			final String userPoolClientSecret,
			final String userName) {
		final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

		final SecretKeySpec signingKey = new SecretKeySpec(
				userPoolClientSecret.getBytes(StandardCharsets.UTF_8),
				HMAC_SHA256_ALGORITHM);
		try {
			final Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
			mac.init(signingKey);
			mac.update(userName.getBytes(StandardCharsets.UTF_8));
			final byte[] rawHmac = mac.doFinal(userPoolClientId.getBytes(StandardCharsets.UTF_8));
			return Base64.getEncoder().encodeToString(rawHmac);
		} catch (final Exception e) {
			throw new RuntimeException("Error while calculating ");
		}
	}

	public List<ResponseCookie> getCookies(final AuthenticationResultType authResult) {
		log.info("child thread " + Thread.currentThread().getId());
		log.info("Getting the cookies now");

		final List<ResponseCookie> cookies = new ArrayList<>();
		if (authResult.getRefreshToken() != null) {
			cookies.add(this.cookieManager.getBarRaiserCookie(AuthTokens.REFRESH_TOKEN, authResult.getRefreshToken())
					.build());
		}
		if (authResult.getIdToken() != null) {
			cookies.add(this.cookieManager.getBarRaiserCookie(AuthTokens.ID_TOKEN, authResult.getIdToken()).build());
		}
		if (authResult.getAccessToken() != null) {
			cookies.add(this.cookieManager.getBarRaiserCookie(AuthTokens.ACCESS_TOKEN, authResult.getAccessToken())
					.build());
		}
		log.info("Cookies have been formed.");
		return cookies;
	}

	public Boolean verifySubmittedOtp(final String email, final String submittedOtp) {
		final Optional<OtpDAO> latestUnExpiredOTP = this.otpManager.getLatestUnExpiredOTP(email);
		if (latestUnExpiredOTP.isEmpty()) {
			log.error("No OTP was sent to the user");
		} else if (submittedOtp.equals(latestUnExpiredOTP.get().getOtp())) {
			this.otpManager.markOtpVerified(latestUnExpiredOTP.get());
			return true;
		}
		return false;
	}

}
