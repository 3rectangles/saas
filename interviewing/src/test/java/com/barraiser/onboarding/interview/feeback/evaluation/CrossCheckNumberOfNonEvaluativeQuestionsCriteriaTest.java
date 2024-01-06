package com.barraiser.onboarding.interview.feeback.evaluation;

import com.barraiser.common.graphql.types.Question;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
@RunWith(MockitoJUnitRunner.class)
public class CrossCheckNumberOfNonEvaluativeQuestionsCriteriaTest {

    @InjectMocks
    private CrossCheckNumberOfNonEvaluativeQuestionsCriteria crossCheckNumberOfNonEvaluativeQuestionsCriteria;

    @Test
    public void shouldReturnImprovementIfNonEvaluativeQuestionsAreGreater() {
        final FeedbackSummaryData data = FeedbackSummaryData.builder()
            .allQuestions(List.of(
                Question.builder().type("NON_EVALUATIVE").build(),
                Question.builder().type("NON_EVALUATIVE").build(),
                Question.builder().type("REQUIRED").build()
            )).build();
        final List<String> improvements = this.crossCheckNumberOfNonEvaluativeQuestionsCriteria.getImprovement(data);
        final List<String> expectedOutput = List.of("You have marked 2 questions as <b>non-evaluative</b>. " +
            "Please check if these questions are marked correctly.");
        assertEquals(expectedOutput, improvements);
    }

    @Test
    public void shouldReturnImprovementIfNonEvaluativeQuestionIsGreaterThanOne() {
        final FeedbackSummaryData data = FeedbackSummaryData.builder()
            .allQuestions(List.of(
                Question.builder().type("NON_EVALUATIVE").build(),
                Question.builder().type("NON_EVALUATIVE").build(),
                Question.builder().type("REQUIRED").build(),
                Question.builder().type("REQUIRED").build(),
                Question.builder().type("REQUIRED").build(),
                Question.builder().type("REQUIRED").build(),
                Question.builder().type("GOOD_TO_KNOW").build(),
                Question.builder().type("DELETED").build(),
                Question.builder().type("DELETED").build(),
                Question.builder().type("DELETED").build(),
                Question.builder().type("GOOD_TO_KNOW").build(),
                Question.builder().type("GOOD_TO_KNOW").build(),
                Question.builder().type("GOOD_TO_KNOW").build(),
                Question.builder().type("GOOD_TO_KNOW").build(),
                Question.builder().type("GOOD_TO_KNOW").build(),
                Question.builder().type("GOOD_TO_KNOW").build(),
                Question.builder().type("GOOD_TO_KNOW").build(),
                Question.builder().type("GOOD_TO_KNOW").build(),
                Question.builder().type("REQUIRED").build(),
                Question.builder().type("REQUIRED").build())).build();
        final List<String> improvements = this.crossCheckNumberOfNonEvaluativeQuestionsCriteria.getImprovement(data);
        final List<String> expectedOutput = List.of("You have marked 2 questions as <b>non-evaluative</b>. Please check if these questions are marked correctly.");
        assertEquals(expectedOutput, improvements);
    }

    @Test
    public void shouldNOTReturnImprovementIfNonEvaluativeQuestionsAreLess() {
        final FeedbackSummaryData data = FeedbackSummaryData.builder()
            .allQuestions(List.of(
                Question.builder().type("REQUIRED").build(),
                Question.builder().type("REQUIRED").build(),
                Question.builder().type("REQUIRED").build())).build();
        final List<String> improvements = this.crossCheckNumberOfNonEvaluativeQuestionsCriteria.getImprovement(data);
        final List<String> expectedOutput = new ArrayList<>();
        expectedOutput.add(null);
        assertEquals(expectedOutput, improvements);
    }

    @Test
    public void shouldNotReturnImprovementIfNonEvaluativeQuestionIsLessThanEqualToOne() {
        final FeedbackSummaryData data = FeedbackSummaryData.builder()
            .allQuestions(List.of(
                Question.builder().type("NON_EVALUATIVE").build(),
                Question.builder().type("REQUIRED").build(),
                Question.builder().type("REQUIRED").build(),
                Question.builder().type("REQUIRED").build(),
                Question.builder().type("REQUIRED").build(),
                Question.builder().type("REQUIRED").build(),
                Question.builder().type("GOOD_TO_KNOW").build(),
                Question.builder().type("DELETED").build(),
                Question.builder().type("DELETED").build(),
                Question.builder().type("DELETED").build(),
                Question.builder().type("GOOD_TO_KNOW").build(),
                Question.builder().type("GOOD_TO_KNOW").build(),
                Question.builder().type("GOOD_TO_KNOW").build(),
                Question.builder().type("GOOD_TO_KNOW").build(),
                Question.builder().type("GOOD_TO_KNOW").build(),
                Question.builder().type("GOOD_TO_KNOW").build(),
                Question.builder().type("GOOD_TO_KNOW").build(),
                Question.builder().type("GOOD_TO_KNOW").build(),
                Question.builder().type("REQUIRED").build(),
                Question.builder().type("REQUIRED").build())).build();
        final List<String> improvements = this.crossCheckNumberOfNonEvaluativeQuestionsCriteria.getImprovement(data);
        final List<String> expectedOutput = new ArrayList<>();
        expectedOutput.add(null);
        assertEquals(expectedOutput, improvements);
    }
}
