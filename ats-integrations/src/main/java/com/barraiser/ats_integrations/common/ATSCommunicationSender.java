/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.common;

import com.barraiser.ats_integrations.common.dto.ATSEvaluationDetailsDTO;
import com.barraiser.commons.dto.ats.enums.ATSAggregator;
import com.barraiser.commons.dto.ats.enums.ATSProvider;

public interface ATSCommunicationSender {

	ATSAggregator atsAggregator();

	ATSProvider atsProvider();

	void postNoteOnApplication(final String message, final ATSEvaluationDetailsDTO atsEvaluationDetailsDTO)
			throws Exception;
}
