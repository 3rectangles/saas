/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.common;

import com.barraiser.ats_integrations.calendar_interception.dto.AtsInterview;
import com.barraiser.common.ats_integrations.ATSProvider;
import com.barraiser.commons.dto.ats.enums.ATSAggregator;

public interface ATSInterviewDetailsFetcher {

	ATSProvider atsProvider();

	ATSAggregator atsAggregator();

	AtsInterview getInterviewDetails(final String interviewStructureId, final String evaluationId,
			final String partnerId, final String atsProvider);

	AtsInterview getInterviewDetails(final String interviewId, final String partnerId,
			final String atsProvider);
}
