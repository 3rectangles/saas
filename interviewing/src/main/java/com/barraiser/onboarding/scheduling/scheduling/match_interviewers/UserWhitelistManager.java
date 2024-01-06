/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.match_interviewers;

import com.barraiser.common.utilities.DateUtils;
import com.barraiser.onboarding.dal.UserWhitelistDAO;
import com.barraiser.onboarding.dal.UserWhitelistRepository;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class UserWhitelistManager {
	private static final String USER_TYPE_EXPERT = "EXPERT";
	private final UserWhitelistRepository userWhitelistRepository;
	private final DateUtils dateUtils;

	private Boolean isWhitelistValidTillDate(final Instant startDate, final Instant endDate) {
		return this.dateUtils.isDateInBetween(startDate, endDate, Instant.now());
	}

	public List<String> getListOfUsersWhitelistedForCompany(final String companyId) {
		final List<UserWhitelistDAO> userWhitelistDAOs = this.userWhitelistRepository
				.findAllByPartnerCompanyIdAndUserType(
						companyId, USER_TYPE_EXPERT);
		return userWhitelistDAOs.stream()
				.filter(
						x -> this.isWhitelistValidTillDate(
								x.getWhitelistStartDate(), x.getWhitelistEndDate()))
				.map(UserWhitelistDAO::getUserId)
				.collect(Collectors.toList());
	}
}
