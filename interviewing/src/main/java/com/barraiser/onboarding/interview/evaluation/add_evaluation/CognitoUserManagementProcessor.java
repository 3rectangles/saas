/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.add_evaluation;

import com.barraiser.common.utilities.PhoneParser;
import com.barraiser.commons.auth.UserRole;
import com.barraiser.onboarding.user.UserInformationManagementHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Component
public class CognitoUserManagementProcessor implements AddEvaluationProcessor {
	private final UserInformationManagementHelper userInformationManagementHelper;
	private final PhoneParser phoneParser;

	@Override
	public void process(final AddEvaluationProcessingData data) {
		final String userId = this.getOrCreateUser(data);
		data.setUserId(userId);
		this.updateUserDetails(data);
	}

	private String getOrCreateUser(final AddEvaluationProcessingData data) {
		final Optional<String> userId = this.userInformationManagementHelper.findUserByEmail(data.getEmail());

		if (userId.isEmpty()) {
			return this.userInformationManagementHelper.createUserInCognito(data.getEmail(), data.getCandidateName(),
					null);
		}
		return userId.get();
	}

	public void updateUserDetails(final AddEvaluationProcessingData data) {
		final String formattedPhone = this.phoneParser.getFormattedPhone(data.getPhone());

		this.userInformationManagementHelper.updateUserAttributes(data.getUserId(), Map.of(
				"given_name", data.getCandidateName(),
				"email", data.getEmail(),
				"custom:phone_number", formattedPhone,
				"email_verified", "true"));
		this.userInformationManagementHelper.addUserRole(data.getUserId(), UserRole.CANDIDATE);

	}
}
