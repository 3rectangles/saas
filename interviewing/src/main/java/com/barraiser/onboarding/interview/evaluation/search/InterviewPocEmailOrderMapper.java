/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.search;

import com.barraiser.common.graphql.input.SearchOrderInput;
import com.barraiser.onboarding.common.search.db.SearchOrder;
import com.barraiser.onboarding.common.search.db.SearchOrderMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class InterviewPocEmailOrderMapper implements SearchOrderMapper {
	@Override
	public String field() {
		return "pocEmail";
	}

	@Override
	public List<SearchOrder> getSearchOrders(SearchOrderInput searchOrderInput) {
		return List.of(
				SearchOrder.builder()
						.field("pocEmail")
						.sortByAscending(searchOrderInput.getAscending())
						.build());
	}
}
