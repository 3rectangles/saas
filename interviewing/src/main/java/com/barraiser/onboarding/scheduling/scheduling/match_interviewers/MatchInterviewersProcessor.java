/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.match_interviewers;

import java.io.IOException;

public interface MatchInterviewersProcessor {
	void process(MatchInterviewersData data) throws IOException;
}
