/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.confirmation;

import com.barraiser.onboarding.scheduling.confirmation.dto.InterviewConfirmationLifecycleDTO;

public interface ConfirmationProcessor {
	void process(InterviewConfirmationLifecycleDTO data) throws Exception;
}
