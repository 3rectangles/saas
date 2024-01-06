/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.zoom;

import com.barraiser.common.graphql.types.ZoomJoinDetails;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.graphql.GraphQLQuery;
import com.barraiser.onboarding.interview.InterViewRepository;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
public class GetZoomJoinDetails implements GraphQLQuery {
	private final InterViewRepository interViewRepository;
	private final ZoomSignatureGenerator signatureGenerator;
	private final ZoomManager zoomManager;

	@Override
	public String name() {
		return "getZoomJoinDetails";
	}

	@Override
	public Object get(DataFetchingEnvironment environment) throws Exception {
		final String interviewId = environment.getArgument("interviewId");
		final InterviewDAO interviewDAO = this.interViewRepository.findById(interviewId).get();
		final String meetingId = this.zoomManager.getMeetingIdFromJoinUrl(interviewDAO.getMeetingLink());
		return DataFetcherResult.newResult().data(ZoomJoinDetails.builder()
				.meetingId(meetingId)
				.signature(this.signatureGenerator.createSignatureForParticipant(meetingId))
				.password("123456")
				.build()).build();
	}
}
