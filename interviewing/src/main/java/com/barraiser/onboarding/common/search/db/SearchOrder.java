package com.barraiser.onboarding.common.search.db;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class SearchOrder {
    private String field;
    private Boolean sortByAscending = Boolean.TRUE;
}
