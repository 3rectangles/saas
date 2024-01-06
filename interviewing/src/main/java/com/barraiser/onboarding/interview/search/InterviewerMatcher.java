/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.search;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Matches an interviewer with an interviewee.
 */
public interface InterviewerMatcher {
	public List<String> getInterviewers(final Map<String, String> parameters) throws IOException;
}
