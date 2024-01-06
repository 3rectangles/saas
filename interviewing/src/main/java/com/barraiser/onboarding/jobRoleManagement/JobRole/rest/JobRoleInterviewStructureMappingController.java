/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.jobRoleManagement.JobRole.rest;

import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.InterviewStructureManager;
import com.barraiser.onboarding.interview.jobrole.JobRoleManager;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

import static com.barraiser.common.constants.ServiceConfigurationConstants.INTERVIEWING_SERVICE_CONTEXT_PATH;

@RestController
@Log4j2
@AllArgsConstructor
public class JobRoleInterviewStructureMappingController {

	private final JobRoleToInterviewStructureRepository jobRoleToInterviewStructureRepository;
	private final JobRoleManager jobRoleManager;
	private final JobRoleRepository jobRoleRepository;
	private final InterviewStructureManager interviewStructureManager;
	private final PartnerCompanyRepository partnerCompanyRepository;

	@GetMapping(value = INTERVIEWING_SERVICE_CONTEXT_PATH + "/interception/jobRole/interviewStructure")
	String getBRInterviewStructureId(
			@RequestParam("partnerId") final String partnerId,
			@RequestParam("jobRoleId") final String jobRoleId,
			@RequestParam("interviewStructureIds") final List<String> interviewStructureIds) {

		String brInterviewStructureId = null;
		final PartnerCompanyDAO partnerCompanyDAO = this.partnerCompanyRepository.findById(partnerId).get();

		if (jobRoleId == null) {
			// TODO: Return global default for self serve free trial.
			// Also check if this check has to be inside getDefaultInterviewStructure in
			// InterviewStructureManager
		} else {
			JobRoleDAO jobRoleDAO = this.jobRoleManager.getLatestVersionOfJobRole(jobRoleId).get();

			// CASE A : Interview structure id mappings were NOT present
			if (interviewStructureIds.size() == 0) {
				brInterviewStructureId = Boolean.TRUE.equals(partnerCompanyDAO.getUseATSFeedback()) ? null
						: this.interviewStructureManager.getFallbackInterviewStructure(jobRoleId);

			} else {
				// CASE B : Interview structure id mappings were present
				for (String interviewStructureId : interviewStructureIds) {
					Optional<JobRoleToInterviewStructureDAO> jobRoleToInterviewStructureDAO = this.jobRoleToInterviewStructureRepository
							.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId(
									jobRoleDAO.getEntityId().getId(),
									jobRoleDAO.getEntityId().getVersion(),
									interviewStructureId);

					if (jobRoleToInterviewStructureDAO.isPresent())
						brInterviewStructureId = jobRoleToInterviewStructureDAO.get().getInterviewStructureId();
				}

			}
		}

		// This case will never come.
		return brInterviewStructureId;
	}

	@GetMapping(value = INTERVIEWING_SERVICE_CONTEXT_PATH + "/partner/{partner_id}/interviewStructure")
	String getInterviewStructureId(@PathVariable("partner_id") String partnerId) {
		List<JobRoleDAO> jobRoleDAOList = jobRoleRepository
				.findLatestByPartnerIdAndDeprecatedOnIsNullAndIsDraftNotTrue(partnerId);

		if (jobRoleDAOList.size() == 1) {
			if (jobRoleDAOList.stream().allMatch(jobRoleDAO -> jobRoleToInterviewStructureRepository
					.countByJobRoleIdAndJobRoleVersion(jobRoleDAO.getEntityId().getId(),
							jobRoleDAO.getEntityId().getVersion()) == 1)) {
				return this.jobRoleToInterviewStructureRepository.findAllByJobRoleIdAndJobRoleVersion(
						jobRoleDAOList.get(0).getEntityId().getId(),
						jobRoleDAOList.get(0).getEntityId().getVersion()).get(0).getInterviewStructureId();
			}
		}

		return null;
	}
}
