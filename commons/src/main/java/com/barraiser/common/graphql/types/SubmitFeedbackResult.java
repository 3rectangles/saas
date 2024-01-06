package com.barraiser.common.graphql.types;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SubmitFeedbackResult {
    private Boolean success;
    private ArrayList<FeedbackValidationError> errors;
    private String type;
    private List<String> feedbackImprovements;
}

