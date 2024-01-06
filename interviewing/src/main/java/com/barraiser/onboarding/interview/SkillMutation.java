/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.input.CreateSkillInput;
import com.barraiser.common.graphql.types.Skill;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.commons.auth.AuthorizationResult;
import com.barraiser.commons.auth.feign.AuthorizationServiceFeignClient;
import com.barraiser.onboarding.auth.AllowAuthenticatedUserAuthorizationInputConstructor;
import com.barraiser.onboarding.auth.AuthorizationException;
import com.barraiser.onboarding.dal.SkillDAO;
import com.barraiser.onboarding.dal.SkillRepository;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLMutation;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.partner.PartnerRepAuthorizer;
import com.barraiser.onboarding.user.auth.SuperAdminAuthorizer;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Log4j2
@Component
public class SkillMutation extends AuthorizedGraphQLMutation<Skill> {
	private final GraphQLUtil graphQLUtil;
	private final SkillRepository skillRepository;
	private final SuperAdminAuthorizer superAdminAuthorizer;
	private final PartnerRepAuthorizer partnerRepAuthorizer;

	public SkillMutation(final AuthorizationServiceFeignClient authorizationServiceFeignClient,
			final AllowAuthenticatedUserAuthorizationInputConstructor allowAuthenticatedUserAuthorizationInputConstructor,
			final GraphQLUtil graphQLUtil, final SkillRepository skillRepository,
			final SuperAdminAuthorizer superAdminAuthorizer, final PartnerRepAuthorizer partnerRepAuthorizer) {
		super(authorizationServiceFeignClient, allowAuthenticatedUserAuthorizationInputConstructor);
		this.graphQLUtil = graphQLUtil;
		this.skillRepository = skillRepository;
		this.superAdminAuthorizer = superAdminAuthorizer;
		this.partnerRepAuthorizer = partnerRepAuthorizer;
	}

	@Override
	public String name() {
		return "createSkill";
	}

	@Override
	protected Skill fetch(DataFetchingEnvironment environment, AuthorizationResult authorizationResult)
			throws Exception {

		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);
		final CreateSkillInput input = this.graphQLUtil.getInput(environment, CreateSkillInput.class);

		if (!this.isSkillAdditionAllowed(authenticatedUser)) {
			throw new AuthorizationException();
		}

		final Optional<SkillDAO> skill = this.skillRepository.findByNameAndDomainAndParent(input.getName(),
				input.getDomainId(), input.getParentSkillId());
		if (skill.isPresent()) {
			return Skill.builder().id(skill.get().getId()).name(skill.get().getName())
					.parentSkillId(skill.get().getParent()).creationSource(skill.get().getCreationSource())
					.build();
		}
		final SkillDAO skillToBeCreated = SkillDAO.builder()
				.id(UUID.randomUUID().toString())
				.name(input.getName())
				.domain(input.getDomainId())
				.parent(input.getParentSkillId())
				.creationSource(input.getCreationSource())
				.build();
		this.skillRepository.save(skillToBeCreated);

		return Skill.builder().id(skillToBeCreated.getId()).name(skillToBeCreated.getName())
				.parentSkillId(skillToBeCreated.getParent()).creationSource(skillToBeCreated.getCreationSource())
				.build();
	}

	private Boolean isSkillAdditionAllowed(final AuthenticatedUser user) {
		return this.superAdminAuthorizer.isSuperAdmin(user) || this.partnerRepAuthorizer.isPartnerRep(user);
	}
}
