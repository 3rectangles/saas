/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling;

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
public class UpdateInterviewProcessor implements SchedulingProcessor {
	private final String TIME_TO_WAIT_FOR_ALLOCATION_FROM_DAY_START_IN_SEC = "time_to_wait_for_ta_allocation_from_day_start_sec";

	private final InterViewRepository interViewRepository;
	private final DynamicAppConfigProperties appConfigProperties;
	private final DateUtils dateUtils;
	private final InterviewService interviewService;
	private final InterviewPadGenerationService interviewPadGenerationService;
	private final InterviewStatusManager interviewStatusManager;

	@Override
	@Transactional
	public void process(final SchedulingProcessingData data) {
		InterviewDAO interviewDAO = this.interViewRepository.findById(data.getInput().getInterviewId()).get();
		final InterviewStatus toStatus = this.interviewStatusManager
				.getScheduledInterviewDestinationStatus(data.getInput().getInterviewId());
		interviewDAO = interviewDAO.toBuilder()
				.startDate(data.getInput().getStartDate())
				.endDate(data.getInput().getEndDate())
				.atsInterviewFeedbackLink(data.getInput().getAtsInterviewFeedbackLink())
				.status(toStatus.getValue())
				.atsInterviewFeedbackLink(data.getInput().getAtsInterviewFeedbackLink())
				.schedulingPlatform(data.getInput().getSchedulingPlatform())
				.intervieweeTimezone(data.getInput().getTimezone())
				.isPendingScheduling(false)
				.meetingLink(data.getInput().getMeetingLink())
				.duration((data.getInput().getInterviewDuration() == null)
						? (Double.valueOf((data.getInput().getEndDate() - data.getInput().getStartDate()) / 60))
						: data.getInput().getInterviewDuration())
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
	}

	public void generateInterviewPadForMachineRound(final InterviewDAO interviewDAO) {
		if (interviewDAO.getInterviewRound().equalsIgnoreCase(RoundType.MACHINE.getValue())
				|| interviewDAO.getInterviewRound().equalsIgnoreCase(RoundType.MACHINE2.getValue())) {
			this.interviewPadGenerationService.getInterviewPad(interviewDAO.getId());
		}
	}

}
