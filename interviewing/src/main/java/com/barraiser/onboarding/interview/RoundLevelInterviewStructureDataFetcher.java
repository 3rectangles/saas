/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.DTO.pricing.JobRoleBasedPricingRequestDTO;
import com.barraiser.common.DTO.pricing.JobRoleBasedPricingResponseDTO;
import com.barraiser.common.graphql.types.Interview;
import com.barraiser.common.graphql.types.RoundLevelInterviewStructure;
import com.barraiser.onboarding.dal.JobRoleToInterviewStructureDAO;
import com.barraiser.onboarding.dal.JobRoleToInterviewStructureRepository;
import com.barraiser.onboarding.graphql.MultiParentTypeDataFetcher;
import com.barraiser.common.graphql.types.JobRole;
import com.barraiser.onboarding.partner.partnerPricing.PricingServiceClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import com.barraiser.common.graphql.input.CategoryCutoffInput;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class RoundLevelInterviewStructureDataFetcher implements MultiParentTypeDataFetcher {
	private final JobRoleToInterviewStructureRepository jobRoleToInterviewStructureRepository;
	private final RoundLevelInterviewStructureMapper roundLevelInterviewStructureMapper;
	private final PricingServiceClient pricingServiceClient;

	@Override
	public Object get(DataFetchingEnvironment environment) throws Exception {
		final GraphQLObjectType type = (GraphQLObjectType) environment.getParentType();
		if (type.getName().equals("JobRole")) {
			final JobRole jobRole = environment.getSource();
			final String jobRoleId = jobRole.getId();
			final Integer jobRoleVersion = jobRole.getVersion() == null ? 0 : jobRole.getVersion();

			final List<JobRoleToInterviewStructureDAO> jobRoleToInterviewStructureDAOs = this.jobRoleToInterviewStructureRepository
					.findAllByJobRoleIdAndJobRoleVersionOrderByOrderIndexAsc(
							jobRoleId,
							jobRoleVersion);

			final JobRoleBasedPricingRequestDTO jobRoleBasedPricingRequestDTO = JobRoleBasedPricingRequestDTO.builder()
					.jobRoleId(jobRoleId)
					.interviewStructureIds(jobRoleToInterviewStructureDAOs.stream()
							.map(JobRoleToInterviewStructureDAO::getInterviewStructureId).collect(Collectors.toList()))
					.build();
			final Optional<JobRoleBasedPricingResponseDTO> result = this.pricingServiceClient.getJobRoleBasedPricing(
					Arrays.asList(jobRoleBasedPricingRequestDTO)).getBody().stream()
					.filter(x -> x.getJobRoleId().equals(jobRoleId)).findFirst();

			final List<RoundLevelInterviewStructure> roundLevelInterviewStructures = jobRoleToInterviewStructureDAOs
					.stream()
					.map(x -> {
						final JobRoleBasedPricingResponseDTO.InterviewStructurePricing pricing = result
								.map(jobRoleBasedPricingResponseDTO -> jobRoleBasedPricingResponseDTO.getPricing()
										.get(x.getInterviewStructureId()))
								.orElse(null);
						try {
							return this.roundLevelInterviewStructureMapper.toRoundLevelInterviewStructure(x).toBuilder()
									.price(pricing != null ? pricing.getPrice() : null)
									.margin(pricing != null ? pricing.getMargin() : null)
									.build();
						} catch (Exception e) {
							e.printStackTrace();
							return null;
						}
					})
					.collect(Collectors.toList());

			return DataFetcherResult.newResult()
					.data(roundLevelInterviewStructures)
					.build();

		} else if (type.getName().equals("Interview")) {
			final Interview interview = environment.getSource();
			final RoundLevelInterviewStructure roundLevelInterviewStructure = this.roundLevelInterviewStructureMapper
					.toRoundLevelInterviewStructure(
							this.jobRoleToInterviewStructureRepository
									.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId(
											interview.getJobRoleId(),
											interview.getJobRoleVersion(),
											interview.getInterviewStructureId())
									.get());

			return DataFetcherResult.newResult()
					.data(roundLevelInterviewStructure)
					.build();
		}
		throw new IllegalArgumentException();
	}

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(
				List.of("JobRole", "roundLevelInterviewStructure"),
				List.of("Interview", "roundLevelInterviewStructure"));
	}
}
