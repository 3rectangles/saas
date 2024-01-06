package com.barraiser.onboarding.zoom.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ZoomMeetingInfoDTO {
    private Long meetingId;
    private Long recordingStartTime;
    private Long recordingEndTime;
}
