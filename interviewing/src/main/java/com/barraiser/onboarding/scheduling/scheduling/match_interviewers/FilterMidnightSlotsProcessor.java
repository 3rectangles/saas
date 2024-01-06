/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.match_interviewers;

import com.barraiser.common.enums.RoundType;
import com.barraiser.common.utilities.DateUtils;
import com.barraiser.onboarding.interview.PartnerConfigManager;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Log4j2
@Component
@AllArgsConstructor
public class FilterMidnightSlotsProcessor implements MatchInterviewersProcessor {
	private final DateUtils dateUtils;
	private final InterviewSchedulingConfig config;
	private final PartnerConfigManager partnerConfigManager;

	@Override
	public void process(final MatchInterviewersData data) throws IOException {
		final Map<Long, String> filteredSlotInterviewerMapping = new HashMap<>();
		for (Map.Entry<Long, String> slotInterviewerMapping : data.getSlotInterviewerMapping().entrySet()) {
			if (!this.shouldSlotBeFiltered(slotInterviewerMapping.getKey(), data.getTimezone(),
					data.getInterviewRound(), data.getPartnerCompanyId())) {
				filteredSlotInterviewerMapping.put(slotInterviewerMapping.getKey(), slotInterviewerMapping.getValue());
			}
		}
		data.setSlotInterviewerMapping(filteredSlotInterviewerMapping);
	}

	private boolean shouldSlotBeFiltered(final Long slotStartDate, final String timezone,
			final String interviewRound, final String partnerId) {
		if (partnerConfigManager.is24HourSchedulingAllowed(partnerId)) {
			return false;
		}
		if (!RoundType.INTERNAL.getValue().equals(interviewRound)) {
			if (this.dateUtils.isBetweenTimeOfDay(slotStartDate, config.getScheduledTimeUpperBound(),
					config.getScheduledTimeLowerBound(), timezone)) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}
}
