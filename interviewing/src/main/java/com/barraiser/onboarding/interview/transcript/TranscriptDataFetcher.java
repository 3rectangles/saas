package com.barraiser.onboarding.interview.transcript;

import com.barraiser.common.graphql.types.Interview;
import com.barraiser.common.model.TranscriptDTO;
import com.barraiser.onboarding.graphql.MultiParentTypeDataFetcher;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Log4j2
@Component
@AllArgsConstructor
public class TranscriptDataFetcher implements MultiParentTypeDataFetcher {
    private final TranscriptClient transcriptClient;

    @Override
    public List<List<String>> typeNameMap() {
        return List.of(
            List.of("Interview", "transcript")
        );
    }

    @Override
    public Object get(DataFetchingEnvironment environment) throws Exception {
        final Interview interview = environment.getSource();
        final TranscriptDTO transcript = this.transcriptClient.getInterviewTranscript(interview.getId());
        return DataFetcherResult.newResult().data(transcript).build();
    }
}
