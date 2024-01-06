package com.barraiser.onboarding.zoom.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ZoomMeetingDTO {
    private String topic;

    private Integer type;

    @JsonProperty("start_time")
    private String startTime;

    private Integer duration;

    @JsonProperty("id")
    private Long meetingId;

    @JsonProperty("join_url")
    private String joinUrl;

    @JsonProperty("encrypted_password")
    private String encryptedPassword;

    private String password;

    @JsonProperty("host_email")
    private String hostEmail;

    private Settings settings;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder(toBuilder = true)
    public static class Settings {
        @JsonProperty("auto_recording")
        private String autoRecording;

        @JsonProperty("waiting_room")
        private boolean waitingRoom;

        @JsonProperty("join_before_host")
        private boolean joinBeforeHost;
    }
}
