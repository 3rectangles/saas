/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.common;

import com.barraiser.ats_integrations.calendar_interception.dto.CandidateDetails;
import com.barraiser.common.ats_integrations.ATSProvider;
import com.barraiser.commons.dto.ats.enums.ATSAggregator;

public interface ATSCandidateDetailsFetcher {

	ATSProvider atsProvider();

	ATSAggregator atsAggregator();

	CandidateDetails getCandidateDetails(final String evaluationId, final String partnerId,
			final String atsProvider);
}
