/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.calendar_interception;

import com.barraiser.ats_integrations.calendar_interception.dto.CandidateDetails;
import com.barraiser.ats_integrations.common.ATSCandidateDetailsFetcher;
import com.barraiser.common.ats_integrations.ATSProvider;
import com.barraiser.commons.dto.ats.enums.ATSAggregator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Component
public class ATSCandidateDetailsExtractor {

	List<ATSCandidateDetailsFetcher> atsCandidateDetailsFetcherList;

	public CandidateDetails extractCandidateDetails(final String evaluationId, final ATSProvider atsProvider,
			final ATSAggregator atsAggregator,
			final String partnerId) {

		for (final ATSCandidateDetailsFetcher fetcher : this.atsCandidateDetailsFetcherList) {

			if (atsProvider.equals(ATSProvider.MERGE_LEVER)) {
				if (fetcher.atsProvider() != null &&
						fetcher.atsProvider().equals(ATSProvider.LEVER)) {
					return fetcher.getCandidateDetails(evaluationId, partnerId,
							atsProvider.getValue());
				}
			} else {
				if (fetcher.atsAggregator() != null &&
						fetcher.atsAggregator().equals(atsAggregator)) {
					return fetcher.getCandidateDetails(evaluationId, partnerId,
							atsProvider.getValue());
				}
				if (fetcher.atsProvider().equals(atsProvider)) {
					return fetcher.getCandidateDetails(evaluationId, partnerId,
							atsProvider.getValue());
				}
			}

		}

		throw new IllegalArgumentException("Candidate Details Fetcher for " + atsProvider + " not defined");

	}
}
