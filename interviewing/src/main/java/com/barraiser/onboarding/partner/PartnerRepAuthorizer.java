/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.partner;

import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.PartnerCompanyDAO;
import com.barraiser.onboarding.dal.PartnerRepsDAO;
import com.barraiser.onboarding.dal.PartnerRepsRepository;
import com.barraiser.onboarding.interview.PartnerConfigManager;
import com.barraiser.commons.auth.UserRole;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
@Log4j2
public class PartnerRepAuthorizer {
	private final PartnerRepsRepository partnerRepsRepository;
	private final PartnerConfigManager partnerConfigManager;

	public Boolean isPartnerForInterview(final AuthenticatedUser user, final String interviewId) {
		if (!user.getRoles().contains(UserRole.PARTNER)) {
			return Boolean.FALSE;
		}
		final PartnerCompanyDAO partnerCompanyDAO = this.partnerConfigManager
				.getPartnerCompanyForInterviewId(interviewId);
		return this.partnerRepsRepository.findAllByPartnerRepId(user.getUserName()).stream()
				.anyMatch(x -> x.getPartnerId().equals(partnerCompanyDAO.getId()));
	}

	public Boolean isPartnerRepForPartner(final AuthenticatedUser user, final String partnerId) {

		final Optional<PartnerRepsDAO> partnerRep = this.partnerRepsRepository
				.findByPartnerRepIdAndPartnerId(user.getUserName(), partnerId);
		return partnerRep.isPresent() && user.getRoles().contains(UserRole.PARTNER);
	}

	public Boolean isPartnerRep(final AuthenticatedUser user) {
		final Optional<PartnerRepsDAO> partnerRep = this.partnerRepsRepository
				.findByPartnerRepId(user.getUserName());
		return partnerRep.isPresent() && user.getRoles().contains(UserRole.PARTNER);
	}
}
