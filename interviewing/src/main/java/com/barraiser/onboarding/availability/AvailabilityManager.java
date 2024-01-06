/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.availability;

import com.amazonaws.util.CollectionUtils;
import com.barraiser.common.enums.DayOfTheWeek;
import com.barraiser.onboarding.availability.DTO.BookedSlotDTO;
import com.barraiser.onboarding.availability.DTO.GetBookedSlotsRequestDTO;
import com.barraiser.onboarding.availability.DTO.InterviewingTimeSlot;
import com.barraiser.onboarding.availability.enums.BookingSource;
import com.barraiser.onboarding.availability.exception.SlotNotAvailableException;
import com.barraiser.common.utilities.DateUtils;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.InterviewService;
import com.barraiser.onboarding.scheduling.scheduling.SchedulingProcessingData;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Availability might
 */
@Log4j2
@Component
@AllArgsConstructor
public class AvailabilityManager {
	private final long GAP_BETWEEN_SUCCESSIVE_SLOTS_IN_MINUTES = 15L;
	private final Double BIAS_FOR_SHIFT_MATCHING_TA = 0.9D;
	private final Double SHIFT_MATCH_BUFFER_TA = 0.1D;
	private final Long BUFFER_TA_SLOT = 1800L;

	private final AvailabilityRepository availabilityRepository;
	private final BookedSlotRepository bookedSlotRepository;
	private final DateUtils dateUtils;
	private final InterViewRepository interViewRepository;
	private final InterviewService interviewService;
	private final RecurringAvailabilityManager recurringAvailabilityManager;
	private final AvailabilityConsolidator availabilityConsolidator;
	private final AvailabilityServiceClient availabilityServiceClient;
	private final InterviewingTimeslotUtilityService interviewingTimeslotUtilityService;

	/**
	 * Returns consolidated user availability that includes custom
	 * avaialability given by user over availability tools + working hours
	 * (recurring
	 * availability provided)
	 *
	 * @param userId
	 * @param availabilityStartDate
	 * @param availabilityEndDate
	 */
	private List<AvailabilityDAO> getConsolidatedAvailabilityForUser(final String userId,
			final Long availabilityStartDate,
			final Long availabilityEndDate) {

		final List<AvailabilityDAO> customUserAvailabilities = this.availabilityRepository
				.findByUserIdAndStartDateLessThanAndEndDateGreaterThan(userId, availabilityEndDate,
						availabilityStartDate);

		final Map<DayOfTheWeek, List<RecurringAvailabilityDAO>> daywiseConsolidatedRecurringSlots = this.recurringAvailabilityManager
				.getDaywiseRecurringAvailabilities(userId);

		/**
		 * TBD : Address timezoning support. We need timezone to convert recurring slot
		 * into epoch
		 * which comes from recurring slot table itself. Hardcoded : ASIA/KOLKATA for
		 * now.
		 */
		final List<AvailabilityDAO> consolidatedAvailabilitySlots = this.availabilityConsolidator
				.consolidateAvailabilites(customUserAvailabilities, daywiseConsolidatedRecurringSlots,
						availabilityStartDate,
						availabilityEndDate);

		// This is to slice the availabilies from the ends to keep them inside the
		// boundary of availabilityStartDate and availabilityEndDate.
		final List<AvailabilityDAO> shrinkedConsolidatedAvailabilities = this.shrinkAvailabilitiesIfNeeded(
				consolidatedAvailabilitySlots, availabilityStartDate, availabilityEndDate);
		return shrinkedConsolidatedAvailabilities;
	}

	/**
	 * @param userId
	 * @param availabilityStartDate
	 * @param availabilityEndDate
	 * @return
	 */
	public List<AvailabilityDAO> getCustomAvailabilitiesForUser(final String userId,
			final Long availabilityStartDate,
			final Long availabilityEndDate, final Long slotSize) {
		final List<AvailabilityDAO> customUserAvailabilities = this.availabilityRepository
				.findByUserIdAndStartDateLessThanAndEndDateGreaterThan(userId, availabilityEndDate,
						availabilityStartDate);

		final List<AvailabilityDAO> shrinkedAvailabilities = this.shrinkAvailabilitiesIfNeeded(
				customUserAvailabilities, availabilityStartDate, availabilityEndDate);

		return shrinkedAvailabilities.stream()
				.map(x -> this.breakIntoSlots(x, slotSize))
				.flatMap(Collection::stream)
				.collect(Collectors.toList());

	}

