/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.search;

import com.barraiser.common.utilities.DateUtils;
import com.barraiser.onboarding.dal.*;
import com.barraiser.common.graphql.types.Interviewer;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.MatchInterviewers;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

@Ignore
@RunWith(MockitoJUnitRunner.class)
public class MatchInterviewersTest {

	@Mock
	private InterViewRepository interViewRepository;
	@Mock
	private JobRoleRepository jobRoleRepository;

	@InjectMocks
	private MatchInterviewers matchInterviewers;

	@Mock
	private DateUtils dateUtils;

	public static List<Interviewer> interviewers = new ArrayList<>();

	// public static List<DayWiseInterviewers> dayWiseInterviewers = new
	// ArrayList<>();
	//
	// public static List<DayWiseInterviewersData> dayWiseInterviewersDataList = new
	// ArrayList<>();
	//
	// public static Map<String, List<BookedSlotsDAO>> bookedSlotsPerInterviewer =
	// new HashMap<>();
	//
	// public static Map<String, List<InterviewDAO>> cancelledSlotsPerInterviewer =
	// new HashMap<>();
	//
	// public static Map<String, List<AvailabilityDAO>> freeSlots = new HashMap<>();
	//
	// String dateFormat = "yyyy-MM-dd";
	//
	// @Mock
	// private CancellationReasonRepository cancellationReasonRepository;
	//
	// @Mock
	// private BookedSlotRepository bookedSlotRepository;
	//
	// @Mock
	// private AvailabilityManager availabilityManager;
	//
	// @Mock
	// private InterviewRoundTypeConfigurationRepository
	// interviewRoundTypeConfigurationRepository;

