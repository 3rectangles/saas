package com.barraiser.onboarding.interview;

import com.barraiser.onboarding.common.Constants;
import com.barraiser.common.graphql.types.Feedback;
import com.barraiser.common.graphql.types.Question;

import java.util.List;

public class QuestionUtil {

    public static Integer getNumberOfQuestionsAskedInDifficulty(final String difficulty, final List<Question> questions) {
        Integer numberOfQuestionsAskedInDifficulty = 0;
        for (final Question question : questions) {
            for (final Feedback feedback : question.getFeedbacks()) {
                if (!Constants.SOFT_SKILL_ID.equals(feedback.getCategoryId()) && difficulty.equals(feedback.getDifficulty())) {
                    numberOfQuestionsAskedInDifficulty += 1;
                    break;
                }
            }
        }
        return numberOfQuestionsAskedInDifficulty;
    }
}
