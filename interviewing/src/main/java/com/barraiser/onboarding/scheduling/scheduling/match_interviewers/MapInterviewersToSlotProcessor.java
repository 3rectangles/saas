/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.match_interviewers;

import com.barraiser.common.utilities.DateUtils;
import com.barraiser.onboarding.availability.AvailabilityManager;
import com.barraiser.onboarding.availability.DTO.BookedSlotDTO;
import com.barraiser.onboarding.dal.AvailabilityDAO;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class MapInterviewersToSlotProcessor implements MatchInterviewersProcessor {
	public static final Integer ONE_DAY_IN_SECONDS = 24 * 60 * 60;
	private final DateUtils dateUtils;
	private final AvailabilityManager availabilityManager;

	@Override
	public void process(final MatchInterviewersData data) {
		data.setInterviewersPerDayDataList(this.formatInterviewersToDateWiseInterviewers(data));
		final List<String> allInterviewerIds = data.getInterviewersId();
		allInterviewerIds.addAll(
				data.getDuplicateExperts().stream()
						.map(InterviewerData::getId)
						.collect(Collectors.toList()));
		data.setBookedSlotsPerInterviewer(
				this.availabilityManager.getBookedSlots(
						allInterviewerIds,
						data.getAvailabilityStartDate() - 7 * ONE_DAY_IN_SECONDS.longValue(),
						data.getAvailabilityEndDate()));

		data.setBookedInterviewingSlotsPerInterviewer(
				this.availabilityManager.getAllInterviewingBookedSlots(
						allInterviewerIds,
						data.getAvailabilityStartDate() - 7 * ONE_DAY_IN_SECONDS.longValue(),
						data.getAvailabilityEndDate()));

		data.setInterviewersPerDayDataList(
				this.findBookedSlotsForInterviewersForEachDate(
						data.getInterviewersPerDayDataList(), data.getBookedSlotsPerInterviewer(),
						data.getBookedInterviewingSlotsPerInterviewer(), data.getTimezone()));
		data.setSlotToAllInterviewers(this.fetchAvailableInterviewers(data));
	}

	private List<InterviewersPerDayData> formatInterviewersToDateWiseInterviewers(
			final MatchInterviewersData data) {

		final List<InterviewersPerDayData> interviewersPerDayDataList = new ArrayList<>();
		final String startDate = this.dateUtils.getFormattedDateString(
				data.getAvailabilityStartDate(), data.getTimezone(), DateUtils.DATE_IN_YYYY_MM_DD_FORMAT);
		final String endDate = this.dateUtils.getFormattedDateString(
				data.getAvailabilityEndDate(), data.getTimezone(), DateUtils.DATE_IN_YYYY_MM_DD_FORMAT);

		try {
			final SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.DATE_IN_YYYY_MM_DD_FORMAT);
			final long numberOfDays = Math.abs(sdf.parse(endDate).getTime() - sdf.parse(startDate).getTime())
					/ (86400 * 1000)
					+ 1;
			for (int i = 0; i < numberOfDays; i++) {
				final Calendar c = Calendar.getInstance();
				c.setTime(sdf.parse(startDate));
				c.add(Calendar.DATE, i);
				final InterviewersPerDayData interviewersPerDayData = new InterviewersPerDayData();
				interviewersPerDayData.setDate(sdf.format(c.getTime()));
				interviewersPerDayData.setInterviewers(
						data.getInterviewers().stream().collect(Collectors.toList()));
				interviewersPerDayDataList.add(interviewersPerDayData);
			}
		} catch (final ParseException e) {
			throw new IllegalArgumentException("Badly formatted date");
		}
		return interviewersPerDayDataList;
	}

	private List<InterviewersPerDayData> findBookedSlotsForInterviewersForEachDate(
			final List<InterviewersPerDayData> interviewersPerDayDataList,
			final Map<String, List<BookedSlotDTO>> bookedSlotsPerInterviewer,
			final Map<String, List<BookedSlotDTO>> bookedInterviewingSlotsPerInterviewer, final String timezone) {

		// TBD: Refactor. Confusing. Why are we deleting and inserting to same list. Why
		// can we not make it immutable and add to new list
		for (final InterviewersPerDayData interviewersPerDayData : new ArrayList<>(interviewersPerDayDataList)) {
			interviewersPerDayDataList.remove(interviewersPerDayData);
			final List<InterviewerData> interviewerList = interviewersPerDayData.getInterviewers();
			for (InterviewerData interviewer : new ArrayList<>(interviewersPerDayData.getInterviewers())) {
				interviewerList.remove(interviewer);
				final String interviewerId = interviewer.getId();
				final String date = interviewersPerDayData.getDate();
				interviewer = interviewer.toBuilder()
						.slotsBookedOnADay(
								this.countSlotsOnDate(bookedSlotsPerInterviewer.get(interviewerId), timezone, date))
						.interviewingSlotsBookedOnADay(this.countSlotsOnDate(
								bookedInterviewingSlotsPerInterviewer.get(interviewerId), timezone, date))
						.slotsBookedInAWeek(
								bookedSlotsPerInterviewer.get(interviewerId).stream()
										.filter(
												x -> this.isBookedSlotPresentInLastWeek(
														x.getStartDate(), date, timezone))
										.collect(Collectors.toList())
										.size())
						.interviewingSlotsBookedInAWeek(
								bookedInterviewingSlotsPerInterviewer.get(interviewerId).stream()
										.filter(
												x -> this.isBookedSlotPresentInLastWeek(
														x.getStartDate(), date, timezone))
										.collect(Collectors.toList())
										.size())
						.build();
				interviewerList.add(interviewer);
			}
			interviewersPerDayData.setInterviewers(interviewerList);
			interviewersPerDayDataList.add(interviewersPerDayData);
		}
		return interviewersPerDayDataList;
	}

	private int countSlotsOnDate(final List<BookedSlotDTO> slots, String timezone, String date) {
		return slots.stream()
				.filter(
						x -> (this.dateUtils
								.getFormattedDateString(
										x.getStartDate(),
										timezone,
										DateUtils.DATE_IN_YYYY_MM_DD_FORMAT)
								.equals(date)))
				.collect(Collectors.toList())
				.size();
	}

	private Map<Long, List<InterviewerData>> fetchAvailableInterviewers(
			final MatchInterviewersData data) {
		final List<AvailabilityDAO> freeSlots = this.availabilityManager.getAvailableSlotsOfAllInterviewers(
				data.getInterviewersId(),
				data.getAvailabilityStartDate(),
				data.getAvailabilityEndDate());
		final Map<Long, List<InterviewerData>> slotToInterviewers = new HashMap<>();
		for (AvailabilityDAO freeSlot : freeSlots) {
			slotToInterviewers.putAll(
					this.addAvailableExpertForSlot(freeSlot, data, slotToInterviewers));
		}
		return slotToInterviewers;
	}

	private Map<Long, List<InterviewerData>> addAvailableExpertForSlot(
			final AvailabilityDAO freeSlot,
			final MatchInterviewersData data,
			final Map<Long, List<InterviewerData>> slotToInterviewers) {
		log.info("All interviewer ids : {}", data.getInterviewers());
		log.info("free slot user : {}", freeSlot.getUserId());

		final Integer remainingNumberOfInterviews = this.getRemainingNumberOfInterviews(
				freeSlot, data.getBookedInterviewingSlotsPerInterviewer().get(freeSlot.getUserId()));
		final AvailabilityDAO freeSlotForInterview = freeSlot.toBuilder()
				.startDate(
						this.dateUtils.getEpochTo15ThMinuteCeil(
								freeSlot.getStartDate() - data.getExpertJoiningTime()))
				.build();
		final List<AvailabilityDAO> splitSlots = this.availabilityManager.splitSlots(
				freeSlotForInterview, data.getDurationOfInterview());
		for (final AvailabilityDAO slot : splitSlots) {
			final List<InterviewerData> interviewers = slotToInterviewers.getOrDefault(slot.getStartDate(),
					new ArrayList<>());
			interviewers.add(
					InterviewerData.builder()
							.id(slot.getUserId())
							.remainingNumberOfInterviews(remainingNumberOfInterviews)
							.build());
			slotToInterviewers.put(slot.getStartDate(), interviewers);
		}
		return slotToInterviewers;
	}

	private Integer getRemainingNumberOfInterviews(
			final AvailabilityDAO freeSlot, final List<BookedSlotDTO> bookedSlots) {
		final int noOfBookedSlotsInThatAvailabilitySlot = this.availabilityManager
				.getOverlappingBookedSlotsCount(freeSlot, bookedSlots);

		final int maxNoOfInterviewsThatInterviewerCanTake = freeSlot.getMaximumNumberOfInterviews() != null
				? freeSlot.getMaximumNumberOfInterviews()
				: 0;
		return maxNoOfInterviewsThatInterviewerCanTake - noOfBookedSlotsInThatAvailabilitySlot;
	}

	private boolean isBookedSlotPresentInLastWeek(final Long startDate, final String date, final String timezone) {
		final Long dayUnderConsideration = this.dateUtils.getDateStringInEpoch(date,
				DateUtils.DATE_IN_YYYY_MM_DD_FORMAT);
		final Long startOfToday = this.dateUtils.getStartOfDayEpochSecond(
				dayUnderConsideration, timezone == null ? DateUtils.TIMEZONE_ASIA_KOLKATA : timezone);
		return startDate >= startOfToday - 6L * ONE_DAY_IN_SECONDS
				&& startDate < startOfToday + ONE_DAY_IN_SECONDS;
	}
}
