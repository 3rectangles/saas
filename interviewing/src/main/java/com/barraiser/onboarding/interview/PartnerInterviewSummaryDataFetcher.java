/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.types.Partner;
import com.barraiser.common.graphql.types.PartnerInterviewSummary;
import com.barraiser.onboarding.dal.PartnerCompanyDAO;
import com.barraiser.onboarding.dal.PartnerCompanyRepository;
import com.barraiser.onboarding.dal.PartnerInterviewSummaryDAO;
import com.barraiser.onboarding.dal.PartnerInterviewSummaryRepository;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Log4j2
@Component
@AllArgsConstructor
public class PartnerInterviewSummaryDataFetcher implements NamedDataFetcher {
	public static final String UNABLE_TO_FIND_SUMMARY_FOR_PARTNER = "Unable to find Summary for Partner";
	public static final String UNABLE_TO_FIND_PARTNER = "Unable to find Partner";
	public static final String RESPONSE_ERROR = "Either this page does not exists or you do not have the permissions to view this page";
	private final PartnerCompanyRepository partnerCompanyRepository;
	private final PartnerInterviewSummaryRepository partnerInterviewSummaryRepository;

	@Override
	public String name() {
		return "partnerInterviewSummary";
	}

	@Override
	public String type() {
		return "Partner";
	}

	@Override
	public Object get(final DataFetchingEnvironment environment) {
		final Partner input = environment.getSource();
		final String partnerId = input.getId();

		if (partnerId != null) {
			final PartnerCompanyDAO partnerCompany = this.partnerCompanyRepository
					.findById(partnerId)
					.orElseThrow(() -> new IllegalArgumentException(RESPONSE_ERROR));
			// log.info("Get Summary for partner_id : {}", partnerCompany.getId());
			return DataFetcherResult.newResult()
					.data(this.getPartnerInterviewingSummaryDetails(partnerCompany))
					.build();
		} else {
			throw new IllegalArgumentException(UNABLE_TO_FIND_PARTNER);
		}
	}

	private PartnerInterviewSummary getPartnerInterviewingSummaryDetails(
			PartnerCompanyDAO partnerCompany) {
		Optional<PartnerInterviewSummaryDAO> partnerInterviewingSummaryDAOOptional = this.partnerInterviewSummaryRepository
				.findById(partnerCompany.getId());

		if (partnerInterviewingSummaryDAOOptional.isPresent()) {
			return PartnerInterviewSummary.builder()
					.averageRating(
							partnerInterviewingSummaryDAOOptional.get().getAverageRating())
					.totalReviewCount(
							partnerInterviewingSummaryDAOOptional.get().getTotalReviewCount())
					.build();
		} else {
			return PartnerInterviewSummary.builder().build();
		}
	}
}
