package com.barraiser.onboarding.common.search.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SearchResult<T> {
    private Page<T> pageResult;
    private List<AggregationResult> aggregationResults;
}
