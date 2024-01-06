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
public class FirstQuestionTypeCheckCriteriaTest {
    @InjectMocks
    private FirstQuestionTypeCheckCriteria firstQuestionTypeCheckCriteria;

    @Test
    public void shouldReturnImprovementIfFirstQuestionIsNonEvaluative() {
        final List<Question> allQuestions = List.of(
                Question.builder().type("DELETED").build(),
                Question.builder().type("GOOD_TO_KNOW").build(),
                Question.builder().type("REQUIRED").build());
        final FeedbackSummaryData data = FeedbackSummaryData.builder()
            .allQuestions(allQuestions)
            .build();
        final List<String> improvements = this.firstQuestionTypeCheckCriteria.getImprovement(data);
        final List<String> expected = List.of("Usually the first question is asked to test the required skill. " +
            "Could you please check if the first question is a <b>Testing Good to know</b> type");
        assertEquals(expected, improvements);
    }

    @Test
    public void shouldReturnImprovementIfFirstQuestionIsGoodToKnow() {
        final List<Question> allQuestions = List.of(
                Question.builder().type("GOOD_TO_KNOW").build(),
                Question.builder().type("NON_EVALUATIVE").build(),
                Question.builder().type("REQUIRED").build());
        final FeedbackSummaryData data = FeedbackSummaryData.builder()
            .allQuestions(allQuestions)
            .build();
        final List<String> improvements = this.firstQuestionTypeCheckCriteria.getImprovement(data);
        final List<String> expected = List.of("Usually the first question is asked to test the required skill. " +
            "Could you please check if the first question is a <b>Testing Good to know</b> type");
        assertEquals(expected, improvements);
    }

    @Test
    public void shouldNotReturnImprovementIfFirstQuestionIsRequired() {
        final List<Question> allQuestions = List.of(
                Question.builder().type("DELETED").build(),
                Question.builder().type("REQUIRED").build(),
                Question.builder().type("NON_EVALUATIVE").build());
        final FeedbackSummaryData data = FeedbackSummaryData.builder()
            .allQuestions(allQuestions)
            .build();
        final List<String> improvements = this.firstQuestionTypeCheckCriteria.getImprovement(data);
        final List<String> expected = new ArrayList<>();
        expected.add(null);
        assertEquals(expected, improvements);
    }
}