	/**
	 * @param userId
	 * @param availabilityStartDate
	 * @param availabilityEndDate
	 * @return
	 */
	public List<AvailabilityDAO> getExtrapolatedRecurringAvailabilitiesForUser(final String userId,
			final Long availabilityStartDate,
			final Long availabilityEndDate, final Long slotSize) {

		final Map<DayOfTheWeek, List<RecurringAvailabilityDAO>> daywiseConsolidatedRecurringSlots = this.recurringAvailabilityManager
				.getDaywiseRecurringAvailabilities(userId);

		final List<AvailabilityDAO> extrapolatedRecurringAvailabilitiesList = this.availabilityConsolidator
				.extrapolateRecurringAvailabilitiesOverTimerange(daywiseConsolidatedRecurringSlots,
						availabilityStartDate,
						availabilityEndDate);

		final List<AvailabilityDAO> mergedAvailabilities = this.availabilityConsolidator
				.mergeAvailabilities(extrapolatedRecurringAvailabilitiesList);

		final List<AvailabilityDAO> shrinkedAvailabilities = this.shrinkAvailabilitiesIfNeeded(
				mergedAvailabilities, availabilityStartDate, availabilityEndDate);

		return shrinkedAvailabilities.stream()
				.map(x -> this.breakIntoSlots(x, slotSize))
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
	}

	/**
	 * This method is used to delete overlapping custom availabilities.
	 * Intent is to delete custom availabilities overlapping with
	 * recurring availabilities.
	 * But even if custom availabilities overlap with custom availabilities they
	 * will get deleted. (This case is actually not possible since we
	 * prevent overlapping of custom availabilities at the time of addition of
	 * availability slots)
	 */
	public void deleteOverlappingCustomAvailabilities(final String userId,
			final Map<DayOfTheWeek, List<RecurringAvailabilityDAO>> daywiseRecurringAvailabilities,
			final Long windowStartDate,
			final Long windowEndDate) {

		final List<AvailabilityDAO> overlappingCustomAvailabilities = this
				.getOverlappingCustomAvailabilities(userId, daywiseRecurringAvailabilities, windowStartDate,
						windowEndDate);

		final List<Long> customSlotIds = overlappingCustomAvailabilities.stream().map(AvailabilityDAO::getId)
				.collect(Collectors.toList());

		log.info("Deleting availabilities : {} ", overlappingCustomAvailabilities);
		this.availabilityRepository.deleteByIdIn(customSlotIds);
	}

	/**
	 * @param daywiseRecurringAvailabilities
	 * @param availabilityStart
	 * @param availabilityEnd
	 * @return
	 */
	public List<AvailabilityDAO> getOverlappingCustomAvailabilities(final String userId,
			final Map<DayOfTheWeek, List<RecurringAvailabilityDAO>> daywiseRecurringAvailabilities,
			final Long availabilityStart, final Long availabilityEnd) {

		final List<AvailabilityDAO> customAvailabilities = this.getCustomAvailabilitiesForUser(userId,
				availabilityStart, availabilityEnd, -1L);

		final List<AvailabilityDAO> extrapolateRecurringAvailabilitiesOverTimerange = this.availabilityConsolidator
				.extrapolateRecurringAvailabilitiesOverTimerange(daywiseRecurringAvailabilities, availabilityStart,
						availabilityEnd);

		final List<InterviewingTimeSlot> timeSlotsToBeCheckedForOverlapping = customAvailabilities.stream()
				.map(x -> this.interviewingTimeslotUtilityService.toInterviewingTimeslot(x))
				.collect(Collectors.toList());

		timeSlotsToBeCheckedForOverlapping.addAll(
				extrapolateRecurringAvailabilitiesOverTimerange.stream()
						.map(x -> this.interviewingTimeslotUtilityService.toInterviewingTimeslot(x))
						.collect(Collectors.toList()));

		return this.interviewingTimeslotUtilityService.getOverlappingSlots(timeSlotsToBeCheckedForOverlapping).stream()
				.filter(x -> x.getId() != null)
				.map(x -> this.interviewingTimeslotUtilityService.toAvailabilityDAO(x))
				.collect(Collectors.toList()); // recurring slots will not have id.
	}

