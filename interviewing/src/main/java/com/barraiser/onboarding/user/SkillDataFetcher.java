/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.user;

import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.MultiParentTypeDataFetcher;
import com.barraiser.common.graphql.types.*;
import com.barraiser.common.graphql.types.SkillInterviewingConfiguration.SkillInterviewingConfiguration;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class SkillDataFetcher implements MultiParentTypeDataFetcher {
	private final SkillRepository skillRepository;
	private final GraphQLUtil graphQLUtil;
	private final InterviewStructureSkillsRepository interviewStructureSkillsRepository;
	private final ExpertSkillsRepository expertSkillsRepository;

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(
				List.of("SkillScore", "skill"),
				List.of("SkillInterviewingConfiguration", "skill"),
				List.of("Query", "getSkills"),
				List.of("SkillWeightage", "skill"),
				List.of("InterviewStructure", "specificSkills"),
				List.of("Interviewer", "specificSkills"));
	}

	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {
		final GraphQLObjectType type = (GraphQLObjectType) environment.getParentType();

		if (type.getName().equals("SkillInterviewingConfiguration")) {
			final SkillInterviewingConfiguration skillInterviewingConfiguration = environment.getSource();
			final Optional<SkillDAO> skillDAO = this.skillRepository
					.findById(skillInterviewingConfiguration.getSkillId());
			final Skill skill = Skill.builder()
					.id(skillDAO.get().getId())
					.name(skillDAO.get().getName())
					.build();

			return DataFetcherResult.newResult()
					.data(skill)
					.build();
		} else if (type.getName().equals("SkillScore")) {
			final SkillScore evaluationScore = environment.getSource();
			final SkillDAO skillDAO = this.skillRepository
					.findById(evaluationScore.getSkillId())
					.orElseThrow(() -> new IllegalArgumentException(
							"Given skill id from evaluation score did not match with any skill"));
			final Skill skill = Skill.builder()
					.id(skillDAO.getId())
					.name(skillDAO.getName())
					.build();
			return DataFetcherResult.newResult()
					.data(skill)
					.build();
		} else if (type.getName().equals("Query")) {
			final String domainId = this.graphQLUtil.getInput(environment, String.class);
			final List<SkillDAO> skillDAOs;
			if (domainId == null) {
				skillDAOs = this.skillRepository.findAllByDomainIsNullAndDeprecatedOnIsNull();

			} else {
				skillDAOs = this.skillRepository.findAllByDomainAndDeprecatedOnIsNull(domainId);
			}
			final List<Skill> skills = skillDAOs.stream().map(skillDAO -> {
				return Skill.builder()
						.id(skillDAO.getId())
						.name(skillDAO.getName())
						.parentSkillId(skillDAO.getParent())
						.build();
			}).collect(Collectors.toList());
			return DataFetcherResult.newResult()
					.data(skills)
					.build();
		} else if (type.getName().equals("SkillWeightage")) {
			final SkillWeightageDAO skillWeightageDAO = environment.getSource();
			final Optional<SkillDAO> skill = this.skillRepository.findById(skillWeightageDAO.getSkillId());

			return DataFetcherResult.newResult()
					.data(skill)
					.build();

		} else if (type.getName().equals("InterviewStructure")) {
			final InterviewStructure interviewStructure = environment.getSource();
			final List<InterviewStructureSkillsDAO> interviewStructureSkillsDAOs = this.interviewStructureSkillsRepository
					.findAllByInterviewStructureIdAndIsSpecific(interviewStructure.getId(), true);
			final List<Skill> skills = this.skillRepository.findAllByIdIn(interviewStructureSkillsDAOs.stream()
					.map(InterviewStructureSkillsDAO::getSkillId).collect(Collectors.toList())).stream()
					.map(x -> Skill.builder()
							.id(x.getId())
							.name(x.getName())
							.isOptional(
									interviewStructureSkillsDAOs.stream().filter(y -> y.getSkillId().equals(x.getId()))
											.findFirst().get().getIsOptional())
							.build())
					.collect(Collectors.toList());
			return DataFetcherResult.newResult()
					.data(skills)
					.build();
		} else if (type.getName().equals("Interviewer")) {
			final Interviewer interviewer = environment.getSource();
			final List<Skill> skills = this.getExpertSpecificSkills(interviewer);
			return DataFetcherResult.newResult()
					.data(skills)
					.build();
		} else {
			throw new IllegalArgumentException("Bad parent type while accessing skill type, please fix your query");
		}

	}

	private List<Skill> getExpertSpecificSkills(final Interviewer interviewer) {
		List<String> domainIdsForAnExpert = new ArrayList<>();
		if (interviewer.getExpertDomains() != null) {
			domainIdsForAnExpert.addAll(interviewer.getExpertDomains());
		}
		if (interviewer.getPeerDomains() != null) {
			domainIdsForAnExpert.addAll(interviewer.getPeerDomains());
		}
		domainIdsForAnExpert = domainIdsForAnExpert.stream().distinct().collect(Collectors.toList());
		final List<SkillDAO> specificSkillsNeeded = this.skillRepository
				.findAllByDomainInAndDeprecatedOnIsNull(domainIdsForAnExpert);
		final List<ExpertSkillsDAO> expertSkillsDAOs = this.expertSkillsRepository
				.findAllByExpertId(interviewer.getId());
		final List<Skill> skills = specificSkillsNeeded.stream().map(x -> {
			final Optional<ExpertSkillsDAO> expertSkillsDAO = expertSkillsDAOs.stream()
					.filter(y -> y.getSkillId().equals(x.getId())).findFirst();
			return Skill.builder().id(x.getId())
					.name(x.getName())
					.proficiency(expertSkillsDAO.isEmpty() ? null : expertSkillsDAO.get().getProficiency())
					.build();
		}).collect(Collectors.toList());
		return skills;
	}
}
