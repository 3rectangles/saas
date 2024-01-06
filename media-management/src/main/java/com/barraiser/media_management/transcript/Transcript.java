package com.barraiser.media_management.transcript;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Transcript {
    @JsonProperty("transcriptions")
    private List<Transcription> transcriptions;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder(toBuilder = true)
    public static class Transcription {
        @JsonProperty("from")
        private Long from;

        @JsonProperty("to")
        private Long to;

        @JsonProperty("speaker")
        private String speaker;

        @JsonProperty("text")
        private String text;
    }
}
