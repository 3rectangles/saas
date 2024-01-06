/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.common;

import com.barraiser.ats_integrations.dal.PartnerATSIntegrationDAO;

public interface ATSWebhookHandlerStrategy {
	String getATSProvider();

	void activateATSWebhook(final PartnerATSIntegrationDAO partnerATSIntegrationDAO);
}
