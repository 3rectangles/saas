/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.calendar_interception;

import com.barraiser.ats_integrations.calendar_interception.dto.SchedulingData;
import com.barraiser.ats_integrations.errorhandling.ATSAnomalyException;

import java.io.IOException;

public interface SchedulingProcessing {
	void process(final SchedulingData data) throws IOException, ATSAnomalyException;
}
