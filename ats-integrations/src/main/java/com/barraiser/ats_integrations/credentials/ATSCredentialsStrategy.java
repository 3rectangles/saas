/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.credentials;

import com.barraiser.common.graphql.input.SubmitATSCredentialsInput;

public interface ATSCredentialsStrategy {
	String atsProvider();

	void submitATSCredentials(final SubmitATSCredentialsInput input)
			throws Exception;
}
