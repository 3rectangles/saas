package com.barraiser.onboarding.interview;

import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.QuestionDAO;
import com.barraiser.onboarding.dal.QuestionRepository;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.common.graphql.types.Question;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class FollowUpQuestionDataFetcher implements NamedDataFetcher {
    private final QuestionRepository questionRepository;
    private final InterViewRepository interViewRepository;
    private final ObjectMapper objectMapper;

    @Override
    public String name() {
        return "followUpQuestions";
    }

    @Override
    public String type() {
        return "Question";
    }

    @Override
    public Object get(final DataFetchingEnvironment environment) throws Exception {
        final Question question = environment.getSource();
        final String interviewId = question.getInterviewId();


        final Optional<InterviewDAO> interview = this.interViewRepository.findById(interviewId);
        final Long videoStartTime = interview.isPresent() ? interview.get().getVideoStartTime() : null;

        final List<QuestionDAO> followUpQuestionDAOs = this.questionRepository
            .findAllByMasterQuestionIdAndRescheduleCountOrderByStartTimeEpochAsc(question.getId(),
                interview.get().getRescheduleCount());

        final List<Question> followUpQuestions =  followUpQuestionDAOs.stream().map(x -> {
            final Question followUpQuestion = this.objectMapper.convertValue(x, Question.class);
            if(x.getStartTimeEpoch() != null && videoStartTime != null) {
                return followUpQuestion.toBuilder().startTime(x.getStartTimeEpoch() - videoStartTime).build();
            }
            return followUpQuestion;

        }).collect(Collectors.toList());

        return DataFetcherResult.newResult()
                .data(followUpQuestions)
                .build();
    }
}
