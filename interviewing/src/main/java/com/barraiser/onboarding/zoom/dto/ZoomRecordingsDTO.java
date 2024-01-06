package com.barraiser.onboarding.zoom.dto;

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
public class ZoomRecordingsDTO {
    private String id;

    private Integer duration;

    @JsonProperty("recording_files")
    private List<RecordingFile> recordingFiles;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder(toBuilder = true)
    public static class RecordingFile {
        @JsonProperty("file_type")
        private String fileType;

        @JsonProperty("file_extension")
        private String fileExtension;

        @JsonProperty("download_url")
        private String downloadUrl;

        @JsonProperty("recording_type")
        private String recordingType;
    }
}
