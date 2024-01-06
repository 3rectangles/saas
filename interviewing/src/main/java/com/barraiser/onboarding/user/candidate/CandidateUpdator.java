/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.user.candidate;

import com.barraiser.onboarding.candidate.CandidateInformationManager;
import com.barraiser.onboarding.dal.CandidateDAO;
import com.barraiser.onboarding.user.UserInformationManagementHelper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.*;

@Log4j2
@Component
@AllArgsConstructor
public class CandidateUpdator {
	private final UserInformationManagementHelper userInformationManagementHelper;
	private final CandidateInformationManager candidateInformationManager;

	public void updateFields(final String candidateId, final String candidateUserId,
			final Map<String, Object> userFields) {

		if (!this.candidateInformationManager.isCandidateAnonymous(candidateId)) {
			this.userInformationManagementHelper.updateUserAccessDataInDb(candidateUserId, userFields);
			this.userInformationManagementHelper.updateCognito(candidateUserId, userFields);
		}

		this.updateCandidateDataInDb(candidateId, candidateUserId, userFields);
	}

	private void updateCandidateDataInDb(final String candidateId, final String candidateUserId,
			final Map<String, Object> userFields) {

		final CandidateDAO candidateDAO = this.candidateInformationManager.getCandidate(candidateId);
		final String firstName = (String) userFields.get("firstName");
		final String lastName = (String) userFields.get("lastName");
		final String designation = (String) userFields.get("designation");
		final String almaMater = (String) userFields.get("almaMater");
		final String currentCompany = (String) userFields.get("currentCompany");
		final List<String> lastCompanies = (List<String>) userFields.get("lastCompanies");
		final String timezone = (String) userFields.get("timezone");
		final Integer workExperience = (Integer) userFields.get("workExperience");

		this.candidateInformationManager.updateCandidate(candidateDAO.toBuilder()
				.userId(candidateUserId)
				.firstName(firstName)
				.lastName(lastName)
				.designation(designation)
				.almaMater(almaMater)
				.currentCompanyName(currentCompany)
				.lastCompanies(lastCompanies)
				.timezone(timezone)
				.workExperienceInMonths(workExperience)
				.build());
	}
}
