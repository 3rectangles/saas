package com.barraiser.onboarding.interview.evaluation.add_evaluation;

import java.io.IOException;

public interface AddEvaluationProcessor {
    void process(final AddEvaluationProcessingData data) throws IOException;
}
