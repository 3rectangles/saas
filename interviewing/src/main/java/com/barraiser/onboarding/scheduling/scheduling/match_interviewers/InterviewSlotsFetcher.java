/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.match_interviewers;

import com.barraiser.common.graphql.types.InterviewSlots;
import com.barraiser.common.graphql.types.Slot;
import com.barraiser.common.utilities.DateUtils;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Log4j2
@Component
@AllArgsConstructor
public class InterviewSlotsFetcher {
	public static final Integer NUMBER_OF_DAYS_TO_BE_CONSIDERED = 3;
	public static final Integer MINIMUM_NUMBER_OF_SLOTS_TO_BE_CONSIDERED = 3;
	public static final Long MINIMUM_NUMBER_OF_SLOTS = 3L;

	private final MatchInterviewers matchInterviewers;
	private final ExpertCostForSchedulingFeatureToggleManager expertCostForSchedulingFeatureToggleManager;
	private final DateUtils dateUtils;

	public void populateInterviewSlots(final MatchInterviewersData data) throws IOException, ParseException {
		this.matchInterviewers.getInterviewSlots(data);
		if (!this.expertCostForSchedulingFeatureToggleManager.isFeatureToggleOn(data.getInterviewId())) {
			return;
		}
		final Boolean shouldMarginBeReduced = this.shouldMarginBeReduced(data);
		if (Boolean.TRUE.equals(shouldMarginBeReduced)) {
			log.info("reducing the margin for interview id: {} to : {}",
					data.getInterviewId(),
					data.getBarRaiserUsedMarginPercentage() / 2);
			data.setIsFallbackEnabled(Boolean.TRUE);
			data.setBarRaiserUsedMarginPercentage(data.getBarRaiserUsedMarginPercentage() / 2);
			this.matchInterviewers.getInterviewSlots(data);

			log.info(
					"after reducing margin for interview id: {} , evaluation id: {} , partner id : {} , job role id : {} , interview structure id : {} , round type : {}  to : {} are min required slots coming : {}",
					data.getInterviewId(), data.getInterview().getEvaluationId(), data.getPartnerCompanyId(),
					data.getJobRoleId(),
					data.getInterviewStructureId(), data.getInterviewRound(), data.getBarRaiserUsedMarginPercentage(),
					this.getNumberOfSlots(data) >= MINIMUM_NUMBER_OF_SLOTS);
		}
	}

	private Integer getNumberOfSlots(final MatchInterviewersData data) {
		int countOfSlots = 0;
		for (int i = 0; i < Math.min(NUMBER_OF_DAYS_TO_BE_CONSIDERED, data.getInterviewSlots().size()); i++) {
			countOfSlots += data.getInterviewSlots().get(i).getAllSlots().size();
		}
		return countOfSlots;
	}

	private Boolean shouldMarginBeReduced(final MatchInterviewersData data) throws ParseException {
		final SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.DATE_IN_YYYY_MM_DD_FORMAT);
		final Calendar c = Calendar.getInstance();
		c.setTime(sdf.parse(this.dateUtils.getFormattedDateString(
				data.getAvailabilityStartDate(), data.getTimezone(), DateUtils.DATE_IN_YYYY_MM_DD_FORMAT)));
		for (int j = 0; j < 2; j++) {
			c.add(Calendar.DATE, 1);
			final String nextDate = sdf.format(c.getTime());
			final Optional<InterviewSlots> interviewDayWiseSlotsData = data.getInterviewSlots().stream()
					.filter(x -> x.getDate().equals(nextDate))
					.findFirst();
			final List<Slot> slots = interviewDayWiseSlotsData.isPresent()
					? interviewDayWiseSlotsData.get().getAllSlots()
					: Collections.emptyList();
			if (slots.size() <= MINIMUM_NUMBER_OF_SLOTS_TO_BE_CONSIDERED) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}
}
