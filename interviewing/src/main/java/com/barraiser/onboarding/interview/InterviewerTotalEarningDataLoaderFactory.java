/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.dal.Money;
import com.barraiser.common.utilities.DateUtils;
import com.barraiser.onboarding.dal.CurrencyDAO;
import com.barraiser.onboarding.dal.ExpertDAO;
import com.barraiser.onboarding.dal.ExpertRepository;
import com.barraiser.onboarding.dal.InterviewCostDAO;
import com.barraiser.onboarding.expert.CostUtil;
import com.barraiser.onboarding.graphql.DataLoaderFactory;
import lombok.AllArgsConstructor;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class InterviewerTotalEarningDataLoaderFactory implements DataLoaderFactory<String, Money> {
	public static final String DATA_LOADER_NAME = "interviewer-totalEarning-dataLoader";
	private static final Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	private final ExpertRepository expertRepository;
	private final DateUtils utilities;
	private final InterviewCostRepository interviewCostRepository;
	private final CostUtil costUtil;

	private static final OffsetDateTime DATE_FIRST_APRIL_2021 = OffsetDateTime.of(2021, 4, 1, 0, 0, 0, 0,
			ZoneOffset.of(DateUtils.IST_TIMEZONE_OFFSET));

	@Override
	public String dataLoaderName() {
		return DATA_LOADER_NAME;
	}

	@Override
	public DataLoader<String, Money> getDataLoader() {
		return DataLoader.newMappedDataLoader(
				(Set<String> interviewers) -> CompletableFuture.supplyAsync(
						() -> getData(interviewers), executor));
	}

	@Override
	public Map<String, Money> getData(final Set<String> interviewerIds) {
		final Map<String, List<InterviewCostDAO>> interviewerToInterviewsCostMapping = this.interviewCostRepository
				.findAllByInterviewerIdIn(interviewerIds)
				.stream()
				.collect(Collectors.groupingBy(InterviewCostDAO::getInterviewerId));
		final List<ExpertDAO> interviewers = this.expertRepository.findAllByIdIn(interviewerIds);

		final Map<String, Money> interviewerToTotalEarningMapping = new HashMap<>();
		for (final ExpertDAO interviewer : interviewers) {
			final Money earningTillDate = this.getTotalEarningOfInterviewer(
					interviewerToInterviewsCostMapping.getOrDefault(interviewer.getId(), new ArrayList<>()),
					interviewer);
			interviewerToTotalEarningMapping.put(interviewer.getId(), earningTillDate);
		}
		return interviewerToTotalEarningMapping;
	}

	private Money getTotalEarningOfInterviewer(final List<InterviewCostDAO> interviews, final ExpertDAO interviewer) {
		final Double totalEarningAfterFY2020And2021 = this.getTotalEarningAfterFY2020And2021(interviews);
		final Double totalEarningForFY2020And2021 = this
				.getTotalEarningForFY2020And2021(interviewer);
		// TODO: adding default currency value, need to set this by partner config
		final CurrencyDAO currencyDAO = this.costUtil
				.getCurrencyForCurrencyCode(interviewer.getCurrency() == null ? "INR" : interviewer.getCurrency());
		return Money.builder()
				.value(totalEarningAfterFY2020And2021 + totalEarningForFY2020And2021)
				.currency(currencyDAO.getCurrencyCode())
				.symbol(currencyDAO.getSymbol())
				.build();
	}

	private Double getTotalEarningForFY2020And2021(final ExpertDAO expertDAO) {
		return expertDAO != null && expertDAO
				.getEarningForFinancialYear2020And2021() != null
						? expertDAO.getEarningForFinancialYear2020And2021()
						: Double.valueOf(0.0);
	}

	/*
	 * We calculate the cost from snapshot stored on interview cost table as it
	 * might happen that the base
	 * cost or multiplier of expert has changed. Hence we snapshot it and get the
	 * total from there.
	 **/
	private Double getTotalEarningAfterFY2020And2021(final List<InterviewCostDAO> interviews) {
		final Long firstApril2021InEpoch = DATE_FIRST_APRIL_2021.toEpochSecond();
		final Long endDate = this.utilities.convertDateTimeToEpoch(Instant.now());
		return interviews.stream().filter(
				x -> x.getInterviewSnapshot().getStartDate() >= firstApril2021InEpoch &&
						x.getInterviewSnapshot().getStartDate() < endDate)
				.mapToDouble(
						InterviewCostDAO::getTotalAmount)
				.sum();
	}
}
