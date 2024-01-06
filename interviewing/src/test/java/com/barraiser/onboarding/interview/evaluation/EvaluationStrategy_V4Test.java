package com.barraiser.onboarding.interview.evaluation;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@Ignore
@RunWith(MockitoJUnitRunner.class)
public class EvaluationStrategy_V4Test {
//
//    @Mock
//    private QuestionRepository questionRepository;
//
//    @Mock
//    private InterViewRepository interViewRepository;
//
//    @Mock
//    private SkillWeightageRepository skillWeightageRepository;
//
//    @Mock
//    private EvaluationRepository evaluationRepository;
//
//    @Mock
//    private EvaluationScoreRepository evaluationScoreRepository;
//
//    @Mock
//    private ModifiedWeightageRepository modifiedWeightageRepository;
//
//    @InjectMocks
//    private EvaluationStrategy_V4 evaluationStrategy_v4;
//
//    @Test
//    public void testEvaluationStrategy() throws Exception {
//        final String evaluationId = "test_evaluation_id";
//        when(this.evaluationRepository.findById(anyString()))
//                .thenReturn(Optional.empty());
//
//        List<SkillWeightageDAO> skillsWeightages = List.of(
//                SkillWeightageDAO.builder()
//                        .evaluationId("test_evaluation_id")
//                        .weightage(40D)
//                        .jobRoleId("")
//                        .skillId("1")
//                        .build(),
//                SkillWeightageDAO.builder()
//                        .evaluationId("test_evaluation_id")
//                        .weightage(35D)
//                        .jobRoleId("")
//                        .skillId("2")
//                        .build(),
//                SkillWeightageDAO.builder()
//                        .evaluationId("test_evaluation_id")
//                        .weightage(25D)
//                        .jobRoleId("")
//                        .skillId("3")
//                        .build()
//        );
//
//        when(this.skillWeightageRepository.findByEvaluationId(anyString())).thenReturn(skillsWeightages);
//        when(this.evaluationScoreRepository.findByEvaluationIdAndSkillIdAndScoringAlgoVersion(anyString(), anyString(), anyString())).thenReturn(Optional.empty());
//
//
//        final InterviewDAO interview = InterviewDAO.builder().id(UUID.randomUUID().toString()).lastQuestionEnd(1607680800l).build();
//        when(this.interViewRepository.findAllByEvaluationId(anyString()))
//                .thenReturn(List.of(interview));
//
//        int i = 0;
//        List<QuestionDAO> listOfQuestions = List.of(
//                QuestionDAO.builder()
//                        .id(UUID.randomUUID().toString())
//                        .serialNumber(i++)
//                        .question(UUID.randomUUID().toString())
//                        .startTimeEpoch(1607677320l)
//                        .feedbacks(List.of(
//                                FeedbackDAO.builder()
//                                        .id(UUID.randomUUID().toString())
//                                        .categoryId("1")
//                                        .rating(7.0F)
//                                        .difficulty("EASY")
//                                        .feedback(UUID.randomUUID().toString())
//                                        .build()
//                        ))
//                        .build(),
//                QuestionDAO.builder()
//                        .id(UUID.randomUUID().toString())
//                        .serialNumber(i++)
//                        .question(UUID.randomUUID().toString())
//                        .startTimeEpoch(1607677800l)
//                        .feedbacks(List.of(
//                                FeedbackDAO.builder()
//                                        .id(UUID.randomUUID().toString())
//                                        .categoryId("3")
//                                        .rating(3.0F)
//                                        .difficulty("MODERATE")
//                                        .feedback(UUID.randomUUID().toString())
//                                        .build()
//                        ))
//                        .build(),
//                QuestionDAO.builder()
//                        .id(UUID.randomUUID().toString())
//                        .serialNumber(i++)
//                        .question(UUID.randomUUID().toString())
//                        .startTimeEpoch(1607678040l)
//                        .feedbacks(List.of(
//                                FeedbackDAO.builder()
//                                        .id(UUID.randomUUID().toString())
//                                        .categoryId("1")
//                                        .rating(7.0F)
//                                        .difficulty("VERY_EASY")
//                                        .feedback(UUID.randomUUID().toString())
//                                        .build(),
//                                FeedbackDAO.builder()
//                                        .id(UUID.randomUUID().toString())
//                                        .categoryId("2")
//                                        .rating(9.0F)
//                                        .difficulty("HARD")
//                                        .feedback(UUID.randomUUID().toString())
//                                        .build()
//                        ))
//                        .build(),
//
//                //irrelevant question
//                QuestionDAO.builder()
//                        .id(UUID.randomUUID().toString())
//                        .serialNumber(i++)
//                        .question(UUID.randomUUID().toString())
//                        .startTimeEpoch(1607678820l)
//                        .irrelevant(true)
//                        .feedbacks(List.of(
//                                FeedbackDAO.builder()
//                                        .id(UUID.randomUUID().toString())
//                                        .categoryId("1")
//                                        .rating(1.0F)
//                                        .difficulty("")
//                                        .feedback(UUID.randomUUID().toString())
//                                        .build()
//                        ))
//                        .build(),
//                QuestionDAO.builder()
//                        .id(UUID.randomUUID().toString())
//                        .serialNumber(i++)
//                        .question(UUID.randomUUID().toString())
//                        .startTimeEpoch(1607680020l)
//                        .feedbacks(List.of(
//                                FeedbackDAO.builder()
//                                        .id(UUID.randomUUID().toString())
//                                        .categoryId("2")
//                                        .rating(8.0F)
//                                        .difficulty("N.A")
//                                        .feedback(UUID.randomUUID().toString())
//                                        .build(),
//                                FeedbackDAO.builder()
//                                        .id(UUID.randomUUID().toString())
//                                        .categoryId("3")
//                                        .rating(7.0F)
//                                        .difficulty("VERY_HARD")
//                                        .feedback(UUID.randomUUID().toString())
//                                        .build()
//                        ))
//                        .build()
//        );
//
//        when(this.questionRepository.findAllByInterviewIdOrderByStartTimeAsc(anyString())).thenReturn(listOfQuestions);
//
//        //No need to save the test scores
//        List<ModifiedWeightageDAO> listOfModifiedWeightages = List.of(
//                ModifiedWeightageDAO.builder()
//                        .difficulty("VERY_EASY")
//                        .rating(1.0D)
//                        .weightage(1.0D)
//                        .build(),
//                ModifiedWeightageDAO.builder()
//                        .difficulty("VERY_EASY")
//                        .rating(2.0D)
//                        .weightage(1.0D)
//                        .build(),
//                ModifiedWeightageDAO.builder()
//                        .difficulty("VERY_EASY")
//                        .rating(3.0D)
//                        .weightage(1.0D)
//                        .build(),
//                ModifiedWeightageDAO.builder()
//                        .difficulty("VERY_EASY")
//                        .rating(4.0D)
//                        .weightage(3.5D)
//                        .build(),
//                ModifiedWeightageDAO.builder()
//                        .difficulty("VERY_EASY")
//                        .rating(5.0D)
//                        .weightage(3.5D)
//                        .build(),
//                ModifiedWeightageDAO.builder()
//                        .difficulty("VERY_EASY")
//                        .rating(6.0D)
//                        .weightage(1.0D)
//                        .build(),
//                ModifiedWeightageDAO.builder()
//                        .difficulty("VERY_EASY")
//                        .rating(7.0D)
//                        .weightage(1.0D)
//                        .build(),
//                ModifiedWeightageDAO.builder()
//                        .difficulty("VERY_EASY")
//                        .rating(8.0D)
//                        .weightage(1.0D)
//                        .build(),
//                ModifiedWeightageDAO.builder()
//                        .difficulty("VERY_EASY")
//                        .rating(9.0D)
//                        .weightage(1.0D)
//                        .build(),
//                ModifiedWeightageDAO.builder()
//                        .difficulty("VERY_EASY")
//                        .rating(10.0D)
//                        .weightage(1.0D)
//                        .build()
//                ,
//                //------------------------------
//                ModifiedWeightageDAO.builder()
//                        .difficulty("EASY")
//                        .rating(1.0D)
//                        .weightage(1.0D)
//                        .build(),
//                ModifiedWeightageDAO.builder()
//                        .difficulty("EASY")
//                        .rating(2.0D)
//                        .weightage(1.0D)
//                        .build(),
//                ModifiedWeightageDAO.builder()
//                        .difficulty("EASY")
//                        .rating(3.0D)
//                        .weightage(1.0D)
//                        .build(),
//                ModifiedWeightageDAO.builder()
//                        .difficulty("EASY")
//                        .rating(4.0D)
//                        .weightage(2.5D)
//                        .build(),
//                ModifiedWeightageDAO.builder()
//                        .difficulty("EASY")
//                        .rating(5.0D)
//                        .weightage(2.5D)
//                        .build(),
//                ModifiedWeightageDAO.builder()
//                        .difficulty("EASY")
//                        .rating(6.0D)
//                        .weightage(1.0D)
//                        .build(),
//                ModifiedWeightageDAO.builder()
//                        .difficulty("EASY")
//                        .rating(7.0D)
//                        .weightage(1.0D)
//                        .build(),
//                ModifiedWeightageDAO.builder()
//                        .difficulty("EASY")
//                        .rating(8.0D)
//                        .weightage(1.0D)
//                        .build(),
//                ModifiedWeightageDAO.builder()
//                        .difficulty("EASY")
//                        .rating(9.0D)
//                        .weightage(1.0D)
//                        .build(),
//                ModifiedWeightageDAO.builder()
//                        .difficulty("EASY")
//                        .rating(10.0D)
//                        .weightage(1.0D)
//                        .build()
//                ,
//                //------------------------------
//                ModifiedWeightageDAO.builder()
//                        .difficulty("MODERATE")
//                        .rating(1.0D)
//                        .weightage(1.0D)
//                        .build(),
//                ModifiedWeightageDAO.builder()
//                        .difficulty("MODERATE")
//                        .rating(2.0D)
//                        .weightage(1.0D)
//                        .build(),
//                ModifiedWeightageDAO.builder()
//                        .difficulty("MODERATE")
//                        .rating(3.0D)
//                        .weightage(1.0D)
//                        .build(),
//                ModifiedWeightageDAO.builder()
//                        .difficulty("MODERATE")
//                        .rating(4.0D)
//                        .weightage(1.0D)
//                        .build(),
//                ModifiedWeightageDAO.builder()
//                        .difficulty("MODERATE")
//                        .rating(5.0D)
//                        .weightage(1.0D)
//                        .build(),
//                ModifiedWeightageDAO.builder()
//                        .difficulty("MODERATE")
//                        .rating(6.0D)
//                        .weightage(1.0D)
//                        .build(),
//                ModifiedWeightageDAO.builder()
//                        .difficulty("MODERATE")
//                        .rating(7.0D)
//                        .weightage(1.0D)
//                        .build(),
//                ModifiedWeightageDAO.builder()
//                        .difficulty("MODERATE")
//                        .rating(8.0D)
//                        .weightage(1.0D)
//                        .build(),
//                ModifiedWeightageDAO.builder()
//                        .difficulty("MODERATE")
//                        .rating(9.0D)
//                        .weightage(1.0D)
//                        .build(),
//                ModifiedWeightageDAO.builder()
//                        .difficulty("MODERATE")
//                        .rating(10.0D)
//                        .weightage(1.0D)
//                        .build()
//                ,
//                //------------------------------
//                ModifiedWeightageDAO.builder()
//                        .difficulty("HARD")
//                        .rating(1.0D)
//                        .weightage(1.0D)
//                        .build(),
//                ModifiedWeightageDAO.builder()
//                        .difficulty("HARD")
//                        .rating(2.0D)
//                        .weightage(1.0D)
//                        .build(),
//                ModifiedWeightageDAO.builder()
//                        .difficulty("HARD")
//                        .rating(3.0D)
//                        .weightage(1.0D)
//                        .build(),
//                ModifiedWeightageDAO.builder()
//                        .difficulty("HARD")
//                        .rating(4.0D)
//                        .weightage(1.0D)
//                        .build(),
//                ModifiedWeightageDAO.builder()
//                        .difficulty("HARD")
//                        .rating(5.0D)
//                        .weightage(1.0D)
//                        .build(),
//                ModifiedWeightageDAO.builder()
//                        .difficulty("HARD")
//                        .rating(6.0D)
//                        .weightage(2.5D)
//                        .build(),
//                ModifiedWeightageDAO.builder()
//                        .difficulty("HARD")
//                        .rating(7.0D)
//                        .weightage(2.5D)
//                        .build(),
//                ModifiedWeightageDAO.builder()
//                        .difficulty("HARD")
//                        .rating(8.0D)
//                        .weightage(2.5D)
//                        .build(),
//                ModifiedWeightageDAO.builder()
//                        .difficulty("HARD")
//                        .rating(9.0D)
//                        .weightage(3.0D)
//                        .build(),
//                ModifiedWeightageDAO.builder()
//                        .difficulty("HARD")
//                        .rating(10.0D)
//                        .weightage(3.0D)
//                        .build()
//                ,
//                //------------------------------
//                ModifiedWeightageDAO.builder()
//                        .difficulty("VERY_HARD")
//                        .rating(1.0D)
//                        .weightage(1.0D)
//                        .build(),
//                ModifiedWeightageDAO.builder()
//                        .difficulty("VERY_HARD")
//                        .rating(2.0D)
//                        .weightage(1.0D)
//                        .build(),
//                ModifiedWeightageDAO.builder()
//                        .difficulty("VERY_HARD")
//                        .rating(3.0D)
//                        .weightage(1.0D)
//                        .build(),
//                ModifiedWeightageDAO.builder()
//                        .difficulty("VERY_HARD")
//                        .rating(4.0D)
//                        .weightage(1.0D)
//                        .build(),
//                ModifiedWeightageDAO.builder()
//                        .difficulty("VERY_HARD")
//                        .rating(5.0D)
//                        .weightage(1.0D)
//                        .build(),
//                ModifiedWeightageDAO.builder()
//                        .difficulty("VERY_HARD")
//                        .rating(6.0D)
//                        .weightage(3.5D)
//                        .build(),
//                ModifiedWeightageDAO.builder()
//                        .difficulty("VERY_HARD")
//                        .rating(7.0D)
//                        .weightage(3.5D)
//                        .build(),
//                ModifiedWeightageDAO.builder()
//                        .difficulty("VERY_HARD")
//                        .rating(8.0D)
//                        .weightage(3.5D)
//                        .build(),
//                ModifiedWeightageDAO.builder()
//                        .difficulty("VERY_HARD")
//                        .rating(9.0D)
//                        .weightage(4.0D)
//                        .build(),
//                ModifiedWeightageDAO.builder()
//                        .difficulty("VERY_HARD")
//                        .rating(10.0D)
//                        .weightage(4.0D)
//                        .build()
//
//                //-----------------------------
//
//        );
//        when(this.modifiedWeightageRepository.findAll()).thenReturn(listOfModifiedWeightages);
//
//        List<EvaluationScoreDAO> evaluationScoreDAOS = this.evaluationStrategy_v4.computeEvaluationScore("test_evaluation_id");
//
//        for (EvaluationScoreDAO evaluationScore : evaluationScoreDAOS) {
//            if ("1".equalsIgnoreCase(evaluationScore.getSkillId())) {
//                assertEquals(469D, evaluationScore.getScore().doubleValue(),0.0);
//            }
//            if ("2".equalsIgnoreCase(evaluationScore.getSkillId())) {
//                assertEquals(661D, evaluationScore.getScore().doubleValue(),0.0);
//            }
//            if ("3".equalsIgnoreCase(evaluationScore.getSkillId())) {
//                assertEquals(635D, evaluationScore.getScore().doubleValue(),0.0);
//            }
//        }
//
//    }

}
