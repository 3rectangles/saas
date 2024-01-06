/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.communication.expertInterviewSummary;

import com.barraiser.common.utilities.DateUtils;
import com.barraiser.onboarding.common.StaticAppConfigValues;

import com.barraiser.onboarding.communication.ExpertInterviewReminderService;
import com.barraiser.onboarding.communication.channels.email.EmailEvent;
import com.barraiser.onboarding.communication.channels.email.EmailService;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.dal.specifications.InterviewSpecifications;
import com.barraiser.onboarding.interview.CancellationReasonManager;
import com.barraiser.onboarding.interview.InterviewManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ExpertInterviewSummaryServiceTest {
	@Mock
	private InterviewSpecifications interviewSpecifications;
	@Mock
	private ExpertInterviewReminderService expertInterviewReminderService;
	@Mock
	private UserDetailsRepository userDetailsRepository;
	@Mock
	private StaticAppConfigValues staticAppConfigValues;
	@Mock
	private EmailService emailService;
	@Mock
	private DateUtils dateUtils;
	@Mock
	private InterviewManager interviewManager;
	@Mock
	private CancellationReasonManager cancellationReasonManager;
	@InjectMocks
	private ExpertInterviewSummaryService expertInterviewSummaryService;

	@Test
	public void shouldNotSendAnyMailForZeroInterviewsCompletedOrCancelled() throws IOException {

		when(this.interviewManager.getInterviewsPerExpert(any()))
				.thenReturn(Map.of("1", List.of(InterviewDAO.builder().status("Done").build(),
						InterviewDAO.builder().status("cancellation_done").build(),
						InterviewDAO.builder().status("cancellation_done").build())));
		when(this.cancellationReasonManager.getCancellationReasonsForInterviewsCancelledByExpert())
				.thenReturn(List.of(CancellationReasonDAO.builder().id("1").build(),
						CancellationReasonDAO.builder().id("1").build()));
		when(this.interviewManager.getCountPerInterviewStatus(any(), any()))
				.thenReturn(Map.of("Done", 0, "cancellation_done", 0, "last_minute_cancelled_interview", 0));
		this.expertInterviewSummaryService.process(EmailEvent.builder().build());
		verify(this.userDetailsRepository, never()).findById(any());
		verify(this.cancellationReasonManager, never()).getCountPerCancellationReason(any(), any());
		verify(this.cancellationReasonManager, never()).getCountPerCancellationReason(any(), any());
	}

	@Test
	public void shouldSendMailForZeroInterviewsCancelled() throws IOException {

		when(this.interviewManager.getInterviewsPerExpert(any()))
				.thenReturn(Map.of("1", List.of(InterviewDAO.builder().status("Done").build(),
						InterviewDAO.builder().status("cancellation_done").build(),
						InterviewDAO.builder().status("cancellation_done").build())));
		when(this.cancellationReasonManager.getCancellationReasonsForInterviewsCancelledByExpert())
				.thenReturn(List.of(CancellationReasonDAO.builder().id("1").build(),
						CancellationReasonDAO.builder().id("1").build()));
		when(this.interviewManager.getCountPerInterviewStatus(any(), any()))
				.thenReturn(Map.of("Done", 1, "cancellation_done", 0, "last_minute_cancelled_interview", 0));
		when(this.userDetailsRepository.findById(any()))
				.thenReturn(Optional.ofNullable(UserDetailsDAO.builder().id("1").firstName("test").build()));
		this.expertInterviewSummaryService.process(EmailEvent.builder().build());
		verify(this.userDetailsRepository).findById(any());
		verify(this.emailService).sendEmail(any(), any(), eq("interview_summary_zero_cancelled_interviews"), any(),
				any(), argThat(arg -> arg.get("totalCompletedInterviews").equals("1") &&
						arg.get("totalCancelledInterviews").equals("0") &&
						arg.get("totalLastMinuteCancelledInterviews").equals("0")),
				any());
		verify(this.cancellationReasonManager, never()).getCountPerCancellationReason(any(), any());
		verify(this.cancellationReasonManager, never()).getCountPerCancellationReason(any(), any());
	}

	@Test
	public void shouldSendMailForNonZeroInterviewsCancelled() throws IOException {

		when(this.interviewManager.getInterviewsPerExpert(any()))
				.thenReturn(Map.of("1", List.of(InterviewDAO.builder().status("Done").build(),
						InterviewDAO.builder().status("cancellation_done").build(),
						InterviewDAO.builder().status("cancellation_done").build())));
		when(this.cancellationReasonManager.getCancellationReasonsForInterviewsCancelledByExpert())
				.thenReturn(List.of(CancellationReasonDAO.builder().id("1").build(),
						CancellationReasonDAO.builder().id("1").build()));
		when(this.interviewManager.getCountPerInterviewStatus(any(), any()))
				.thenReturn(Map.of("Done", 1, "cancellation_done", 1, "last_minute_cancelled_interview", 1));
		when(this.userDetailsRepository.findById(any()))
				.thenReturn(Optional.ofNullable(UserDetailsDAO.builder().id("1").firstName("test").build()));
		this.expertInterviewSummaryService.process(EmailEvent.builder().build());
		verify(this.userDetailsRepository).findById(any());
		verify(this.emailService).sendEmail(any(), any(), eq("interview_summary_non_zero_cancelled_interviews"), any(),
				any(),
				argThat(arg -> arg.get("totalCompletedInterviews").equals("1")
						&& arg.get("totalCancelledInterviews").equals("1")
						&& arg.get("totalLastMinuteCancelledInterviews").equals("1")),
				any());
		verify(this.cancellationReasonManager).getCountPerCancellationReason(any(), any());
	}

	@Test
	public void shouldSendMailForNoLastMinuteCancellationOfInterviews() throws IOException {

		when(this.interviewManager.getInterviewsPerExpert(any()))
				.thenReturn(Map.of("1", List.of(InterviewDAO.builder().status("Done").build(),
						InterviewDAO.builder().status("cancellation_done").build(),
						InterviewDAO.builder().status("cancellation_done").build())));
		when(this.cancellationReasonManager.getCancellationReasonsForInterviewsCancelledByExpert())
				.thenReturn(List.of(CancellationReasonDAO.builder().id("1").build(),
						CancellationReasonDAO.builder().id("1").build()));
		when(this.interviewManager.getCountPerInterviewStatus(any(), any()))
				.thenReturn(Map.of("Done", 1, "cancellation_done", 1, "last_minute_cancelled_interview", 0));
		when(this.userDetailsRepository.findById(any()))
				.thenReturn(Optional.ofNullable(UserDetailsDAO.builder().id("1").firstName("test").build()));
		this.expertInterviewSummaryService.process(EmailEvent.builder().build());
		verify(this.userDetailsRepository).findById(any());
		verify(this.emailService).sendEmail(any(), any(), eq("interview_summary_non_zero_cancelled_interviews"), any(),
				any(),
				argThat(arg -> arg.get("totalCompletedInterviews").equals("1")
						&& arg.get("totalCancelledInterviews").equals("1")
						&& arg.get("totalLastMinuteCancelledInterviews").equals("0")),
				any());
		verify(this.cancellationReasonManager).getCountPerCancellationReason(any(), any());
	}
}