	/**
	 * Returns consolidated availability of ALL USERS provided. that includes custom
	 * availability given by user over availability tools + working hours
	 * (recurring availability)
	 * availability provided)
	 *
	 * @param userIds
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	private List<AvailabilityDAO> getConsolidatedAvailabilityForAllUsers(final List<String> userIds,
			final Long startDate,
			final Long endDate) {

		final List<AvailabilityDAO> customUserAvailabilities = this.availabilityRepository
				.findByUserIdInAndStartDateLessThanAndEndDateGreaterThan(userIds, endDate, startDate);

		final Map<String, List<AvailabilityDAO>> userToCustomAvailabilitiesMap = this
				.getUserToCustomAvailabilitiesMapping(customUserAvailabilities);
		final List<AvailabilityDAO> consolidatedAvailabilities = new ArrayList<>();

		for (String userId : userIds) {
			final List<AvailabilityDAO> customAvailabilitiesForUser = userToCustomAvailabilitiesMap.getOrDefault(userId,
					new ArrayList<>());
			final Map<DayOfTheWeek, List<RecurringAvailabilityDAO>> daywiseConsolidatedRecurringSlotsOfUser = this.recurringAvailabilityManager
					.getDaywiseRecurringAvailabilities(userId);

			final List<AvailabilityDAO> consolidatedAvailabilitySlotsForUser = this.availabilityConsolidator
					.consolidateAvailabilites(customAvailabilitiesForUser, daywiseConsolidatedRecurringSlotsOfUser,
							startDate,
							endDate);

			consolidatedAvailabilities.addAll(consolidatedAvailabilitySlotsForUser);
		}
		return consolidatedAvailabilities;
	}

	private List<AvailabilityDAO> shrinkAvailabilitiesIfNeeded(final List<AvailabilityDAO> availabilities,
			final Long availabilityStartDate, final Long availabilityEndDate) {

		// copying all values into this list to mantain immutability
		final List<AvailabilityDAO> consolidatedAvailabilities = availabilities.stream().collect(Collectors.toList());

		final List<AvailabilityDAO> availabilitiesToBeShrunk = consolidatedAvailabilities.stream()
				.filter(x -> x.getStartDate() < availabilityStartDate || x.getEndDate() > availabilityEndDate)
				.collect(Collectors.toList());
		consolidatedAvailabilities.removeAll(availabilitiesToBeShrunk);

		if (!availabilitiesToBeShrunk.isEmpty()) {
			consolidatedAvailabilities.addAll(
					this.shrinkAvailabilities(availabilitiesToBeShrunk, availabilityStartDate, availabilityEndDate));
		}

		return consolidatedAvailabilities;
	}

	private Map<String, List<AvailabilityDAO>> getUserToCustomAvailabilitiesMapping(
			final List<AvailabilityDAO> customUserAvailabilities) {
		final Map<String, List<AvailabilityDAO>> userToCustomAvailabilitiesMap = new HashMap<>();

		for (AvailabilityDAO availabilityDAO : customUserAvailabilities) {
			final List<AvailabilityDAO> customAvailabilitiesForUser = userToCustomAvailabilitiesMap
					.computeIfAbsent(availabilityDAO.getUserId(), x -> new ArrayList<>());

			customAvailabilitiesForUser.add(availabilityDAO);
		}
		return userToCustomAvailabilitiesMap;
	}

	private void ensureNoOverlappingBookedSlot(final String userId, final Long startDate, final Long endDate) {

		final List<BookedSlotDTO> bookedSlots = this.availabilityServiceClient
				.getBookedSlots(GetBookedSlotsRequestDTO.builder()
						.userIds(List.of(userId))
						.startDate(startDate)
						.endDate(endDate)
						.excludeBufferForOverlappingCheck(false)
						.overlappingType(GetBookedSlotsRequestDTO.OverlappingType.PARTIAL)
						.build())
				.get(userId);
		if (bookedSlots.size() > 0) {
			throw new SlotNotAvailableException("Overlapping booked slot found, please choose another slot.");
		}
	}

	@Transactional
	public void addASlot(final String userId, final Long startDate, final Long endTime,
			final Integer maximumNumberOfInterviews) {

		final Integer maxInterviewsThatInterviewerCanTake = (int) (endTime - startDate) / (60 * 60);
		final Integer maximumInterviews = ((float) (endTime - startDate) / (60 * 60) <= 2f ? 1
				: maximumNumberOfInterviews);
		if (maximumInterviews > maxInterviewsThatInterviewerCanTake) {
			throw new IllegalArgumentException("Can take maximum " + maxInterviewsThatInterviewerCanTake
					+ " interviews only for the given period");
		}

		final List<AvailabilityDAO> overlappingAvailabilitySlots = this.availabilityRepository
				.findByUserIdAndStartDateLessThanAndEndDateGreaterThan(userId, endTime, startDate);
		if (!overlappingAvailabilitySlots.isEmpty()) {
			throw new IllegalArgumentException("overlapping slot timings");
		}

		final AvailabilityDAO rightAdjacentSlot = this.availabilityRepository.findByUserIdAndStartDate(userId, endTime);
		final AvailabilityDAO leftAdjacentSlot = this.availabilityRepository.findByUserIdAndEndDate(userId, startDate);
		final Long mergedStartDate = leftAdjacentSlot != null ? leftAdjacentSlot.getStartDate() : startDate;
		final Long mergedEndDate = rightAdjacentSlot != null ? rightAdjacentSlot.getEndDate() : endTime;
		final Integer mergedMaxInterviews = Math.min(
				(leftAdjacentSlot != null ? leftAdjacentSlot.getMaximumNumberOfInterviews() : 0)
						+ maximumInterviews
						+ (rightAdjacentSlot != null ? rightAdjacentSlot.getMaximumNumberOfInterviews() : 0),
				5);
		this.availabilityRepository.save(AvailabilityDAO.builder()
				.userId(userId)
				.startDate(mergedStartDate)
				.endDate(mergedEndDate)
				.maximumNumberOfInterviews(mergedMaxInterviews)
				.build());
		if (leftAdjacentSlot != null) {
			this.availabilityRepository.delete(leftAdjacentSlot);
		}
		if (rightAdjacentSlot != null) {
			this.availabilityRepository.delete(rightAdjacentSlot);
		}
	}

	public int getOverlappingBookedSlotsCount(final AvailabilityDAO availabilityDAO,
			final List<BookedSlotDTO> bookedSlots) {
		return (int) bookedSlots.stream()
				.filter(x -> ((availabilityDAO.getStartDate() >= x.getStartDate()
						&& availabilityDAO.getStartDate() < x.getEndDate())
						||
						(availabilityDAO.getStartDate() < x.getStartDate()
								&& availabilityDAO.getEndDate() > x.getStartDate())

				)).count();
	}

	private List<AvailabilityDAO> breakIntoSlots(final AvailabilityDAO availabilityDAO, final Long slotSizeInMinutes) {
		if (slotSizeInMinutes == -1) {
			return Arrays.asList(availabilityDAO);
		}

		long nextSlotStartDate = availabilityDAO.getStartDate();
		long nextSlotEndTime = availabilityDAO.getStartDate() + slotSizeInMinutes * 60;
		final List<AvailabilityDAO> availabilities = new ArrayList<>();
		while (nextSlotEndTime <= availabilityDAO.getEndDate()) {
			availabilities.add(availabilityDAO.toBuilder()
					.startDate(nextSlotStartDate)
					.endDate(nextSlotEndTime)
					.build());
			nextSlotStartDate = nextSlotEndTime;
			nextSlotEndTime += slotSizeInMinutes * 60;
		}
		return availabilities;
	}

	/**
	 * Books slot even if the interviewer is not available. However, only books when
	 * there is no other booking slot clashing
	 */
	public void bookSlotNeedlessAvailability(final String userId, final String bookedByUser, final Long startDate,
			final Long endTime, final Long buffer) {
		final Long startDateWithBuffer = startDate - buffer;
		final Long endTimeWithBuffer = endTime + buffer;
		if (startDate >= endTime) {
			throw new SlotNotAvailableException("Given time slot has end time before start time");
		}

		this.ensureNoOverlappingBookedSlot(userId, startDate, endTime);
		this.bookedSlotRepository.save(BookedSlotsDAO.builder()
				.userId(userId)
				.bookedBy(bookedByUser)
				.startDate(startDateWithBuffer)
				.endDate(endTimeWithBuffer)
				.ttl(Instant.now().getEpochSecond() + 20 * 60)
				.buffer(buffer)
				.source(BookingSource.BARRAISER_TOOL)
				.build());
	}

