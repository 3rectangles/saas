/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.auth.sso;

import com.barraiser.onboarding.auth.AuthenticationException;
import com.barraiser.onboarding.auth.AuthenticationManager;
import com.barraiser.onboarding.partner.EvaluationManager;
import com.barraiser.onboarding.user.PartnerEmployeeWhiteLister;
import com.barraiser.onboarding.user.UserInformationManagementHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Log4j2
@RequiredArgsConstructor
public class SSOAuthenticator {
	private final AuthenticationManager authenticationManager;
	private final PartnerEmployeeWhiteLister partnerEmployeeWhiteLister;
	private final EvaluationManager evaluationManager;
	private final UserInformationManagementHelper userInformationManagementHelper;

	public List<ResponseCookie> authenticateSSOUser(final String source, final String email,
			final String refreshToken) {
		final Optional<String> userId = this.userInformationManagementHelper.findUserByEmail(email);
		if (userId.isEmpty()) {
			throw new AuthenticationException(AuthenticationException.USER_NOT_FOUND, "User not found : " + email);
		}
		this.storeRefreshToken(userId.get(), source, refreshToken);
		return this.authenticationManager.authenticateEmailViaSSO(email);
	}

	private void storeRefreshToken(final String userId, final String source, final String refreshToken) {
		this.userInformationManagementHelper.updateUserAttributes(userId, Map.of(
				"custom:sso_source", source,
				"custom:sso_refresh_token", refreshToken));
	}

	public void signUpPartnerForBGS(final String email, final String evaluationId) {
		final String partnerId = this.evaluationManager.getPartnerCompanyForEvaluation(evaluationId);
		this.partnerEmployeeWhiteLister.signUpUserIfWhiteListed(email, partnerId);
	}
}
