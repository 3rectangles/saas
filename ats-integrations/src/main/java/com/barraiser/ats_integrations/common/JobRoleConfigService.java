/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.common;

import com.barraiser.ats_integrations.dal.ATSJobPostingToBRJobRoleDAO;
import com.barraiser.ats_integrations.dal.ATSJobPostingToBRJobRoleRepository;
import com.barraiser.ats_integrations.dal.ATSToBRInterviewStructureMappingDAO;
import com.barraiser.ats_integrations.dal.ATSToBRInterviewStructureMappingRepository;
import com.barraiser.ats_integrations.dto.ATSInterviewStructureMappingsDTO;
import com.barraiser.ats_integrations.dto.ATSJobRoleMappingsDTO;
import com.barraiser.ats_integrations.dto.UpdateAtsJobRoleMappingDTO;
import com.barraiser.commons.dto.ats.enums.ATSProvider;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Log4j2
@AllArgsConstructor
public class JobRoleConfigService {
	private final ATSJobPostingToBRJobRoleRepository atsJobPostingToBRJobRoleRepository;
	private final ATSToBRInterviewStructureMappingRepository atsToBRInterviewStructureMappingRepository;

	@Transactional
	public void updateJobRoleMappings(final UpdateAtsJobRoleMappingDTO input) {
		input.getJobRoleMappings()
				.forEach(x -> this.updateJobRoleMapping(x, input.getAtsProvider(), input.getPartnerId()));
	}

	/**
	 * NOTE : For now everything is made assuming that only one ATS can be
	 * integrated per partner.
	 */
	private void updateJobRoleMapping(final UpdateAtsJobRoleMappingDTO.JobRoleMapping jobRoleMapping,
			final ATSProvider atsProvider,
			final String partnerId) {

		if (jobRoleMapping.getInterviewStructureMappings() != null) {
			jobRoleMapping.getInterviewStructureMappings()
					.forEach(x -> this.updateInterviewStructureMapping(partnerId, atsProvider, x));
		}

		if (jobRoleMapping.getAtsJobPostingId() == null) {
			this.atsJobPostingToBRJobRoleRepository.deleteByBrJobRoleId(jobRoleMapping.getJobRoleId());
			return;
		}
		final ATSJobPostingToBRJobRoleDAO jobRoleMappingDAO = this.atsJobPostingToBRJobRoleRepository
				.findByBrJobRoleId(jobRoleMapping.getJobRoleId()).orElse(
						ATSJobPostingToBRJobRoleDAO.builder()
								.id(UUID.randomUUID().toString())
								.atsProvider(atsProvider != null ? atsProvider.getValue() : null)
								.partnerId(partnerId)
								.brJobRoleId(jobRoleMapping.getJobRoleId())
								.build());
		this.atsJobPostingToBRJobRoleRepository.save(jobRoleMappingDAO.toBuilder()
				.atsProvider(null)
				.atsJobPostingId(jobRoleMapping.getAtsJobPostingId())
				.build());
	}

	private void updateInterviewStructureMapping(final String partnerId,
			final ATSProvider atsProvider,
			final UpdateAtsJobRoleMappingDTO.InterviewStructureMapping interviewStructureMapping) {
		if (interviewStructureMapping.getInterviewStructureId() == null) {
			this.atsToBRInterviewStructureMappingRepository
					.deleteByBrInterviewStructureId(interviewStructureMapping.getInterviewStructureId());
			return;
		}
		final ATSToBRInterviewStructureMappingDAO interviewStructureMappingDAO = this.atsToBRInterviewStructureMappingRepository
				.findByBrInterviewStructureId(interviewStructureMapping.getInterviewStructureId())
				.orElse(ATSToBRInterviewStructureMappingDAO.builder()
						.id(UUID.randomUUID().toString())
						.brInterviewStructureId(interviewStructureMapping.getInterviewStructureId())
						.partnerId(partnerId)
						.build());
		this.atsToBRInterviewStructureMappingRepository.save(interviewStructureMappingDAO.toBuilder()
				.atsProvider(atsProvider != null
						? com.barraiser.common.ats_integrations.ATSProvider.fromString(atsProvider.getValue())
						: null) // TODO:
				// Eliminate
				// one
				// of
				// the
				// enums
				.atsInterviewStructureId(interviewStructureMapping.getAtsInterviewStructureId())
				.atsProvider(null)
				.build());
	}

	public String getAtsJobRoleId(final String brJobRoleId) {
		final Optional<ATSJobPostingToBRJobRoleDAO> jobRoleMapping = this.atsJobPostingToBRJobRoleRepository
				.findByBrJobRoleId(brJobRoleId);
		return jobRoleMapping.map(ATSJobPostingToBRJobRoleDAO::getAtsJobPostingId).orElse(null);
	}

	public String getAtsInterviewStructureId(final String brInterviewStructureId) {
		final Optional<ATSToBRInterviewStructureMappingDAO> interviewStructureMapping = this.atsToBRInterviewStructureMappingRepository
				.findByBrInterviewStructureId(brInterviewStructureId);
		return interviewStructureMapping.map(ATSToBRInterviewStructureMappingDAO::getAtsInterviewStructureId)
				.orElse(null);
	}

	/**
	 * Returns job role mappings without interview structure in it
	 *
	 * @param partnerId
	 * @return
	 */
	public List<ATSJobRoleMappingsDTO.JobRoleMapping> getATSJobRoleMappings(final String partnerId) {
		return this.atsJobPostingToBRJobRoleRepository.findAllByPartnerId(partnerId).stream()
				.map(x -> ATSJobRoleMappingsDTO.JobRoleMapping.builder()
						.atsProvider(x.getAtsProvider())
						.atsJobRoleId(x.getBrJobRoleId())
						.brJobRoleId(x.getAtsJobPostingId())
						.build())
				.collect(
						Collectors.toList());
	}

	/**
	 * TODO: Not considering ATSProvider for now as we have an assumption there will
	 * be one ATS per partner.
	 * Will have to change this , if because of IaaS this is not the case.
	 *
	 * @param partnerId
	 * @return
	 */
	public List<ATSInterviewStructureMappingsDTO.InterviewStructureMapping> getATSInterviewStructureMappings(
			final String partnerId) {

		return this.atsToBRInterviewStructureMappingRepository.findAllByPartnerId(partnerId).stream()
				.map(x -> ATSInterviewStructureMappingsDTO.InterviewStructureMapping.builder()
						.atsInterviewStructureId(x.getAtsInterviewStructureId())
						.brInterviewStructureId(x.getBrInterviewStructureId())
						.build())
				.collect(
						Collectors.toList());
	}

}
