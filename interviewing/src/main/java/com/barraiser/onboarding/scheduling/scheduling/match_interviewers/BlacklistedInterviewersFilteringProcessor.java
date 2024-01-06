/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.match_interviewers;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class BlacklistedInterviewersFilteringProcessor implements MatchInterviewersProcessor {

	private final UserBlacklistManager userBlacklistManager;
	private final UserWhitelistManager userWhitelistManager;

	@Override
	public void process(final MatchInterviewersData data) {
		this.filterOutBlacklistedInterviewers(data);
		this.filterForWhitelistedInterviewers(data);
	}

	public void filterOutBlacklistedInterviewers(final MatchInterviewersData data) {
		final String companyId = data.getPartnerCompanyId();
		final List<String> blacklistedInterviewers = this.userBlacklistManager
				.getAllBlacklistedInterviewersForCompany(companyId);

		List<InterviewerData> nonBlacklistedInterviewers = data.getInterviewers().stream()
				.filter(i -> !blacklistedInterviewers.contains(i.getId()))
				.collect(Collectors.toList());
		data.setInterviewers(nonBlacklistedInterviewers);
	}

	public void filterForWhitelistedInterviewers(final MatchInterviewersData data) {
		final String companyId = data.getPartnerCompanyId();
		final List<String> whiteListedInterviewers = this.userWhitelistManager
				.getListOfUsersWhitelistedForCompany(companyId);
		if (!whiteListedInterviewers.isEmpty()) {
			final List<InterviewerData> interviewersThatCanTakeInterview = data.getInterviewers().stream()
					.filter(x -> whiteListedInterviewers.contains(x.getId()))
					.collect(Collectors.toList());
			data.setInterviewers(interviewersThatCanTakeInterview);
		}
	}
}
