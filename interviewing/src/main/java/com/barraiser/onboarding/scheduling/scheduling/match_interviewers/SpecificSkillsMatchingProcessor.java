/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.match_interviewers;

import com.barraiser.onboarding.dal.*;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class SpecificSkillsMatchingProcessor implements MatchInterviewersProcessor {
	private final InterviewStructureSkillsRepository interviewStructureSkillsRepository;
	private final ExpertSkillsRepository expertSkillsRepository;

	@Override
	public void process(final MatchInterviewersData data) {
		final List<InterviewerData> interviewers = this.getFilteredExpertBasisSpecificSkills(data.getInterviewers(),
				data.getInterviewStructureId());
		data.setInterviewers(this.calculateAverageProficiencyForExperts(interviewers));
	}

	private List<InterviewerData> getFilteredExpertBasisSpecificSkills(List<InterviewerData> interviewers,
			final String interviewStructureId) {
		final List<InterviewStructureSkillsDAO> specificSkills = this.getSpecificSkills(interviewStructureId);
		final List<String> mandatorySpecificSkills = specificSkills.stream()
				.filter(x -> !Boolean.TRUE.equals(x.getIsOptional()))
				.map(InterviewStructureSkillsDAO::getSkillId).collect(Collectors.toList());
		interviewers = this.populateSpecificSkillsForExperts(interviewers, specificSkills);
		final List<InterviewerData> filteredExperts = new ArrayList<>();
		interviewers.forEach(x -> {
			final List<String> intersectedSkills = x.getSpecificSkills().stream().filter(y -> y.getProficiency() >= 3)
					.map(ExpertSkillsDAO::getSkillId).collect(Collectors.toList());
			intersectedSkills.retainAll(mandatorySpecificSkills);
			if (intersectedSkills.size() == mandatorySpecificSkills.size()) {
				filteredExperts.add(x);
			}
		});
		return filteredExperts;
	}

	private List<InterviewStructureSkillsDAO> getSpecificSkills(final String interviewStructureId) {
		return this.interviewStructureSkillsRepository
				.findAllByInterviewStructureIdAndIsSpecific(interviewStructureId, true);
	}

	private List<InterviewerData> populateSpecificSkillsForExperts(final List<InterviewerData> interviewers,
			final List<InterviewStructureSkillsDAO> specificSkills) {
		final List<ExpertSkillsDAO> expertSkillsDAOs = this.expertSkillsRepository
				.findAllByExpertIdInAndSkillIdIn(
						interviewers.stream().map(InterviewerData::getId).collect(Collectors.toList()),
						specificSkills.stream().map(InterviewStructureSkillsDAO::getSkillId)
								.collect(Collectors.toList()));
		interviewers.forEach(x -> {
			x.setSpecificSkills(expertSkillsDAOs.stream().filter(y -> y.getExpertId()
					.equals(x.getId())).collect(Collectors.toList()));
		});
		return interviewers;
	}

	private List<InterviewerData> calculateAverageProficiencyForExperts(final List<InterviewerData> interviewers) {
		return interviewers.stream().map(x -> {
			final Double averageProficiency;
			if (x.getSpecificSkills() == null || x.getSpecificSkills().isEmpty()) {
				averageProficiency = 0D;
			} else {
				averageProficiency = x.getSpecificSkills().stream()
						.map(s -> s.getProficiency() == null ? 0 : s.getProficiency()).mapToDouble(Double::doubleValue)
						.sum() / (float) x.getSpecificSkills().size();
			}
			return x.toBuilder().averageProficiencyInSkills(averageProficiency).build();
		}).collect(Collectors.toList());
	}
}
