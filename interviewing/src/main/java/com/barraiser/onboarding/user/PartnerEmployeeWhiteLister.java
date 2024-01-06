/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.user;

import com.barraiser.commons.auth.UserRole;
import com.barraiser.onboarding.auth.AuthorizationException;
import com.barraiser.common.utilities.EmailParser;
import com.barraiser.onboarding.dal.*;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class PartnerEmployeeWhiteLister {
	private final UserInformationManagementHelper userManagement;
	private final PartnerWhitelistedDomainsRepository partnerWhitelistedDomainsRepository;
	private final PartnerBlacklistedDomainsRepository partnerBlacklistedDomainsRepository;
	private final PartnerRepsRepository partnerRepsRepository;
	public static final String DYNAMO_DEMO_COMPANIES = "demo-companies";

	public void signUpUserIfWhiteListed(final String email, final String partnerId) throws AuthorizationException {
		if (this.userManagement.doesUserExistsByEmail(email)) {
			final UserDetailsDAO userDetails = this.userManagement.getOrCreateUserByEmail(email);
			final Boolean isUserWhitelistedForPartner = this.isUserWhiteListedForPartner(email, partnerId);
			if (isUserWhitelistedForPartner) {
				this.userManagement.updateUserAttributes(userDetails.getId(),
						Map.of("custom:partnerId", this.getUpdatedUserPartnerId(userDetails.getId(), partnerId)));
			}
			return;
		}
		if (!this.isUserWhiteListedForPartner(email, partnerId)) {
			throw new AuthorizationException();
		}
		final UserDetailsDAO user = this.userManagement.getOrCreateUserByEmail(email);
		this.userManagement.addUserRole(user.getId(), UserRole.PARTNER_EMPLOYEE);
		this.userManagement.updateUserAttributes(user.getId(),
				Map.of("custom:partnerId", this.getUpdatedUserPartnerId(user.getId(), partnerId)));
	}

	private boolean isValidPartnerRep(final String userId, final String partnerId, final List<String> roles) {
		final Optional<PartnerRepsDAO> partnerRep = this.partnerRepsRepository.findByPartnerRepIdAndPartnerId(userId,
				partnerId);
		return partnerRep.isPresent() && roles.contains(UserRole.PARTNER.getRole());
	}

	public boolean isUserWhiteListedForPartner(final String email, final String partnerId) {
		final List<String> emailDomains = this.partnerWhitelistedDomainsRepository.findAllByPartnerId(partnerId)
				.stream().map(PartnerWhitelistedDomainDAO::getEmailDomain).collect(Collectors.toList());
		final String domainOfEmail = EmailParser.getDomainFromEmail(email);
		return emailDomains.contains("*") || emailDomains.contains(domainOfEmail);
	}

	public boolean isUserDomainBlackListedForPartner(final String email, final String partnerId) {
		final String domainOfEmail = EmailParser.getDomainFromEmail(email);
		return this.partnerBlacklistedDomainsRepository.findByPartnerIdAndEmailDomain(partnerId, domainOfEmail)
				.isPresent();
	}

	private String getUpdatedUserPartnerId(final String userId, final String partnerId) {
		final String userPartnerIds = this.userManagement.getUserAttributes(userId).get("custom:partnerId");
		return userPartnerIds == null || userPartnerIds.isEmpty() ? partnerId
				: Arrays.stream(userPartnerIds.split(","))
						.noneMatch(x -> x.equals(partnerId)) ? userPartnerIds + "," + partnerId : userPartnerIds;
	}
}
