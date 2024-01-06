/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling;

import com.barraiser.common.enums.RoundType;
import com.barraiser.common.graphql.input.GetInterviewersInput;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.auth.AuthorizationResourceDTO;
import com.barraiser.onboarding.auth.Authorizer;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.onboarding.interview.auth.InterviewAuthorizer;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.InterviewSlotsFetcher;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.MatchInterviewersData;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.internalInterviews.MatchInterviewersForInternalInterviews;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class GetSchedulingSlots implements NamedDataFetcher {
	private final GraphQLUtil graphQLUtil;
	private final MatchInterviewersForInternalInterviews matchInterviewersForInternalInterviews;
	private final Authorizer authorizer;
	private final InterviewSlotsFetcher interviewSlotsFetcher;
	private final SchedulingSessionManager schedulingSessionManager;
	private final MatchInterviewersDataHelper matchInterviewersDataHelper;

	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {
		final AuthenticatedUser user = this.graphQLUtil.getLoggedInUser(environment);
		final GetInterviewersInput input = this.graphQLUtil.getArgument(environment, "input",
				GetInterviewersInput.class);
		final AuthorizationResourceDTO authorizationResource = AuthorizationResourceDTO.builder()
				.type(InterviewAuthorizer.RESOURCE_TYPE)
				.resource(input.getInterviewId())
				.build();
		this.authorizer.can(user, InterviewAuthorizer.ACTION_SCHEDULE,
				authorizationResource);
		MatchInterviewersData data = this.matchInterviewersDataHelper
				.prepareDataForInterviewSlots(input.getInterviewId());
		data.setAvailabilityStartDate(input.getAvailabilityStartDate());
		data.setAvailabilityEndDate(input.getAvailabilityEndDate());
		data.setTimezone(input.getTimezone());

		if (RoundType.INTERNAL.getValue().equalsIgnoreCase(data.getInterviewRound())) {
			this.matchInterviewersForInternalInterviews.getInterviewSlots(data);
		} else {
			this.interviewSlotsFetcher.populateInterviewSlots(data);
		}
		this.schedulingSessionManager.storeSchedulingSessionData(data.getInterview().getId(),
				data.getInterview().getRescheduleCount(),
				data.getInterviewCost(), data.getBarRaiserUsedMarginPercentage(),
				data.getBarRaiserConfiguredMarginPercentage());

		return DataFetcherResult.newResult().data(data.getInterviewSlots()).build();
	}

	@Override
	public String name() {
		return "getSchedulingSlots";
	}

	@Override
	public String type() {
		return QUERY_TYPE;
	}
}
