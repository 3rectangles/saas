/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.jobrole;

import com.barraiser.onboarding.dal.EvaluationDAO;
import com.barraiser.onboarding.dal.EvaluationRepository;
import com.barraiser.onboarding.dal.SkillWeightageDAO;
import com.barraiser.onboarding.dal.SkillWeightageRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Component
public class SkillWeightageManager {
	private final EvaluationRepository evaluationRepository;
	private final SkillWeightageRepository skillWeightageRepository;

	public List<SkillWeightageDAO> getSkillWeightageForEvaluation(final String evaluationId) {
		final EvaluationDAO evaluationDAO = this.evaluationRepository.findById(evaluationId).get();
		final List<SkillWeightageDAO> skillWeightageDAOS = this
				.getSkillWeightageForJobRole(evaluationDAO.getJobRoleId(), evaluationDAO.getJobRoleVersion());
		return skillWeightageDAOS;
	}

	public List<SkillWeightageDAO> getSkillWeightageForJobRole(final String jobRoleId, final Integer jobRoleVersion) {
		return this.skillWeightageRepository.findAllByJobRoleIdAndJobRoleVersion(jobRoleId, jobRoleVersion);
	}

	public void saveAll(final List<SkillWeightageDAO> skillWeightageDAOS) {
		this.skillWeightageRepository.saveAll(skillWeightageDAOS);
	}

}
