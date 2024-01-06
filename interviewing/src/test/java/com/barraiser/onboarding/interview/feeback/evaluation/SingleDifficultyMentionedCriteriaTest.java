package com.barraiser.onboarding.interview.feeback.evaluation;

import com.barraiser.common.graphql.types.Feedback;
import com.barraiser.common.graphql.types.Question;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class SingleDifficultyMentionedCriteriaTest {

    @InjectMocks
    private SingleDifficultyMentionedCriteria singleDifficultyMentionedCriteria;

    @Test
    public void shouldReturnImprovementIfAllQuestionsBelongToSameDifficulty() {
        final List<Question> allQuestions = List.of(
                Question.builder().feedbacks(List.of(
                    Feedback.builder().categoryId("1").difficulty("EASY").build(),
                    Feedback.builder().categoryId("52").difficulty("HARD").build()
                )).build(),
                Question.builder().feedbacks(List.of(
                    Feedback.builder().categoryId("2").difficulty("EASY").build()
                )).build());
        final FeedbackSummaryData data = FeedbackSummaryData.builder().allQuestions(allQuestions).build();
        final List<String> improvements = this.singleDifficultyMentionedCriteria.getImprovement(data);
        final List<String> expectedOutput = List.of("All questions are marked as <b>easy</b>.");
        assertEquals(expectedOutput, improvements);
    }

    @Test
    public void shouldNotReturnImprovementIfQuestionsBelongToDifferentDifficulty() {
        final List<Question> allQuestions = List.of(
                Question.builder().feedbacks(List.of(
                    Feedback.builder().categoryId("1").difficulty("EASY").build(),
                    Feedback.builder().categoryId("52").difficulty("HARD").build()
                )).build(),
                Question.builder().feedbacks(List.of(
                    Feedback.builder().categoryId("2").difficulty("HARD").build()
                )).build()
            );
        final FeedbackSummaryData data = FeedbackSummaryData.builder().allQuestions(allQuestions).build();
        final List<String> improvements = this.singleDifficultyMentionedCriteria.getImprovement(data);
        final List<String> expectedOutput = new ArrayList<>();
        expectedOutput.add(null);
        assertEquals(expectedOutput, improvements);
    }
}
