package com.barraiser.onboarding.zoom;


import com.barraiser.common.utilities.DateUtils;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.onboarding.interview.InterViewRepository;
import graphql.schema.DataFetchingEnvironment;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class PostProcessZoomTranscriptMutation implements NamedDataFetcher {
    private final GraphQLUtil graphQLUtil;
    private final InterViewRepository interViewRepository;
    private final QuestionRepository questionRepository;
    private final ZoomTranscriptTimeAnnotatorFeignClient zoomTranscriptTimeAnnotatorFeignClient;
    private final UserDetailsRepository userDetailsRepository;
    private final DateUtils dateUtils;


    @Override
    public String type() {
        return MUTATION_TYPE;
    }

    @Override
    public String name() {
        return "postProcessZoomTranscript";
    }


    @Override
    public Object get(final DataFetchingEnvironment environment) throws Exception {

        final String zoomMeetingId = this.graphQLUtil.getArgument(environment, "input", String.class);
        return this.process(zoomMeetingId);
    }

    private Boolean process(String zoomMeetingId) {
        final InterviewDAO interviewDAO = this.interViewRepository.findByZoomLinkLike("%" + zoomMeetingId + "%");
        final UserDetailsDAO interviewer = this.userDetailsRepository.findById(interviewDAO.getInterviewerId()).get();
        final String interviewerFullName = String.format("%s %s", interviewer.getFirstName(),
            interviewer.getLastName() != null ? interviewer.getLastName() : "");

        List<QuestionDAO> questionsDAO = this.questionRepository.findAllByInterviewId(interviewDAO.getId());
        questionsDAO = questionsDAO.stream().map(x ->
            x.toBuilder().startTimePredicted(x.getStartTimeEpoch() - interviewDAO.getVideoStartTime()).build()
        ).collect(Collectors.toList());

        try {
            final QuestionTimeTaggingRequest questionTimeTaggingRequest = new QuestionTimeTaggingRequest()
                .toBuilder().zoomMeetingId(zoomMeetingId)
                .questions(questionsDAO).interviewerInitials(interviewerFullName)
                .interviewerInitials(interviewer.getInitials()).build();

            final QuestionTimeTaggingResponse questionTimeTaggingResponse =
                this.zoomTranscriptTimeAnnotatorFeignClient
                    .getQuestionStartTimePredicted(questionTimeTaggingRequest);

            this.updateQuestions(questionsDAO, questionTimeTaggingResponse, interviewDAO);
            return Boolean.TRUE;

        } catch (final Exception ex) {
            log.error(ex);
            return Boolean.FALSE;
        }
    }

    private void updateQuestions(List<QuestionDAO> questionDAOS,
                                 QuestionTimeTaggingResponse questionTimeTaggingResponse,
                                 InterviewDAO interviewDAO) {
        final HashMap<String, QuestionWithPredictedTime> questionIdToPredictedTimeMapper =
            new HashMap<String, QuestionWithPredictedTime>();

        questionTimeTaggingResponse.getQuestions().forEach(
            x -> questionIdToPredictedTimeMapper.put(
                x.getId(), x)
        );

        for (QuestionDAO questionDAO : questionDAOS) {
            try {
                final QuestionWithPredictedTime questionWithPredictedTime =
                    questionIdToPredictedTimeMapper.get(questionDAO.getId());

                questionRepository.save(questionDAO.toBuilder()
                    .startTimePredicted(interviewDAO.getVideoStartTime()
                        + this.dateUtils.convertHHMMSSToSeconds(questionWithPredictedTime.getPredictedStartTime()))
                    .questionToTranscriptMatchScore(questionWithPredictedTime.getQuestionToTranscriptMatchScore())
                    .transcriptText(questionWithPredictedTime.getTranscriptText())
                    .build());
            } catch (final Exception ex) {
                log.error(ex);
                log.error("Exception while processing time tagging for questionId %s", questionDAO.getId());
            }
        }
    }
}
