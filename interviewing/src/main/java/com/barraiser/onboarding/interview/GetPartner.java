/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.types.Interview;
import com.barraiser.common.graphql.types.JobRole;
import com.barraiser.onboarding.auth.AuthorizationResourceDTO;
import com.barraiser.onboarding.auth.Authorizer;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.MultiParentTypeDataFetcher;
import com.barraiser.common.graphql.input.PartnerInput;
import com.barraiser.common.graphql.types.Partner;
import com.barraiser.onboarding.partner.PartnerConfigurationManager;
import com.barraiser.onboarding.partner.auth.PartnerPortalAuthorizer;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class GetPartner implements MultiParentTypeDataFetcher {
	private final GraphQLUtil graphQLUtil;
	private final PartnerCompanyRepository partnerCompanyRepository;
	private final Authorizer authorizer;
	private final PartnerConfigurationManager partnerConfigurationManager;

	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {
		final GraphQLObjectType type = (GraphQLObjectType) environment.getParentType();
		if (type.getName().equals(QUERY_TYPE)) {
			final PartnerInput input = this.graphQLUtil.getArgument(environment, "input", PartnerInput.class);
			final String partnerId = input.getPartnerId();
			String responseError = "Either this page does not exists or you do not have the permissions to view this page";
			final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);

			final AuthorizationResourceDTO authorizationResource = AuthorizationResourceDTO.builder()
					.type(PartnerPortalAuthorizer.RESOURCE_TYPE)
					.resource(input.getPartnerId() == null ? "" : input.getPartnerId())
					.build();
			this.authorizer.can(authenticatedUser, PartnerPortalAuthorizer.ACTION_READ_AND_WRITE,
					authorizationResource);

			if (partnerId != null) {
				final PartnerCompanyDAO partnerCompany = this.partnerCompanyRepository.findById(partnerId).orElseThrow(
						() -> new IllegalArgumentException(responseError));
				log.info("getPartner for id : {}", partnerCompany.getId());

				final List<Partner> partners = this.mapPartnerCompanyDAOsToPartners(List.of(partnerCompany)).stream()
						.map(p -> p.toBuilder().evaluationsSearchQuery(input.getEvaluationsSearchQuery()).build())
						.collect(Collectors.toList());

				return DataFetcherResult.newResult().data(partners).build();
			} else {
				// TODO: why this?
				// TODO: should partner id be put null here in thread context?
				final List<PartnerCompanyDAO> partnerCompanyDAOS = this.partnerCompanyRepository.findAll();
				return DataFetcherResult.newResult().data(this.mapPartnerCompanyDAOsToPartners(partnerCompanyDAOS))
						.build();
			}
		} else if (type.getName().equals("JobRole")) {
			final JobRole jobRole = environment.getSource();
			final PartnerCompanyDAO partnerCompany = this.partnerCompanyRepository
					.findByCompanyId(jobRole.getCompanyId()).get();
			return DataFetcherResult.newResult()
					.data(this.mapPartnerCompanyDAOsToPartners(List.of(partnerCompany)).get(0)).build();
		} else if (type.getName().equals("Interview")) {
			final Interview interview = environment.getSource();
			final PartnerCompanyDAO partnerCompany = this.partnerCompanyRepository
					.findById(interview.getPartnerId()).get();
			return DataFetcherResult.newResult()
					.data(this.mapPartnerCompanyDAOsToPartners(List.of(partnerCompany)).get(0)).build();
		} else {
			throw new IllegalArgumentException("Bad parent type while accessing Partner type, please fix your query");
		}
	}

	private List<Partner> mapPartnerCompanyDAOsToPartners(final List<PartnerCompanyDAO> partnerCompanyDAOs) {
		return partnerCompanyDAOs.stream().map(
				pc -> Partner.builder()
						.id(pc.getId())
						.companyId(pc.getCompanyId())
						.useATSFeedback(pc.getUseATSFeedback())
						.isCandidateSchedulingEnabled(Boolean.TRUE
								.equals(this.partnerConfigurationManager.isCandidateSchedulingEnabled(pc.getId())))
						.partnershipModelId(pc.getPartnershipModelId())
						.build())
				.collect(Collectors.toList());
	}

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(
				List.of(QUERY_TYPE, "getPartner"),
				List.of("JobRole", "partner"),
				List.of("Interview", "partner"));
	}
}
