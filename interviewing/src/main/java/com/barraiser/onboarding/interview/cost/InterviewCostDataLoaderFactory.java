/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.cost;

import com.barraiser.common.dal.Money;
import com.barraiser.onboarding.dal.CurrencyDAO;
import com.barraiser.onboarding.dal.InterviewCostDAO;
import com.barraiser.onboarding.expert.CostUtil;
import com.barraiser.onboarding.graphql.DataLoaderFactory;
import com.barraiser.onboarding.interview.InterviewCostRepository;
import lombok.AllArgsConstructor;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class InterviewCostDataLoaderFactory implements DataLoaderFactory<InterviewCostCriteria, Money> {
	public static final String DATA_LOADER_NAME = "interview-cost-dataloader";
	private static final Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	private final InterviewCostRepository interviewCostRepository;
	private final CostUtil costUtil;

	@Override
	public String dataLoaderName() {
		return DATA_LOADER_NAME;
	}

	@Override
	public DataLoader<InterviewCostCriteria, Money> getDataLoader() {
		return DataLoader.newMappedDataLoader(
				(Set<InterviewCostCriteria> interviewCostCriterias) -> CompletableFuture.supplyAsync(
						() -> getData(interviewCostCriterias), executor));
	}

	@Override
	public Map<InterviewCostCriteria, Money> getData(final Set<InterviewCostCriteria> costCriteriaSet) {
		final List<String> interviewIds = costCriteriaSet.stream().map(InterviewCostCriteria::getInterviewId).distinct()
				.collect(Collectors.toList());
		final List<InterviewCostDAO> interviewCostDAOs = this.interviewCostRepository
				.findAllByInterviewIdIn(interviewIds);
		HashMap<InterviewCostCriteria, Money> criteriaToMoneyMapping = new HashMap<>();
		final List<CurrencyDAO> currencyDAOs = this.costUtil.getCurrencies(
				interviewCostDAOs.stream().map(InterviewCostDAO::getCurrencyId).collect(Collectors.toList()));
		for (final InterviewCostCriteria ic : costCriteriaSet) {
			criteriaToMoneyMapping.put(ic, this.getCostOfInterview(ic, interviewCostDAOs, currencyDAOs));
		}
		return criteriaToMoneyMapping;
	}

	private Money getCostOfInterview(final InterviewCostCriteria criteria,
			final List<InterviewCostDAO> interviewCostDAOs, final List<CurrencyDAO> currencyDAOs) {
		final Optional<InterviewCostDAO> interviewCostDAO = interviewCostDAOs.stream()
				.filter(x -> x.getInterviewId().equals(criteria.getInterviewId()) &&
						x.getRescheduleCount().equals(criteria.getRescheduleCount())
						&& x.getInterviewerId().equals(criteria.getInterviewerId()))
				.findFirst();
		if (interviewCostDAO.isEmpty()) {
			return null;
		}
		final CurrencyDAO currencyDAO = currencyDAOs.stream()
				.filter(x -> x.getId().equals(interviewCostDAO.get().getCurrencyId())).findFirst().get();
		return Money.builder()
				.symbol(currencyDAO.getSymbol())
				.value(interviewCostDAO.get().getTotalAmount())
				.currency(currencyDAO.getCurrencyCode())
				.build();
	}
}
