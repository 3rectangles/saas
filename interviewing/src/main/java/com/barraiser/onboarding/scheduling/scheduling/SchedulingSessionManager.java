/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling;

import com.barraiser.common.dal.Money;
import com.barraiser.onboarding.config.DynamicAppConfigProperties;
import com.barraiser.onboarding.dal.SchedulingSessionDAO;
import com.barraiser.onboarding.dal.SchedulingSessionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@Component
@AllArgsConstructor
public class SchedulingSessionManager {
	private static final String SCHEDULING_SESSION_VALIDITY_PERIOD = "scheduling-session-validity-period";
	private final SchedulingSessionRepository schedulingSessionRepository;
	private final DynamicAppConfigProperties dynamicAppConfigProperties;

	public void storeSchedulingSessionData(final String interviewId, final Integer rescheduleCount,
			final Money interviewPrice, final Double usedMargin, final Double configuredMargin) {
		this.schedulingSessionRepository.save(SchedulingSessionDAO.builder()
				.id(UUID.randomUUID().toString())
				.interviewId(interviewId)
				.rescheduleCount(rescheduleCount)
				.interviewCost(interviewPrice)
				.usedMargin(usedMargin)
				.configuredMargin(configuredMargin)
				.build());
	}

	public void checkIfSchedulingSessionDataIsStale(final String interviewId, final Integer rescheduleCount) {
		final Optional<SchedulingSessionDAO> schedulingContext = this.schedulingSessionRepository
				.findTopByInterviewIdAndRescheduleCountOrderByCreatedOnDesc(interviewId, rescheduleCount);
		if (schedulingContext.isPresent() && (Instant.now().getEpochSecond()
				- schedulingContext.get().getCreatedOn().getEpochSecond()) > (long) this.dynamicAppConfigProperties
						.getInt(SCHEDULING_SESSION_VALIDITY_PERIOD)) {
			throw new IllegalArgumentException("Scheduling session is expired. Please refresh the page and try again.");
		}
	}
}
