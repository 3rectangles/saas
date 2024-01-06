package com.barraiser.onboarding.interview.feeback.evaluation;

import java.util.List;

public interface FeedbackEvaluator {
    List<String>  getImprovement(final FeedbackSummaryData data);

    int order();
}
