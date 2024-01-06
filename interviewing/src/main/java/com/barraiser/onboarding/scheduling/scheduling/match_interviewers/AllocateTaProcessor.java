/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.match_interviewers;

import com.barraiser.common.utilities.DateUtils;
import com.barraiser.commons.auth.UserRole;
import com.barraiser.onboarding.availability.AvailabilityManager;
import com.barraiser.onboarding.common.Constants;
import com.barraiser.onboarding.config.DynamicAppConfigProperties;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.InterviewManager;
import com.barraiser.onboarding.scheduling.scheduling.OverBookingThresholdCalculator;
import com.barraiser.onboarding.scheduling.scheduling.SchedulingProcessingData;
import com.barraiser.onboarding.scheduling.scheduling.SchedulingProcessor;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.*;

import static com.barraiser.common.utilities.DateUtils.TIMEZONE_UTC;

@Log4j2
@Component
@AllArgsConstructor
public class AllocateTaProcessor implements SchedulingProcessor {
	public static final String DYNAMO_TIME_TO_WAIT_BEFORE_INFORMING_OPS_IN_SEC = "time-to-wait-before-informing-ops";

	private final DateUtils dateUtils;
	private final DynamicAppConfigProperties appConfigProperties;
	private final OverBookingThresholdCalculator overBookingThresholdCalculator;
	private final AvailabilityManager availabilityManager;
	private final UserDetailsRepository userDetailsRepository;
	private final InterViewRepository interViewRepository;
	private final InterviewManager interviewManager;

	@Override
	@Transactional
	public void process(final SchedulingProcessingData data) throws Exception {
		if (!data.getExecuteTaAssignment())
			return;
		log.info("TA Allocation processing started with overBooking Fraction: {} ",
				this.overBookingThresholdCalculator.getOverBookingThresholdForTa());
		final InterviewDAO interviewDAO = this.interViewRepository.findById(data.getInput().getInterviewId()).get();
		Long waitForTaAllocation = interviewDAO.getStartDate()
				- (long) this.appConfigProperties.getInt(DYNAMO_TIME_TO_WAIT_BEFORE_INFORMING_OPS_IN_SEC)
				+ (long) (Constants.SECONDS_IN_MINUTE * (new Random().nextDouble()));
		data.setTimestampToWaitUntilForTaReassignment(this.dateUtils.getFormattedDateString(waitForTaAllocation,
				TIMEZONE_UTC, DateUtils.DATEFORMAT_ISO_8601));
		this.allocate(data, interviewDAO);

	}

	private void allocate(SchedulingProcessingData data, InterviewDAO interviewDAO) {
		if (Boolean.TRUE.equals(data.getIsTaAllocationLoopActive())
				|| this.canTaBeAllocated(this.overBookingThresholdCalculator.getOverBookingThresholdForTa(), data)) {
			if (this.getStatusToBeSkippedForTaAllocation().contains(interviewDAO.getStatus())) {
				log.info("status: {} interview: {} at time: {} ,hence skipping allocation", interviewDAO.getStatus(),
						interviewDAO.getId(), System.currentTimeMillis());
				data.setIsTaAllocationLoopActive(false);
				data.setExecuteTaAssignment(false);
				return;
			}

			if (Objects.nonNull(interviewDAO.getTaggingAgent())) {
				log.info("Tagging Agent found for interview: {} at time: {}", interviewDAO.getId(),
						System.currentTimeMillis());
				data.setIsTaAllocated(true);
				data.setIsTaAllocationLoopActive(false);
				data.setExecuteTaAssignment(false);
				return;
			}

			AvailabilityDAO availabilityDAO = this.availabilityManager.findRandomAvailableSlotForTimeFrame(
					interviewDAO.getStartDate(), interviewDAO.getEndDate(), UserRole.TAGGING_AGENT.getRole());

			if (Objects.isNull(availabilityDAO)) {
				log.info("No Ta Availability found for interview: {} at time: {}", interviewDAO.getId(),
						System.currentTimeMillis());
				data.setIsTaAllocated(false);
				data.setIsTaAllocationLoopActive(!this.isItPastReassignmentThreshold(data));
				return;
			}

			data.setTaId(availabilityDAO.getUserId());
			data.setIsTaAllocationLoopActive(
					!this.isItPastReassignmentThreshold(data) && Objects.isNull(data.getTaId()));
			data.setIsTaAllocated(true);
			this.updateTaData(data, availabilityDAO, interviewDAO.getId());

			if (Objects.nonNull(interviewDAO.getTaggingAgent())) {
				data.setExecuteTaAssignment(false);
				log.info("Deactivating Assignment as Ta has already been allocated for interview: {} ",
						interviewDAO.getId());
				data.setIsTaAllocationLoopActive(false);
				return;
			}
			this.availabilityManager.bookTaSlotAndUpdateInterview(data, availabilityDAO);
		} else {
			log.info("Skipping TA Allocation as per overBooking Fraction: {} ",
					this.overBookingThresholdCalculator.getOverBookingThresholdForTa());
			data.setIsTaAllocated(false);
			data.setIsTaAllocationLoopActive(false);
		}

	}

	private void updateTaData(SchedulingProcessingData data, AvailabilityDAO availabilityDAO,
			final String interviewId) {
		UserDetailsDAO ta = this.userDetailsRepository.findById(availabilityDAO.getUserId()).get();
		final Map<String, String> taData = new HashMap<>();
		taData.put("taEmailId", ta.getEmail());
		taData.put("taName", ta.getFirstName());
		taData.put("taTimeZone", ta.getTimezone());
		taData.put("interview_id", interviewId);
		data.getSchedulingCommunicationData().setTaData(taData);
	}

	private boolean canTaBeAllocated(Double overbookingFraction, SchedulingProcessingData data) {
		Double fraction = this.interviewManager.getTaBookingFractionForSlot(data.getInput().getStartDate(),
				data.getInput().getEndDate());
		if (Objects.isNull(overbookingFraction))
			overbookingFraction = 0.0D;
		if (fraction <= (1D / (1D + overbookingFraction))) {
			return true;
		} else {
			return false;
		}
	}

	private Boolean isItPastReassignmentThreshold(SchedulingProcessingData data) {
		if (Objects.isNull(data.getInput().getStartDate()))
			return true;
		return (System.currentTimeMillis() / 1000L) > (data.getInput().getStartDate()
				- (long) this.appConfigProperties.getInt(DYNAMO_TIME_TO_WAIT_BEFORE_INFORMING_OPS_IN_SEC));
	}

	private List<String> getStatusToBeSkippedForTaAllocation() {
		return Arrays.asList(InterviewStatus.CANCELLATION_DONE.getValue(), InterviewStatus.DONE.getValue(),
				InterviewStatus.EXPERT_NEEDED_FOR_DUMMY_INTERVIEW.getValue());
	}
}
