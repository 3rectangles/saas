/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling;

import com.barraiser.common.dal.Money;
import com.barraiser.common.graphql.input.ScheduleInterviewInput;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.zoom.dto.ZoomMeetingDTO;

import lombok.Data;

/**
 * Here we are intentionally not using builder pattern as this POJO is a
 * placeholder of data that is
 * being passed around in different {@link SchedulingProcessor} s, hence it is
 * intended to be
 * mutable.
 */
@Data
public class SchedulingProcessingData {
	private ScheduleInterviewInput input;
	private AuthenticatedUser user;
	private ZoomMeetingDTO zoomMeeting;
	private SchedulingCommunicationData schedulingCommunicationData;
	private String timestampToWaitUntil;
	private String timestampToWaitUntilForTaReassignment;
	private String timestampToWaitUntilForTaAllocationStart;
	private Boolean isExpertDuplicate;
	private Boolean isTaAllocated;
	private String taId;
	private Boolean executeTaAssignment;
	private Boolean isTAAutoAllocationNeeded;
	private Boolean isTaAllocationLoopActive;
	private String partnerId;
	private Money interviewPrice;
	private Double margin;
	private String schedulingPlatform;
}