	// @Test
	// public void testFormatInterviewersToDateWiseInterviewers() {
	// this.addInterviewers();
	// this.getDateWhen();
	// dayWiseInterviewersDataList =
	// this.matchInterviewers.formatInterviewersToDateWiseInterviewers(interviewers,
	// 1621035000L, 1621186200L);
	// assertEquals(2, dayWiseInterviewersDataList.size());
	// }
	//
	// public void addInterviewers() {
	//
	// interviewers.add(Interviewer.builder().id("1").workExperienceInMonthsRelative(30).cost(200d).build());
	// interviewers.add(Interviewer.builder().id("2").workExperienceInMonthsRelative(40).cost(400d).build());
	// interviewers.add(Interviewer.builder().id("3").workExperienceInMonthsRelative(50).cost(400d).build());
	// interviewers.add(Interviewer.builder().id("4").workExperienceInMonthsRelative(60).cost(400d).build());
	// interviewers.add(Interviewer.builder().id("5").workExperienceInMonthsRelative(74).cost(400d).build());
	// interviewers.add(Interviewer.builder().id("6").workExperienceInMonthsRelative(72).cost(600d).build());
	// interviewers.add(Interviewer.builder().id("7").workExperienceInMonthsRelative(-12).cost(600d).build());
	// interviewers.add(Interviewer.builder().id("8").workExperienceInMonthsRelative(1).cost(600d).build());
	// }
	//
	// public void getDateWhen() {
	// when(this.dateUtils.getFormattedDateString(1621035000L, null,
	// dateFormat)).thenReturn("2021-05-15");
	// when(this.dateUtils.getFormattedDateString(1621058000L, null,
	// dateFormat)).thenReturn("2021-05-15");
	// when(this.dateUtils.getFormattedDateString(1621186200L, null,
	// dateFormat)).thenReturn("2021-05-16");
	// when(this.dateUtils.convertDateToEpoch("2021-05-15",
	// dateFormat)).thenReturn(1621035000L);
	// when(this.dateUtils.convertDateToEpoch("2021-05-16",
	// dateFormat)).thenReturn(1621186200L);
	// when(this.dateUtils.getFormattedDateString(1621060200L, null,
	// dateFormat)).thenReturn("2021-05-15");
	// when(this.dateUtils.getFormattedDateString(1621061100L, null,
	// dateFormat)).thenReturn("2021-05-15");
	// when(this.dateUtils.getFormattedDateString(1621062000L, null,
	// dateFormat)).thenReturn("2021-05-15");
	// when(this.dateUtils.getFormattedDateString(1621062900L, null,
	// dateFormat)).thenReturn("2021-05-15");
	// when(this.dateUtils.getFormattedDateString(1621063800L, null,
	// dateFormat)).thenReturn("2021-05-15");
	// when(this.dateUtils.getFormattedDateString(1621064700L, null,
	// dateFormat)).thenReturn("2021-05-15");
	// }
	//
	// @Test
	// public void testFilterThroughFeedback() {
	// this.addInterviewers();
	// this.getDateWhen();
	// when(this.interViewRepository.findAllByStatusAndInterviewerIdIn("pending_feedback_submission",
	// interviewers.stream().map(x ->
	// x.getId()).collect(Collectors.toList()))).thenReturn(
	// Arrays.asList(
	// InterviewDAO.builder().interviewerId("1").endDate(1620715203L).build(),
	// InterviewDAO.builder().interviewerId("1").endDate(1620715203L).build(),
	// InterviewDAO.builder().interviewerId("2").endDate(1620715203L).build(),
	// InterviewDAO.builder().interviewerId("3").build(),
	// InterviewDAO.builder().interviewerId("4").actualEndDate(1620715203L).build()
	// )
	// );
	// interviewers =
	// this.matchInterviewers.filterThroughPendingFeedbackStatus(interviewers,
	// 1621035000L);
	// assertEquals(5, interviewers.size());
	// }
	//
	// @Test
	// public void testFindBookedSlotAndCancelSlotForInterviewers() {
	// this.addInterviewers();
	// this.getDateWhen();
	// dayWiseInterviewersDataList =
	// this.matchInterviewers.formatInterviewersToDateWiseInterviewers(interviewers,
	// 1621035000L, 1621186200L);
	// this.getBookedAndCancelledSlots();
	// when(this.cancellationReasonRepository.findAllByCancellationType("EXPERT")).thenReturn(Arrays.asList(CancellationReasonDAO.builder().id("6").build()));
	// List<InterviewDAO> slots = new ArrayList<>();
	// slots.add(InterviewDAO.builder().startDate(1621035000L).cancellationReasonId("6").build());
	// dayWiseInterviewersDataList =
	// this.matchInterviewers.findBookedSlotAndCancelSlotForInterviewers(dayWiseInterviewersDataList,
	// bookedSlotsPerInterviewer, cancelledSlotsPerInterviewer);
	// String str ="";
	// }
	//
	// public void getBookedAndCancelledSlots() {
	// bookedSlotsPerInterviewer.put("1", Arrays.asList(
	// BookedSlotsDAO.builder().startDate(1621035000L).build(),
	// BookedSlotsDAO.builder().startDate(1621058000L).build()
	// ));
	// bookedSlotsPerInterviewer.put("2", Arrays.asList(
	// BookedSlotsDAO.builder().startDate(1621186200L).build()
	// ));
	// cancelledSlotsPerInterviewer.put("1", Arrays.asList(
	// InterviewDAO.builder().startDate(1621035000L).cancellationReasonId("6").build(),
	// InterviewDAO.builder().startDate(1621058000L).cancellationReasonId("2").build()
	// ));
	// cancelledSlotsPerInterviewer.put("2", Arrays.asList(
	// InterviewDAO.builder().startDate(1621035000L).cancellationReasonId("6").build()
	// ));
	// bookedSlotsPerInterviewer.put("3", Arrays.asList());
	// bookedSlotsPerInterviewer.put("4", Arrays.asList());
	// bookedSlotsPerInterviewer.put("5", Arrays.asList());
	// bookedSlotsPerInterviewer.put("6", Arrays.asList());
	// cancelledSlotsPerInterviewer.put("3", Arrays.asList());
	// cancelledSlotsPerInterviewer.put("4", Arrays.asList());
	// cancelledSlotsPerInterviewer.put("5", Arrays.asList());
	// cancelledSlotsPerInterviewer.put("6", Arrays.asList());
	// bookedSlotsPerInterviewer.put("7", Arrays.asList());
	// cancelledSlotsPerInterviewer.put("7", Arrays.asList());
	// bookedSlotsPerInterviewer.put("8", Arrays.asList());
	// cancelledSlotsPerInterviewer.put("8", Arrays.asList());
	//
	// freeSlots.put("1", Arrays.asList());
	// freeSlots.put("2", Arrays.asList());
	// freeSlots.put("3",Arrays.asList(
	// AvailabilityDAO.builder().id(1L).startDate(1621060200L).maximumNumberOfInterviews(3).build(),
	// AvailabilityDAO.builder().id(2L).startDate(1621061100L).maximumNumberOfInterviews(3).build(),
	// AvailabilityDAO.builder().id(3L).startDate(1621062000L).maximumNumberOfInterviews(3).build()
	// ));
	// freeSlots.put("4",Arrays.asList(
	// AvailabilityDAO.builder().id(4L).startDate(1621060200L).maximumNumberOfInterviews(3).build(),
	// AvailabilityDAO.builder().id(5L).startDate(1621061100L).maximumNumberOfInterviews(3).build(),
	// AvailabilityDAO.builder().id(6L).startDate(1621062000L).maximumNumberOfInterviews(3).build(),
	// AvailabilityDAO.builder().id(7L).startDate(1621062900L).maximumNumberOfInterviews(3).build()
	// ));
	// freeSlots.put("5",Arrays.asList(
	// AvailabilityDAO.builder().id(8L).startDate(1621060200L).maximumNumberOfInterviews(3).build(),
	// AvailabilityDAO.builder().id(9L).startDate(1621061100L).maximumNumberOfInterviews(3).build(),
	// AvailabilityDAO.builder().id(10L).startDate(1621062000L).maximumNumberOfInterviews(3).build(),
	// AvailabilityDAO.builder().id(11L).startDate(1621062900L).maximumNumberOfInterviews(3).build(),
	// AvailabilityDAO.builder().id(12L).startDate(1621063800L).maximumNumberOfInterviews(3).build()
	// ));
	// freeSlots.put("6",Arrays.asList(
	// AvailabilityDAO.builder().id(13L).startDate(1621060200L).maximumNumberOfInterviews(3).build(),
	// AvailabilityDAO.builder().id(14L).startDate(1621061100L).maximumNumberOfInterviews(3).build(),
	// AvailabilityDAO.builder().id(15L).startDate(1621062000L).maximumNumberOfInterviews(3).build(),
	// AvailabilityDAO.builder().id(16L).startDate(1621062900L).maximumNumberOfInterviews(3).build(),
	// AvailabilityDAO.builder().id(17L).startDate(1621063800L).maximumNumberOfInterviews(3).build(),
	// AvailabilityDAO.builder().id(18L).startDate(1621064700L).maximumNumberOfInterviews(3).build()
	// ));
	// freeSlots.put("7",Arrays.asList(
	// AvailabilityDAO.builder().id(19L).startDate(1621060200L).maximumNumberOfInterviews(3).build()
	// ));
	// freeSlots.put("8",Arrays.asList(
	// AvailabilityDAO.builder().id(20L).startDate(1621060200L).maximumNumberOfInterviews(3).build(),
	// AvailabilityDAO.builder().id(21L).startDate(1621061100L).maximumNumberOfInterviews(3).build()
	// ));
	// }
	//
	// @Test
	// public void testSorting() {
	// this.addInterviewers();
	// this.getDateWhen();
	// dayWiseInterviewersDataList =
	// this.matchInterviewers.formatInterviewersToDateWiseInterviewers(interviewers,
	// 1621035000L, 1621186200L);
	// this.getBookedAndCancelledSlots();
	// when(this.cancellationReasonRepository.findAllByCancellationType("EXPERT")).thenReturn(Arrays.asList(CancellationReasonDAO.builder().id("6").build()));
	// List<InterviewDAO> slots = new ArrayList<>();
	// slots.add(InterviewDAO.builder().startDate(1621035000L).cancellationReasonId("6").build());
	// dayWiseInterviewersDataList =
	// this.matchInterviewers.findBookedSlotAndCancelSlotForInterviewers(dayWiseInterviewersDataList,
	// bookedSlotsPerInterviewer, cancelledSlotsPerInterviewer);
	// dayWiseInterviewersDataList =
	// this.matchInterviewers.sorting(dayWiseInterviewersDataList);
	// assertEquals("7",
	// dayWiseInterviewersDataList.get(0).getInterviewers().get(0).getId());
	// assertEquals("8",
	// dayWiseInterviewersDataList.get(0).getInterviewers().get(1).getId());
	// assertEquals("3",
	// dayWiseInterviewersDataList.get(0).getInterviewers().get(2).getId());
	// assertEquals("4",
	// dayWiseInterviewersDataList.get(0).getInterviewers().get(3).getId());
	// assertEquals("5",
	// dayWiseInterviewersDataList.get(0).getInterviewers().get(4).getId());
	// assertEquals("6",
	// dayWiseInterviewersDataList.get(0).getInterviewers().get(5).getId());
	// assertEquals("2",
	// dayWiseInterviewersDataList.get(0).getInterviewers().get(6).getId());
	// assertEquals("1",
	// dayWiseInterviewersDataList.get(0).getInterviewers().get(7).getId());
	// }
	//
	// @Test
	// public void testFetchDayWiseSlotsForEachInterviewer() {
	//
	// this.addInterviewers();
	// this.getDateWhen();
	// dayWiseInterviewersDataList =
	// this.matchInterviewers.formatInterviewersToDateWiseInterviewers(interviewers,
	// 1621035000L, 1621186200L);
	// this.getBookedAndCancelledSlots();
	// when(this.cancellationReasonRepository.findAllByCancellationType("EXPERT")).thenReturn(Arrays.asList(CancellationReasonDAO.builder().id("6").build()));
	// when(this.interviewRoundTypeConfigurationRepository.findByRoundType(any())).thenReturn(Optional.ofNullable(InterviewRoundTypeConfigurationDAO.builder().candidateStartTimeOffsetMinutes(0L).candidateEndTimeOffsetMinutes(120L).build()));
	// List<InterviewDAO> slots = new ArrayList<>();
	// slots.add(InterviewDAO.builder().startDate(1621035000L).cancellationReasonId("6").build());
	// dayWiseInterviewersDataList =
	// this.matchInterviewers.findBookedSlotAndCancelSlotForInterviewers(dayWiseInterviewersDataList,
	// bookedSlotsPerInterviewer, cancelledSlotsPerInterviewer);
	// dayWiseInterviewersDataList =
	// this.matchInterviewers.sorting(dayWiseInterviewersDataList);
	// when(this.availabilityManager.doesOverLapWithBookedSlots(freeSlots.get("3").get(0),
	// bookedSlotsPerInterviewer.get("3"), 30L)).thenReturn(1);
	// when(this.availabilityManager.doesOverLapWithBookedSlots(freeSlots.get("3").get(1),
	// bookedSlotsPerInterviewer.get("3"), 30L)).thenReturn(1);
	// when(this.availabilityManager.doesOverLapWithBookedSlots(freeSlots.get("3").get(2),
	// bookedSlotsPerInterviewer.get("3"), 30L)).thenReturn(1);
	// when(this.availabilityManager.doesOverLapWithBookedSlots(freeSlots.get("4").get(0),
	// bookedSlotsPerInterviewer.get("4"), 30L)).thenReturn(1);
	// when(this.availabilityManager.doesOverLapWithBookedSlots(freeSlots.get("4").get(1),
	// bookedSlotsPerInterviewer.get("4"), 30L)).thenReturn(1);
	// when(this.availabilityManager.doesOverLapWithBookedSlots(freeSlots.get("4").get(2),
	// bookedSlotsPerInterviewer.get("4"), 30L)).thenReturn(1);
	// when(this.availabilityManager.doesOverLapWithBookedSlots(freeSlots.get("4").get(3),
	// bookedSlotsPerInterviewer.get("4"), 30L)).thenReturn(1);
	// when(this.availabilityManager.doesOverLapWithBookedSlots(freeSlots.get("5").get(0),
	// bookedSlotsPerInterviewer.get("5"), 30L)).thenReturn(1);
	// when(this.availabilityManager.doesOverLapWithBookedSlots(freeSlots.get("5").get(1),
	// bookedSlotsPerInterviewer.get("5"), 30L)).thenReturn(1);
	// when(this.availabilityManager.doesOverLapWithBookedSlots(freeSlots.get("5").get(2),
	// bookedSlotsPerInterviewer.get("5"), 30L)).thenReturn(1);
	// when(this.availabilityManager.doesOverLapWithBookedSlots(freeSlots.get("5").get(3),
	// bookedSlotsPerInterviewer.get("5"), 30L)).thenReturn(1);
	// when(this.availabilityManager.doesOverLapWithBookedSlots(freeSlots.get("5").get(4),
	// bookedSlotsPerInterviewer.get("5"), 30L)).thenReturn(1);
	// when(this.availabilityManager.doesOverLapWithBookedSlots(freeSlots.get("6").get(0),
	// bookedSlotsPerInterviewer.get("6"), 30L)).thenReturn(1);
	// when(this.availabilityManager.doesOverLapWithBookedSlots(freeSlots.get("6").get(1),
	// bookedSlotsPerInterviewer.get("6"), 30L)).thenReturn(1);
	// when(this.availabilityManager.doesOverLapWithBookedSlots(freeSlots.get("6").get(2),
	// bookedSlotsPerInterviewer.get("6"), 30L)).thenReturn(1);
	// when(this.availabilityManager.doesOverLapWithBookedSlots(freeSlots.get("6").get(3),
	// bookedSlotsPerInterviewer.get("6"), 30L)).thenReturn(1);
	// when(this.availabilityManager.doesOverLapWithBookedSlots(freeSlots.get("6").get(4),
	// bookedSlotsPerInterviewer.get("6"), 30L)).thenReturn(1);
	// when(this.availabilityManager.doesOverLapWithBookedSlots(freeSlots.get("6").get(5),
	// bookedSlotsPerInterviewer.get("6"), 30L)).thenReturn(1);
	// when(this.availabilityManager.doesOverLapWithBookedSlots(freeSlots.get("7").get(0),
	// bookedSlotsPerInterviewer.get("7"), 30L)).thenReturn(1);
	// when(this.availabilityManager.doesOverLapWithBookedSlots(freeSlots.get("8").get(0),
	// bookedSlotsPerInterviewer.get("8"), 30L)).thenReturn(1);
	// when(this.availabilityManager.doesOverLapWithBookedSlots(freeSlots.get("8").get(1),
	// bookedSlotsPerInterviewer.get("8"), 30L)).thenReturn(1);
	//
	// when(this.availabilityManager.doesOverLapWithCancelledSlots(freeSlots.get("3").get(0),
	// cancelledSlotsPerInterviewer.get("3"))).thenReturn(1);
	// when(this.availabilityManager.doesOverLapWithCancelledSlots(freeSlots.get("3").get(1),
	// cancelledSlotsPerInterviewer.get("3"))).thenReturn(1);
	// when(this.availabilityManager.doesOverLapWithCancelledSlots(freeSlots.get("3").get(2),
	// cancelledSlotsPerInterviewer.get("3"))).thenReturn(1);
	// when(this.availabilityManager.doesOverLapWithCancelledSlots(freeSlots.get("4").get(0),
	// cancelledSlotsPerInterviewer.get("3"))).thenReturn(1);
	// when(this.availabilityManager.doesOverLapWithCancelledSlots(freeSlots.get("4").get(1),
	// cancelledSlotsPerInterviewer.get("3"))).thenReturn(1);
	// when(this.availabilityManager.doesOverLapWithCancelledSlots(freeSlots.get("4").get(2),
	// cancelledSlotsPerInterviewer.get("3"))).thenReturn(1);
	// when(this.availabilityManager.doesOverLapWithCancelledSlots(freeSlots.get("4").get(3),
	// cancelledSlotsPerInterviewer.get("3"))).thenReturn(1);
	// when(this.availabilityManager.doesOverLapWithCancelledSlots(freeSlots.get("5").get(0),
	// cancelledSlotsPerInterviewer.get("3"))).thenReturn(1);
	// when(this.availabilityManager.doesOverLapWithCancelledSlots(freeSlots.get("5").get(1),
	// cancelledSlotsPerInterviewer.get("3"))).thenReturn(1);
	// when(this.availabilityManager.doesOverLapWithCancelledSlots(freeSlots.get("5").get(2),
	// cancelledSlotsPerInterviewer.get("3"))).thenReturn(1);
	// when(this.availabilityManager.doesOverLapWithCancelledSlots(freeSlots.get("5").get(3),
	// cancelledSlotsPerInterviewer.get("3"))).thenReturn(1);
	// when(this.availabilityManager.doesOverLapWithCancelledSlots(freeSlots.get("5").get(4),
	// cancelledSlotsPerInterviewer.get("3"))).thenReturn(1);
	// when(this.availabilityManager.doesOverLapWithCancelledSlots(freeSlots.get("6").get(0),
	// cancelledSlotsPerInterviewer.get("3"))).thenReturn(1);
	// when(this.availabilityManager.doesOverLapWithCancelledSlots(freeSlots.get("6").get(1),
	// cancelledSlotsPerInterviewer.get("3"))).thenReturn(1);
	// when(this.availabilityManager.doesOverLapWithCancelledSlots(freeSlots.get("6").get(2),
	// cancelledSlotsPerInterviewer.get("3"))).thenReturn(1);
	// when(this.availabilityManager.doesOverLapWithCancelledSlots(freeSlots.get("6").get(3),
	// cancelledSlotsPerInterviewer.get("3"))).thenReturn(1);
	// when(this.availabilityManager.doesOverLapWithCancelledSlots(freeSlots.get("6").get(4),
	// cancelledSlotsPerInterviewer.get("3"))).thenReturn(1);
	// when(this.availabilityManager.doesOverLapWithCancelledSlots(freeSlots.get("6").get(5),
	// cancelledSlotsPerInterviewer.get("3"))).thenReturn(1);
	// when(this.availabilityManager.doesOverLapWithCancelledSlots(freeSlots.get("7").get(0),
	// cancelledSlotsPerInterviewer.get("3"))).thenReturn(1);
	// when(this.availabilityManager.doesOverLapWithCancelledSlots(freeSlots.get("8").get(0),
	// cancelledSlotsPerInterviewer.get("3"))).thenReturn(1);
	// when(this.availabilityManager.doesOverLapWithCancelledSlots(freeSlots.get("8").get(1),
	// cancelledSlotsPerInterviewer.get("3"))).thenReturn(1);
	//
	// when(this.availabilityManager.getTotalAvailableSlotsOfInterviewerAfterSplit(freeSlots.get("3").get(0),
	// 120L, 30L, bookedSlotsPerInterviewer.get("3"),
	// cancelledSlotsPerInterviewer.get("3"))).thenReturn(Collections.singletonList(freeSlots.get("3").get(0)));
	// when(this.availabilityManager.getTotalAvailableSlotsOfInterviewerAfterSplit(freeSlots.get("3").get(1),
	// 120L, 30L, bookedSlotsPerInterviewer.get("3"),
	// cancelledSlotsPerInterviewer.get("3"))).thenReturn(Collections.singletonList(freeSlots.get("3").get(1)));
	// when(this.availabilityManager.getTotalAvailableSlotsOfInterviewerAfterSplit(freeSlots.get("3").get(2),
	// 120L, 30L, bookedSlotsPerInterviewer.get("3"),
	// cancelledSlotsPerInterviewer.get("3"))).thenReturn(Collections.singletonList(freeSlots.get("3").get(2)));
	// when(this.availabilityManager.getTotalAvailableSlotsOfInterviewerAfterSplit(freeSlots.get("4").get(0),
	// 120L, 30L, bookedSlotsPerInterviewer.get("4"),
	// cancelledSlotsPerInterviewer.get("4"))).thenReturn(Collections.singletonList(freeSlots.get("4").get(0)));
	// when(this.availabilityManager.getTotalAvailableSlotsOfInterviewerAfterSplit(freeSlots.get("4").get(1),
	// 120L, 30L, bookedSlotsPerInterviewer.get("4"),
	// cancelledSlotsPerInterviewer.get("4"))).thenReturn(Collections.singletonList(freeSlots.get("4").get(1)));
	// when(this.availabilityManager.getTotalAvailableSlotsOfInterviewerAfterSplit(freeSlots.get("4").get(2),
	// 120L, 30L, bookedSlotsPerInterviewer.get("4"),
	// cancelledSlotsPerInterviewer.get("4"))).thenReturn(Collections.singletonList(freeSlots.get("4").get(2)));
	// when(this.availabilityManager.getTotalAvailableSlotsOfInterviewerAfterSplit(freeSlots.get("4").get(3),
	// 120L, 30L, bookedSlotsPerInterviewer.get("4"),
	// cancelledSlotsPerInterviewer.get("4"))).thenReturn(Collections.singletonList(freeSlots.get("4").get(3)));
	// when(this.availabilityManager.getTotalAvailableSlotsOfInterviewerAfterSplit(freeSlots.get("5").get(0),
	// 120L, 30L, bookedSlotsPerInterviewer.get("5"),
	// cancelledSlotsPerInterviewer.get("5"))).thenReturn(Collections.singletonList(freeSlots.get("5").get(0)));
	// when(this.availabilityManager.getTotalAvailableSlotsOfInterviewerAfterSplit(freeSlots.get("5").get(0),
	// 120L, 30L, bookedSlotsPerInterviewer.get("5"),
	// cancelledSlotsPerInterviewer.get("5"))).thenReturn(Collections.singletonList(freeSlots.get("5").get(1)));
	// when(this.availabilityManager.getTotalAvailableSlotsOfInterviewerAfterSplit(freeSlots.get("5").get(0),
	// 120L, 30L, bookedSlotsPerInterviewer.get("5"),
	// cancelledSlotsPerInterviewer.get("5"))).thenReturn(Collections.singletonList(freeSlots.get("5").get(2)));
	// when(this.availabilityManager.getTotalAvailableSlotsOfInterviewerAfterSplit(freeSlots.get("5").get(0),
	// 120L, 30L, bookedSlotsPerInterviewer.get("5"),
	// cancelledSlotsPerInterviewer.get("5"))).thenReturn(Collections.singletonList(freeSlots.get("5").get(3)));
	// when(this.availabilityManager.getTotalAvailableSlotsOfInterviewerAfterSplit(freeSlots.get("5").get(0),
	// 120L, 30L, bookedSlotsPerInterviewer.get("5"),
	// cancelledSlotsPerInterviewer.get("5"))).thenReturn(Collections.singletonList(freeSlots.get("5").get(4)));
	// when(this.availabilityManager.getTotalAvailableSlotsOfInterviewerAfterSplit(freeSlots.get("6").get(0),
	// 120L, 30L, bookedSlotsPerInterviewer.get("6"),
	// cancelledSlotsPerInterviewer.get("6"))).thenReturn(Collections.singletonList(freeSlots.get("6").get(0)));
	// when(this.availabilityManager.getTotalAvailableSlotsOfInterviewerAfterSplit(freeSlots.get("6").get(0),
	// 120L, 30L, bookedSlotsPerInterviewer.get("6"),
	// cancelledSlotsPerInterviewer.get("6"))).thenReturn(Collections.singletonList(freeSlots.get("6").get(1)));
	// when(this.availabilityManager.getTotalAvailableSlotsOfInterviewerAfterSplit(freeSlots.get("6").get(0),
	// 120L, 30L, bookedSlotsPerInterviewer.get("6"),
	// cancelledSlotsPerInterviewer.get("6"))).thenReturn(Collections.singletonList(freeSlots.get("6").get(2)));
	// when(this.availabilityManager.getTotalAvailableSlotsOfInterviewerAfterSplit(freeSlots.get("6").get(0),
	// 120L, 30L, bookedSlotsPerInterviewer.get("6"),
	// cancelledSlotsPerInterviewer.get("6"))).thenReturn(Collections.singletonList(freeSlots.get("6").get(3)));
	// when(this.availabilityManager.getTotalAvailableSlotsOfInterviewerAfterSplit(freeSlots.get("6").get(0),
	// 120L, 30L, bookedSlotsPerInterviewer.get("6"),
	// cancelledSlotsPerInterviewer.get("6"))).thenReturn(Collections.singletonList(freeSlots.get("6").get(4)));
	// when(this.availabilityManager.getTotalAvailableSlotsOfInterviewerAfterSplit(freeSlots.get("6").get(0),
	// 120L, 30L, bookedSlotsPerInterviewer.get("6"),
	// cancelledSlotsPerInterviewer.get("6"))).thenReturn(Collections.singletonList(freeSlots.get("6").get(5)));
	// when(this.availabilityManager.getTotalAvailableSlotsOfInterviewerAfterSplit(freeSlots.get("7").get(0),
	// 120L, 30L, bookedSlotsPerInterviewer.get("7"),
	// cancelledSlotsPerInterviewer.get("7"))).thenReturn(Collections.singletonList(freeSlots.get("7").get(0)));
	// when(this.availabilityManager.getTotalAvailableSlotsOfInterviewerAfterSplit(freeSlots.get("8").get(0),
	// 120L, 30L, bookedSlotsPerInterviewer.get("8"),
	// cancelledSlotsPerInterviewer.get("8"))).thenReturn(Collections.singletonList(freeSlots.get("8").get(0)));
	// when(this.availabilityManager.getTotalAvailableSlotsOfInterviewerAfterSplit(freeSlots.get("8").get(1),
	// 120L, 30L, bookedSlotsPerInterviewer.get("8"),
	// cancelledSlotsPerInterviewer.get("8"))).thenReturn(Collections.singletonList(freeSlots.get("8").get(1)));
	//
	// dayWiseInterviewers =
	// this.matchInterviewers.fetchDayWiseSlotsForEachInterviewer("MACHINE",
	// dayWiseInterviewersDataList, bookedSlotsPerInterviewer,
	// cancelledSlotsPerInterviewer, freeSlots);
	// String str1="";
	// }
}
