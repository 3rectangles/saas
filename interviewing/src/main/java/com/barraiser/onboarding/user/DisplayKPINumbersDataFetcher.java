/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.user;

import com.barraiser.onboarding.dal.ExpertRepository;
import com.barraiser.onboarding.dal.PartnerCompanyRepository;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.common.graphql.types.DisplayKPINumbers;
import com.barraiser.onboarding.interview.InterViewRepository;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DisplayKPINumbersDataFetcher implements NamedDataFetcher {

	private final ExpertRepository expertRepository;
	private final PartnerCompanyRepository partnerCompanyRepository;
	private final InterViewRepository interViewRepository;

	@Override
	public String name() {
		return "getDisplayKPINumbers";
	}

	@Override
	public String type() {
		return "Query";
	}

	@Override
	public Object get(DataFetchingEnvironment environment) throws Exception {

		final DisplayKPINumbers displayKPINumbers = DisplayKPINumbers.builder()
				.numberOfExperts(this.expertRepository.findAll().size())
				.numberOfPartners(this.partnerCompanyRepository.findAll().size())
				.numberOfInterviews(this.interViewRepository.findAllByCancellationTimeIsNull().size())
				.build();

		return DataFetcherResult.newResult()
				.data(displayKPINumbers)
				.build();

	}
}
