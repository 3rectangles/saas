package com.barraiser.common.graphql.types;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class FeedbackSentiment {
    private String id;

    private String feedbackSentimentLabel;

    private Double feedbackSentimentScore;

    private Boolean looksGood;
}
