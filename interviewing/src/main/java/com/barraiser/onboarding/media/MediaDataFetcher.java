package com.barraiser.onboarding.media;

import com.barraiser.common.model.Media;
import com.barraiser.onboarding.graphql.MultiParentTypeDataFetcher;
import com.barraiser.common.graphql.types.Interview;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@AllArgsConstructor
@Component
public class MediaDataFetcher implements MultiParentTypeDataFetcher {

    private static final String TYPE_INTERVIEW = "Interview";
    private final MediaManagementServiceFeignClient mediaManagementServiceFeignClient;

    @Override
    public List<List<String>> typeNameMap() {
        return List.of(
            List.of(TYPE_INTERVIEW, "media")
        );
    }

    @Override
    public Object get(final DataFetchingEnvironment environment) throws Exception {

        final GraphQLObjectType type = (GraphQLObjectType) environment.getParentType();
        final Interview interview = environment.getSource();
        List<Media> interviewMedia = new ArrayList<>();

        if (type.getName().equals(TYPE_INTERVIEW)) {
            interviewMedia = this.mediaManagementServiceFeignClient.getInterviewMedia(interview.getId());
        }

        return DataFetcherResult.newResult()
            .data(interviewMedia)
            .build();
    }
}
