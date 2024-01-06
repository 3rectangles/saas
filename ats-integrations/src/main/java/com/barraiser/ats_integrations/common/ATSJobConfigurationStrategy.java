/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.common;

import com.barraiser.ats_integrations.dal.PartnerATSIntegrationDAO;
import com.barraiser.common.graphql.input.MapAtsJobPostingToBRJobRoleInput;
import com.barraiser.common.graphql.types.AtsIntegration;

public interface ATSJobConfigurationStrategy {
	String atsProvider();

	AtsIntegration getAtsIntegrationData(
			final PartnerATSIntegrationDAO partnerATSIntegrationDAO)
			throws Exception;
}
