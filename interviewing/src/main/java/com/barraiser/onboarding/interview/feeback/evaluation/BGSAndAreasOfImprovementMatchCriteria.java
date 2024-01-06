package com.barraiser.onboarding.interview.feeback.evaluation;

import com.barraiser.common.graphql.types.Feedback;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@AllArgsConstructor
public class BGSAndAreasOfImprovementMatchCriteria implements FeedbackEvaluator {

    @Override
    public List<String> getImprovement(final FeedbackSummaryData data) {
        return Arrays.asList(this.isBgsAndAreasOfImprovementMatching(data.getOverallFeedback().getAreasOfImprovement(), data.getBgsScore()));
    }

    @Override
    public int order() {
        return 2;
    }

    private String isBgsAndAreasOfImprovementMatching(final Feedback areasOfImprovement, final Integer bgs) {
        final Integer lengthOfImprovementFeedback = areasOfImprovement.getFeedback().length();
        if (bgs >= 200) {
            final Integer lowerBound = FeedbackSummaryConstants.LOWER_BOUND_IN_AREAS_OF_IMPROVEMENT.get((bgs - 200) / 100);
            return lengthOfImprovementFeedback < lowerBound ? FeedbackSummaryConstants.CHARACTER_LENGTH_IS_SMALL_IN_AREAS_OF_IMPROVEMENT_MESSAGE : null;
        }
        return null;
    }
}
