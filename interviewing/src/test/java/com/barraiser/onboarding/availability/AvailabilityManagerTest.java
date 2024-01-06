/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.availability;

import com.barraiser.onboarding.availability.DTO.BookedSlotDTO;
import com.barraiser.onboarding.availability.exception.SlotNotAvailableException;
import com.barraiser.common.utilities.DateUtils;
import com.barraiser.onboarding.common.TestingUtil;
import com.barraiser.onboarding.dal.AvailabilityDAO;
import com.barraiser.onboarding.dal.AvailabilityRepository;
import com.barraiser.onboarding.dal.BookedSlotRepository;
import com.barraiser.onboarding.dal.BookedSlotsDAO;

import com.barraiser.onboarding.scheduling.match_interviewers.data.AvailabilityManagerTestData;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AvailabilityManagerTest {

	@Spy
	private ObjectMapper objectMapper;

	@Mock
	private AvailabilityRepository availabilityRepository;
	@Mock
	private BookedSlotRepository bookedSlotRepository;
	@Mock
	private DateUtils dateUtils;
	@Mock
	private AvailabilityServiceClient availabilityServiceClient;

	@Mock
	private AvailabilityConsolidator availabilityConsolidator;

	@Mock
	private RecurringAvailabilityManager recurringAvailabilityManager;

	@InjectMocks
	private AvailabilityManager availabilityManager;

	@InjectMocks
	private TestingUtil testingUtil;

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

	@Test
	public void shouldNotAddSlotIfGreaterThanMaximumNumber() {
		this.exceptionRule.expect(IllegalArgumentException.class);
		this.exceptionRule.expectMessage("Can take maximum 2 interviews only for the given period");
		this.availabilityManager.addASlot("1", 1626754635L, 1626763620L, 3);
	}

	@Test
	public void shouldNotAddOverlappingSlots() {
		this.exceptionRule.expect(IllegalArgumentException.class);
		this.exceptionRule.expectMessage("overlapping slot timings");
		when(this.availabilityRepository.findByUserIdAndStartDateLessThanAndEndDateGreaterThan("1", 1626763620L,
				1626754635L))
						.thenReturn(Arrays.asList(AvailabilityDAO.builder().build()));
		this.availabilityManager.addASlot("1", 1626754635L, 1626763620L, 2);
	}

	@Test
	public void shouldAddToRightAdjacent() {
		when(this.availabilityRepository.findByUserIdAndStartDateLessThanAndEndDateGreaterThan("1", 1626777000L,
				1626769800L))
						.thenReturn(List.of());
		when(this.availabilityRepository.findByUserIdAndStartDate("1", 1626777000L))
				.thenReturn(AvailabilityDAO
						.builder()
						.userId("1")
						.startDate(1626777000L)
						.endDate(1626784200L)
						.maximumNumberOfInterviews(1).build());
		when(this.availabilityRepository.findByUserIdAndEndDate("1", 1626769800L))
				.thenReturn(null);

		this.availabilityManager.addASlot("1", 1626769800L, 1626777000L, 2);
		verify(this.availabilityRepository)
				.save(argThat(arg -> arg.getUserId().equals("1") && arg.getMaximumNumberOfInterviews().equals(2)
						&& arg.getStartDate().equals(1626769800L) && arg.getEndDate().equals(1626784200L)));

		verify(this.availabilityRepository).delete(argThat(arg -> arg.getStartDate().equals(1626777000L)
				&& arg.getEndDate().equals(1626784200L) && arg.getMaximumNumberOfInterviews().equals(1)));
	}

	@Test
	public void shouldAddToLeftAdjacent() {
		when(this.availabilityRepository.findByUserIdAndStartDateLessThanAndEndDateGreaterThan("1", 1626777000L,
				1626769800L))
						.thenReturn(List.of());
		when(this.availabilityRepository.findByUserIdAndStartDate("1", 1626777000L))
				.thenReturn(null);
		when(this.availabilityRepository.findByUserIdAndEndDate("1", 1626769800L))
				.thenReturn(AvailabilityDAO.builder()
						.userId("1")
						.startDate(1626762600L)
						.endDate(1626769800L)
						.maximumNumberOfInterviews(4).build());

		this.availabilityManager.addASlot("1", 1626769800L, 1626777000L, 2);
		verify(this.availabilityRepository)
				.save(argThat(arg -> arg.getUserId().equals("1") && arg.getMaximumNumberOfInterviews().equals(5)
						&& arg.getStartDate().equals(1626762600L) && arg.getEndDate().equals(1626777000L)));

		verify(this.availabilityRepository).delete(argThat(arg -> arg.getStartDate().equals(1626762600L)
				&& arg.getEndDate().equals(1626769800L) && arg.getMaximumNumberOfInterviews().equals(4)));
	}

	@Test
	public void shouldAddToBothLefAdjacentAndRightAdjacent() {
		when(this.availabilityRepository.findByUserIdAndStartDateLessThanAndEndDateGreaterThan("1", 1626777000L,
				1626769800L))
						.thenReturn(List.of());
		when(this.availabilityRepository.findByUserIdAndStartDate("1", 1626777000L))
				.thenReturn(AvailabilityDAO
						.builder()
						.userId("1")
						.startDate(1626777000L)
						.endDate(1626784200L)
						.maximumNumberOfInterviews(1).build());
		when(this.availabilityRepository.findByUserIdAndEndDate("1", 1626769800L))
				.thenReturn(AvailabilityDAO.builder()
						.userId("1")
						.startDate(1626762600L)
						.endDate(1626769800L)
						.maximumNumberOfInterviews(4).build());

		this.availabilityManager.addASlot("1", 1626769800L, 1626777000L, 2);
		verify(this.availabilityRepository)
				.save(argThat(arg -> arg.getUserId().equals("1") && arg.getMaximumNumberOfInterviews().equals(5)
						&& arg.getStartDate().equals(1626762600L) && arg.getEndDate().equals(1626784200L)));

		verify(this.availabilityRepository).delete(argThat(arg -> arg.getStartDate().equals(1626777000L)
				&& arg.getEndDate().equals(1626784200L) && arg.getMaximumNumberOfInterviews().equals(1)));

		verify(this.availabilityRepository).delete(argThat(arg -> arg.getStartDate().equals(1626762600L)
				&& arg.getEndDate().equals(1626769800L) && arg.getMaximumNumberOfInterviews().equals(4)));
	}

	@Test
	public void shouldNotMergeToAny() {
		when(this.availabilityRepository.findByUserIdAndStartDateLessThanAndEndDateGreaterThan("1", 1626777000L,
				1626769800L))
						.thenReturn(List.of());
		when(this.availabilityRepository.findByUserIdAndStartDate("1", 1626777000L))
				.thenReturn(null);
		when(this.availabilityRepository.findByUserIdAndEndDate("1", 1626769800L))
				.thenReturn(null);

		this.availabilityManager.addASlot("1", 1626769800L, 1626777000L, 2);
		verify(this.availabilityRepository)
				.save(argThat(arg -> arg.getUserId().equals("1") && arg.getMaximumNumberOfInterviews().equals(1)
						&& arg.getStartDate().equals(1626769800L) && arg.getEndDate().equals(1626777000L)));

		verify(this.availabilityRepository, never()).delete(argThat(arg -> arg.getStartDate().equals(1626777000L)
				&& arg.getEndDate().equals(1626784200L) && arg.getMaximumNumberOfInterviews().equals(1)));

		verify(this.availabilityRepository, never()).delete(argThat(arg -> arg.getStartDate().equals(1626762600L)
				&& arg.getEndDate().equals(1626769800L) && arg.getMaximumNumberOfInterviews().equals(4)));
	}

	@Test
	public void shouldReturnAllAvailableSlots() {
		when(this.availabilityRepository.findByUserIdAndStartDateLessThanAndEndDateGreaterThan("1", 1626784200L,
				1626755400L))
						.thenReturn(List.of(
								AvailabilityDAO.builder().userId("1").startDate(1626760800L).endDate(1626768000L)
										.build(),
								AvailabilityDAO.builder().userId("1").startDate(1626764400L).endDate(1626771600L)
										.build()));

		when(this.availabilityConsolidator.consolidateAvailabilites(any(), any(), any(), any()))
				.thenReturn(List.of(
						AvailabilityDAO.builder().userId("1").startDate(1626760800L).endDate(1626768000L)
								.build(),
						AvailabilityDAO.builder().userId("1").startDate(1626764400L).endDate(1626771600L)
								.build()));

		final List<AvailabilityDAO> freeSlots = this.availabilityManager.getAllAvailableSlots("1", 1626755400L,
				1626784200L, -1L);
		assertEquals(Optional.ofNullable(1626760800L), Optional.ofNullable(freeSlots.get(0).getStartDate()));
		assertEquals(Optional.ofNullable(1626768000L), Optional.ofNullable(freeSlots.get(0).getEndDate()));
		assertEquals(Optional.ofNullable(1626764400L), Optional.ofNullable(freeSlots.get(1).getStartDate()));
		assertEquals(Optional.ofNullable(1626771600L), Optional.ofNullable(freeSlots.get(1).getEndDate()));
	}

	@Test(expected = SlotNotAvailableException.class)
	public void shouldNotBookSlotIfOverlappingBookedSlot() {
		when(this.availabilityServiceClient.getBookedSlots(any()))
				.thenReturn(Map.of("1", Collections.singletonList(
						BookedSlotDTO.builder().userId("1").startDate(1624080600L).endDate(1624087800L)
								.build())));
		// when(this.bookedSlotRepository.findAllByUserIdAndStartDateGreaterThanEqualAndStartDateLessThan("1",
		// 1624084200L, 1624091400L))
		// .thenReturn(new ArrayList<>());
		this.availabilityManager.bookSlotNeedlessAvailability("1", "x", 1624084200L, 1624091400L, 1800L);
	}

	@Test
	public void shouldBookSlot() {
		when(this.availabilityServiceClient.getBookedSlots(any()))
				.thenReturn(Map.of("1", new ArrayList<>()));
		this.availabilityManager.bookSlotNeedlessAvailability("1", "x", 1624084200L, 1624091400L, 1800L);
	}

	@Test
	public void getAvailableSlotsOfAllInterviewersTest() throws IOException {

		final AvailabilityManagerTestData testData = this.testingUtil
				.getTestingData(
						"src/test/resources/json_data_files/AvailabilitySlotsOfAllInterviewersTestDataJson.json",
						AvailabilityManagerTestData.class);
		when(this.availabilityRepository.findAllByUserIdInAndStartDateGreaterThanEqualAndStartDateLessThan(
				Arrays.asList("1", "2"),
				1624084200L, 1624091400L)).thenReturn(testData.getAvailabilityPerInterviewer().get("1"));
		when(this.availabilityRepository.findByUserIdInAndStartDateLessThanAndEndDateGreaterThan(
				Arrays.asList("1", "2"),
				1624084200L, 1624084200L)).thenReturn(testData.getAvailabilityPerInterviewer().get("2"));
		final List<AvailabilityDAO> freeSlots = this.availabilityManager.getAvailableSlotsOfAllInterviewers(
				Arrays.asList("1", "2"),
				1624084200L, 1624091400L);
		AtomicInteger index = new AtomicInteger();
		testData.getFreeSlots().forEach(x -> {
			assertEquals(x.getStartDate(), freeSlots.get(index.get()).getStartDate());
			assertEquals(x.getEndDate(), freeSlots.get(index.get()).getEndDate());
			index.getAndIncrement();
		});
	}

	@Test
	public void splitAvailableSlotAndFilterByBookedSlotsTestWithMoreThanOneOverlappingSlot() throws IOException {
		final AvailabilityManagerTestData testData = this.testingUtil
				.getTestingData(
						"src/test/resources/json_data_files/SpitAndFilterAvailableSlotsWithOverlappingBookedJson.json",
						AvailabilityManagerTestData.class);
		testData.getEpochTo15thMinuteCeil().forEach((x, y) -> when(this.dateUtils.getEpochTo15ThMinuteCeil(x))
				.thenReturn(y));
		final List<AvailabilityDAO> actualFreeSlots = this.availabilityManager
				.splitAvailableSlotAndFilterByBookedSlots(testData.getAvailabilityPerInterviewer().get("1").get(0),
						testData.getBookedSlotsPerInterviewerMidOverlap().get("1"), 120L);
		AtomicInteger index = new AtomicInteger();
		testData.getCalculatedAvailabilityOfInterviewer().get("1")
				.forEach(x -> {
					assertEquals(x.getStartDate(), actualFreeSlots.get(index.get()).getStartDate());
					assertEquals(x.getEndDate(), actualFreeSlots.get(index.get()).getEndDate());
					index.getAndIncrement();
				});
	}

	@Test
	public void splitAvailableSlotAndFilterByBookedSlotsTestWithNoSlotsAvailable() {

		final AvailabilityDAO freeSlot = AvailabilityDAO.builder().userId("1").startDate(1624170600L)
				.endDate(1624185000L).build();
		final List<BookedSlotDTO> bookedSlotsDAOS = new ArrayList<>();
		bookedSlotsDAOS.add(BookedSlotDTO.builder().userId("1").startDate(1624167000L).endDate(1624188600L).build());
		when(this.dateUtils.getEpochTo15ThMinuteCeil(1624170600L)).thenReturn(1624170600L);
		final List<AvailabilityDAO> actualFreeSlots = this.availabilityManager
				.splitAvailableSlotAndFilterByBookedSlots(freeSlot, bookedSlotsDAOS, 120L);
		assertEquals(0, actualFreeSlots.size());
	}

	@Test
	public void getSplittedSlotsInBetween() {

		final AvailabilityDAO freeSlot = AvailabilityDAO.builder().userId("1").startDate(1624170600L)
				.endDate(1624185000L).build();
		final List<BookedSlotDTO> bookedSlotsDAOS = new ArrayList<>();
		bookedSlotsDAOS.add(BookedSlotDTO.builder().userId("1").startDate(1624167000L).endDate(1624174200L).build());
		bookedSlotsDAOS.add(BookedSlotDTO.builder().userId("1").startDate(1624181400L).endDate(1624188600L).build());
		when(this.dateUtils.getEpochTo15ThMinuteCeil(1624170600L)).thenReturn(1624170600L);
		final List<AvailabilityDAO> actualFreeSlots = this.availabilityManager
				.splitAvailableSlotAndFilterByBookedSlots(freeSlot, bookedSlotsDAOS, 120L);
		assertEquals(Optional.ofNullable(1624174200L), Optional.ofNullable(actualFreeSlots.get(0).getStartDate()));
		assertEquals(Optional.ofNullable(1624181400L), Optional.ofNullable(actualFreeSlots.get(0).getEndDate()));
	}

}
