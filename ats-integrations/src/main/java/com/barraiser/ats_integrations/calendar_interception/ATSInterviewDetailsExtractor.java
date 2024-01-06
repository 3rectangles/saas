/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.calendar_interception;

import com.barraiser.ats_integrations.calendar_interception.dto.AtsInterview;
import com.barraiser.ats_integrations.common.ATSInterviewDetailsFetcher;
import com.barraiser.ats_integrations.common.LeverInterviewDetailsFetcher;
import com.barraiser.common.ats_integrations.ATSProvider;
import com.barraiser.commons.dto.ats.enums.ATSAggregator;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Component
@Log4j2
public class ATSInterviewDetailsExtractor {

	private final List<ATSInterviewDetailsFetcher> interviewDetailsFetchers;

	public AtsInterview extractAtsInterview(final String interviewStructureId,
			final String evaluationId,
			final ATSProvider atsProvider,
			final ATSAggregator atsAggregator,
			final String partnerId) {

		for (final ATSInterviewDetailsFetcher fetcher : this.interviewDetailsFetchers) {
			if (fetcher.atsAggregator() != null && fetcher.atsAggregator().equals(atsAggregator)) {
				return fetcher.getInterviewDetails(interviewStructureId, evaluationId, partnerId,
						atsProvider.getValue());
			}
			if (fetcher.atsProvider().equals(atsProvider)) {
				return fetcher.getInterviewDetails(interviewStructureId, evaluationId, partnerId,
						atsProvider.getValue());
			}
		}
		throw new IllegalArgumentException("Interview Details Fetcher for " + atsProvider + " not defined");

	}

	public AtsInterview extractAtsInterview(final String interviewId,
			final ATSProvider atsProvider,
			final ATSAggregator atsAggregator,
			final String partnerId) {
		for (final ATSInterviewDetailsFetcher fetcher : this.interviewDetailsFetchers) {

			// Lever
			if (atsProvider.equals(ATSProvider.MERGE_LEVER)) {
				if (fetcher.atsProvider() != null && fetcher.atsProvider().equals(ATSProvider.LEVER)) {
					return fetcher.getInterviewDetails(interviewId, partnerId,
							atsProvider.getValue());
				}
			} else {
				if (fetcher.atsAggregator() != null && fetcher.atsAggregator().equals(atsAggregator)) {
					return fetcher.getInterviewDetails(interviewId, partnerId, atsProvider.getValue());
				}
				if (fetcher.atsProvider().equals(atsProvider)) {
					return fetcher.getInterviewDetails(interviewId, partnerId, atsProvider.getValue());
				}
			}
		}
		throw new IllegalArgumentException("Interview Details Fetcher for " + atsProvider + " not defined");

	}
}