	/**
	 * Frees a booking slot
	 */
	public void freeBookedSlot(final BookedSlotDTO bookedSlot) {
		if (bookedSlot != null)
			this.availabilityServiceClient.deleteBookedSlot(bookedSlot.getId());
	}

	public void removeBufferInBookedSlot(final BookedSlotDTO bookedSlot) {
		if (bookedSlot != null)
			this.availabilityServiceClient.removeBookedSlotBuffer(bookedSlot.getId());
	}

	public List<AvailabilityDAO> splitAvailableSlotAndFilterByBookedSlots(final AvailabilityDAO freeSlot,
			final List<BookedSlotDTO> bookedSlots, final Long slotSizeInMinutes) {

		return this.splitSlots(freeSlot, slotSizeInMinutes)
				.stream()
				.filter(x -> !(this.getOverlappingBookedSlotsCount(x, bookedSlots) > 0))
				.collect(Collectors.toList());
	}

	public List<AvailabilityDAO> splitSlots(final AvailabilityDAO availabilityDAO, final Long slotSizeInMinutes) {

		if (slotSizeInMinutes < 0) {
			throw new IllegalArgumentException("slot size cannot be negative");
		}

		long nextSlotStartDate = this.dateUtils.getEpochTo15ThMinuteCeil(availabilityDAO.getStartDate());
		long nextSlotEndTime = availabilityDAO.getStartDate() + slotSizeInMinutes * 60;
		final List<AvailabilityDAO> availabilities = new ArrayList<>();
		while (nextSlotEndTime <= availabilityDAO.getEndDate()) {
			availabilities.add(availabilityDAO.toBuilder()
					.userId(availabilityDAO.getUserId())
					.startDate(nextSlotStartDate)
					.endDate(nextSlotEndTime)
					.build());
			nextSlotStartDate += this.GAP_BETWEEN_SUCCESSIVE_SLOTS_IN_MINUTES * 60;
			nextSlotEndTime = nextSlotStartDate + slotSizeInMinutes * 60;
		}
		return availabilities;
	}

