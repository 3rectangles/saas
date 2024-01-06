package com.barraiser.onboarding.zoom;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ZoomRecordingQueueEvent {

    @JsonProperty("MessageId")
    private String messageId;

    @JsonProperty("Message")
    private String message;

}
