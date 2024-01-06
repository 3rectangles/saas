/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dyte;

import com.barraiser.common.graphql.types.DyteMeetingDetail;
import com.barraiser.common.graphql.types.DyteParticipantDetail;
import com.barraiser.common.graphql.types.Interview;
import com.barraiser.onboarding.dal.*;
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
public class DyteMeetingDetailsDataFetcher implements MultiParentTypeDataFetcher {
	private final DyteMeetingRepository dyteMeetingRepository;

	@Override

	public List<List<String>> typeNameMap() {
		return List.of(List.of("Interview", "dyteMeetingDetails"));
	}

	@Override
	public Object get(DataFetchingEnvironment environment) throws Exception {
		final Interview interview = environment.getSource();

		final DyteMeetingDAO meetingDAO = this.dyteMeetingRepository
				.findByInterviewIdAndRescheduleCount(interview.getId(), interview.getRescheduleCount());

		if (meetingDAO == null) {
			return null;
		}

		return DataFetcherResult.newResult().data(DyteMeetingDetail.builder()
				.meetingId(meetingDAO.getMeetingId())
				.roomName(meetingDAO.getRoomName())
				.build())
				.build();
	}

}
