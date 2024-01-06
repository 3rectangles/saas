/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.jira.expert;

import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.MatchInterviewersData;
import com.barraiser.onboarding.user.expert.dto.ExpertDetails;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.search.SearchResponse;

import java.io.IOException;

public interface ExpertSearchManager {

	void updateExpertDetails(final ExpertDetails expertDetails) throws IOException;

	SearchResponse searchExpertDetails(final MatchInterviewersData interviewersData) throws IOException;

	DeleteResponse deleteExpertDetails(final String index, final String docId) throws IOException;
}
