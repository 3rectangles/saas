package com.barraiser.onboarding.interview.feeback.evaluation;

import com.barraiser.onboarding.dal.SkillDAO;
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
public class MissedCategoryCriteriaTest {
    @InjectMocks
    private MissedCategoryCriteria missedCategoryCriteria;

    @Test
    public void shouldReturnImprovementsForMissingCategories() {
        final List<SkillDAO> skillDAOList = List.of(
            SkillDAO.builder().id("1").name("id-1").build(),
            SkillDAO.builder().id("2").name("id-2").build(),
            SkillDAO.builder().id("3").name("id-3").build(),
            SkillDAO.builder().id("4").name("id-4").build(),
            SkillDAO.builder().id("5").name("id-5").build(),
            SkillDAO.builder().id("6").name("id-6").build()
        );
        final List<Feedback> feedbacks1 = List.of(Feedback.builder().categoryId("1").build(),
            Feedback.builder().categoryId("2").build());
        final List<Feedback> feedbacks2 = List.of(Feedback.builder().categoryId("2").build(),
            Feedback.builder().categoryId("4").build());
        final List<Feedback> feedbacks3 = List.of(Feedback.builder().categoryId("6").build());
        final List<Question> parentQuestions = List.of(Question.builder().feedbacks(feedbacks3).build());
        final List<Question> followupQuestions = List.of(
            Question.builder().feedbacks(feedbacks1).build(),
            Question.builder().feedbacks(feedbacks2).build()
        );
        final List<Question> allQuestions = new ArrayList<>(parentQuestions);
        allQuestions.addAll(followupQuestions);
        final FeedbackSummaryData data = FeedbackSummaryData.builder()
            .skillDAOs(skillDAOList)
            .parentQuestions(parentQuestions)
            .followUpQuestions(followupQuestions)
            .allQuestions(allQuestions)
            .build();
        final List<String> improvements = missedCategoryCriteria.getImprovement(data);
        final List<String> expectedOutput = List.of("You didn’t evaluate the candidate on <b>id-3</b>, <b>id-5</b>.");
        assertEquals(expectedOutput, improvements);
    }

    @Test
    public void shouldNotReturnImprovements() {
        final List<SkillDAO> skillDAOList = List.of(
            SkillDAO.builder().id("1").name("id-1").build(),
            SkillDAO.builder().id("2").name("id-2").build(),
            SkillDAO.builder().id("3").name("id-3").build()
        );
        final List<Feedback> feedbacks1 = List.of(Feedback.builder().categoryId("1").build(),
            Feedback.builder().categoryId("2").build());
        final List<Feedback> feedbacks2 = List.of(Feedback.builder().categoryId("2").build(),
            Feedback.builder().categoryId("3").build());
        final List<Question> allQuestions = List.of(
                Question.builder().feedbacks(feedbacks1).build(),
                Question.builder().feedbacks(feedbacks2).build()
            );
        final FeedbackSummaryData data = FeedbackSummaryData.builder()
            .skillDAOs(skillDAOList)
            .allQuestions(allQuestions)
            .build();
        final List<String> improvements = missedCategoryCriteria.getImprovement(data);
        final List<String> expectedOutput = new ArrayList<>();
        expectedOutput.add(null);
        assertEquals(expectedOutput, improvements);
    }

    @Test
    public void shouldNotReturnImprovementsForDeletedAndNonEvaluativeQuestions() {
        final List<SkillDAO> skillDAOList = List.of(
            SkillDAO.builder().id("1").name("id-1").build(),
            SkillDAO.builder().id("2").name("id-2").build(),
            SkillDAO.builder().id("3").name("id-3").build(),
            SkillDAO.builder().id("4").name("id-4").build(),
            SkillDAO.builder().id("5").name("id-5").build()
        );
        final List<Feedback> feedbacks1 = List.of(Feedback.builder().categoryId("1").build(),
            Feedback.builder().categoryId("2").build());
        final List<Feedback> feedbacks2 = List.of(Feedback.builder().categoryId("3").build());
        final List<Feedback> feedbacks3 = List.of(Feedback.builder().categoryId("3").build());
        final List<Feedback> feedbacks4 = List.of(Feedback.builder().categoryId("4").build());
        final List<Question> allQuestions = List.of(
            Question.builder().feedbacks(feedbacks1).build(),
            Question.builder().feedbacks(feedbacks2).build(),
            Question.builder().type("DELETED").feedbacks(feedbacks3).build(),
            Question.builder().type("NON_EVALUATIVE").feedbacks(feedbacks4).build()
        );
        final FeedbackSummaryData data = FeedbackSummaryData.builder()
            .skillDAOs(skillDAOList)
            .allQuestions(allQuestions)
            .build();
        final List<String> improvements = missedCategoryCriteria.getImprovement(data);
        final List<String> expectedOutput = List.of("You didn’t evaluate the candidate on <b>id-4</b>, <b>id-5</b>.");
        assertEquals(expectedOutput, improvements);
    }
}
