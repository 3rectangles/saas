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
public class GreaterNumberOfDeletedQuestionsCriteriaTest {
    @InjectMocks
    private GreaterNumberOfDeletedQuestionsCriteria greaterNumberOfDeletedQuestionsCriteria;

    @Test
    public void shouldReturnImprovementIfHigherNumberOfDeletedQuestionsPresent() {
        final FeedbackSummaryData data = FeedbackSummaryData.builder()
            .allQuestions(List.of(Question.builder().id("1").type("DELETED").build(),
                Question.builder().id("2").type("DELETED").build()))
            .build();
        final List<String> improvements = this.greaterNumberOfDeletedQuestionsCriteria.getImprovement(data);
        final List<String> expected = List.of("You have deleted 2 questions. Please make sure you don’t delete " +
            "any question for which separate feedback can be provided");
        assertEquals(expected, improvements);
    }

    @Test
    public void shouldNotReturnImprovementIfLesserNumberOfDeletedQuestionsPresent() {
        final FeedbackSummaryData data = FeedbackSummaryData.builder()
            .allQuestions(List.of(Question.builder().id("1").type("DELETED").build(),
                Question.builder().id("2").type("REQUIRED").build()))
            .build();
        final List<String> improvements = this.greaterNumberOfDeletedQuestionsCriteria.getImprovement(data);
        final List<String> expected = new ArrayList<>();
        expected.add(null);
        assertEquals(expected, improvements);
    }

}
