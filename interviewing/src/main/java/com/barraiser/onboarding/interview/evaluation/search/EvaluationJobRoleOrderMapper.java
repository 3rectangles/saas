package com.barraiser.onboarding.interview.evaluation.search;

import com.barraiser.onboarding.common.search.db.SearchOrder;
import com.barraiser.onboarding.common.search.db.SearchOrderMapper;
import com.barraiser.common.graphql.input.SearchOrderInput;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class EvaluationJobRoleOrderMapper implements SearchOrderMapper {
    public String field() {
        return "jobRole";
    }

    @Override
    public List<SearchOrder> getSearchOrders(SearchOrderInput searchOrderInput) {
        return List.of(
            SearchOrder.builder()
                .field("jobRoleName")
                .sortByAscending(searchOrderInput.getAscending())
                .build()
        );
    }
}