	/**
	 * This includes all kinds of booked slots including slots that are booked for
	 * interviewing,
	 * busy (google calendar) etc
	 *
	 * @param userIds
	 * @param availabilityStartDate
	 * @param availabilityEndDate
	 * @return
	 */
	public Map<String, List<BookedSlotDTO>> getBookedSlots(final List<String> userIds,
			final Long availabilityStartDate, final Long availabilityEndDate) {

		log.info("List of userids : {}", userIds);
		return this.availabilityServiceClient.getBookedSlots(GetBookedSlotsRequestDTO.builder()
				.userIds(userIds)
				.startDate(availabilityStartDate)
				.excludeBufferForOverlappingCheck(false)
				.overlappingType(GetBookedSlotsRequestDTO.OverlappingType.PARTIAL)
				.endDate(availabilityEndDate)
				.build());
	}

	/**
	 * This includes only slots that are booked for interviewing.
	 *
	 * @param userIds
	 * @param availabilityStartDate
	 * @param availabilityEndDate
	 * @return
	 */
	public Map<String, List<BookedSlotDTO>> getAllInterviewingBookedSlots(final List<String> userIds,
			final Long availabilityStartDate, final Long availabilityEndDate) {
		return this.availabilityServiceClient.getBookedSlots(GetBookedSlotsRequestDTO.builder()
				.userIds(userIds)
				.startDate(availabilityStartDate)
				.endDate(availabilityEndDate)
				.overlappingType(GetBookedSlotsRequestDTO.OverlappingType.PARTIAL)
				.excludeBufferForOverlappingCheck(false)
				.source(BookingSource.BARRAISER_TOOL)
				.build());
	}

