/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.onboarding.dal.InterviewStructureSkillsDAO;
import com.barraiser.onboarding.dal.InterviewStructureSkillsRepository;
import com.barraiser.onboarding.dal.SkillDAO;
import com.barraiser.onboarding.dal.SkillRepository;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.common.graphql.types.InterviewCategory;
import com.barraiser.common.graphql.types.InterviewStructure;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class InterviewCategoriesDataFetcher implements NamedDataFetcher {
	private final SkillRepository skillRepository;
	private final InterviewStructureSkillsRepository interviewStructureSkillsRepository;

	@Override
	public String name() {
		return "categories";
	}

	@Override
	public String type() {
		return "InterviewStructure";
	}

	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {
		final InterviewStructure interviewStructure = environment.getSource();

		final List<InterviewCategory> parents = this.getParentInterviewCategoryOfSkills(interviewStructure.getId());

		return DataFetcherResult.newResult()
				.data(parents)
				.build();
	}

	public List<InterviewCategory> getParentInterviewCategoryOfSkills(final String interviewStructureId) {
		final List<SkillDAO> skillDAOS = this.getInterviewStructureSkills(interviewStructureId);

		List<InterviewCategory> parents = new ArrayList<>();

		final Map<String, InterviewCategory> interviewCategoryMap = new HashMap<>();

		skillDAOS.forEach(x -> {
			InterviewCategory interviewCategory = InterviewCategory.builder()
					.id(x.getId())
					.name(x.getName())
					.build();

			SkillDAO s = x;

			if (s.getParent() == null) {
				parents.add(InterviewCategory.builder()
						.name(s.getName())
						.id(s.getId())
						.subCategories(new ArrayList<>())
						.build());
				interviewCategoryMap.put(s.getId(), InterviewCategory.builder()
						.name(s.getName())
						.id(s.getId())
						.subCategories(new ArrayList<>())
						.build());
			}
			while (s.getParent() != null) {
				if (interviewCategoryMap.get(s.getParent()) != null) {
					interviewCategoryMap
							.get(s.getParent())
							.getSubCategories()
							.add(interviewCategory);
					break;
				} else {
					final SkillDAO parentDAO = this.skillRepository
							.findById(s.getParent()).get();

					final InterviewCategory parent = InterviewCategory.builder()
							.name(parentDAO.getName())
							.id(parentDAO.getId())
							.subCategories(new ArrayList<>())
							.build();

					parent.getSubCategories().add(interviewCategory);

					interviewCategoryMap.put(
							parent.getId(),
							parent);

					if (parentDAO.getParent() == null) {
						parents.add(parent);
					}

					s = parentDAO;
					interviewCategory = parent;
				}
			}
		});

		return parents;
	}

	private List<SkillDAO> getInterviewStructureSkills(final String interviewStructureId) {
		return this.skillRepository
				.findAllByIdIn(
						this.interviewStructureSkillsRepository
								.findAllByInterviewStructureIdAndIsSpecific(
										interviewStructureId,
										false)
								.stream()
								.map(InterviewStructureSkillsDAO::getSkillId)
								.collect(Collectors.toList()));
	}
}
