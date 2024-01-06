/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.jira;

import com.barraiser.onboarding.common.IdNameField;
import com.barraiser.onboarding.dal.JiraUUIDDAO;
import com.barraiser.onboarding.dal.JiraUUIDRepository;
import com.barraiser.onboarding.dal.UserDetailsDAO;
import com.barraiser.onboarding.dal.UserDetailsRepository;
import com.barraiser.onboarding.interview.jira.dto.EvaluationServiceDeskIssue;
import com.barraiser.onboarding.interview.jira.dto.IdLinkedIssueField;
import com.barraiser.onboarding.interview.jira.dto.IdValueField;
import com.barraiser.onboarding.interview.jira.dto.InterviewServiceDeskIssue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class JiraUtil {

	private static final String IS_PART_OF_EVALUATION_LINK_ID = "10007";
	private static final String PENDING_SCHEDULING_TRANISTION_ID = "151";

	private final PropertyUtilsBean propertyUtilsBean;
	private final JiraUUIDRepository jiraUUIDRepository;
	private final UserDetailsRepository userDetailsRepository;
	private final ObjectMapper objectMapper;

	public String getIdFromString(final String interviewerString) {
		if (interviewerString == null) {
			return null;
		}

		final String[] splits = interviewerString.split(":");

		if (splits.length >= 2) {
			return splits[1].strip();
		}
		return null;
	}

	public String getValueFromString(final String value) {
		if (value == null) {
			return null;
		}

		final String[] splits = value.split(":");

		if (splits.length >= 2) {
			return splits[0].strip();
		}
		return null;
	}

	public String getValueFromField(final IdValueField idValueField) {
		return idValueField == null ? null : idValueField.getValue();
	}

	public JiraUUIDDAO getOrCreateIdAgainstJira(final String jiraId) {
		return this.getOrCreateIdAgainstJira(jiraId, UUID.randomUUID().toString());
	}

	public JiraUUIDDAO getOrCreateIdAgainstJira(final String jiraId, final String uuid) {

		final Optional<JiraUUIDDAO> optionalJiraUUID = this.jiraUUIDRepository.findByJira(jiraId);

		return optionalJiraUUID
				.orElseGet(() -> this.jiraUUIDRepository.save(JiraUUIDDAO.builder()
						.jira(jiraId)
						.uuid(uuid)
						.build()));
	}

	public ObjectNode getIssueLinkUpdateBody(final String inwardIssueKey, final String inwardIssueLinkId) {
		final IdNameField issueLinkType = IdNameField.builder().id(inwardIssueLinkId).build();

		final IdLinkedIssueField linkedEvaluation = IdLinkedIssueField.builder()
				.inwardIssue(EvaluationServiceDeskIssue.builder()
						.key(inwardIssueKey)
						.build())
				.type(issueLinkType)
				.build();

		final ObjectNode issueLink = this.objectMapper.createObjectNode();
		issueLink.putPOJO("add", linkedEvaluation);

		final ObjectNode updateBody = this.objectMapper.createObjectNode();
		updateBody.putPOJO("issuelinks", List.of(issueLink));

		return updateBody;
	}

	public ObjectNode getCreateInterviewIssueBody(final InterviewServiceDeskIssue.Fields fields,
			final String evaluationJiraKey) {

		final ObjectNode requestBody = this.objectMapper.createObjectNode();
		requestBody.putPOJO("fields", fields);
		requestBody.putPOJO("update", this.getIssueLinkUpdateBody(evaluationJiraKey, IS_PART_OF_EVALUATION_LINK_ID));
		requestBody.putPOJO("transition", IdNameField.builder().id(PENDING_SCHEDULING_TRANISTION_ID).build());

		return requestBody;
	}

	@SneakyThrows
	public String getString(final Object object, final String property) {
		return (String) this.propertyUtilsBean.getProperty(object, "issue.fields." + property);
	}

	@SneakyThrows
	public Double getDouble(final Object object, final String property) {
		return (Double) this.propertyUtilsBean.getProperty(object, "issue.fields." + property);
	}

	public String getEntityJiraKey(final String entityId) {
		final JiraUUIDDAO jiraUUIDDAO = this.jiraUUIDRepository.findByUuid(entityId).orElseThrow(
				() -> new IllegalArgumentException("No such UUID for an entity exists in Jiraa UUID table"));
		return jiraUUIDDAO.getJira();
	}

	public List<String> extractIdFromList(final List<IdValueField> fields) {
		if (fields == null) {
			return null;
		}
		return fields.stream()
				.map(IdValueField::getValue)
				.map(this::getIdFromString)
				.collect(Collectors.toList());
	}

	public String getTaggingAgentValueForJira(final String taggingAgentId) {
		StringBuilder sb = new StringBuilder();
		UserDetailsDAO taggingAgent = this.userDetailsRepository.findById(taggingAgentId).get();
		final String taggingAgentFieldValue = sb.append(taggingAgent.getFirstName()).append(" ")
				.append(Objects.isNull(taggingAgent.getLastName()) ? "" : taggingAgent.getLastName()).append(":")
				.append(taggingAgentId).toString();
		return taggingAgentFieldValue;
	}

}