	public List<String> filterBookedUsersWithinTimeFrame(final Long availabilityStartDate,
			final Long availabilityEndDate) {
		final List<BookedSlotsDAO> bookedSlots = this.bookedSlotRepository
				.findAllByStartDateGreaterThanEqualAndStartDateLessThanAndDeletedOnIsNull(availabilityStartDate,
						availabilityEndDate);
		bookedSlots.addAll(this.bookedSlotRepository.findByStartDateLessThanAndEndDateGreaterThanAndDeletedOnIsNull(
				availabilityStartDate,
				availabilityStartDate));
		return bookedSlots.stream().map(x -> x.getUserId()).collect(Collectors.toList());
	}

	public List<AvailabilityDAO> getAvailableSlotsOfAllInterviewers(final List<String> userIds,
			final Long availabilityStartDate, final Long availabilityEndDate) {

		final List<AvailabilityDAO> availabilities = this.getConsolidatedAvailabilityForAllUsers(userIds,
				availabilityStartDate, availabilityEndDate);
		final List<AvailabilityDAO> shrinkedAvailabilities = this.shrinkAvailabilitiesIfNeeded(availabilities,
				availabilityStartDate, availabilityEndDate);

		return shrinkedAvailabilities;
	}

	public AvailabilityDAO findRandomAvailableSlotForTimeFrame(final Long startDate, final Long endDate,
			final String role) {
		final List<String> bookedUserIds = this.filterBookedUsersWithinTimeFrame(startDate, endDate);
		bookedUserIds.add("");
		Integer count;
		final List<AvailabilityDAO> availabilityDAOList = this.availabilityRepository
				.findAllByStartDateLessThanEqualAndEndDateGreaterThanEqualAndRoleAndUserIdNotIn(startDate, endDate,
						role, bookedUserIds);
		if (CollectionUtils.isNullOrEmpty(availabilityDAOList)) {
			log.info("No Ta availability found for interview startDate: {} , endDate: {} ", startDate, endDate);
			return null;
		}

		final List<AvailabilityDAO> availabilityShrinked = this
				.readjustMaxCountsForAvailabilities(availabilityDAOList, startDate, endDate).stream()
				.filter(x -> Objects.nonNull(x.getMaximumNumberOfInterviews())
						&& x.getMaximumNumberOfInterviews() > 0)
				.sorted(Comparator.comparing(x -> x.getStartDate())).collect(Collectors.toList());
		count = availabilityShrinked.size();
		if (count == 0) {
			return null;
		}
		double rand = this.generateIndexCoefficientWithBias();
		final int idx = (int) (rand * count);
		return availabilityShrinked.get(idx);
	}

	private double generateIndexCoefficientWithBias() {
		double rand = Math.random();
		double res;
		if (rand < this.BIAS_FOR_SHIFT_MATCHING_TA) {
			final double lowerBound = (1D - this.SHIFT_MATCH_BUFFER_TA);
			res = ThreadLocalRandom.current().nextDouble(lowerBound, 1D);

		} else {
			final double upperBound = (1D - this.SHIFT_MATCH_BUFFER_TA);
			res = ThreadLocalRandom.current().nextDouble(0D, upperBound);
		}
		return res;
	}

