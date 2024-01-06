package com.barraiser.onboarding.zoom;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class QuestionTimeTaggingResponse {
    @JsonProperty("questions")
    private List<QuestionWithPredictedTime> questions;
}
