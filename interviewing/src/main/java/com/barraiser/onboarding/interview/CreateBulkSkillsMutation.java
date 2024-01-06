/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.input.CreateBulkSkillsInput;
import com.barraiser.common.graphql.input.CreateSkillInput;
import com.barraiser.common.graphql.types.Skill;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.commons.auth.AuthorizationResult;
import com.barraiser.commons.auth.feign.AuthorizationServiceFeignClient;
import com.barraiser.onboarding.auth.AllowAuthenticatedUserAuthorizationInputConstructor;
import com.barraiser.onboarding.dal.SkillDAO;
import com.barraiser.onboarding.dal.SkillRepository;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLMutation;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@Component
public class CreateBulkSkillsMutation extends AuthorizedGraphQLMutation<List<Skill>> {

	private final GraphQLUtil graphQLUtil;
	private final SkillRepository skillRepository;

	public CreateBulkSkillsMutation(final AuthorizationServiceFeignClient authorizationServiceFeignClient,
			final AllowAuthenticatedUserAuthorizationInputConstructor allowAuthenticatedUserAuthorizationInputConstructor,
			final GraphQLUtil graphQLUtil,
			final SkillRepository skillRepository) {
		super(authorizationServiceFeignClient, allowAuthenticatedUserAuthorizationInputConstructor);
		this.graphQLUtil = graphQLUtil;
		this.skillRepository = skillRepository;
	}

	@Override
	protected List<Skill> fetch(DataFetchingEnvironment environment, AuthorizationResult authorizationResult)
			throws Exception {
		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);

		final CreateBulkSkillsInput input = this.graphQLUtil.getInput(environment, CreateBulkSkillsInput.class);
		final List<Skill> createdSkills = new ArrayList<>();

		for (CreateSkillInput skillInput : input.getSkillInputs()) {
			createdSkills.add(this.addNewSkill(skillInput));
		}

		return createdSkills;
	}

	private Skill addNewSkill(CreateSkillInput input) {
		final Optional<SkillDAO> skill = this.skillRepository.findByNameIgnoreCaseAndDomainAndParent(input.getName(),
				input.getDomainId(), input.getParentSkillId());

		if (skill.isEmpty()) {

			final SkillDAO skillToBeCreated = SkillDAO.builder()
					.id(UUID.randomUUID().toString())
					.name(input.getName())
					.domain(input.getDomainId())
					.parent(input.getParentSkillId())
					.creationSource(input.getCreationSource())
					.build();

			this.skillRepository.save(skillToBeCreated);

			return Skill.builder()
					.id(skillToBeCreated.getId())
					.name(skillToBeCreated.getName())
					.parentSkillId(skillToBeCreated.getParent())
					.creationSource(skillToBeCreated.getCreationSource())
					.build();
		}

		return null;
	}

	@Override
	public String name() {
		return "createBulkSkills";
	}
}