	/**
	 * This method returns all available user slots where
	 * the user has declared availability ie does not consider booked slots.
	 *
	 * @param userId
	 * @param startDate
	 * @param endDate
	 * @param slotSize
	 * @return
	 */
	public List<AvailabilityDAO> getAllAvailableSlots(final String userId, final Long startDate, final Long endDate,
			final Long slotSize) {
		final List<AvailabilityDAO> freeSlots = this.getConsolidatedAvailabilityForUser(userId, startDate, endDate);

		if (freeSlots.size() == 0) {
			return Collections.emptyList();
		}

		return freeSlots.stream()
				.map(x -> this.breakIntoSlots(x, slotSize))
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
	}

	/**
	 * We have multiple kinds of booked slots now
	 * for interviewing , busy slots from calendar etc.
	 * <p>
	 * Returns the slots booked for taking interviews.
	 *
	 * @param userId
	 * @param startDate
	 * @return
	 */
	public BookedSlotDTO findInterviewingBookedSlot(final String userId, final Long startDate, final Long endDate) {
		final List<BookedSlotDTO> bookedSlotDTOs = this.availabilityServiceClient
				.getBookedSlots(GetBookedSlotsRequestDTO.builder()
						.userIds(List.of(userId))
						.startDate(startDate)
						.endDate(endDate)
						.excludeBufferForOverlappingCheck(true)
						.overlappingType(GetBookedSlotsRequestDTO.OverlappingType.EXACT)
						.source(BookingSource.BARRAISER_TOOL)
						.build())
				.get(userId);
		return bookedSlotDTOs.size() > 0 ? bookedSlotDTOs.get(0) : null;
	}

	private List<AvailabilityDAO> shrinkAvailabilities(List<AvailabilityDAO> availabilityDAOs, final Long startDate,
			final Long endDate) {
		final Long minStartDate = availabilityDAOs.stream().mapToLong(AvailabilityDAO::getStartDate).min()
				.orElseThrow();
		final Long maxEndDate = availabilityDAOs.stream().mapToLong(AvailabilityDAO::getEndDate).max().orElseThrow();
		final List<String> userIds = availabilityDAOs.stream().map(AvailabilityDAO::getUserId).distinct()
				.collect(Collectors.toList());
		final Map<String, List<BookedSlotDTO>> interviewerToBookedSlots = this.getBookedSlots(userIds, minStartDate,
				maxEndDate);
		availabilityDAOs = availabilityDAOs.stream().map(
				f -> {
					final List<BookedSlotDTO> bookedSlots = interviewerToBookedSlots.get(f.getUserId());
					if (f.getStartDate() < startDate) {
						final Integer numberOfOverlappingBookedSlots = this
								.getOverlappingBookedSlotsCount(f.toBuilder().endDate(startDate).build(), bookedSlots);
						f = f.toBuilder().startDate(startDate).maximumNumberOfInterviews(
								f.getMaximumNumberOfInterviews() - numberOfOverlappingBookedSlots).build();
					}
					if (f.getEndDate() > endDate) {
						final Integer numberOfOverlappingBookedSlots = this
								.getOverlappingBookedSlotsCount(f.toBuilder().startDate(endDate).build(), bookedSlots);
						f = f.toBuilder().endDate(endDate).maximumNumberOfInterviews(
								f.getMaximumNumberOfInterviews() - numberOfOverlappingBookedSlots).build();
					}
					return f;
				}).collect(Collectors.toList());
		return availabilityDAOs;
	}

