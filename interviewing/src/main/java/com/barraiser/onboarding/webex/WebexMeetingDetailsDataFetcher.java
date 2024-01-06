/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.webex;

import com.barraiser.common.graphql.types.Interview;
import com.barraiser.common.graphql.types.WebexMeetingDetails;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.WebexMeetingDAO;
import com.barraiser.onboarding.dal.WebexMeetingRepository;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.MultiParentTypeDataFetcher;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
@Log4j2
public class WebexMeetingDetailsDataFetcher implements MultiParentTypeDataFetcher {
	private final GraphQLUtil graphQLUtil;
	private final WebexMeetingRepository webexMeetingRepository;
	private final WebexAccessTokenGenerator webexAccessTokenGenerator;

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(
				List.of("Interview", "candidateWebexMeetingDetails"),
				List.of("Interview", "interviewerWebexMeetingDetails"));
	}

	@Override
	public Object get(DataFetchingEnvironment environment) throws Exception {
		final Interview interview = environment.getSource();

		final WebexMeetingDAO meetingDAO = this.webexMeetingRepository
				.findByInterviewIdAndRescheduleCount(interview.getId(), interview.getRescheduleCount());

		if (meetingDAO == null) {
			return null;
		}

		// TODO: add authZ
		if (environment.getFieldDefinition().getName().equals("candidateWebexMeetingDetails")) {
			return DataFetcherResult.newResult().data(
					WebexMeetingDetails.builder()
							.joinLink(meetingDAO.getJoinLink())
							.accessToken(this.webexAccessTokenGenerator
									.generateCandidateAccessToken("candidate"))
							.build())
					.build();
		} else {
			final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);
			return DataFetcherResult.newResult().data(
					WebexMeetingDetails.builder()
							.joinLink(meetingDAO.getJoinLink())
							.accessToken(this.webexAccessTokenGenerator
									.generateInterviewerAccessToken(authenticatedUser.getUserName()))
							.build())
					.build();
		}
	}
}
