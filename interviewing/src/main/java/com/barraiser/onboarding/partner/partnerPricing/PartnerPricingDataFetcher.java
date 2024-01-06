/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.partner.partnerPricing;

import com.barraiser.common.graphql.types.Partner;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class PartnerPricingDataFetcher implements NamedDataFetcher<Object> {

	@Override
	public String name() {
		return "pricing";
	}

	@Override
	public String type() {
		return "Partner";
	}

	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {
		final Partner partner = environment.getSource();
		return environment
				.getDataLoader(PartnerPricingDataLoaderFactory.DATA_LOADER_NAME)
				.load(partner.getId());
	}
}
