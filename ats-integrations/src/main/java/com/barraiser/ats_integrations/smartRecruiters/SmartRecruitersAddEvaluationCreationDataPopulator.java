/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.smartRecruiters;

import com.barraiser.ats_integrations.dal.PartnerATSIntegrationDAO;
import com.barraiser.ats_integrations.smartRecruiters.DTO.*;
import com.barraiser.ats_integrations.smartRecruiters.POJO.SmartRecruitersAddEvaluationCreationData;
import com.barraiser.ats_integrations.smartRecruiters.requests.SmartRecruitersWebhookRequestBody;
import com.barraiser.common.graphql.types.Document;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Log4j2
@AllArgsConstructor
public class SmartRecruitersAddEvaluationCreationDataPopulator {
	private static final String RESUME = "RESUME";
	private static final String HIRING_MANAGER = "HIRING_MANAGER";

	private final SmartRecruitersAttachmentsFetcher smartRecruitersAttachmentsFetcher;
	private final SmartRecruitersAttachmentDownloader smartRecruitersAttachmentDownloader;
	private final SmartRecruitersHiringTeamFetcher smartRecruitersHiringTeamFetcher;
	private final SmartRecruitersUserFetcher smartRecruitersUserFetcher;

	public SmartRecruitersAddEvaluationCreationData getAddEvaluationCreationData(
			final PartnerATSIntegrationDAO partnerATSIntegrationDAO,
			final SmartRecruitersWebhookRequestBody webhookRequestBody,
			final CandidateDTO candidateDTO) throws Exception {
		final SmartRecruitersAddEvaluationCreationData addEvaluationCreationData = new SmartRecruitersAddEvaluationCreationData();

		addEvaluationCreationData
				.setJobId(webhookRequestBody.getJobId());

		addEvaluationCreationData
				.setCandidateDTO(candidateDTO);

		final Document resume = this.getResume(
				partnerATSIntegrationDAO,
				webhookRequestBody,
				candidateDTO);

		if (resume == null) {
			return null;
		}

		addEvaluationCreationData
				.setResume(resume);

		final UserDTO hiringManager = this.getHiringManager(
				partnerATSIntegrationDAO,
				webhookRequestBody.getJobId());

		if (hiringManager == null) {
			return null;
		}

		addEvaluationCreationData
				.setHiringManager(hiringManager);

		return addEvaluationCreationData;
	}

	public Document getResume(
			final PartnerATSIntegrationDAO partnerATSIntegrationDAO,
			SmartRecruitersWebhookRequestBody requestBody,
			final CandidateDTO candidateDTO)
			throws Exception {
		final List<AttachmentDTO> attachmentDTOs = this.smartRecruitersAttachmentsFetcher
				.getAttachments(
						partnerATSIntegrationDAO,
						candidateDTO.getId(),
						requestBody.getJobId());

		final List<AttachmentDTO> resumes = attachmentDTOs
				.stream()
				.filter(attachmentDTO -> RESUME.equalsIgnoreCase(attachmentDTO.getType()))
				.collect(Collectors.toList());

		if (resumes.isEmpty()) {
			log.info(String.format(
					"No resumes attached to candidateId:%s jobId:%s partner:%s",
					candidateDTO.getId(),
					requestBody.getJobId(),
					partnerATSIntegrationDAO.getPartnerId()));
			return null;
		}

		return this.smartRecruitersAttachmentDownloader
				.getAttachment(
						partnerATSIntegrationDAO,
						resumes.get(0));
	}

	private UserDTO getHiringManager(
			final PartnerATSIntegrationDAO partnerATSIntegrationDAO,
			final String jobId) {
		final List<HiringTeamMemberDTO> hiringTeamDTOS = this.smartRecruitersHiringTeamFetcher
				.getHiringTeam(
						partnerATSIntegrationDAO,
						jobId);

		final List<HiringTeamMemberDTO> hiringManagers = hiringTeamDTOS
				.stream()
				.filter(hiringTeamMemberDTO -> HIRING_MANAGER.equalsIgnoreCase(hiringTeamMemberDTO.getRole()))
				.collect(Collectors.toList());

		if (hiringManagers.isEmpty()) {
			return null;
		}

		return this.smartRecruitersUserFetcher
				.getUser(
						partnerATSIntegrationDAO,
						hiringManagers
								.get(0)
								.getId());
	}
}
