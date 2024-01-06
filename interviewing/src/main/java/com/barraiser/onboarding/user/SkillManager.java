/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.user;

import com.barraiser.onboarding.common.Constants;
import com.barraiser.onboarding.dal.*;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class SkillManager {
	private final SkillRepository skillRepository;
	private final InterviewStructureSkillsRepository interviewStructureSkillsRepository;
	// TBD see related to search index later
	// DO we add skills to search inedx?

	public Optional<SkillDAO> getSkill(final String id) {
		return this.skillRepository.findById(id);
	}

	public List<SkillDAO> getCategoriesCoveredInInterviewStructure(final String interviewStructureId) {
		return this.skillRepository.findAllByIdIn(this.interviewStructureSkillsRepository
				.findAllByInterviewStructureIdAndIsSpecific(interviewStructureId, false)
				.stream().map(InterviewStructureSkillsDAO::getSkillId).collect(Collectors.toList())).stream()
				.filter(x -> !Constants.SOFT_SKILL_ID.equals(x.getId()))
				.filter(x -> !Constants.OTHERS_SKILL_ID.equals(x.getId()))
				.collect(Collectors.toList());
	}

	/**
	 * This function ensures it returns all skills including others and soft skill
	 *
	 * @param interviewStructureId
	 * @return
	 */
	public List<SkillDAO> getAllCategoriesCovered(final String interviewStructureId) {
		return this.skillRepository.findAllByIdIn(this.interviewStructureSkillsRepository
				.findAllByInterviewStructureIdAndIsSpecific(interviewStructureId, false)
				.stream().map(InterviewStructureSkillsDAO::getSkillId).collect(Collectors.toList())).stream()
				.collect(Collectors.toList());
	}

	public List<SkillDAO> getSpecificSkillsCoveredInInterviewStructure(final String interviewStructureId) {
		return this.skillRepository.findAllByIdIn(this.interviewStructureSkillsRepository
				.findAllByInterviewStructureIdAndIsSpecific(interviewStructureId, true)
				.stream().map(InterviewStructureSkillsDAO::getSkillId).collect(Collectors.toList())).stream()
				.filter(x -> !Constants.SOFT_SKILL_ID.equals(x.getId()))
				.collect(Collectors.toList());
	}

}
