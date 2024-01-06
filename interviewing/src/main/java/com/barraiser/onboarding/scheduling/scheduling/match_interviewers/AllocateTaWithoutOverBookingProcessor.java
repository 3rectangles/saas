/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.match_interviewers;

import com.barraiser.onboarding.availability.AvailabilityManager;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.InterViewRepository;

import com.barraiser.onboarding.scheduling.scheduling.SchedulingProcessingData;
import com.barraiser.onboarding.scheduling.scheduling.SchedulingProcessor;
import com.barraiser.commons.auth.UserRole;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.*;

@Log4j2
@Component
@AllArgsConstructor
public class AllocateTaWithoutOverBookingProcessor implements SchedulingProcessor {
	private final AvailabilityManager availabilityManager;
	private final UserDetailsRepository userDetailsRepository;
	private final InterViewRepository interViewRepository;

	@Override
	@Transactional
	public void process(final SchedulingProcessingData data) throws Exception {
		if (!data.getExecuteTaAssignment())
			return;
		this.allocate(data);
	}

	private void allocate(SchedulingProcessingData data) {
		final InterviewDAO interviewDAO = interViewRepository.findById(data.getInput().getInterviewId()).get();
		if (getStatusToBeSkippedForTaAllocation().contains(interviewDAO.getStatus())) {
			log.info("status: {}  for interview: {} at time: {} in allocate without overbooking flow,hence skipping",
					interviewDAO.getStatus(), interviewDAO.getId(), System.currentTimeMillis());
			data.setExecuteTaAssignment(false);
			return;
		}

		if (Objects.nonNull(interviewDAO.getTaggingAgent())) {
			log.info("Tagging Agent found for interview: {} at time: {} in allocate without overbooking flow",
					interviewDAO.getId(), System.currentTimeMillis());
			data.setIsTaAllocated(true);
			data.setExecuteTaAssignment(false);
			return;
		}
		AvailabilityDAO availabilityDAO = availabilityManager.findRandomAvailableSlotForTimeFrame(
				interviewDAO.getStartDate(), interviewDAO.getEndDate(), UserRole.TAGGING_AGENT.getRole());
		if (Objects.nonNull(availabilityDAO)) {
			data.setTaId(availabilityDAO.getUserId());
			data.setIsTaAllocated(true);
			updateTaData(data, availabilityDAO);
			availabilityManager.bookTaSlotAndUpdateInterview(data, availabilityDAO);
		} else {
			data.setIsTaAllocated(false);
		}

	}

	private void updateTaData(SchedulingProcessingData data, AvailabilityDAO availabilityDAO) {
		UserDetailsDAO ta = userDetailsRepository.findById(availabilityDAO.getUserId()).get();
		final Map<String, String> taData = new HashMap<>();
		taData.put("taEmailId", ta.getEmail());
		taData.put("taTimeZone", ta.getTimezone());
		taData.put("taName", ta.getFirstName());
		data.getSchedulingCommunicationData().setTaData(taData);
	}

	private List<String> getStatusToBeSkippedForTaAllocation() {
		return Arrays.asList(InterviewStatus.CANCELLATION_DONE.getValue(), InterviewStatus.DONE.getValue(),
				InterviewStatus.EXPERT_NEEDED_FOR_DUMMY_INTERVIEW.getValue());
	}

}
