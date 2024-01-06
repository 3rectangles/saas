package com.barraiser.onboarding.interview.evaluation.percentile;

import com.barraiser.onboarding.dal.EvaluationDAO;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Log4j2
@Component
public class EvaluationPercentileFetcher {
    public static Integer getPercentileToDisplay(final EvaluationDAO evaluation) {
        final Double actualPercentile = evaluation.getPercentile();
        if(actualPercentile == null) {
            return null;
        }
        return (int) ((Math.floor(actualPercentile * 100 / 5) + (actualPercentile >= 0.5 ? 0 : 1)) * 5);
    }
}
