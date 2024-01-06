package com.barraiser.onboarding.interview;

import com.barraiser.common.utilities.DateUtils;
import com.barraiser.onboarding.dal.QcCommentDAO;
import com.barraiser.onboarding.dal.QcCommentRepository;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.common.graphql.types.Feedback;
import com.barraiser.common.graphql.types.QcComment;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class QcCommentDataFetcher implements NamedDataFetcher {
    private final QcCommentRepository qcCommentRepository;
    private final ObjectMapper objectMapper;
    private final DateUtils dateUtils;


    @Override
    public String name() {
        return "qcComments";
    }

    @Override
    public String type() {
        return "Feedback";
    }

    @Override
    public Object get(final DataFetchingEnvironment environment) throws Exception {
        final Feedback feedback = environment.getSource();

        List<QcCommentDAO> qcCommentsDAO = this.qcCommentRepository.findByFeedbackIdOrderByCreatedOnDesc(feedback.getId());

        List<QcComment> qcComments = qcCommentsDAO.stream()
            .map(x -> QcComment.builder()
                .id(x.getId())
                .comment(x.getComment())
                .commentedById(x.getCommentedBy() != null ? x.getCommentedBy().getId() : null)
                .updatedAt(this.dateUtils.convertDateTimeToEpoch(x.getCreatedOn()))
                .build()).collect(Collectors.toList());

        return DataFetcherResult.newResult()
            .data(qcComments)
            .build();
    }
}
