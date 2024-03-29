package com.barraiser.onboarding.interview.evaluation.search;

import com.barraiser.onboarding.common.search.db.SearchAggregation;
import com.barraiser.onboarding.common.search.db.SearchAggregationMapper;
import com.barraiser.common.graphql.input.AggregateInput;
import com.barraiser.onboarding.dal.EvaluationDAO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EvaluationStatusAggregationMapper implements SearchAggregationMapper<EvaluationDAO> {
    @Override
    public String field() {
        return "status";
    }

    @Override
    public SearchAggregation getSearchAggregation(AggregateInput aggregateInput) {
        return SearchAggregation.builder()
            .name("status")
            .field("displayStatus")
            .build();
    }
}
