/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.dal.specifications.CancellationReasonSpecifications;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.MultiParentTypeDataFetcher;
import com.barraiser.common.graphql.input.ReasonInput;
import com.barraiser.common.graphql.types.Reason;
import com.barraiser.common.graphql.types.Evaluation;
import com.barraiser.common.graphql.types.Interview;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@AllArgsConstructor
@Component
public class ReasonsDataFetcher implements MultiParentTypeDataFetcher {
	private static final String TYPE_INTERVIEW = "Interview";
	private static final String TYPE_EVALUATION = "Evaluation";
	private static final List<String> cancellationReasonIdsToBeExcluded = List.of("11", "24", "34");

	private final CancellationReasonSpecifications cancellationReasonSpecifications;
	private final CancellationReasonRepository cancellationReasonRepository;
	private final WaitingReasonRepository waitingReasonRepository;
	private final GraphQLUtil graphQLUtil;
	private final ReasonRepository reasonRepository;
	private final ObjectMapper objectMapper;

	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {

		final GraphQLObjectType type = (GraphQLObjectType) environment.getParentType();

		Reason reason = null;
		log.info("Parent type : {}", type);
		if (type.getName().equals(QUERY_TYPE)) {
			final List<Reason> reasons;
			final ReasonInput input = this.graphQLUtil.getInput(environment, ReasonInput.class);
			if (environment.getFieldDefinition().getName().equals("getCancellationReasons")) {
				List<String> reasonTypes = new ArrayList<>();

				if (input.getReasonType() != null) {
					reasonTypes = List.of(input.getReasonType());
				}

				if (input.getReasonTypes() != null) {
					reasonTypes = input.getReasonTypes();
				}

				reasons = this.getCancellationReasons(reasonTypes, TYPE_INTERVIEW);
			} else if (environment.getFieldDefinition().getName().equals("getReopeningReasons")) {
				final List<ReasonDAO> reasonDAOS = this.reasonRepository
						.findAllByContext(ReasonContext.REOPEN.getValue());
				reasons = reasonDAOS.stream().map(x -> this.objectMapper.convertValue(x, Reason.class))
						.collect(Collectors.toList());
			} else if (environment.getFieldDefinition().getName().equals("getRedoReasons")) {
				final List<ReasonDAO> reasonDAOS = this.reasonRepository
						.findAllByContext(ReasonContext.REDO.getValue());
				reasons = reasonDAOS.stream().map(x -> this.objectMapper.convertValue(x, Reason.class))
						.collect(Collectors.toList());
			} else if (environment.getFieldDefinition().getName().equals("getCandidateRejectionReasons")) {
				final List<ReasonDAO> reasonDAOS = this.reasonRepository
						.findAllByContext(ReasonContext.CANDIDATURE_REJECTION.getValue());
				reasons = reasonDAOS.stream().map(x -> this.objectMapper.convertValue(x, Reason.class))
						.collect(Collectors.toList());
			} else {
				throw new IllegalArgumentException(
						"Bad parent type while accessing reason type, please fix your query");
			}
			return DataFetcherResult.newResult()
					.data(reasons)
					.build();

		} else if (type.getName().equals(TYPE_INTERVIEW)) {
			final Interview interview = environment.getSource();

			if (interview.getCancellationReasonId() != null) {
				reason = this.getCancellationReason(interview.getCancellationReasonId(), TYPE_INTERVIEW);
			}

		} else if (type.getName().equals(TYPE_EVALUATION)) {
			final Evaluation evaluation = environment.getSource();

			if (environment.getFieldDefinition().getName().equals("cancellationReason")) {
				if (evaluation.getCancellationReasonId() != null) {
					reason = this.getCancellationReason(evaluation.getCancellationReasonId(), TYPE_EVALUATION);
				}
			} else if (environment.getFieldDefinition().getName().equals("waitingReason")) {
				if (evaluation.getWaitingReasonId() != null) {
					reason = this.getWaitingReason(evaluation.getWaitingReasonId(), TYPE_EVALUATION);
				}
			}
		}
		return DataFetcherResult.newResult()
				.data(reason)
				.build();
	}

	public Reason getCancellationReason(final String cancellationReasonId, final String processType) {

		final CancellationReasonDAO cancellationReasonDAO = this.cancellationReasonRepository
				.findByIdAndProcessType(cancellationReasonId, processType).get();

		return Reason.builder()
				.reason(cancellationReasonDAO.getCancellationReason())
				.id(cancellationReasonDAO.getId())
				.type(cancellationReasonDAO.getCancellationType())
				.displayText(cancellationReasonDAO.getCustomerDisplayableReason())
				.customerDisplayableReason(cancellationReasonDAO.getCustomerDisplayableReason())
				.nonReschedulableReason(cancellationReasonDAO.getNonReschedulableReason())
				.build();
	}

	public List<Reason> getCancellationReasons(final List<String> reasonTypes, final String processType) {

		final Specification<CancellationReasonDAO> specification = this.cancellationReasonSpecifications
				.getCancellationReasonSpecifications(reasonTypes, Boolean.TRUE, processType);

		return this.cancellationReasonRepository.findAll(specification)
				.stream()
				.filter(x -> !cancellationReasonIdsToBeExcluded.contains(x.getId()))
				.sorted(Comparator.comparing(CancellationReasonDAO::getOrderIndex))
				.map(x -> Reason.builder()
						.id(x.getId())
						.type(x.getCancellationType())
						.displayText(x.getCustomerDisplayableReason())
						.customerDisplayableReason(x.getCustomerDisplayableReason())
						.reason(x.getCancellationReason())
						.nonReschedulableReason(x.getNonReschedulableReason())
						.build())
				.collect(Collectors.toList());
	}

	public Reason getWaitingReason(final String waitingReasonId, final String processType) {

		final WaitingReasonDAO waitingReasonDAO = this.waitingReasonRepository
				.findByIdAndProcessType(waitingReasonId, processType).get();

		return Reason.builder()
				.id(waitingReasonDAO.getId())
				.reason(waitingReasonDAO.getReason())
				.customerDisplayableReason(waitingReasonDAO.getCustomerDisplayableReason())
				.build();
	}

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(
				List.of(QUERY_TYPE, "getCancellationReasons"),
				List.of(TYPE_INTERVIEW, "cancellationReason"),
				List.of(TYPE_EVALUATION, "cancellationReason"),
				List.of(TYPE_EVALUATION, "waitingReason"),
				List.of(QUERY_TYPE, "getReopeningReasons"),
				List.of(QUERY_TYPE, "getRedoReasons"),
				List.of(QUERY_TYPE, "getCandidateRejectionReasons"));
	}
}
