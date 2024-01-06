package com.barraiser.onboarding.zoom;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class QuestionWithPredictedTime {
    @JsonProperty("id")
    private String id;

    @JsonProperty("predicted_start_time")
    private String predictedStartTime;

    @JsonProperty("match_score")
    private Float questionToTranscriptMatchScore;

    @JsonProperty("transcript_text")
    private String transcriptText;
}
