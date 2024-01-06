package com.barraiser.onboarding.search;

import com.barraiser.common.graphql.input.AggregateInput;
import com.barraiser.common.graphql.input.SearchFilterInput;
import com.barraiser.common.graphql.input.SearchOrderInput;
import com.barraiser.onboarding.common.search.db.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Component
public class SearchQueryMapperUtil {


    private List<SearchAggregationMapper> searchAggregationMappers;

    private List<SearchOrderMapper> searchOrderMappers;


    public List<SearchOrder> mapSearchOrder(final SearchOrderInput searchOrderInput) {
        for(final SearchOrderMapper mapper : this.searchOrderMappers) {
            if(mapper.field().equals(searchOrderInput.getField())) {
                return mapper.getSearchOrders(searchOrderInput);
            }
        }
        throw new IllegalArgumentException("sort for " + searchOrderInput.getField() + " is not defined");
    }

    public List<SearchAggregation> mapAggregation(final List<AggregateInput> aggregateInputs) {
        final List<SearchAggregation> searchAggregations = new ArrayList<>();
        for(final AggregateInput aggregateInput : aggregateInputs) {
            boolean mapperFound = false;
            for(final SearchAggregationMapper mapper : this.searchAggregationMappers) {
                if(mapper.field().equals(aggregateInput.getField())) {
                    searchAggregations.add(mapper.getSearchAggregation(aggregateInput));
                    mapperFound = true;
                    break;
                }
            }
            if(!mapperFound) {
                throw new IllegalArgumentException("Aggregation for " + aggregateInput.getField() + " not defined");
            }
        }
        return searchAggregations;
    }

}
