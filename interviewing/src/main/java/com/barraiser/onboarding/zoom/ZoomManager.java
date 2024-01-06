/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.zoom;

import com.barraiser.common.monitoring.Profiled;
import com.barraiser.onboarding.config.DynamicAppConfigProperties;
import com.barraiser.onboarding.zoom.dto.ZoomMeetingDTO;
import com.barraiser.onboarding.zoom.dto.ZoomMeetingInstancesDTO;
import com.barraiser.onboarding.zoom.dto.ZoomRecordingsDTO;
import com.barraiser.onboarding.zoom.dto.ZoomUpdateStatusDTO;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class ZoomManager {
	public static final String ZOOM_ACCOUNTS_KEY = "zoom-accounts";
	public static final int MAX_PARALLEL_MEETINGS_ALLOWED_PER_ZOOM_ACCOUNT = 2;
	private static final String ZOOM_END_MEETING_ACTION = "end";

	private final ZoomClient zoomClient;
	private final DynamicAppConfigProperties dynamicAppConfigProperties;

	public ZoomMeetingDTO scheduleMeeting(
			final String account, final Long startDate, final Long endDate, final String topic) {
		final String startTimeFormatted = Instant.ofEpochSecond(startDate).truncatedTo(ChronoUnit.SECONDS).toString();
		ZoomMeetingDTO meeting = ZoomMeetingDTO.builder()
				.startTime(startTimeFormatted)
				.duration((int) ((endDate - startDate) / 60))
				.topic(topic)
				.type(2)
				.password("123456")
				.settings(
						ZoomMeetingDTO.Settings.builder()
								.autoRecording("cloud")
								.waitingRoom(false)
								.joinBeforeHost(true)
								.build())
				.build();
		meeting = this.zoomClient.createMeeting(account, meeting);
		return meeting;
	}

	public void cancelMeeting(final String meetingId) {
		try {
			this.zoomClient.getZoomMeeting(meetingId);
		} catch (final FeignException.NotFound e) {
			log.warn("Meeting does not exist: {}", meetingId);
			return;
		}
		try {
			this.zoomClient.updateStatus(
					meetingId,
					ZoomUpdateStatusDTO.builder().action(ZOOM_END_MEETING_ACTION).build());
		} catch (final Exception e) {
			log.warn(e, e);
		}
		this.zoomClient.deleteMeeting(meetingId);
	}

	/**
	 * Finds out one account which is free to use for a particular slot.
	 *
	 * @param usedAccounts
	 *            - a list of zoom accounts which have been already used, may
	 *            contain duplicates.
	 */
	@Profiled(name = "zoomaccount")
	public String getZoomAccountToUse(final List<String> usedAccounts) {
		final List<String> validAccounts = this.dynamicAppConfigProperties.getListOfString(ZOOM_ACCOUNTS_KEY);

		final Map<String, Long> usedAccountFrequency = usedAccounts.stream()
				.collect(
						Collectors.groupingBy(Function.identity(), HashMap::new, Collectors.counting()));

		// First try and find the account that does no interview scheduled.
		// Then try to see if there is a zoom account where only one interview has been
		// scheduled.
		return validAccounts.stream()
				.filter(
						x -> !usedAccountFrequency.containsKey(x))
				.findFirst()
				.orElseGet(() -> validAccounts.stream()
						.filter(x -> usedAccountFrequency.get(x) < MAX_PARALLEL_MEETINGS_ALLOWED_PER_ZOOM_ACCOUNT)
						.findFirst()
						.orElseThrow(
								() -> new NoZoomAccountAvailableForInterviewException(
										"No available Zoom Account found for interview")));
	}

	public List<String> getMeetingInstances(final String meetingId) {
		return this.zoomClient.getZoomMeetingInstances(meetingId).getMeetings()
				.stream()
				.map(ZoomMeetingInstancesDTO.Instance::getUuid)
				.collect(Collectors.toList());
	}

	public ZoomRecordingsDTO getMeetingRecordings(final String meetingId) {
		try {
			return this.zoomClient.getZoomRecordings(meetingId);
		} catch (final Exception e) {
			log.warn(e, e);
			return null;
		}
	}

	public String getMeetingIdFromJoinUrl(final String url) {
		int stopIdx = url.length();
		int startIdx = -1;
		for (int i = 0; i < stopIdx; ++i) {
			if (url.charAt(i) == '?') {
				stopIdx = i;
			}
			if (url.charAt(i) == '/') {
				startIdx = i;
			}
		}
		final StringBuilder meetingId = new StringBuilder();
		for (int i = startIdx + 1; i < stopIdx; ++i) {
			meetingId.append(url.charAt(i));
		}
		return meetingId.toString();
	}
}