	private List<AvailabilityDAO> readjustMaxCountsForAvailabilities(List<AvailabilityDAO> availabilityDAOs,
			final Long startDate, final Long endDate) {
		final Long minStartDate = availabilityDAOs.stream().mapToLong(AvailabilityDAO::getStartDate).min()
				.orElseThrow();
		final Long maxEndDate = availabilityDAOs.stream().mapToLong(AvailabilityDAO::getEndDate).max().orElseThrow();
		final List<String> userIds = availabilityDAOs.stream().map(AvailabilityDAO::getUserId).distinct()
				.collect(Collectors.toList());
		final Map<String, List<BookedSlotDTO>> taToBookedSlots = this.getBookedSlots(userIds, minStartDate,
				maxEndDate);
		availabilityDAOs = availabilityDAOs.stream().map(
				f -> {
					final List<BookedSlotDTO> bookedSlots = taToBookedSlots.get(f.getUserId());
					if (f.getStartDate() < startDate) {
						final Integer numberOfOverlappingBookedSlots = this
								.getOverlappingBookedSlotsCount(f.toBuilder().endDate(startDate).build(), bookedSlots);
						f = f.toBuilder().maximumNumberOfInterviews(
								f.getMaximumNumberOfInterviews() - numberOfOverlappingBookedSlots).build();
					}
					if (f.getEndDate() > endDate) {
						final Integer numberOfOverlappingBookedSlots = this
								.getOverlappingBookedSlotsCount(f.toBuilder().startDate(endDate).build(), bookedSlots);
						f = f.toBuilder().maximumNumberOfInterviews(
								f.getMaximumNumberOfInterviews() - numberOfOverlappingBookedSlots).build();
					}
					return f;
				}).collect(Collectors.toList());
		return availabilityDAOs;
	}

	public List<String> getAllAvailableUsers(final List<String> userIds, final Long startDate, final Long endDate) {
		List<AvailabilityDAO> availabilityDAOs = this.getAvailableSlotsOfAllInterviewers(userIds, startDate, endDate);
		final Map<String, List<BookedSlotDTO>> bookedSlotsDAOs = this.getBookedSlots(userIds, startDate, endDate);
		availabilityDAOs = availabilityDAOs.stream().filter(x -> x.getMaximumNumberOfInterviews() > 0 &&
				(bookedSlotsDAOs.get(x.getUserId()) == null || bookedSlotsDAOs.get(x.getUserId()).isEmpty()))
				.collect(Collectors.toList());
		return availabilityDAOs.stream().map(AvailabilityDAO::getUserId).distinct().collect(Collectors.toList());
	}

	public void bookTaSlotAndUpdateInterview(SchedulingProcessingData data, AvailabilityDAO availabilityDAO) {
		InterviewDAO interviewDAO = this.interViewRepository.findById(data.getInput().getInterviewId()).get();
		this.bookSlotNeedlessAvailability(availabilityDAO.getUserId(), "BarRaiser", interviewDAO.getStartDate(),
				interviewDAO.getEndDate(), this.getBufferForTa());
		final InterviewStatus status = InterviewStatus.PENDING_INTERVIEWING;
		interviewDAO = interviewDAO.toBuilder()
				.status(status.getValue())
				.taggingAgent(availabilityDAO.getUserId()).build();
		interviewDAO = this.interviewService.save(interviewDAO);
		data.getSchedulingCommunicationData().setInterviewDAO(interviewDAO);
	}

	public long getBufferForTa() {
		return this.BUFFER_TA_SLOT;
	}

	public List<String> filterForCompletelyBookedExperts(
			final List<String> eligibleExperts, final Long startDate, final Long endDate) {
		final Map<String, List<BookedSlotDTO>> bookedSlotsPerExpert = this.getBookedSlots(eligibleExperts, startDate,
				endDate);
		final List<String> overlappedExpert = new ArrayList<>();
		for (final String interviewer : eligibleExperts) {
			final List<BookedSlotDTO> bookedSlotsDAOs = bookedSlotsPerExpert.get(interviewer);
			for (final BookedSlotDTO bookedSlot : bookedSlotsDAOs) {
				if (((bookedSlot.getStartDate() + bookedSlot.getBuffer()) <= startDate
						&& (bookedSlot.getEndDate() - bookedSlot.getBuffer()) >= endDate)
						&& bookedSlot.getBuffer() > 0) {
					overlappedExpert.add(interviewer);
					break;
				}
			}
		}
		return overlappedExpert;
	}
}
