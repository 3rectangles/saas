/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.types.Interview;
import com.barraiser.common.graphql.types.InterviewChangeHistory;
import com.barraiser.common.graphql.types.UserDetails;
import com.barraiser.onboarding.dal.EvaluationDAO;
import com.barraiser.onboarding.dal.InterviewHistoryDAO;
import com.barraiser.onboarding.dal.UserDetailsDAO;
import com.barraiser.onboarding.dal.UserDetailsRepository;
import com.barraiser.onboarding.interview.status.EvaluationStatusManager;
import com.barraiser.onboarding.scheduling.confirmation.InterviewConfirmationManager;
import com.barraiser.onboarding.user.UserInformationManagementHelper;
import graphql.schema.DataFetchingEnvironment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InterviewTimelineDataFetcherTest {
	@Mock
	private UserDetailsRepository userDetailsRepository;
	@Mock
	private CancellationReasonManager cancellationReasonManager;
	@Mock
	private InterviewHistoryManager interviewHistoryManager;
	@Mock
	private InterviewManager interviewManager;
	@Mock
	private DataFetchingEnvironment dataFetchingEnvironment;
	@Mock
	private Map<String, String> mapStatusToDisplayStatus;

	@Mock
	private EvaluationStatusManager evaluationStatusManager;

	@InjectMocks
	private InterviewTimelineDataFetcher interviewTimelineDataFetcher;

	@Mock
	private InterviewConfirmationManager interviewConfirmationManager;
	@Mock
	private UserInformationManagementHelper userManagement;

	@Test
	public void shouldReturnListForRescheduledForExpertCancellation() throws Exception {
		when(this.dataFetchingEnvironment.getSource()).thenReturn(Interview.builder().build());
		when(this.interviewHistoryManager.getEarliestInterviewChangeHistoriesByField("1", "status"))
				.thenReturn(List.of(
						InterviewHistoryDAO.builder().id("ic-1").interviewId("1").status("pending_scheduling")
								.createdOn(Instant.now()).createdBy("BarRaiser").build(),
						InterviewHistoryDAO.builder().id("ic-1").interviewId("1").status("pending_interviewing")
								.createdOn(Instant.now()).createdBy("u-1").build(),
						InterviewHistoryDAO.builder().id("ic-1").interviewId("1").status("pending_scheduling")
								.createdOn(Instant.now()).createdBy("BarRaiser").build(),
						InterviewHistoryDAO.builder().id("ic-1").interviewId("1").status("pending_interviewing")
								.createdOn(Instant.now()).createdBy("BarRaiser").build(),
						InterviewHistoryDAO.builder().id("ic-1").interviewId("1").status("pending_decision")
								.createdOn(Instant.now()).createdBy("BarRaiser").build(),
						InterviewHistoryDAO.builder().id("ic-1").interviewId("1").status("cancellation_done")
								.createdOn(Instant.now()).createdBy("BarRaiser").build(),
						InterviewHistoryDAO.builder().id("ic-1").interviewId("1").status("pending_scheduling")
								.createdOn(Instant.now()).createdBy("BarRaiser").build(),
						InterviewHistoryDAO.builder().id("ic-1").interviewId("1").status("pending_interviewing")
								.createdOn(Instant.now()).createdBy("BarRaiser").build()));
		when(this.userDetailsRepository.findAllByIdIn(List.of("u-1")))
				.thenReturn(List.of(UserDetailsDAO.builder().id("u-1")
						.firstName("f").lastName("l").email("e").phone("p").build()));

		final Map<String, String> map = Map.of("pending_scheduling", "Created",
				"pending_interviewing", "Scheduled",
				"pending_decision", "Incomplete Interview");
		when(this.interviewManager.getRescheduledTimeOfInterview("1"))
				.thenReturn(1630725451L);
		when(this.evaluationStatusManager.getEvaluation("e-1"))
				.thenReturn(EvaluationDAO.builder().status("pending_assignment").build());
		when(this.interviewConfirmationManager.getInterviewConfirmationStatus(any())).thenReturn("CONFIRMED");
		when(this.mapStatusToDisplayStatus.get("pending_scheduling")).thenReturn(map.get("pending_scheduling"));
		when(this.mapStatusToDisplayStatus.get("pending_interviewing")).thenReturn(map.get("pending_interviewing"));
		when(this.mapStatusToDisplayStatus.get("pending_decision")).thenReturn(map.get("pending_decision"));
		when(this.cancellationReasonManager.getDisplayReason("c-1"))
				.thenReturn("cancellation reason");
		when(this.userManagement.getRolesOfUser("u-1")).thenReturn(List.of("partner"));
		final List<InterviewChangeHistory> actual = (List<InterviewChangeHistory>) this.interviewTimelineDataFetcher
				.get(this.dataFetchingEnvironment);
		final List<InterviewChangeHistory> expected = List.of(
				InterviewChangeHistory.builder().interviewId("1").displayValue("Created")
						.createdByUser(UserDetails.builder().firstName("BarRaiser").build()).build(),
				InterviewChangeHistory.builder().interviewId("1").displayValue("Scheduled")
						.createdByUser(UserDetails.builder().firstName("f").build())
						.scheduledTime(1630639051L).build(),
				InterviewChangeHistory.builder().interviewId("1").displayValue("Rescheduled")
						.createdByUser(UserDetails.builder()
								.firstName("BarRaiser").build())
						.rescheduledTime(1630725451L).displayReason("cancellation reason").build());
		assertEquals(expected.size(), actual.size());
		AtomicInteger index = new AtomicInteger();
		expected.forEach(x -> {
			final InterviewChangeHistory ic = actual.get(index.get());
			assertEquals(x.getInterviewId(), ic.getInterviewId());
			assertEquals(x.getDisplayValue(), ic.getDisplayValue());
			assertEquals(x.getDisplayReason(), ic.getDisplayReason());
			assertEquals(x.getScheduledTime(), ic.getScheduledTime());
			assertEquals(x.getRescheduledTime(), ic.getRescheduledTime());
			assertEquals(x.getCreatedByUser().getFirstName(), ic.getCreatedByUser().getFirstName());
			index.getAndIncrement();
		});
	}

	@Test
	public void shouldReturnListForRescheduledForCandidateRejection() throws Exception {
		when(this.dataFetchingEnvironment.getSource())
				.thenReturn(Interview.builder().id("1").startDate(1630639051L).build());
		when(this.interviewHistoryManager.getEarliestInterviewChangeHistoriesByField("1", "status"))
				.thenReturn(List.of(
						InterviewHistoryDAO.builder().id("ic-1").interviewId("1").status("pending_scheduling")
								.createdOn(Instant.now()).createdBy("BarRaiser").build(),
						InterviewHistoryDAO.builder().id("ic-1").interviewId("1").status("pending_interviewing")
								.startDate(1630639051L).createdOn(Instant.now()).createdBy("u-1").build(),
						InterviewHistoryDAO.builder().id("ic-1").interviewId("1").status("pending_scheduling")
								.createdOn(Instant.now()).createdBy("BarRaiser").build(),
						InterviewHistoryDAO.builder().id("ic-1").interviewId("1").status("pending_interviewing")
								.startDate(1630639051L).createdOn(Instant.now()).createdBy("BarRaiser").build(),
						InterviewHistoryDAO.builder().id("ic-1").interviewId("1").status("pending_decision")
								.createdOn(Instant.now()).createdBy("BarRaiser").build(),
						InterviewHistoryDAO.builder().id("ic-1").interviewId("1").status("cancellation_done")
								.cancellationReasonId("c-1").createdOn(Instant.now()).createdBy("BarRaiser").build(),
						InterviewHistoryDAO.builder().id("ic-1").interviewId("1").status("pending_scheduling")
								.createdOn(Instant.now()).createdBy("BarRaiser").build(),
						InterviewHistoryDAO.builder().id("ic-1").interviewId("1").status("pending_interviewing")
								.startDate(1630639051L).createdOn(Instant.now()).createdBy("BarRaiser").build()));
		when(this.userDetailsRepository.findAllByIdIn(List.of("u-1")))
				.thenReturn(List.of(UserDetailsDAO.builder().id("u-1")
						.firstName("f").lastName("l").email("e").phone("p").build()));

		final Map<String, String> map = Map.of("pending_scheduling", "Created",
				"pending_interviewing", "Scheduled",
				"pending_decision", "Incomplete Interview");

		when(this.evaluationStatusManager.getEvaluation("e-1"))
				.thenReturn(EvaluationDAO.builder().status("pending_assignment").build());

		when(this.evaluationStatusManager.getEvaluation("e-1"))
				.thenReturn(EvaluationDAO.builder().status("pending_assignment").build());
		when(this.interviewConfirmationManager.getInterviewConfirmationStatus(any(), any())).thenReturn("CONFIRMED");
		when(this.mapStatusToDisplayStatus.get("pending_scheduling")).thenReturn(map.get("pending_scheduling"));
		when(this.mapStatusToDisplayStatus.get("pending_interviewing")).thenReturn(map.get("pending_interviewing"));
		when(this.mapStatusToDisplayStatus.get("pending_decision")).thenReturn(map.get("pending_decision"));
		when(this.cancellationReasonManager.getDisplayReason("c-1"))
				.thenReturn("cancellation reason");
		when(this.userManagement.getRolesOfUser("u-1")).thenReturn(List.of("partner"));
		final List<InterviewChangeHistory> actual = (List<InterviewChangeHistory>) this.interviewTimelineDataFetcher
				.get(this.dataFetchingEnvironment);
		final List<InterviewChangeHistory> expected = List.of(
				InterviewChangeHistory.builder().interviewId("1").displayValue("Created")
						.createdByUser(UserDetails.builder().firstName("BarRaiser").build()).build(),
				InterviewChangeHistory.builder().interviewId("1").displayValue("Scheduled")
						.createdByUser(UserDetails.builder().firstName("f").build())
						.scheduledTime(1630639051L).build(),
				InterviewChangeHistory.builder().interviewId("1").displayValue("Rescheduled")
						.createdByUser(UserDetails.builder()
								.firstName("BarRaiser").build())
						.displayReason("cancellation reason").build());
		assertEquals(expected.size(), actual.size());
		AtomicInteger index = new AtomicInteger();
		expected.forEach(x -> {
			final InterviewChangeHistory ic = actual.get(index.get());
			assertEquals(x.getInterviewId(), ic.getInterviewId());
			assertEquals(x.getDisplayValue(), ic.getDisplayValue());
			assertEquals(x.getDisplayReason(), ic.getDisplayReason());
			assertEquals(x.getScheduledTime(), ic.getScheduledTime());
			// assertEquals(x.getRescheduledTime(), ic.getRescheduledTime());
			assertEquals(x.getCreatedByUser().getFirstName(), ic.getCreatedByUser().getFirstName());
			index.getAndIncrement();
		});
	}

	@Test
	public void shouldReturnListForNoShow() throws Exception {
		when(this.dataFetchingEnvironment.getSource()).thenReturn(Interview.builder().build());
		when(this.interviewHistoryManager.getEarliestInterviewChangeHistoriesByField("1", "status"))
				.thenReturn(List.of(
						InterviewHistoryDAO.builder().id("ic-1").interviewId("1").status("pending_scheduling")
								.createdOn(Instant.now()).createdBy("BarRaiser").build(),
						InterviewHistoryDAO.builder().id("ic-1").interviewId("1").status("pending_interviewing")
								.createdOn(Instant.now()).createdBy("u-1").build(),
						InterviewHistoryDAO.builder().id("ic-1").interviewId("1").status("pending_scheduling")
								.createdOn(Instant.now()).createdBy("BarRaiser").build(),
						InterviewHistoryDAO.builder().id("ic-1").interviewId("1").status("pending_interviewing")
								.createdOn(Instant.now()).createdBy("BarRaiser").build(),
						InterviewHistoryDAO.builder().id("ic-1").interviewId("1").status("pending_decision")
								.createdOn(Instant.now()).createdBy("BarRaiser").build(),
						InterviewHistoryDAO.builder().id("ic-1").interviewId("1").status("cancellation_done")
								.createdOn(Instant.now()).createdBy("BarRaiser").build(),
						InterviewHistoryDAO.builder().id("ic-1").interviewId("1").status("pending_scheduling")
								.createdOn(Instant.now()).createdBy("BarRaiser").build(),
						InterviewHistoryDAO.builder().id("ic-1").interviewId("1").status("pending_interviewing")
								.createdOn(Instant.now()).createdBy("BarRaiser").build()));
		when(this.userDetailsRepository.findAllByIdIn(List.of("u-1")))
				.thenReturn(List.of(UserDetailsDAO.builder().id("u-1")
						.firstName("f").lastName("l").email("e").phone("p").build()));
		when(this.userManagement.getRolesOfUser("u-1")).thenReturn(List.of("ops"));

		final Map<String, String> map = Map.of("pending_scheduling", "Created",
				"pending_interviewing", "Scheduled",
				"pending_decision", "Incomplete Interview");
		when(this.evaluationStatusManager.getEvaluation("e-1"))
				.thenReturn(EvaluationDAO.builder().status("pending_assignment").build());
		when(this.interviewConfirmationManager.getInterviewConfirmationStatus(any())).thenReturn("CONFIRMED");
		when(this.mapStatusToDisplayStatus.get("pending_scheduling")).thenReturn(map.get("pending_scheduling"));
		when(this.mapStatusToDisplayStatus.get("pending_interviewing")).thenReturn(map.get("pending_interviewing"));
		when(this.mapStatusToDisplayStatus.get("pending_decision")).thenReturn(map.get("pending_decision"));

		final List<InterviewChangeHistory> actual = (List<InterviewChangeHistory>) this.interviewTimelineDataFetcher
				.get(this.dataFetchingEnvironment);
		final List<InterviewChangeHistory> expected = List.of(
				InterviewChangeHistory.builder().interviewId("1").displayValue("Created")
						.createdByUser(UserDetails.builder().firstName("BarRaiser").build()).build(),
				InterviewChangeHistory.builder().interviewId("1").displayValue("Scheduled")
						.createdByUser(UserDetails.builder().firstName("BarRaiser").build())
						.scheduledTime(1630639051L).build(),
				InterviewChangeHistory.builder().interviewId("1").displayValue("No Show")
						.createdByUser(UserDetails.builder()
								.firstName("BarRaiser").build())
						.build(),
				InterviewChangeHistory.builder().interviewId("1").displayValue("Scheduled")
						.createdByUser(UserDetails.builder().firstName("BarRaiser").build())
						.scheduledTime(1630725451L).build());
		assertEquals(expected.size(), actual.size());
		AtomicInteger index = new AtomicInteger();
		expected.forEach(x -> {
			final InterviewChangeHistory ic = actual.get(index.get());
			assertEquals(x.getInterviewId(), ic.getInterviewId());
			assertEquals(x.getDisplayValue(), ic.getDisplayValue());
			assertEquals(x.getDisplayReason(), ic.getDisplayReason());
			assertEquals(x.getScheduledTime(), ic.getScheduledTime());
			assertEquals(x.getRescheduledTime(), ic.getRescheduledTime());
			assertEquals(x.getCreatedByUser().getFirstName(), ic.getCreatedByUser().getFirstName());
			index.getAndIncrement();
		});
	}

	@Test
	public void shouldReturnListForCancelled() throws Exception {
		when(this.dataFetchingEnvironment.getSource()).thenReturn(Interview.builder().build());
		when(this.interviewHistoryManager.getEarliestInterviewChangeHistoriesByField("1", "status"))
				.thenReturn(List.of(
						InterviewHistoryDAO.builder().id("ic-1").interviewId("1").status("pending_scheduling")
								.createdOn(Instant.now()).createdBy("BarRaiser").build(),
						InterviewHistoryDAO.builder().id("ic-1").interviewId("1").status("pending_interviewing")
								.createdOn(Instant.now()).createdBy("u-1").build(),
						InterviewHistoryDAO.builder().id("ic-1").interviewId("1").status("pending_scheduling")
								.createdOn(Instant.now()).createdBy("BarRaiser").build(),
						InterviewHistoryDAO.builder().id("ic-1").interviewId("1").status("pending_interviewing")
								.createdOn(Instant.now()).createdBy("BarRaiser").build(),
						InterviewHistoryDAO.builder().id("ic-1").interviewId("1").status("pending_decision")
								.createdOn(Instant.now()).createdBy("BarRaiser").build(),
						InterviewHistoryDAO.builder().id("ic-1").interviewId("1").status("cancellation_done")
								.createdOn(Instant.now()).createdBy("BarRaiser").build()));
		when(this.userDetailsRepository.findAllByIdIn(List.of("u-1")))
				.thenReturn(List.of(UserDetailsDAO.builder().id("u-1")
						.firstName("f").lastName("l").email("e").phone("p").build()));
		when(this.userManagement.getRolesOfUser("u-1")).thenReturn(List.of("ops"));

		final Map<String, String> map = Map.of("pending_scheduling", "Created",
				"pending_interviewing", "Scheduled",
				"pending_decision", "Incomplete Interview");
		when(this.evaluationStatusManager.getEvaluation("e-1"))
				.thenReturn(EvaluationDAO.builder().status("Cancelled").build());
		when(this.interviewConfirmationManager.getInterviewConfirmationStatus(any())).thenReturn("CONFIRMED");
		when(this.mapStatusToDisplayStatus.get("pending_scheduling")).thenReturn(map.get("pending_scheduling"));
		when(this.mapStatusToDisplayStatus.get("pending_interviewing")).thenReturn(map.get("pending_interviewing"));
		when(this.mapStatusToDisplayStatus.get("pending_decision")).thenReturn(map.get("pending_decision"));
		when(this.cancellationReasonManager.getDisplayReason("c-1"))
				.thenReturn("cancellation reason");
		final List<InterviewChangeHistory> actual = (List<InterviewChangeHistory>) this.interviewTimelineDataFetcher
				.get(this.dataFetchingEnvironment);
		final List<InterviewChangeHistory> expected = List.of(
				InterviewChangeHistory.builder().interviewId("1").displayValue("Created")
						.createdByUser(UserDetails.builder().firstName("BarRaiser").build()).build(),
				InterviewChangeHistory.builder().interviewId("1").displayValue("Scheduled")
						.createdByUser(UserDetails.builder().firstName("BarRaiser").build())
						.scheduledTime(1630639051L).build(),
				InterviewChangeHistory.builder().interviewId("1").displayValue("Cancelled")
						.createdByUser(UserDetails.builder()
								.firstName("BarRaiser").build())
						.displayReason("cancellation reason").build());
		assertEquals(expected.size(), actual.size());
		AtomicInteger index = new AtomicInteger();
		expected.forEach(x -> {
			final InterviewChangeHistory ic = actual.get(index.get());
			assertEquals(x.getInterviewId(), ic.getInterviewId());
			assertEquals(x.getDisplayValue(), ic.getDisplayValue());
			assertEquals(x.getDisplayReason(), ic.getDisplayReason());
			assertEquals(x.getScheduledTime(), ic.getScheduledTime());
			assertEquals(x.getRescheduledTime(), ic.getRescheduledTime());
			assertEquals(x.getCreatedByUser().getFirstName(), ic.getCreatedByUser().getFirstName());
			index.getAndIncrement();
		});
	}
}
