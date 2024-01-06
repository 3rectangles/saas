package com.barraiser.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TranscriptDTO {
    private List<TranscriptionDTO> transcriptions;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class TranscriptionDTO {
        private Long startTime;

        private Long endTime;

        private String speaker;

        private String text;
    }
}
