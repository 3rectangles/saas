/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.communication.expertInterviewSummary;

import com.barraiser.common.utilities.DateUtils;
import com.barraiser.onboarding.common.StaticAppConfigValues;
import com.barraiser.onboarding.communication.EmailHandler;
import com.barraiser.onboarding.communication.ExpertInterviewReminderService;
import com.barraiser.onboarding.communication.channels.email.EmailEvent;
import com.barraiser.onboarding.communication.channels.email.EmailService;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.CancellationReasonManager;
import com.barraiser.onboarding.interview.ExpertInterviewsFetcher;
import com.barraiser.onboarding.interview.InterviewManager;
import com.barraiser.onboarding.user.expert.ExpertUtil;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class ExpertInterviewSummaryService implements EmailHandler {
	public static final String SEND_INTERVIEW_SUMMARY_MAIL_TO_EXPERT = "send_interview_summary_email_to_expert";

	private final UserDetailsRepository userDetailsRepository;
	private final StaticAppConfigValues staticAppConfigValues;
	private final EmailService emailService;
	private final DateUtils dateUtils;
	private final InterviewManager interviewManager;
	private final CancellationReasonManager cancellationReasonManager;
	private final ExpertUtil expertUtil;
	private final ExpertInterviewsFetcher expertInterviewsFetcher;

	@Override
	public String objective() {
		return SEND_INTERVIEW_SUMMARY_MAIL_TO_EXPERT;
	}

	@Override
	public String subject() {
		return "Interview Summary Of Last Week";
	}

	@Override
	public void process(final EmailEvent emailEvent) {
		// TODO: IT WILL HAVE PERFORMANCE ISSUES

		final OffsetDateTime offsetDateTime = OffsetDateTime.now();
		final Long startTimeStamp = offsetDateTime
				.minusDays(7)
				.toLocalDate()
				.atTime(0, 0, 0)
				.atZone(ZoneId.of("Asia/Kolkata"))
				.toEpochSecond();
		final Long endTimeStamp = offsetDateTime
				.minusDays(1)
				.toLocalDate()
				.atTime(23, 59, 59)
				.atZone(ZoneId.of("Asia/Kolkata"))
				.toEpochSecond();
		final List<InterviewDAO> interviewDAOS = this.expertInterviewsFetcher.getInterviewsForExpertUsingSpecification(
				emailEvent.getExpertId(),
				startTimeStamp,
				endTimeStamp,
				List.of(
						InterviewStatus.DONE.getValue(),
						InterviewStatus.CANCELLATION_DONE.getValue()),
				null);
		final List<ExpertInterviewSummaryData> expertInterviewSummaryDataList = this.createExpertInterviewSummaryData(
				this.expertUtil.splitInterviewsByExpert(interviewDAOS),
				this.cancellationReasonManager
						.getCancellationReasonsForInterviewsCancelledByExpert());
		this.sendMailToExpert(
				expertInterviewSummaryDataList,
				this.staticAppConfigValues.getInterviewNotificationEmail(),
				startTimeStamp,
				endTimeStamp);
	}

	private List<ExpertInterviewSummaryData> createExpertInterviewSummaryData(
			final Map<String, List<InterviewDAO>> interviewsPerExpert,
			final List<CancellationReasonDAO> cancellationReasonDAOs) {
		final List<ExpertInterviewSummaryData> expertInterviewSummaryDataList = new ArrayList<>();
		interviewsPerExpert.forEach(
				(x, y) -> {
					final Map<String, Integer> countPerInterviewStatusForAnExpert = this.interviewManager
							.getCountPerInterviewStatus(
									y,
									cancellationReasonDAOs.stream()
											.map(CancellationReasonDAO::getId)
											.collect(Collectors.toList()));
					Map<String, Integer> countPerCancellationReason = new HashMap<>();
					if (countPerInterviewStatusForAnExpert.get(
							InterviewStatus.CANCELLATION_DONE.getValue()) != null
							&& countPerInterviewStatusForAnExpert.get(
									InterviewStatus.CANCELLATION_DONE.getValue()) > 0) {
						countPerCancellationReason = this.cancellationReasonManager.getCountPerCancellationReason(
								y, cancellationReasonDAOs);
					}
					expertInterviewSummaryDataList.add(
							ExpertInterviewSummaryData.builder()
									.interviewerId(x)
									.completedInterviews(
											countPerInterviewStatusForAnExpert.getOrDefault(
													InterviewStatus.DONE.getValue(), 0))
									.cancelledInterviews(
											countPerInterviewStatusForAnExpert.getOrDefault(
													InterviewStatus.CANCELLATION_DONE.getValue(),
													0))
									.lastMinuteCancelledInterviews(
											countPerInterviewStatusForAnExpert.getOrDefault(
													InterviewManager.LAST_MINUTE_CANCELLED_INTERVIEW,
													0))
									.countPerCancellationReason(countPerCancellationReason)
									.build());
				});
		return expertInterviewSummaryDataList;
	}

	private Map<String, String> constructMailData(
			final UserDetailsDAO expert,
			final ExpertInterviewSummaryData expertInterviewSummaryData,
			final Long startTime,
			final Long endTime) {
		final Map<String, String> data = new HashMap<>();
		data.put("expertName", expert.getFirstName());
		data.put("startTime", this.dateUtils.getFormattedDateString(startTime, null, "dd-MM-yy"));
		data.put("endTime", this.dateUtils.getFormattedDateString(endTime, null, "dd-MM-yy"));
		data.put(
				"totalCompletedInterviews",
				expertInterviewSummaryData.getCompletedInterviews().toString());
		data.put(
				"totalCancelledInterviews",
				expertInterviewSummaryData.getCancelledInterviews().toString());
		data.put(
				"totalLastMinuteCancelledInterviews",
				expertInterviewSummaryData.getLastMinuteCancelledInterviews().toString());
		String cancellationReasonCountData = "";
		for (final Map.Entry<String, Integer> entry : expertInterviewSummaryData.getCountPerCancellationReason()
				.entrySet()) {
			cancellationReasonCountData += "<li>" + entry.getKey() + " : " + entry.getValue() + " cancellation </li>";
		}
		data.put("reasonForCancellation", cancellationReasonCountData);
		return data;
	}

	private void sendMailToExpert(
			final List<ExpertInterviewSummaryData> expertInterviewSummaryDataList,
			final String InterviewBarriserEmail,
			final Long startTime,
			final Long endTime) {
		expertInterviewSummaryDataList.forEach(
				x -> {
					if (x.getCompletedInterviews() + x.getCancelledInterviews() > 0) {
						final UserDetailsDAO expert = this.userDetailsRepository
								.findById(x.getInterviewerId())
								.orElse(null);
						final Map<String, String> emailData = this.constructMailData(expert, x, startTime, endTime);
						final String template;
						if (x.getCancelledInterviews() > 0) {
							template = "interview_summary_non_zero_cancelled_interviews";
						} else {
							template = "interview_summary_zero_cancelled_interviews";
						}
						final List<String> toEmail = new ArrayList<>();
						toEmail.add(expert.getEmail());
						final List<String> ccEmail = new ArrayList<>();

						try {
							this.emailService.sendEmailWithAllOptions(
									InterviewBarriserEmail,
									this.subject(),
									template,
									toEmail,
									ccEmail,
									null,
									emailData,
									null);
						} catch (final Exception e) {
							log.error(
									String.format(
											"Error sending interview summary mail to expert: %s",
											x));
							throw new RuntimeException(e);
						}
					}
				});
	}
}
