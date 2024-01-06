package com.barraiser.onboarding.interview.feeback.evaluation;

import com.barraiser.common.graphql.types.Feedback;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@AllArgsConstructor
public class BGSAndStrengthMatchCriteria implements FeedbackEvaluator {
    @Override
    public List<String> getImprovement(final FeedbackSummaryData data) {
        return Arrays.asList(this.isBgsAndOverallStrengthMatching(data.getOverallFeedback().getStrength(), data.getBgsScore()));
    }

    @Override
    public int order() {
        return 1;
    }

    private String isBgsAndOverallStrengthMatching(final Feedback strengths, final Integer bgs) {

        final Integer lengthOfStrengthFeedback = strengths.getFeedback().length();
        if (bgs >= 200) {
            final Integer lowerBound = FeedbackSummaryConstants.LOWER_BOUND_IN_OVERALL_STRENGTH.get((bgs - 200) / 100);
            return lengthOfStrengthFeedback < lowerBound ? FeedbackSummaryConstants.CHARACTER_LENGTH_IS_SMALL_IN_STRENGTH_MESSAGE : null;
        }
        return null;
    }
}
