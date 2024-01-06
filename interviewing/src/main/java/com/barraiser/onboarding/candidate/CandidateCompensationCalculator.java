/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.candidate;

import com.barraiser.onboarding.cms.CMSManager;
import com.barraiser.onboarding.cms.pages.CandidateCompensationConstants;
import com.barraiser.onboarding.dal.CandidateCompensationCalculatorHistoryDAO;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.common.graphql.input.CandidateCompensationCalculatorInput;
import com.barraiser.common.graphql.types.CandidateCompensation;
import com.buttercms.model.Page;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class CandidateCompensationCalculator implements NamedDataFetcher {

	private final GraphQLUtil graphQLUtil;
	private final CMSManager cmsManager;
	private final CandidateCompensationCalculatorHistoryRepository candidateCompensationCalculatorHistoryRepository;

	@Override
	public Object get(DataFetchingEnvironment environment) throws Exception {
		final CandidateCompensationCalculatorInput input = this.graphQLUtil.getInput(environment,
				CandidateCompensationCalculatorInput.class);
		final List<CandidateCompensation> compensation = this.getAndSaveCandidateCompensation(input);
		return DataFetcherResult.newResult()
				.data(compensation).build();
	}

	public List<CandidateCompensation> getAndSaveCandidateCompensation(
			final CandidateCompensationCalculatorInput input) {
		final CandidateCompensationConstants.Constants constants = this.getSlopeAndConstantFromCMS(input.getDomainId());
		final Double currentCTC = input.getCurrentCTC() * 100000;
		final Double slope = constants.getSlope();
		final Double constant = constants.getConstant();
		final Integer workExperienceRangeNo = (input.getWorkExperience() + 1) / 12
				+ ((input.getWorkExperience() + 1) % 12 != 0 ? 1 : 0);
		final Double temp = workExperienceRangeNo * slope + constant;
		final double C1 = currentCTC > temp ? currentCTC : currentCTC + (currentCTC / 10);
		this.saveCalculatorHistory(input, slope, constant);
		return this.getCompensationForAllBGS(C1, slope);
	}

	public List<CandidateCompensation> getCompensationForAllBGS(final Double C1, final Double slope) {
		List<CandidateCompensation> compensation = new ArrayList<>();
		int bgsScore = 500;

		while (bgsScore <= 800) {
			final double C2 = Math.abs(((bgsScore - 500) / 20) * (slope));
			final int minSalary = (int) Math.floor((bgsScore == 500 ? C1 + C2 / 8 : C1 + C2 / 4) / 100000);
			int maxSalary = (int) Math.floor((bgsScore == 500 ? C1 + C2 / 4 : C1 + C2 / 2) / 100000);
			maxSalary = minSalary == maxSalary ? maxSalary + 1 : maxSalary;
			compensation.add(CandidateCompensation.builder()
					.bgsScore(bgsScore)
					.minSalary(minSalary)
					.maxSalary(maxSalary)
					.build());
			bgsScore += 20;
		}
		return compensation;
	}

	public CandidateCompensationConstants.Constants getSlopeAndConstantFromCMS(final String domainId) {
		final Page<CandidateCompensationConstants> expertsAverageCTCMatricesPage = this.cmsManager
				.getPage("candidate-compensation-constants", CandidateCompensationConstants.class, true);

		return expertsAverageCTCMatricesPage.getFields().getConstants().stream()
				.filter(x -> domainId.equals(x.getDomainId())).collect(Collectors.toList()).get(0);
	}

	private void saveCalculatorHistory(final CandidateCompensationCalculatorInput input, final Double slope,
			final Double constant) {
		this.candidateCompensationCalculatorHistoryRepository.save(CandidateCompensationCalculatorHistoryDAO.builder()
				.domainId(input.getDomainId())
				.currentCTC(input.getCurrentCTC())
				.workExperience(input.getWorkExperience())
				.slope(slope)
				.constant(constant)
				.userIdentity(input.getUserIdentity())
				.build());
	}

	@Override
	public String name() {
		return "getCandidateCompensations";
	}

	@Override
	public String type() {
		return QUERY_TYPE;
	}
}
