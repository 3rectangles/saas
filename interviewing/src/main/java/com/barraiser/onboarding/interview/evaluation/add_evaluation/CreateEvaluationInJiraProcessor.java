/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.add_evaluation;

import com.barraiser.onboarding.common.IdNameField;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.evaluation.DemoEvaluationChecker;
import com.barraiser.onboarding.interview.jira.evaluation.EvaluationServiceDeskHandler;
import com.barraiser.onboarding.interview.jira.JiraWorkflowManager;
import com.barraiser.onboarding.interview.jira.dto.CreateIssueResponse;
import com.barraiser.onboarding.interview.jira.dto.EvaluationServiceDeskIssue;
import com.barraiser.onboarding.interview.jira.dto.IdValueField;
import com.barraiser.onboarding.resume.ParsedResumeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static com.barraiser.onboarding.common.Constants.ROUND_TYPE_INTERNAL;

@AllArgsConstructor
@Component
public class CreateEvaluationInJiraProcessor implements AddEvaluationProcessor {

	private final ObjectMapper objectMapper;

	@Qualifier("applicationEnvironment")
	private final String jiraSyncEnvironment;

	private final JiraWorkflowManager jiraWorkflowManager;
	private final DomainRepository domainRepository;
	private final JiraUUIDRepository jiraUUIDRepository;
	private final CompanyRepository companyRepository;
	private final JobRoleToInterviewStructureRepository jobRoleToInterviewStructureRepository;
	private static final String PENDING_ASSIGNMENT_TO_PENDING_SCHEDULING_TRANSITION_ID = "11";

	private final static Integer DEFAULT_JIRA_SERVICE_ORGANIZATION_ID = 43;
	private final DemoEvaluationChecker demoEvaluationChecker;
	public static final String DEMO_EVALUATION = "DEMO";
	private final ParsedResumeRepository parsedResumeRepository;
	private static final String MESSAGE_FOR_UPDATING_USER_DETAILS_FOR_OPS = "Team \n" +
			"Please cross check alma mater, designation & Current company for the candidate with resume";

	private static final String PROXY_CANDIDATE_NAME_ANONYMOUS_CANDIDATE = "ANONYMOUS CANDIDATE";

	@Override
	public void process(final AddEvaluationProcessingData data) {
		final String jiraKey = this.createEvaluation(data);
		data.setEvaluationJiraKey(jiraKey);
		this.addJiraUuid(data, jiraKey);
	}

