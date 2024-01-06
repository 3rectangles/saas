package com.barraiser.onboarding.interview.feeback.evaluation;

import com.barraiser.common.graphql.types.Feedback;
import com.barraiser.common.graphql.types.OverallFeedback;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class BGSAndStrengthMatchCriteriaTest {
    @InjectMocks
    private BGSAndStrengthMatchCriteria bgsAndStrengthMatchCriteria;

    @Test
    public void shouldNotReturnIfBGSIsLessThan200() {
        final OverallFeedback overallFeedback = OverallFeedback.builder()
            .strength(Feedback.builder().feedback("areas of improvement").build()).build();
        final FeedbackSummaryData data = FeedbackSummaryData.builder()
            .bgsScore(199)
            .overallFeedback(overallFeedback)
            .build();
        final List<String> improvements = this.bgsAndStrengthMatchCriteria.getImprovement(data);
        final List<String> expectedOutput = new ArrayList<>();
        expectedOutput.add(null);
        assertEquals(expectedOutput, improvements);
    }

    @Test
    public void shouldReturnImprovementIfBGSIsLessThan399AndCharacterIsLess() {
        final OverallFeedback overallFeedback = OverallFeedback.builder()
            .strength(Feedback.builder().feedback("areas of improvement").build()).build();
        final FeedbackSummaryData data = FeedbackSummaryData.builder()
            .bgsScore(309)
            .overallFeedback(overallFeedback)
            .build();
        final List<String> improvements = this.bgsAndStrengthMatchCriteria.getImprovement(data);
        final List<String> expectedOutput = List.of("Overall feedback has very few " +
            "<b>strengths</b> mentioned in it. Please add any other strengths you observed.");
        assertEquals(expectedOutput, improvements);
    }

    @Test
    public void shouldReturnImprovementIfBGSIsLessThan499AndCharacterIsLess() {
        final OverallFeedback overallFeedback = OverallFeedback.builder()
            .strength(Feedback.builder().feedback("areas of improvement").build()).build();
        final FeedbackSummaryData data = FeedbackSummaryData.builder()
            .bgsScore(409)
            .overallFeedback(overallFeedback)
            .build();
        final List<String> improvements = this.bgsAndStrengthMatchCriteria.getImprovement(data);
        final List<String> expectedOutput = List.of("Overall feedback has very few " +
            "<b>strengths</b> mentioned in it. Please add any other strengths you observed.");
        assertEquals(expectedOutput, improvements);
    }

    @Test
    public void shouldReturnImprovementIfBGSIsLessThan599AndCharacterIsLess() {
        final OverallFeedback overallFeedback = OverallFeedback.builder()
            .strength(Feedback.builder().feedback("areas of improvement").build()).build();
        final FeedbackSummaryData data = FeedbackSummaryData.builder()
            .bgsScore(509)
            .overallFeedback(overallFeedback)
            .build();
        final List<String> improvements = this.bgsAndStrengthMatchCriteria.getImprovement(data);
        final List<String> expectedOutput = List.of("Overall feedback has very few " +
            "<b>strengths</b> mentioned in it. Please add any other strengths you observed.");
        assertEquals(expectedOutput, improvements);
    }

    @Test
    public void shouldReturnImprovementIfBGSIsLessThan699AndCharacterIsLess() {
        final OverallFeedback overallFeedback = OverallFeedback.builder()
            .strength(Feedback.builder().feedback("areas of improvement").build()).build();
        final FeedbackSummaryData data = FeedbackSummaryData.builder()
            .bgsScore(699)
            .overallFeedback(overallFeedback)
            .build();
        final List<String> improvements = this.bgsAndStrengthMatchCriteria.getImprovement(data);
        final List<String> expectedOutput = List.of("Overall feedback has very few " +
            "<b>strengths</b> mentioned in it. Please add any other strengths you observed.");
        assertEquals(expectedOutput, improvements);
    }

    @Test
    public void shouldReturnImprovementIfBGSIsLessThan800AndCharacterIsLess() {
        final OverallFeedback overallFeedback = OverallFeedback.builder()
            .strength(Feedback.builder().feedback("areas of improvement").build()).build();
        final FeedbackSummaryData data = FeedbackSummaryData.builder()
            .bgsScore(800)
            .overallFeedback(overallFeedback)
            .build();
        final List<String> improvements = this.bgsAndStrengthMatchCriteria.getImprovement(data);
        final List<String> expectedOutput = List.of("Overall feedback has very few " +
            "<b>strengths</b> mentioned in it. Please add any other strengths you observed.");
        assertEquals(expectedOutput, improvements);
    }

    @Test
    public void shouldReturnImprovementIfBGSIsLessThan399AndCharacterIsBetween() {
        final OverallFeedback overallFeedback = OverallFeedback.builder()
            .strength(Feedback.builder().feedback("areas of improvement areas of improvement jibran should " +
                    "sleep early jibran works too much jibran is cool jibran eats aalo pyazzz").build()).build();
        final FeedbackSummaryData data = FeedbackSummaryData.builder()
            .bgsScore(309)
            .overallFeedback(overallFeedback)
            .build();
        final List<String> improvements = this.bgsAndStrengthMatchCriteria.getImprovement(data);
        final List<String> expectedOutput = new ArrayList<>();
        expectedOutput.add(null);
        assertEquals(expectedOutput, improvements);
    }

    @Test
    public void shouldReturnImprovementIfBGSIsLessThan499AndCharacterIsBetween() {
        final OverallFeedback overallFeedback = OverallFeedback.builder()
            .strength(Feedback.builder().feedback("areas of improvement areas of improvement jibran should" +
                    "sleep early jibran works too much jibran is cool jibran eats aalo pyazzz areas of improvement areas of improvement").build()).build();
        final FeedbackSummaryData data = FeedbackSummaryData.builder()
            .bgsScore(402)
            .overallFeedback(overallFeedback)
            .build();
        final List<String> improvements = this.bgsAndStrengthMatchCriteria.getImprovement(data);
        final List<String> expectedOutput = new ArrayList<>();
        expectedOutput.add(null);
        assertEquals(expectedOutput, improvements);
    }

    @Test
    public void shouldReturnImprovementIfBGSIsLessThan599AndCharacterIsBetween() {
        final OverallFeedback overallFeedback = OverallFeedback.builder()
            .strength(Feedback.builder().feedback("areas of improvement areas of improvement jibran should " +
                    "sleep early jibran works too much jibran is cool jibran eats aalo pyazzz areas of improvement areas of improvement" +
                    " working with jibs is funnnnnnnn").build()).build();
        final FeedbackSummaryData data = FeedbackSummaryData.builder()
            .bgsScore(599)
            .overallFeedback(overallFeedback)
            .build();
        final List<String> improvements = this.bgsAndStrengthMatchCriteria.getImprovement(data);
        final List<String> expectedOutput = new ArrayList<>();
        expectedOutput.add(null);
        assertEquals(expectedOutput, improvements);
    }

    @Test
    public void shouldReturnImprovementIfBGSIsLessThan699AndCharacterIsBetween() {
        final OverallFeedback overallFeedback = OverallFeedback.builder()
            .strength(Feedback.builder().feedback("areas of improvement areas of improvement jibran should " +
                    "sleep early jibran works too much jibran is cool jibran eats aalo pyazzz areas of improvement areas of improvement" +
                    " working with jibs is funnnnnnnn paisa badi buri cheez h should not run after be like jibran").build()).build();
        final FeedbackSummaryData data = FeedbackSummaryData.builder()
            .bgsScore(689)
            .overallFeedback(overallFeedback)
            .build();
        final List<String> improvements = this.bgsAndStrengthMatchCriteria.getImprovement(data);
        final List<String> expectedOutput = new ArrayList<>();
        expectedOutput.add(null);
        assertEquals(expectedOutput, improvements);
    }

    @Test
    public void shouldReturnImprovementIfBGSIsLessThan800AndCharacterIsBetween() {
        final OverallFeedback overallFeedback = OverallFeedback.builder()
            .strength(Feedback.builder().feedback("areas of improvement areas of improvement jibran should " +
                    "sleep early jibran works too much jibran is cool jibran eats aalo pyazzz areas of improvement areas of improvement" +
                    " working with jibs is funnnnnnnn paisa badi buri cheez h should not run after be like jibran be like vedhs too barraiser barraiser yes").build()).build();
        final FeedbackSummaryData data = FeedbackSummaryData.builder()
            .bgsScore(800)
            .overallFeedback(overallFeedback)
            .build();
        final List<String> improvements = this.bgsAndStrengthMatchCriteria.getImprovement(data);
        final List<String> expectedOutput = new ArrayList<>();
        expectedOutput.add(null);
        assertEquals(expectedOutput, improvements);
    }

    @Test
    public void shouldReturNullIfBGSIsLessThan699AndCharacterIsMore() {
        final OverallFeedback overallFeedback = OverallFeedback.builder()
            .strength(Feedback.builder().feedback("areas of improvement areas of improvement jibran should " +
                "sleep early jibran works too much jibran is cool jibran eats aalo pyazzz areas of improvement areas of improvement" +
                " working with jibs is funnnnnnnn paisa badi buri cheez h should not run after be like jibran be like vedhs too barraiser barraiser yes").build()).build();
        final FeedbackSummaryData data = FeedbackSummaryData.builder()
            .bgsScore(600)
            .overallFeedback(overallFeedback)
            .build();
        final List<String> improvements = this.bgsAndStrengthMatchCriteria.getImprovement(data);
        final List<String> expectedOutput = new ArrayList<>();
        expectedOutput.add(null);
        assertEquals(expectedOutput, improvements);
    }

}
