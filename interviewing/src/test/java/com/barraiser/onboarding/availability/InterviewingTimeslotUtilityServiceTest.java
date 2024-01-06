/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.availability;

import com.barraiser.common.graphql.types.Interview;
import com.barraiser.onboarding.availability.DTO.InterviewingTimeSlot;
import com.barraiser.onboarding.dal.AvailabilityDAO;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class InterviewingTimeslotUtilityServiceTest {

	@InjectMocks
	private InterviewingTimeslotUtilityService interviewingTimeslotUtilityService;

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

	@Test
	public void shouldReturnEmpty() {
		Assert.assertEquals(0,
				this.interviewingTimeslotUtilityService.mergeSlots(new ArrayList<InterviewingTimeSlot>()).size());
	}

	@Test
	public void shouldNotMergeNonAdjacentSlots() {
		final List<InterviewingTimeSlot> slotsToMerge = Arrays.asList(InterviewingTimeSlot.builder()
				.userId("test_expert")
				.startTimeEpoch(1658723400l)
				.endTimeEpoch(1658727000l)
				.maxInterviews(1)
				.build(),
				InterviewingTimeSlot.builder()
						.userId("test_expert")
						.startTimeEpoch(1658727010l)
						.endTimeEpoch(1658730600l)
						.maxInterviews(1)
						.build());

		final List<InterviewingTimeSlot> mergedSlots = this.interviewingTimeslotUtilityService.mergeSlots(slotsToMerge);

		Assert.assertEquals(2, mergedSlots.size());
		Assert.assertEquals(1, mergedSlots.get(0).getMaxInterviews().longValue());
		Assert.assertEquals(1, mergedSlots.get(1).getMaxInterviews().longValue());
	}

	@Test
	public void shouldMergeSortedAdjacentSlots() {

		final List<InterviewingTimeSlot> slotsToMerge = Arrays.asList(InterviewingTimeSlot.builder()
				.userId("test_expert")
				.startTimeEpoch(1658723400l)
				.endTimeEpoch(1658727000l)
				.maxInterviews(1)
				.build(),
				InterviewingTimeSlot.builder()
						.userId("test_expert")
						.startTimeEpoch(1658727000l)
						.endTimeEpoch(1658730600l)
						.maxInterviews(1)
						.build());

		final List<InterviewingTimeSlot> mergedSlots = this.interviewingTimeslotUtilityService.mergeSlots(slotsToMerge);

		Assert.assertEquals(1, mergedSlots.size());
		Assert.assertEquals(1658730600l, mergedSlots.get(0).getEndTimeEpoch().longValue());
		Assert.assertEquals(2, mergedSlots.get(0).getMaxInterviews().longValue());
	}

	@Test
	public void shouldMergeUnsortedAdjacentSlots() {

		final List<InterviewingTimeSlot> slotsToMerge = Arrays.asList(
				InterviewingTimeSlot.builder()
						.userId("test_expert")
						.startTimeEpoch(1658727000l)
						.endTimeEpoch(1658730600l)
						.maxInterviews(1)
						.build(),
				InterviewingTimeSlot.builder()
						.userId("test_expert")
						.startTimeEpoch(1658723400l)
						.endTimeEpoch(1658727000l)
						.maxInterviews(1)
						.build());

		final List<InterviewingTimeSlot> mergedSlots = this.interviewingTimeslotUtilityService.mergeSlots(slotsToMerge);

		Assert.assertEquals(1, mergedSlots.size());
		Assert.assertEquals(1658730600l, mergedSlots.get(0).getEndTimeEpoch().longValue());
		Assert.assertEquals(2, mergedSlots.get(0).getMaxInterviews().longValue());
	}

	/**
	 * If N slots are adjacent to each other
	 * they should get merged into one slot.
	 */
	@Test
	public void shouldKeepMergingAllAdjacentSlots() {

		final List<InterviewingTimeSlot> slotsToMerge = Arrays.asList(
				InterviewingTimeSlot.builder()
						.userId("test_expert")
						.startTimeEpoch(1658727000l)
						.endTimeEpoch(1658730600l)
						.maxInterviews(1)
						.build(),
				InterviewingTimeSlot.builder()
						.userId("test_expert")
						.startTimeEpoch(1658731300l)
						.endTimeEpoch(1658732300l)
						.maxInterviews(1)
						.build(),
				InterviewingTimeSlot.builder()
						.userId("test_expert")
						.startTimeEpoch(1658732300l)
						.endTimeEpoch(1658733000l)
						.maxInterviews(1)
						.build(),
				InterviewingTimeSlot.builder()
						.userId("test_expert")
						.startTimeEpoch(1658730600l)
						.endTimeEpoch(1658730900l)
						.maxInterviews(1)
						.build(),
				InterviewingTimeSlot.builder()
						.userId("test_expert")
						.startTimeEpoch(1658730900l)
						.endTimeEpoch(1658731300l)
						.maxInterviews(1)
						.build());

		final List<InterviewingTimeSlot> mergedSlots = this.interviewingTimeslotUtilityService.mergeSlots(slotsToMerge);

		Assert.assertEquals(1, mergedSlots.size());
		Assert.assertEquals(1658733000l, mergedSlots.get(0).getEndTimeEpoch().longValue());
		Assert.assertEquals(5, mergedSlots.get(0).getMaxInterviews().longValue());
	}

	@Test
	public void maxInterviewsShouldNotCross5onMerging() {
		final List<InterviewingTimeSlot> slotsToMerge = Arrays.asList(
				InterviewingTimeSlot.builder()
						.userId("test_expert")
						.startTimeEpoch(1658727000l)
						.endTimeEpoch(1658730600l)
						.maxInterviews(2)
						.build(),
				InterviewingTimeSlot.builder()
						.userId("test_expert")
						.startTimeEpoch(1658730600l)
						.endTimeEpoch(1658730900l)
						.maxInterviews(3)
						.build(),
				InterviewingTimeSlot.builder()
						.userId("test_expert")
						.startTimeEpoch(1658730900l)
						.endTimeEpoch(1658731300l)
						.maxInterviews(1)
						.build());

		final List<InterviewingTimeSlot> mergedSlots = this.interviewingTimeslotUtilityService.mergeSlots(slotsToMerge);

		Assert.assertEquals(1, mergedSlots.size());
		Assert.assertEquals(1658731300l, mergedSlots.get(0).getEndTimeEpoch().longValue());
		Assert.assertEquals(5, mergedSlots.get(0).getMaxInterviews().longValue());
	}

	@Test
	public void shouldNotMergeOverlappingSlots() {

		final List<InterviewingTimeSlot> slotsToMerge = Arrays.asList(InterviewingTimeSlot.builder()
				.userId("test_expert")
				.startTimeEpoch(1658723400l)
				.endTimeEpoch(1658727000l)
				.maxInterviews(1)
				.build(),
				InterviewingTimeSlot.builder()
						.userId("test_expert")
						.startTimeEpoch(1658723400l)
						.endTimeEpoch(1658730600l)
						.maxInterviews(1)
						.build());

		final List<InterviewingTimeSlot> mergedSlots = this.interviewingTimeslotUtilityService.mergeSlots(slotsToMerge);
		Assert.assertEquals(2, mergedSlots.size());
	}

	/**
	 * Note that '*' is used to denote space or tab
	 * in each scenario. Because formatting realigns the dotted lines otherwise
	 */
	@Test
	public void shouldConfirmSlotsOverlapping() {

		/**
		 * -------------
		 * ------
		 */

		InterviewingTimeSlot s1 = InterviewingTimeSlot.builder()
				.userId("test_expert")
				.startTimeEpoch(1658723000l)
				.endTimeEpoch(1658724000l)
				.build();

		InterviewingTimeSlot s2 = InterviewingTimeSlot.builder()
				.userId("test_expert")
				.startTimeEpoch(1658723000l)
				.endTimeEpoch(1658723500l)
				.build();

		Assert.assertEquals(Boolean.TRUE, this.interviewingTimeslotUtilityService.isOverlapping(s1, s2));

		/**
		 * -------------
		 * -----------------
		 */

		s1 = InterviewingTimeSlot.builder()
				.userId("test_expert")
				.startTimeEpoch(1658723000l)
				.endTimeEpoch(1658723500l)
				.build();

		s2 = InterviewingTimeSlot.builder()
				.userId("test_expert")
				.startTimeEpoch(1658723000l)
				.endTimeEpoch(1658724000l)
				.build();

		Assert.assertEquals(Boolean.TRUE, this.interviewingTimeslotUtilityService.isOverlapping(s1, s2));

		/**
		 * --------------
		 ******** --------------
		 */

		s1 = InterviewingTimeSlot.builder()
				.userId("test_expert")
				.startTimeEpoch(1658723000l)
				.endTimeEpoch(1658724000l)
				.build();

		s2 = InterviewingTimeSlot.builder()
				.userId("test_expert")
				.startTimeEpoch(1658723500l)
				.endTimeEpoch(1658725000l)
				.build();

		Assert.assertEquals(Boolean.TRUE, this.interviewingTimeslotUtilityService.isOverlapping(s1, s2));

		/**
		 * ------------
		 * ------------
		 */

		s1 = InterviewingTimeSlot.builder()
				.userId("test_expert")
				.startTimeEpoch(1658723000l)
				.endTimeEpoch(1658724000l)
				.build();

		s2 = InterviewingTimeSlot.builder()
				.userId("test_expert")
				.startTimeEpoch(1658723000l)
				.endTimeEpoch(1658724000l)
				.build();

		Assert.assertEquals(Boolean.TRUE, this.interviewingTimeslotUtilityService.isOverlapping(s1, s2));

		/**
		 * --------------------
		 ******** ---------
		 */

		s1 = InterviewingTimeSlot.builder()
				.userId("test_expert")
				.startTimeEpoch(1658723000l)
				.endTimeEpoch(1658724000l)
				.build();

		s2 = InterviewingTimeSlot.builder()
				.userId("test_expert")
				.startTimeEpoch(1658723500l)
				.endTimeEpoch(1658723700l)
				.build();

		Assert.assertEquals(Boolean.TRUE, this.interviewingTimeslotUtilityService.isOverlapping(s1, s2));

		/**
		 ****** ----------
		 * ------------------
		 */

		s1 = InterviewingTimeSlot.builder()
				.userId("test_expert")
				.startTimeEpoch(1658723500l)
				.endTimeEpoch(1658723700l)
				.build();

		s2 = InterviewingTimeSlot.builder()
				.userId("test_expert")
				.startTimeEpoch(1658723000l)
				.endTimeEpoch(1658724000l)
				.build();

		Assert.assertEquals(Boolean.TRUE, this.interviewingTimeslotUtilityService.isOverlapping(s1, s2));

	}

	/**
	 * Note that '*' is used to denote space or tab
	 * in each scenario. Because formatting realigns the dotted lines otherwise
	 */
	@Test
	public void shouldDenySlotsOverlapping() {

		/**
		 * --------
		 ************* ---------
		 */

		InterviewingTimeSlot s1 = InterviewingTimeSlot.builder()
				.userId("test_expert")
				.startTimeEpoch(1658723000l)
				.endTimeEpoch(1658724000l)
				.build();

		InterviewingTimeSlot s2 = InterviewingTimeSlot.builder()
				.userId("test_expert")
				.startTimeEpoch(1658724500l)
				.endTimeEpoch(1658727000l)
				.build();

		Assert.assertEquals(Boolean.FALSE, this.interviewingTimeslotUtilityService.isOverlapping(s1, s2));

		/**
		 ************* -----------
		 * ---------
		 */

		s1 = InterviewingTimeSlot.builder()
				.userId("test_expert")
				.startTimeEpoch(1658724500l)
				.endTimeEpoch(1658727000l)
				.build();

		s2 = InterviewingTimeSlot.builder()
				.userId("test_expert")
				.startTimeEpoch(1658723000l)
				.endTimeEpoch(1658724000l)
				.build();

		Assert.assertEquals(Boolean.FALSE, this.interviewingTimeslotUtilityService.isOverlapping(s1, s2));

		/**
		 * -------
		 ******** -------
		 */

		s1 = InterviewingTimeSlot.builder()
				.userId("test_expert")
				.startTimeEpoch(1658723000l)
				.endTimeEpoch(1658724000l)
				.build();

		s2 = InterviewingTimeSlot.builder()
				.userId("test_expert")
				.startTimeEpoch(1658724000l)
				.endTimeEpoch(1658725000l)
				.build();

		Assert.assertEquals(Boolean.FALSE, this.interviewingTimeslotUtilityService.isOverlapping(s1, s2));
	}

	@Test
	public void shouldNotReturnIdenticalSlots() {
		/**
		 * Only identical slots
		 */
		List<InterviewingTimeSlot> interviewingTimeSlots = Arrays.asList(
				InterviewingTimeSlot.builder()
						.id(1L)
						.userId("user1")
						.startTimeEpoch(100L)
						.endTimeEpoch(200L)
						.maxInterviews(2)
						.build(),
				InterviewingTimeSlot.builder()
						.id(1L)
						.userId("user1")
						.startTimeEpoch(100L)
						.endTimeEpoch(200L)
						.maxInterviews(2)
						.build());

		Assert.assertEquals(1,
				this.interviewingTimeslotUtilityService.getOverlappingSlots(interviewingTimeSlots).size());

		/**
		 * One slot overlapping with two identical slots
		 */
		interviewingTimeSlots = Arrays.asList(
				InterviewingTimeSlot.builder()
						.id(1L)
						.userId("user1")
						.startTimeEpoch(100L)
						.endTimeEpoch(200L)
						.maxInterviews(2)
						.build(),
				InterviewingTimeSlot.builder()
						.id(1L)
						.userId("user1")
						.startTimeEpoch(100L)
						.endTimeEpoch(200L)
						.maxInterviews(2)
						.build(),
				InterviewingTimeSlot.builder()
						.id(1L)
						.userId("user1")
						.startTimeEpoch(50L)
						.endTimeEpoch(150L)
						.maxInterviews(2)
						.build());

		Assert.assertEquals(2,
				this.interviewingTimeslotUtilityService.getOverlappingSlots(interviewingTimeSlots).size());

	}

	@Test
	public void shouldNotReturnOverlappingForAdjacentSlots() {
		List<InterviewingTimeSlot> interviewingTimeSlots = Arrays.asList(
				InterviewingTimeSlot.builder()
						.id(1L)
						.userId("user1")
						.startTimeEpoch(100L)
						.endTimeEpoch(200L)
						.maxInterviews(2)
						.build(),
				InterviewingTimeSlot.builder()
						.id(1L)
						.userId("user1")
						.startTimeEpoch(200L)
						.endTimeEpoch(300L)
						.maxInterviews(2)
						.build());

		Assert.assertEquals(0,
				this.interviewingTimeslotUtilityService.getOverlappingSlots(interviewingTimeSlots).size());
	}

	@Test
	public void shouldReturnOverlappingSlots() {
		List<InterviewingTimeSlot> interviewingTimeSlots = Arrays.asList(
				InterviewingTimeSlot.builder()
						.id(1L)
						.userId("user1")
						.startTimeEpoch(100L)
						.endTimeEpoch(200L)
						.maxInterviews(2)
						.build(),
				InterviewingTimeSlot.builder()
						.userId("user1")
						.startTimeEpoch(150L)
						.endTimeEpoch(300L)
						.maxInterviews(2)
						.build());

		Assert.assertEquals(2,
				this.interviewingTimeslotUtilityService.getOverlappingSlots(interviewingTimeSlots).size());

	}

	/**
	 * Three slots consecutively overlapping
	 */
	@Test
	public void shouldReturnOverlappingSlots_1() {
		List<InterviewingTimeSlot> interviewingTimeSlots = Arrays.asList(
				InterviewingTimeSlot.builder()
						.id(1L)
						.userId("user1")
						.startTimeEpoch(100L)
						.endTimeEpoch(200L)
						.maxInterviews(2)
						.build(),
				InterviewingTimeSlot.builder()
						.userId("user1")
						.startTimeEpoch(150L)
						.endTimeEpoch(300L)
						.maxInterviews(2)
						.build(),
				InterviewingTimeSlot.builder()
						.userId("user1")
						.startTimeEpoch(250L)
						.endTimeEpoch(400L)
						.maxInterviews(2)
						.build());

		Assert.assertEquals(3,
				this.interviewingTimeslotUtilityService.getOverlappingSlots(interviewingTimeSlots).size());
	}
}
