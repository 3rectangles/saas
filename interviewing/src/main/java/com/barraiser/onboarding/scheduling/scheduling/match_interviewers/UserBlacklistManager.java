/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.match_interviewers;

import com.barraiser.onboarding.dal.UserBlacklistDAO;
import com.barraiser.onboarding.dal.UserBlacklistRepository;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class UserBlacklistManager {

	private final UserBlacklistRepository userBlacklistRepository;

	private static final String USER_TYPE_EXPERT = "EXPERT";
	private static final String BLACKLIST_GRANULARITY_ALL_COMPANIES = "ALL_COMPANIES";
	private static final String BLACKLIST_GRANULARITY_COMPANY_SPECIFIC = "COMPANY_SPECIFIC";

	public List<String> getAllBlacklistedInterviewersForCompany(final String partnerCompanyId) {

		List<String> blacklistedInterviewers = this.userBlacklistRepository.findAllByUserType(USER_TYPE_EXPERT).stream()
				.filter(
						userBlacklistDAO -> this.isInBlacklistedForCompany(
								userBlacklistDAO, partnerCompanyId)
								&& this.isBlacklistValidTillDate(
										userBlacklistDAO, Instant.now()))
				.map(userBlacklistDAO -> userBlacklistDAO.getUserId())
				.collect(Collectors.toList());

		return blacklistedInterviewers;
	}

	private Boolean isInBlacklistedForCompany(
			final UserBlacklistDAO userBlacklistDAO, final String partnerCompanyId) {
		return BLACKLIST_GRANULARITY_ALL_COMPANIES.equalsIgnoreCase(
				userBlacklistDAO.getGranularity())
				|| (BLACKLIST_GRANULARITY_COMPANY_SPECIFIC.equalsIgnoreCase(
						userBlacklistDAO.getGranularity())
						&& (partnerCompanyId.equals(userBlacklistDAO.getPartnerCompanyId())));
	}

	private Boolean isBlacklistValidTillDate(
			final UserBlacklistDAO userBlacklistDAO, final Instant date) {

		final Instant blacklistStartDate = userBlacklistDAO.getBlacklistStartDate() == null
				? Instant.EPOCH
				: userBlacklistDAO.getBlacklistStartDate();
		final Instant blacklistEndDate = userBlacklistDAO.getBlacklistEndDate() == null
				? Instant.MAX
				: userBlacklistDAO.getBlacklistEndDate();

		return date.isAfter(blacklistStartDate) && date.isBefore(blacklistEndDate);
	}
}
