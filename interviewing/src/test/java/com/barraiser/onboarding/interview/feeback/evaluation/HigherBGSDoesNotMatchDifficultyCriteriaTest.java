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
public class HigherBGSDoesNotMatchDifficultyCriteriaTest {
    @InjectMocks
    private HigherBGSDoesNotMatchDifficultyCriteria higherBGSDoesNotMatchDifficultyCriteria;

    @Test
    public void shouldReturnImprovementsIfBGSAndDifficultyDoNotMatch() {

            final List<Question> allQuestions = List.of(
                Question.builder().feedbacks(List.of(Feedback.builder().difficulty("HARD").build())).build(),
                Question.builder().feedbacks(List.of(Feedback.builder().difficulty("EASY").build())).build(),
                Question.builder().feedbacks(List.of(Feedback.builder().difficulty("EASY").build())).build(),
                Question.builder().feedbacks(List.of(Feedback.builder().difficulty("EASY").build())).build(),
                Question.builder().feedbacks(List.of(Feedback.builder().difficulty("EASY").build())).build(),
                Question.builder().feedbacks(List.of(Feedback.builder().difficulty("EASY").build())).build(),
                Question.builder().feedbacks(List.of(Feedback.builder().difficulty("EASY").build())).build(),
                Question.builder().feedbacks(List.of(Feedback.builder().difficulty("MODERATE").build())).build(),
                Question.builder().feedbacks(List.of(Feedback.builder().difficulty("MODERATE").build())).build(),
                Question.builder().feedbacks(List.of(Feedback.builder().difficulty("MODERATE").build())).build(),
                Question.builder().feedbacks(List.of(Feedback.builder().difficulty("MODERATE").build())).build());
        final FeedbackSummaryData data = FeedbackSummaryData.builder()
            .bgsScore(588)
            .allQuestions(allQuestions)
            .build();
        final List<String> improvement = this.higherBGSDoesNotMatchDifficultyCriteria.getImprovement(data);
        final List<String> expectedOutput = List.of("You have not asked many hard questions to this candidate, " +
            "who was performing well during the interview. Please make sure to ask questions with increased difficulty for such candidates in future.");
        assertEquals(expectedOutput, improvement);
    }

    @Test
    public void shouldNotReturnImprovementIfBGSIsLess() {
        final List<Question> allQuestions = List.of(
            Question.builder().feedbacks(List.of(Feedback.builder().difficulty("HARD").build())).build(),
                Question.builder().feedbacks(List.of(Feedback.builder().difficulty("EASY").build())).build(),
                Question.builder().feedbacks(List.of(Feedback.builder().difficulty("EASY").build())).build(),
                Question.builder().feedbacks(List.of(Feedback.builder().difficulty("EASY").build())).build(),
                Question.builder().feedbacks(List.of(Feedback.builder().difficulty("EASY").build())).build(),
                Question.builder().feedbacks(List.of(Feedback.builder().difficulty("EASY").build())).build(),
                Question.builder().feedbacks(List.of(Feedback.builder().difficulty("EASY").build())).build(),
                Question.builder().feedbacks(List.of(Feedback.builder().difficulty("MODERATE").build())).build(),
                Question.builder().feedbacks(List.of(Feedback.builder().difficulty("MODERATE").build())).build(),
                Question.builder().feedbacks(List.of(Feedback.builder().difficulty("MODERATE").build())).build(),
                Question.builder().feedbacks(List.of(Feedback.builder().difficulty("MODERATE").build())).build());
        final FeedbackSummaryData data = FeedbackSummaryData.builder()
            .bgsScore(587)
            .allQuestions(allQuestions)
            .build();
        final List<String> improvement = this.higherBGSDoesNotMatchDifficultyCriteria.getImprovement(data);
        final List<String> expectedOutput = new ArrayList<>();
        expectedOutput.add(null);
        assertEquals(expectedOutput, improvement);
    }

    @Test
    public void shouldNotReturnImprovementIfHardQuestionsAreMore() {
        final List<Question> allQuestions = List.of(Question.builder().feedbacks(List.of(Feedback.builder().difficulty("HARD").build())).build(),
                Question.builder().feedbacks(List.of(Feedback.builder().difficulty("HARD").build())).build(),
                Question.builder().feedbacks(List.of(Feedback.builder().difficulty("EASY").build())).build(),
                Question.builder().feedbacks(List.of(Feedback.builder().difficulty("EASY").build())).build(),
                Question.builder().feedbacks(List.of(Feedback.builder().difficulty("EASY").build())).build(),
                Question.builder().feedbacks(List.of(Feedback.builder().difficulty("EASY").build())).build(),
                Question.builder().feedbacks(List.of(Feedback.builder().difficulty("EASY").build())).build(),
                Question.builder().feedbacks(List.of(Feedback.builder().difficulty("MODERATE").build())).build(),
                Question.builder().feedbacks(List.of(Feedback.builder().difficulty("MODERATE").build())).build(),
                Question.builder().feedbacks(List.of(Feedback.builder().difficulty("MODERATE").build())).build(),
                Question.builder().feedbacks(List.of(Feedback.builder().difficulty("MODERATE").build())).build());
        final FeedbackSummaryData data = FeedbackSummaryData.builder()
            .bgsScore(588)
            .allQuestions(allQuestions)
            .build();
        final List<String> improvement = this.higherBGSDoesNotMatchDifficultyCriteria.getImprovement(data);
        final List<String> expectedOutput = new ArrayList<>();
        expectedOutput.add(null);
        assertEquals(expectedOutput, improvement);
    }

    @Test
    public void shouldReturnImprovementIZerofHardQuestionsAsked() {
        final List<Question> allQuestions = List.of(Question.builder().feedbacks(List.of(Feedback.builder().difficulty("EASY").build())).build(),
            Question.builder().feedbacks(List.of(Feedback.builder().difficulty("EASY").build())).build(),
            Question.builder().feedbacks(List.of(Feedback.builder().difficulty("EASY").build())).build(),
            Question.builder().feedbacks(List.of(Feedback.builder().difficulty("EASY").build())).build(),
            Question.builder().feedbacks(List.of(Feedback.builder().difficulty("EASY").build())).build(),
            Question.builder().feedbacks(List.of(Feedback.builder().difficulty("EASY").build())).build(),
            Question.builder().feedbacks(List.of(Feedback.builder().difficulty("EASY").build())).build(),
            Question.builder().feedbacks(List.of(Feedback.builder().difficulty("MODERATE").build())).build(),
            Question.builder().feedbacks(List.of(Feedback.builder().difficulty("MODERATE").build())).build(),
            Question.builder().feedbacks(List.of(Feedback.builder().difficulty("MODERATE").build())).build(),
            Question.builder().feedbacks(List.of(Feedback.builder().difficulty("MODERATE").build())).build());
        final FeedbackSummaryData data = FeedbackSummaryData.builder()
            .bgsScore(588)
            .allQuestions(allQuestions)
            .build();
        final List<String> improvement = this.higherBGSDoesNotMatchDifficultyCriteria.getImprovement(data);
        final List<String> expectedOutput = List.of("You have not asked any hard question to " +
            "this candidate, who was performing well during the interview. Please make sure to ask questions with " +
            "increased difficulty for such candidates in future.");
        assertEquals(expectedOutput, improvement);
    }

}
