/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.partner;

import com.barraiser.common.graphql.input.PartnerAccessInput;
import com.barraiser.commons.auth.UserRole;
import com.barraiser.onboarding.dal.ExpertDAO;
import com.barraiser.onboarding.dal.PartnerCompanyRepository;
import com.barraiser.onboarding.interview.jira.expert.ExpertElasticSearchManager;
import com.barraiser.onboarding.user.UserInformationManagementHelper;
import com.barraiser.onboarding.user.expert.ExpertDBManager;
import com.barraiser.onboarding.user.expert.dto.ExpertDetails;
import com.barraiser.onboarding.user.expert.mapper.ExpertMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Component
public class PartnerExpertMaker {

	private final ExpertDBManager expertDBManager;
	private final ExpertElasticSearchManager expertElasticSearchManager;
	private final ExpertMapper expertMapper;
	private final PartnerCompanyRepository partnerCompanyRepository;
	private final UserInformationManagementHelper userInformationManagementHelper;

	public void makeExpert(final String userId, final PartnerAccessInput input) throws IOException {
		final ExpertDAO expertDAO = this.expertDBManager.getOrCreateExpertById(userId);
		final ExpertDetails expertDetails = this.constructExpertDetails(expertDAO, input);
		this.expertDBManager.updateExpertDetails(expertDetails);
		this.expertElasticSearchManager.updateExpertDetails(expertDetails);
		this.userInformationManagementHelper.addUserRole(userId, UserRole.EXPERT);
	}

	private ExpertDetails constructExpertDetails(final ExpertDAO expertDAO, final PartnerAccessInput input) {
		return this.expertMapper.toExpertDetails(expertDAO).toBuilder()
				.isActive(Boolean.TRUE)
				.companiesForWhichExpertCanTakeInterview(this.getUpdatedCompaniesExpertCanTakeInterviewsFor(
						input.getPartnerId(), expertDAO.getCompaniesForWhichExpertCanTakeInterview()))
				.tenantId(input.getPartnerId())
				.build();
	}

	private List<String> getUpdatedCompaniesExpertCanTakeInterviewsFor(final String partnerId,
			final List<String> companiesExpertCanTakeInterviewsFor) {
		final String companyId = this.partnerCompanyRepository.findById(partnerId).get().getCompanyId();
		final List<String> updatedCompaniesForWhichExpertCanTakeInterviews = new ArrayList<>();

		if (companiesExpertCanTakeInterviewsFor != null) {
			updatedCompaniesForWhichExpertCanTakeInterviews.addAll(companiesExpertCanTakeInterviewsFor);
		}
		updatedCompaniesForWhichExpertCanTakeInterviews.add(companyId);
		return updatedCompaniesForWhichExpertCanTakeInterviews;
	}
}
