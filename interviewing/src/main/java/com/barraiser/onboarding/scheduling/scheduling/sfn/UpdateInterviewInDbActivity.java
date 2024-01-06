/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.sfn;

import com.barraiser.common.enums.RoundType;
import com.barraiser.common.utilities.DateUtils;
import com.barraiser.onboarding.common.Constants;
import com.barraiser.onboarding.config.DynamicAppConfigProperties;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.InterviewStatus;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.InterviewService;
import com.barraiser.onboarding.interview.InterviewStatusManager;
import com.barraiser.onboarding.interviewing.interviewpad.InterviewPadGenerationService;
import com.barraiser.onboarding.scheduling.scheduling.SchedulingProcessingData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Random;

import static com.barraiser.common.utilities.DateUtils.TIMEZONE_UTC;

@Component
@AllArgsConstructor
public class UpdateInterviewInDbActivity implements InterviewSchedulingActivity {
	private final String TIME_TO_WAIT_FOR_ALLOCATION_FROM_DAY_START_IN_SEC = "time_to_wait_for_ta_allocation_from_day_start_sec";
	public static final String UPDATE_INTERVIEW_IN_DB = "update-interview-in-DB";

	private final InterViewRepository interViewRepository;
	private final DynamicAppConfigProperties appConfigProperties;
	private final DateUtils dateUtils;
	private final InterviewService interviewService;
	private final InterviewPadGenerationService interviewPadGenerationService;
	private final InterviewStatusManager interviewStatusManager;
	private final ObjectMapper objectMapper;

	@Override
	public String name() {
		return UPDATE_INTERVIEW_IN_DB;
	}

	@Override
	@Transactional
	public SchedulingProcessingData process(String input) throws Exception {
		final SchedulingProcessingData data = this.objectMapper.readValue(input, SchedulingProcessingData.class);
		InterviewDAO interviewDAO = this.interViewRepository.findById(data.getInput().getInterviewId()).get();
		final InterviewStatus toStatus = this.interviewStatusManager
				.getScheduledInterviewDestinationStatus(data.getInput().getInterviewId());
		interviewDAO = interviewDAO.toBuilder()
				.interviewerId(data.getInput().getInterviewerId())
				.startDate(data.getInput().getStartDate())
				.endDate(data.getInput().getEndDate())
				.status(toStatus.getValue())
				.schedulingPlatform(data.getInput().getSchedulingPlatform())
				.intervieweeTimezone(data.getInput().getTimezone())
				.isPendingScheduling(false)
				.build();
		interviewDAO = this.interviewService.save(
				interviewDAO, data.getUser().getUserName(), "INTERVIEW_SCHEDULING");
		final ZoneId z = ZoneId.of("Asia/Kolkata");
		long waitTimeStamp = ZonedDateTime.ofInstant(Instant.ofEpochSecond(interviewDAO.getStartDate()), z)
				.toLocalDate()
				.atStartOfDay(z)
				.minusDays(1)
				.toInstant()
				.getEpochSecond()
				+ this.appConfigProperties.getInt(
						this.TIME_TO_WAIT_FOR_ALLOCATION_FROM_DAY_START_IN_SEC);
		final Long slotHourNumber = (interviewDAO.getStartDate()
				- ZonedDateTime.ofInstant(
						Instant.ofEpochSecond(interviewDAO.getStartDate()),
						z)
						.toLocalDate()
						.atStartOfDay(z)
						.toInstant()
						.getEpochSecond())
				/ Constants.SECONDS_IN_HOUR;
		final Long additionalWaitForSlot = Constants.mapTaSlotsWait.get(slotHourNumber.intValue());
		waitTimeStamp = waitTimeStamp
				+ additionalWaitForSlot
				+ (long) (Constants.SECONDS_IN_MINUTE
						* (new Random()
								.nextDouble())); // adding to avoid race conditions
		data.setTimestampToWaitUntilForTaAllocationStart(
				this.dateUtils.getFormattedDateString(
						waitTimeStamp, TIMEZONE_UTC, DateUtils.DATEFORMAT_ISO_8601));
		this.generateInterviewPadForMachineRound(interviewDAO);
		return data;
	}

	public void generateInterviewPadForMachineRound(final InterviewDAO interviewDAO) {
		if (interviewDAO.getInterviewRound().equalsIgnoreCase(RoundType.MACHINE.getValue())
				|| interviewDAO.getInterviewRound().equalsIgnoreCase(RoundType.MACHINE2.getValue())) {
			this.interviewPadGenerationService.getInterviewPad(interviewDAO.getId());
		}
	}
}
