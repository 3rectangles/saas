/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.expert;

import com.barraiser.common.graphql.input.PredictNumberOfInterviewsOfExpertInput;
import com.barraiser.common.graphql.types.PredictedNumberOfInterviewsOfExpert;
import com.barraiser.common.graphql.types.ValidationResult;
import com.barraiser.common.utilities.ObjectFieldsFilter;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.graphql.AuthorizationResult;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLQuery;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLQuery_deprecated;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.barraiser.onboarding.graphql.NamedDataFetcher.QUERY_TYPE;

@Component
public class PredictedNumberOfInterviewsDataFetcher
		extends AuthorizedGraphQLQuery_deprecated<PredictedNumberOfInterviewsOfExpert> {
	public static final Integer MAX_INTERVIEWS_THAT_EXPERT_CAN_TAKE_IN_A_MONTH = 100;

	private final GraphQLUtil graphQLUtil;
	private final ExpertInterviewSummaryRepository expertInterviewSummaryRepository;
	private final ExpertRepository expertRepository;
	private final ExpertInterviewsPredictionHistoryRepository expertInterviewsPredictionHistoryRepository;
	private final ObjectMapper objectMapper;

	public PredictedNumberOfInterviewsDataFetcher(final NumberOfInterviewsPredictionAuthorizer abacAuthorizer,
			final ObjectFieldsFilter<PredictedNumberOfInterviewsOfExpert> objectFieldsFilter,
			final GraphQLUtil graphQLUtil, final ExpertInterviewSummaryRepository expertInterviewSummaryRepository,
			final ExpertRepository expertRepository,
			final ExpertInterviewsPredictionHistoryRepository expertInterviewsPredictionHistoryRepository,
			final ObjectMapper objectMapper) {
		super(abacAuthorizer, objectFieldsFilter);
		this.graphQLUtil = graphQLUtil;
		this.expertInterviewSummaryRepository = expertInterviewSummaryRepository;
		this.expertRepository = expertRepository;
		this.expertInterviewsPredictionHistoryRepository = expertInterviewsPredictionHistoryRepository;
		this.objectMapper = objectMapper;
	}

	@Override
	protected PredictedNumberOfInterviewsOfExpert fetch(final DataFetchingEnvironment environment,
			final AuthorizationResult authorizationResult) {
		final PredictNumberOfInterviewsOfExpertInput input = this.graphQLUtil.getInput(environment,
				PredictNumberOfInterviewsOfExpertInput.class);
		return this.predictNumberOfInterviewsForExpert(input.getExpertId(), input.getMinCost());
	}

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(
				List.of(QUERY_TYPE, "predictNumberOfInterviewsOfExpert"));
	}

	private PredictedNumberOfInterviewsOfExpert predictNumberOfInterviewsForExpert(final String expertId,
			final Double minCost) {
		final Optional<ExpertInterviewSummaryDAO> expertInterviewSummaryDAO = this.expertInterviewSummaryRepository
				.findByExpertIdOrderByCreatedOnDesc(expertId).stream().findFirst();
		final ExpertDAO expertDAO = this.expertRepository.findById(expertId).get();
		PredictedNumberOfInterviewsOfExpert predictedNumberOfInterviewsOfExpert = PredictedNumberOfInterviewsOfExpert
				.builder()
				.build();
		if (expertInterviewSummaryDAO.isPresent()) {
			final Integer expectedNumberOfInterviews = this.getNumberOfExpectedInterviewsForExpert(
					expertInterviewSummaryDAO.get().getSummary(),
					expertDAO.getBaseCost() * expertDAO.getMultiplier(), minCost);
			predictedNumberOfInterviewsOfExpert = predictedNumberOfInterviewsOfExpert.toBuilder()
					.expectedNumberOfInterviews(expectedNumberOfInterviews)
					.build();
			this.savePrediction(expertId, expertInterviewSummaryDAO.get().getSummary(), minCost,
					expectedNumberOfInterviews, expertDAO.getBaseCost() * expertDAO.getMultiplier());
		} else {
			predictedNumberOfInterviewsOfExpert = predictedNumberOfInterviewsOfExpert.toBuilder()
					.validationResult(this.getValidationResult())
					.build();
		}
		return predictedNumberOfInterviewsOfExpert;
	}

	private Integer getNumberOfExpectedInterviewsForExpert(final ExpertInterviewSummary expertInterviewSummary,
			final Double maxCostOfExpert, final Double minCostOfExpert) {
		if (minCostOfExpert > maxCostOfExpert) {
			return null;
		}
		final int interviewsThatExpertCanTakeInAMonth = Math.min((int) Math.ceil(expertInterviewSummary
				.getNumberOfInterviewsTaken() *
				(Math.pow(0.95,
						Math.ceil(expertInterviewSummary.getNumberOfInterviewsTaken()
								/ 10D))
						/
						(Math.pow(minCostOfExpert / (maxCostOfExpert),
								2 / (1 + expertInterviewSummary.getUtilisation()))))),
				5 * expertInterviewSummary
						.getNumberOfInterviewsTaken());
		return Math.min(interviewsThatExpertCanTakeInAMonth, MAX_INTERVIEWS_THAT_EXPERT_CAN_TAKE_IN_A_MONTH);
	}

	private ValidationResult getValidationResult() {
		final ValidationResult validationResult = new ValidationResult();
		validationResult.setOverallErrors(List.of(
				"Expected interviews will be predicted after 7 days of onboarding and completion of at least 1 interviews in last 30 days. However, you can update the minimum price"));
		return validationResult;
	}

	private void savePrediction(final String expertId, final ExpertInterviewSummary expertInterviewSummary,
			final Double minCost, final Integer predictedNumberOfInterviews, final Double maxCost) {
		final Map<String, java.lang.Object> payload = this.objectMapper.convertValue(expertInterviewSummary,
				new TypeReference<>() {
				});
		payload.put("maxCost", maxCost);
		this.expertInterviewsPredictionHistoryRepository.save(
				ExpertInterviewsPredictionHistoryDAO.builder()
						.id(UUID.randomUUID().toString())
						.expertId(expertId)
						.payload(payload)
						.minCost(minCost)
						.predictedNumberOfInterviews(predictedNumberOfInterviews)
						.build());
	}
}