	public String createEvaluation(final AddEvaluationProcessingData data) {
		DomainDAO domainDAO = data.getJobRoleDAO() != null ? data.getJobRoleDAO().getDomainId() != null
				? this.domainRepository.findById(data.getJobRoleDAO().getDomainId())
						.orElseThrow(() -> new IllegalArgumentException("Domain does not exist"))
				: null : null;

		final CompanyDAO targetCompanyDAO = data.getJobRoleDAO() != null
				? this.companyRepository.findById(data.getJobRoleDAO().getCompanyId())
						.orElseThrow(() -> new IllegalArgumentException("Target company does not exist"))
				: null;

		final IdNameField issueType = IdNameField.builder()
				.id(EvaluationServiceDeskHandler.JIRA_ISSUE_TYPE_ID_Evaluation).build();
		final IdNameField project = IdNameField.builder().id(JiraWorkflowManager.JIRA_PROJECT_ID_EVALUATION).build();
		final long totalRounds = data.getJobRoleDAO() != null ? this.jobRoleToInterviewStructureRepository
				.countByJobRoleIdAndJobRoleVersionAndInterviewRoundNotIn(data.getJobRoleDAO().getEntityId().getId(),
						data.getJobRoleDAO().getEntityId().getVersion(), Arrays.asList(ROUND_TYPE_INTERNAL))
				: 1;

		final IdValueField syncEnvironment = IdValueField.builder().value(this.jiraSyncEnvironment).build();

		EvaluationServiceDeskIssue.Fields fields = EvaluationServiceDeskIssue.Fields.builder()
				.entityId(data.getEvaluationId())
				.fullName(data.getCandidateName() != null ? data.getCandidateName()
						: PROXY_CANDIDATE_NAME_ANONYMOUS_CANDIDATE + "_" + DateTime.now())
				.email(data.getEmail())
				.phone(data.getPhone())
				.workExperience(data.getWorkExperience() != null ? data.getWorkExperience().toString() : null)
				.resumeLink(data.getResumeUrl())
				.issuetype(issueType)
				.domain(domainDAO != null ? domainDAO.getName() : null)
				.jobRoleId(
						data.getJobRoleDAO() != null ? data.getJobRoleDAO().getEntityId().getId() : null)
				.totalBarraiserRounds(totalRounds)
				.jobRoleName(data.getJobRoleDAO() != null ? data.getJobRoleDAO().getInternalDisplayName()
						: null)
				.project(project)
				.pocEmail(data.getPocEmail())
				.targetCompany(targetCompanyDAO != null ? targetCompanyDAO.getName() : null)
				.organization(List.of(DEFAULT_JIRA_SERVICE_ORGANIZATION_ID))
				.syncEnvironment(syncEnvironment)
				.designation(
						data.getParsedResumeDAO() != null ? data.getParsedResumeDAO().getCurrentDesignation() : null)
				.almaMater(data.getParsedResumeDAO() != null ? data.getParsedResumeDAO().getAlmaMater() : null)
				.currentCompany(
						data.getParsedResumeDAO() != null ? data.getParsedResumeDAO().getCurrentEmployer() : null)
				.build();

		if (data.getIsAddedViaCalendarInterception() == null || !data.getIsAddedViaCalendarInterception()) { // TODO:
																												// Confirm
			if (this.demoEvaluationChecker.isDemoEvaluation(data.getEvaluationId(),
					data.getJobRoleDAO().getEntityId().getId(), data.getJobRoleDAO().getEntityId().getVersion())) {
				fields = fields.toBuilder().priorityFlags(List.of(DEMO_EVALUATION)).build();
			}
			if (this.shouldInformOpsForUserDetailsUpdation(data.getParsedResumeDAO())) {
				fields = fields.toBuilder().description(MESSAGE_FOR_UPDATING_USER_DETAILS_FOR_OPS).build();
			}
		}

		final CreateIssueResponse issueResponse = this.jiraWorkflowManager.createIssue(this.getCreateIssueBody(fields));

		return issueResponse.getKey();
	}

	public ObjectNode getCreateIssueBody(final EvaluationServiceDeskIssue.Fields fields) {

		final ObjectNode requestBody = this.objectMapper.createObjectNode();
		requestBody.putPOJO("fields", fields);
		requestBody.putPOJO("transition",
				IdNameField.builder().id(PENDING_ASSIGNMENT_TO_PENDING_SCHEDULING_TRANSITION_ID).build());

		return requestBody;
	}

	public void addJiraUuid(final AddEvaluationProcessingData data, final String jiraKey) {
		this.jiraUUIDRepository.save(JiraUUIDDAO.builder()
				.jira(jiraKey)
				.uuid(data.getEvaluationId())
				.build());
	}

	private Boolean shouldInformOpsForUserDetailsUpdation(final ParsedResumeDAO parsedResumeDAO) {
		return parsedResumeDAO == null || parsedResumeDAO.getAlmaMater() == null
				|| parsedResumeDAO.getAlmaMater().isEmpty() ||
				parsedResumeDAO.getCurrentDesignation() == null ||
				parsedResumeDAO.getCurrentDesignation().isEmpty() ||
				parsedResumeDAO.getCurrentEmployer() == null ||
				parsedResumeDAO.getCurrentEmployer().isEmpty();
	}

}
