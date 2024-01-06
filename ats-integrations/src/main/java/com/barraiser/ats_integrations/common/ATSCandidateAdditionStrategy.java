/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.common;

import com.barraiser.ats_integrations.dal.ATSToBREvaluationDAO;
import com.barraiser.ats_integrations.dal.PartnerATSIntegrationDAO;

public interface ATSCandidateAdditionStrategy {
	String atsProvider();

	void performNecessaryOperationsUponCandidateAddition(
			final PartnerATSIntegrationDAO partnerATSIntegrationDAO,
			final ATSToBREvaluationDAO atsToBREvaluationDAO) throws Exception;
}
