/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.user;

import com.barraiser.commons.auth.Dimension;
import com.barraiser.commons.auth.feign.AuthorizationServiceFeignClient;
import com.barraiser.commons.dto.DeletePartnerRolesRequestDTO;
import com.barraiser.onboarding.auth.AuthorizationResourceDTO;
import com.barraiser.onboarding.auth.Authorizer;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.ExpertDAO;
import com.barraiser.onboarding.dal.PartnerCompanyRepository;
import com.barraiser.onboarding.dal.PartnerRepsDAO;
import com.barraiser.onboarding.dal.PartnerRepsRepository;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.common.graphql.input.PartnerAccessInput;
import com.barraiser.onboarding.interview.jira.expert.ExpertElasticSearchManager;
import com.barraiser.onboarding.partner.auth.PartnerPortalAuthorizer;
import com.barraiser.onboarding.user.expert.ExpertDBManager;
import com.barraiser.onboarding.user.expert.dto.ExpertDetails;
import com.barraiser.onboarding.user.expert.mapper.ExpertMapper;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class RemovePartnerRep implements NamedDataFetcher {
	private final GraphQLUtil graphQLUtil;
	private final UserInformationManagementHelper userManagement;
	private final Authorizer authorizer;
	private final PartnerRepsRepository partnerRepsRepository;
	private final PartnerCompanyRepository partnerCompanyRepository;

	private final ExpertElasticSearchManager expertElasticSearchManager;
	private final ExpertDBManager expertDBManager;
	private final ExpertMapper expertMapper;

	private final AuthorizationServiceFeignClient authorizationServiceFeignClient;

	private final static String PARTNER_SUPER_USER_ROLE_ID = "PARTNER_SUPER_ADMIN";

	@Override
	public String name() {
		return "removePartnerRep";
	}

	@Override
	public String type() {
		return MUTATION_TYPE;
	}

	@Transactional
	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {
		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);

		final PartnerAccessInput input = this.graphQLUtil.getArgument(environment, "input", PartnerAccessInput.class);

		this.isAuthorizedToRemovePartnerRep(authenticatedUser, input);

		final String userId = this.getUser(input.getEmail());
		this.removeAccessAsPartnerRepForPartner(userId, input.getPartnerId());
		this.removeAccessAsExpertForPartner(userId, input.getPartnerId());
		this.removeAsSuperAdminForPartner(userId, input.getPartnerId());

		log.info(String.format("Partner Rep access revoked for %s by %s", userId, authenticatedUser.getUserName()));

		return DataFetcherResult.newResult().data(true).build();
	}

	private void removeAsSuperAdminForPartner(final String userId, final String partnerId) {

		final DeletePartnerRolesRequestDTO deletePartnerRolesRequestDTO = DeletePartnerRolesRequestDTO.builder()
				.dimension(Dimension.PARTNER)
				.dimensionValues(List.of(partnerId))
				.roleIds(List.of(PARTNER_SUPER_USER_ROLE_ID))
				.build();
		this.authorizationServiceFeignClient.deleteUserPartnerRoles(userId, deletePartnerRolesRequestDTO);
	}

	private void isAuthorizedToRemovePartnerRep(final AuthenticatedUser authenticatedUser,
			final PartnerAccessInput input) {

		final AuthorizationResourceDTO authorizationResource = AuthorizationResourceDTO.builder()
				.type(PartnerPortalAuthorizer.RESOURCE_TYPE)
				.resource(input.getPartnerId())
				.build();
		this.authorizer.can(authenticatedUser, PartnerPortalAuthorizer.ACTION_READ_AND_WRITE, authorizationResource);

		final String userId = this.getUser(input.getEmail());
		if (userId.equals(authenticatedUser.getUserName())) {
			throw new IllegalArgumentException("You cannot revoke access to yourself");
		}

	}

	private String getUser(final String email) {
		final Optional<String> userId = this.userManagement.findUserByEmail(email);
		return userId.get();
	}

	private void removeAccessAsPartnerRepForPartner(final String userId, final String partnerId) {
		final Optional<PartnerRepsDAO> partnerRepsDAO = this.partnerRepsRepository
				.findByPartnerRepIdAndPartnerId(userId, partnerId);
		if (partnerRepsDAO.isPresent()) {
			this.partnerRepsRepository.deleteById(partnerRepsDAO.get().getId());
		}
		final String partnerIds = this.userManagement.getUserAttributes(userId).getOrDefault("custom:partnerId", "");
		final String updatedPartnerIds = Arrays.stream(partnerIds.split(",")).filter(x -> !partnerId.equals(x))
				.collect(Collectors.joining(","));
		this.userManagement.updateUserAttributes(userId, Map.of("custom:partnerId", updatedPartnerIds));
	}

	private void removeAccessAsExpertForPartner(final String userId, final String partnerId) throws IOException {
		final ExpertDAO expertDAO = this.expertDBManager.getExpert(userId);
		if (expertDAO != null) {
			final ExpertDetails expertDetails = this.constructExpertDetails(expertDAO, partnerId);
			this.expertDBManager.updateExpertDetails(expertDetails);
			this.expertElasticSearchManager.updateExpertDetails(expertDetails);
		}

	}

	private ExpertDetails constructExpertDetails(final ExpertDAO expertDAO, final String partnerId) {
		return this.expertMapper.toExpertDetails(expertDAO).toBuilder()
				.companiesForWhichExpertCanTakeInterview(this.getUpdatedCompaniesExpertCanTakeInterviewsFor(
						partnerId, expertDAO.getCompaniesForWhichExpertCanTakeInterview()))
				.build();
	}

	private List<String> getUpdatedCompaniesExpertCanTakeInterviewsFor(final String partnerId,
			final List<String> companiesExpertCanTakeInterviewsFor) {
		final String companyId = this.partnerCompanyRepository.findById(partnerId).get().getCompanyId();
		final List<String> updatedCompaniesForWhichExpertCanTakeInterviews = new ArrayList<>();

		if (companiesExpertCanTakeInterviewsFor != null) {
			updatedCompaniesForWhichExpertCanTakeInterviews.addAll(companiesExpertCanTakeInterviewsFor);
		}
		updatedCompaniesForWhichExpertCanTakeInterviews.remove(companyId);
		return updatedCompaniesForWhichExpertCanTakeInterviews;
	}
}
