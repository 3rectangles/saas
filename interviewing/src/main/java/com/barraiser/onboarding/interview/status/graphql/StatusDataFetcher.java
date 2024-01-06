package com.barraiser.onboarding.interview.status.graphql;

import com.barraiser.common.graphql.types.Interview;
import com.barraiser.onboarding.dal.InterviewHistoryDAO;
import com.barraiser.onboarding.dal.InterviewStatus;
import com.barraiser.onboarding.dal.StatusDAO;
import com.barraiser.onboarding.graphql.MultiParentTypeDataFetcher;
import com.barraiser.common.graphql.types.Partner;
import com.barraiser.onboarding.interview.InterviewHistoryManager;
import com.barraiser.onboarding.interview.InterviewStatusManager;
import com.barraiser.onboarding.interview.status.EvaluationStatusManager;
import com.barraiser.common.graphql.types.StatusType;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class StatusDataFetcher implements MultiParentTypeDataFetcher {
    private final EvaluationStatusManager evaluationStatusManager;
    private final InterviewStatusManager interviewStatusManager;
    private final InterviewHistoryManager interviewHistoryManager;

    @Override
    public List<List<String>> typeNameMap() {
        return List.of(
            List.of("Partner", "partnerEvaluationStatus"),
            List.of("Interview", "interviewStatus")
        );
    }

    @Override
    public Object get(final DataFetchingEnvironment environment) throws Exception {
        final GraphQLObjectType type = (GraphQLObjectType) environment.getParentType();

        if(type.getName().equals("Partner")) {
            final Partner partner = environment.getSource();

            return DataFetcherResult.newResult()
                .data(evaluationStatusManager.getAllStatusForPartner(partner.getId()).stream()
                    .filter(s -> !EvaluationStatusManager.BARRAISER_PARTNER_ID.equals(s.getPartnerId()))
                    .map(s -> StatusType.builder()
                        .id(s.getId())
                        .displayStatus(s.getDisplayStatus())
                        .build())
                    .collect(Collectors.toList()))
                .build();
        } else if (type.getName().equals("Interview")) {
            final Interview interview = environment.getSource();
            final InterviewHistoryDAO interviewChangeHistoryDAO = this.interviewHistoryManager
                .getLatestChangeInStatusOfInterview(interview.getId(), interview.getStatus());
            final StatusDAO statusDAO = this.interviewStatusManager.getStatusOfInterview(InterviewStatus.fromString(interview.getStatus()));
            final StatusType status = StatusType.builder()
                .id(statusDAO.getId())
                .internalStatus(statusDAO.getInternalStatus())
                .displayStatus(statusDAO.getDisplayStatus())
                .transitionedOn(interviewChangeHistoryDAO.getCreatedOn().getEpochSecond())
                .build();
            return DataFetcherResult.newResult().data(status).build();
        }

        throw new IllegalArgumentException();
    }
}
