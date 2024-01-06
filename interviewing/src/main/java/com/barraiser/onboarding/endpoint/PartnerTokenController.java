/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.endpoint;

import com.barraiser.onboarding.auth.AuthenticationException;
import com.barraiser.onboarding.auth.AuthenticationManager;
import com.barraiser.onboarding.auth.otp.OtpManager;
import com.barraiser.onboarding.auth.pojo.AuthTokens;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.OtpDAO;
import com.barraiser.onboarding.dal.UserDetailsDAO;
import com.barraiser.onboarding.dal.UserDetailsRepository;

import com.barraiser.onboarding.partner.partnerTokens.pojo.GetPartnerTokensRequest;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Component
@RestController
@AllArgsConstructor
public class PartnerTokenController {

	private final UserDetailsRepository userDetailsRepository;
	private final AuthenticationManager AuthenticationManager;
	private final OtpManager otpManager;

	@PostMapping(value = "/GetPartnerTokens", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity GetPartnerTokens(final HttpServletResponse response,
			@RequestBody final GetPartnerTokensRequest getPartnerTokenRequest,
			@RequestHeader("API") final String apiKey) {

		final String endCustomerEmail = getPartnerTokenRequest.getEmail(); // for ex: Email ID of Hiring express's
																			// Customer
		log.info("Tokens requested for end user: " + endCustomerEmail);

		final Optional<AuthenticatedUser> user = this.AuthenticationManager.authenticateWithApiKey(apiKey);

		if (user.isEmpty()) {
			throw new AuthenticationException("Invalid API Key");
		}

		final UserDetailsDAO userDetails = this.userDetailsRepository.findById(user.get().getUserName()).get();

		final OtpDAO otp = this.otpManager.generateAndSaveOtp(userDetails.getEmail());

		final List<ResponseCookie> cookieList = this.AuthenticationManager.authenticateWithOtp(
				userDetails.getEmail(), // authenticated user email [hiring express emp email]
				otp.getOtp());
		final Map<String, String> cookies = cookieList.stream()
				.collect(
						Collectors.toMap(
								ResponseCookie::getName, ResponseCookie::getValue, (o1, o2) -> o1));

		final String AccessToken = cookies.get(AuthTokens.ACCESS_TOKEN);
		final String IDToken = cookies.get(AuthTokens.ID_TOKEN);

		response.addHeader(AuthTokens.ACCESS_TOKEN, AccessToken);
		response.addHeader(AuthTokens.ID_TOKEN, IDToken);

		return ResponseEntity.status(201).build();

	}
}
