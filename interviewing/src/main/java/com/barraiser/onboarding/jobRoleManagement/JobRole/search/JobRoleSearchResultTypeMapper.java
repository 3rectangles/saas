/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.jobRoleManagement.JobRole.search;

import com.barraiser.common.graphql.input.FilterType;
import com.barraiser.common.graphql.input.SearchFilterInput;
import com.barraiser.common.graphql.types.JobRole;
import com.barraiser.common.graphql.types.SearchCard;
import com.barraiser.onboarding.common.search.db.SearchResult;
import com.barraiser.onboarding.common.search.graphql.types.SearchResultType;
import com.barraiser.onboarding.jobRoleManagement.JobRole.JobRoleSearchService;
import com.barraiser.onboarding.jobRoleManagement.JobRole.graphql.input.SearchJobRoleInput;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.barraiser.onboarding.common.Constants.PARTNERSHIP_MODEL_SAAS;

@Component
@AllArgsConstructor
public class JobRoleSearchResultTypeMapper {
	private final JobRoleSearchService jobRoleSearchUtil;

	/**
	 *
	 * @param searchJobRoleInput
	 * @param jobRoleSearchResult
	 * @param partnerModel
	 * @return
	 */
	public SearchResultType toSearchResultType(final SearchJobRoleInput searchJobRoleInput,
			final SearchResult<JobRole> jobRoleSearchResult, final String partnerModel) {
		final List<SearchCard> cards = this
				.addCardDetails(jobRoleSearchResult.getPageResult().get().collect(Collectors.toList()),
						this.getVersionForPartnerModel(partnerModel));
		List<FilterType> filtersApplied = new ArrayList<>();
		if (searchJobRoleInput.getSearchJobRoleQuery().getFilters() != null) {
			filtersApplied = searchJobRoleInput.getSearchJobRoleQuery().getFilters().stream()
					.map(this::toFilterType)
					.collect(Collectors.toList());
		}

		String context = (searchJobRoleInput.getPartnerId() != null)
				? this.jobRoleSearchUtil.getJobRoleContext(searchJobRoleInput.getPartnerId())
				: "";

		return SearchResultType.builder()
				.filterContext(context)
				.pageNumber(jobRoleSearchResult.getPageResult().getNumber())
				.pageSize(jobRoleSearchResult.getPageResult().getSize())
				.totalRecords(jobRoleSearchResult.getPageResult().getTotalElements())
				.totalPages(jobRoleSearchResult.getPageResult().getTotalPages())
				.records(jobRoleSearchResult.getPageResult().get().collect(Collectors.toList()))
				.cards(cards)
				.filtersApplied(filtersApplied)
				.aggregations(null)
				.build();
	}

	private List<SearchCard> addCardDetails(final List<Object> searchResultsList,
			final String version) {

		ArrayList<SearchCard> cards = new ArrayList<>();
		for (Object o : searchResultsList) {
			cards.add(
					SearchCard.builder()
							.type("job_role")
							.detail(o)
							.cardVersion(version)
							.build());
		}
		return cards;
	}

	public FilterType toFilterType(final SearchFilterInput searchFilterInput) {
		return FilterType.builder()
				.field(searchFilterInput.getField())
				.value(searchFilterInput.getValue())
				.build();
	}

	private String getVersionForPartnerModel(final String model) {
		if (model.equals(PARTNERSHIP_MODEL_SAAS))
			return "2.0";
		return "1.0";
	}
}
