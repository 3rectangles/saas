package com.barraiser.onboarding.interview;

import com.barraiser.onboarding.graphql.NamedDataFetcher;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class CandidateBgsCostDataFetcher implements NamedDataFetcher<DataFetcherResult<Object>> {
    final private BgsEnquiryManager bgsEnquiryManager;

    @Override
    public DataFetcherResult<Object> get(final DataFetchingEnvironment environment) throws Exception {
        return DataFetcherResult.newResult()
            .data(bgsEnquiryManager.getCandidateBgsCost())
            .build();
    }

    @Override
    public String name() {
        return "getCandidateBgsCost";
    }

    @Override
    public String type() {
        return QUERY_TYPE;
    }
}
