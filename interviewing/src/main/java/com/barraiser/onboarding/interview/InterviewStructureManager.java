/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.types.Interview;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.feeback.firestore.v1.InterviewFlow;
import com.barraiser.onboarding.interview.jobrole.JobRoleManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class InterviewStructureManager {
	/*
	 * NEW INTERVIEW FLOW VERSION CORRESPONDS TO THE VERSION
	 * WHERE WE DO NOT NEED ANY TAGGING AGENT AND DO NOT GET
	 * DATA OF JOB ROLE FROM GOOGLE SHEET
	 **/
	private static final String NEW_INTERVIEW_FLOW_VERSION = "1";
	/*
	 * OLD INTERVIEW FLOW VERSION CORRESPONDS TO THE VERSION
	 * WHERE WE NEED TAGGING AGENT AND GET
	 * DATA OF JOB ROLE FROM GOOGLE SHEET
	 **/
	private static final String OLD_INTERVIEW_FLOW_VERSION = "0";

	private final JobRoleRepository jobRoleRepository;
	private final InterviewStructureRepository interviewStructureRepository;
	private final ObjectMapper objectMapper;
	private final JobRoleToInterviewStructureRepository jobRoleToInterviewStructureRepository;
	private final PartnerCompanyRepository partnerCompanyRepository;
	private final JobRoleManager jobRoleManager;

	public Optional<InterviewStructureDAO> getInterviewStructure(final String id) {
		return this.interviewStructureRepository.findByJiraIssueId(id);
	}

	public void add(final InterviewStructureDAO interviewStructureDAO) {
		this.interviewStructureRepository.save(interviewStructureDAO);
	}

	public Integer getExpertJoiningTime(final String interviewStructureId) {
		return this.interviewStructureRepository.findById(interviewStructureId).get().getExpertJoiningTime() * 60;
	}

	public Integer getExpertJoiningTime(final InterviewStructureDAO interviewStructureDAO) {
		return interviewStructureDAO.getExpertJoiningTime() * 60;
	}

	public Integer getDurationOfInterview(final InterviewStructureDAO interviewStructureDAO) {
		return interviewStructureDAO.getDuration() * 60;
	}

	public InterviewStructureDAO getInterviewStructureById(final String interviewStructureId) {
		return this.interviewStructureRepository.findById(interviewStructureId).get();
	}

	public Boolean isTaggingAgentRequired(final String interviewStructureId) {

		if (interviewStructureId == null) {
			return Boolean.FALSE;
		}

		final InterviewStructureDAO interviewStructureDAO = this.getInterviewStructureById(interviewStructureId);
		try {
			final InterviewFlow interviewFlow = this.objectMapper.readValue(interviewStructureDAO.getInterviewFlow(),
					InterviewFlow.class);
			if (interviewFlow != null && NEW_INTERVIEW_FLOW_VERSION.equals(interviewFlow.getVersion())) {
				return Boolean.FALSE;
			}
			return Boolean.TRUE;
		} catch (JsonProcessingException | IllegalArgumentException e) {
			log.warn(e, e);
			return Boolean.TRUE;
		}
	}

	public String getVersionOfInterviewStructureFlow(final String interviewStructureId) {
		final InterviewStructureDAO interviewStructureDAO = this.getInterviewStructureById(interviewStructureId);
		try {
			final InterviewFlow interviewFlow = this.objectMapper.readValue(interviewStructureDAO.getInterviewFlow(),
					InterviewFlow.class);
			if (interviewFlow != null) {
				return interviewFlow.getVersion();
			}
			return OLD_INTERVIEW_FLOW_VERSION;
		} catch (JsonProcessingException | IllegalArgumentException e) {
			return OLD_INTERVIEW_FLOW_VERSION;
		}
	}

	public Boolean isInterviewStructureWithNewFlow(final String interviewStructureId) {
		final String flowVersion = this.getVersionOfInterviewStructureFlow(interviewStructureId);
		return this.isInterviewStructureOnNewFlow(flowVersion);
	}

	public Boolean isInterviewStructureOnNewFlow(final String interviewFlowVersion) {
		if (NEW_INTERVIEW_FLOW_VERSION.equals(interviewFlowVersion)) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	public List<String> getInterviewStructuresForJobRole(final String jobRoleId, final Integer jobRoleVersion) {
		return this.jobRoleToInterviewStructureRepository.findAllByJobRoleIdAndJobRoleVersion(jobRoleId, jobRoleVersion)
				.stream().map(x -> x.getInterviewStructureId()).collect(Collectors.toList());
	}

	public Boolean isInterviewStructureOnNewFlow(final JobRoleDAO jobRoleDAO) {
		final List<String> interviewStructureIds = this.getInterviewStructuresForJobRole(
				jobRoleDAO.getEntityId().getId(), jobRoleDAO.getEntityId().getVersion());
		final String interviewFlowVersion = this.getVersionOfInterviewStructureFlow(interviewStructureIds.get(0));
		return this.isInterviewStructureOnNewFlow(interviewFlowVersion);
	}

	public InterviewStructureDAO getDefaultInterviewStructure(final String partnerId) {
		String defaultInterviewStructureId = null;
		final PartnerCompanyDAO partnerCompanyDAO = this.partnerCompanyRepository.findById(partnerId).get();
		defaultInterviewStructureId = partnerCompanyDAO.getDefaultInterviewStructure() != null
				? partnerCompanyDAO.getDefaultInterviewStructure()
				: this.getGlobalDefaultInterviewStructureId();

		return this.interviewStructureRepository.findById(defaultInterviewStructureId).get();
	}

	/**
	 * What is a fallback interview structure id ?
	 * TODO : Define
	 *
	 * @param jobRoleId
	 * @return
	 */
	public String getFallbackInterviewStructure(final String jobRoleId) {
		String fallbackInterviewStructureId = null;

		// TODO : Also put check of BR interview feedback needed or not
		if (jobRoleId == null) {
			// TODO : Self serve usecase
		} else {
			JobRoleDAO jobRoleDAO = this.jobRoleManager.getLatestVersionOfJobRole(jobRoleId).get();

			final List<String> interviewStructureIds = this.jobRoleToInterviewStructureRepository
					.findAllByJobRoleIdAndJobRoleVersion(jobRoleDAO.getEntityId().getId(),
							jobRoleDAO.getEntityId().getVersion())
					.stream()
					.map(x -> x.getInterviewStructureId())
					.collect(Collectors.toList());

			fallbackInterviewStructureId = interviewStructureIds.size() == 1 ? interviewStructureIds.get(0) : null;
		}

		return fallbackInterviewStructureId;
	}

	private String getGlobalDefaultInterviewStructureId() {
		final JobRoleDAO globalDefualtJobRole = this.jobRoleRepository.findByPartnerIdNullAndIsDefault(Boolean.TRUE)
				.get();
		final String globalDefaultInterviewStructureId = this.jobRoleToInterviewStructureRepository
				.findAllByJobRoleIdAndJobRoleVersion(globalDefualtJobRole.getEntityId().getId(),
						globalDefualtJobRole.getEntityId().getVersion())
				.get(0).getInterviewStructureId();

		return globalDefaultInterviewStructureId;
	}

}
