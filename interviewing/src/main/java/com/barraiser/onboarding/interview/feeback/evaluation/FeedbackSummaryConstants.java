package com.barraiser.onboarding.interview.feeback.evaluation;

import java.util.Map;

public class FeedbackSummaryConstants {
    public static final String CHARACTER_LENGTH_IS_SMALL_IN_AREAS_OF_IMPROVEMENT_MESSAGE = "Overall feedback has fewer " +
        "<b>areas of improvement</b> in it. Please add any other improvement areas you observed.";
    public static final String CHARACTER_LENGTH_IS_SMALL_IN_STRENGTH_MESSAGE = "Overall feedback has very few " +
        "<b>strengths</b> mentioned in it. Please add any other strengths you observed.";

    public static final Map<Integer, Integer> LOWER_BOUND_IN_AREAS_OF_IMPROVEMENT = Map.of(
        0, 300,
        1, 300,
        2, 250,
        3, 200,
        4, 150,
        5, 100,
        6, 100
    );
    public static final Map<Integer, Integer> LOWER_BOUND_IN_OVERALL_STRENGTH = Map.of(
        0, 100,
        1, 100,
        2, 150,
        3, 200,
        4, 250,
        5, 300,
        6, 300
    );
    public static final Integer TOTAL_NUMBER_OF_NON_EVALUATIVE_QUESTIONS_THRESHOLD = 1;
    public static final Double RATIO_OF_NON_EVALUATIVE_QUESTIONS_TO_TOTAL_QUESTIONS_THRESHOLD = 0.05;
    public static final String NON_EVALUATIVE_QUESTION_CRITERIA_IMPROVEMENT_MESSAGE = "You have marked %s questions as " +
        "<b>non-evaluative</b>. Please check if these questions are marked correctly.";
    public static final String SINGLE_DIFFICULTY_MENTIONED_CRITERIA_IMPROVEMENT_MESSAGE = "All questions are marked as <b>%s</b>.";
    public static final String  MISSED_CATEGORY_CRITERIA_IMPROVEMENT_MESSAGE = "You didn’t evaluate the candidate on %s.";
    public static final String FIRST_QUESTION_TYPE_CHECK_CRITERIA_IMPROVEMENT_MESSAGE = "Usually the first question is asked to test the required skill. " +
        "Could you please check if the first question is a <b>Testing Good to know</b> type";
    public static final String HIGHER_BGS_DOES_NOT_MATCH_DIFFICULTY_CRITERIA_IMPROVEMENT_MESSAGE = "You have not asked many <b>hard</b> " +
        "questions to this candidate, who was performing well during the interview. Please make sure to ask questions " +
        "with increased difficulty for such candidates in future.";
    public static final Double HIGHER_BOUND_FOR_BGS_SCORE = 587.5;
    public static final Double RATIO_OF_HARD_QUESTIONS_TO_TOTAL_QUESTIONS_THRESHOLD = 0.1;
    public static final String ZERO_FOLLOW_UP_QUESTIONS_ASKED_IMPROVEMENT_MESSAGE = "You have not marked any question as " +
        "<b>follow up</b>.";
    public static final String LESSER_FOLLOW_UP_QUESTIONS_ASKED_IMPROVEMENT_MESSAGE = "You have only marked %s question(s) as " +
        "<b>follow up</b>.";
    public static final String NO_HARD_QUESTIONS_ASKED_IN_HIGHER_BGS_IMPROVEMENT_MESSAGE = "You have not asked any <b>hard</b> " +
        "question to this candidate, who was performing well during the interview. Please make sure to ask questions " +
        "with increased difficulty for such candidates in future.";
    public static final Integer NUMBER_OF_DELETED_QUESTIONS_THRESHOLD = 1;
    public static final String GREATER_NUMBER_OF_DELETED_QUESTIONS_IMPROVEMENT_MESSAGE = "You have deleted %s questions. " +
        "Please make sure you don’t delete any question for which separate feedback can be provided";
}
