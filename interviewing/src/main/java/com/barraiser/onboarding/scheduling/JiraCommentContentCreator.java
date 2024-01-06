/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling;

import com.barraiser.onboarding.common.MustacheFormattingUtil;
import com.barraiser.onboarding.dal.*;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

@Log4j2
@Component
@AllArgsConstructor
public class JiraCommentContentCreator {
	private final MustacheFormattingUtil mustacheFormattingUtil;
	private InterviewStructureRepository interviewStructureRepository;
	private JobRoleToInterviewStructureRepository jobRoleToInterviewStructureRepository;
	private EvaluationRepository evaluationRepository;

	@SneakyThrows
	public String createInterviewOperationalUtilityContent(
			final InterviewDAO interview, final UserDetailsDAO interviewer, final Boolean isExpertDuplicate) {
		final String overbooked = isExpertDuplicate ? "(overbooked)" : "";

		final Map<String, String> data = Map.of(
				"interviewId",
				interview.getId(),
				"evaluationId",
				interview.getEvaluationId(),
				"zoomLink",
				interview.getMeetingLink(),
				"firstName",
				interviewer.getFirstName(),
				"lastName",
				interviewer.getLastName() + "" + overbooked,
				"interviewerId",
				interviewer.getId(),
				"startDateTime",
				this.formatDate(interview.getStartDate()),
				"endDateTime",
				this.formatDate(interview.getEndDate()),
				"interviewStructureLink",
				interview.getInterviewStructureId() != null ? this.interviewStructureLink(interview) : "");
		return this.mustacheFormattingUtil.formatDataToText("jira_utility_info", data);
	}

	private String interviewStructureLink(final InterviewDAO interviewDAO) {
		final Optional<EvaluationDAO> evaluationDAO = this.evaluationRepository
				.findById(interviewDAO.getEvaluationId());
		if (evaluationDAO.isEmpty()) {
			log.error("no evaluation present for the interview");
			return "";
		}

		final Optional<JobRoleToInterviewStructureDAO> jobRoleToInterviewStructure = this.jobRoleToInterviewStructureRepository
				.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId(
						evaluationDAO.get().getJobRoleId(),
						evaluationDAO.get().getJobRoleVersion(),
						interviewDAO.getInterviewStructureId());
		if (jobRoleToInterviewStructure.isPresent()) {
			return Optional.ofNullable(jobRoleToInterviewStructure.get().getInterviewStructureLink())
					.orElse("");
		} else {
			log.error("No interview Structure present for the interview id");
			return "";
		}
	}

	private String formatDate(final Long inputDate) {
		final ZonedDateTime date = Instant.ofEpochSecond(inputDate).atZone(ZoneId.of("Asia/Kolkata"));
		return date.format(DateTimeFormatter.ofPattern("dd/MM/YYYY HH:mm:ss"));
	}

	@SneakyThrows
	public String createInterviewerDetailsJiraCommentContent(
			final InterviewDAO interview, final UserDetailsDAO interviewer, final Boolean isExpertDuplicate) {
		final String overbooked = isExpertDuplicate ? "(overbooked)" : "";

		final Map<String, String> data = Map.of(
				"interviewId",
				interview.getId(),
				"firstName",
				interviewer.getFirstName(),
				"lastName",
				interviewer.getLastName() + "" + overbooked,
				"interviewerId",
				interviewer.getId());
		return this.mustacheFormattingUtil.formatDataToText("jira_expert_info_utility", data);
	}
}
