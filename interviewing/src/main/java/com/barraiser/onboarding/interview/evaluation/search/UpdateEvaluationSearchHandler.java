/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.search;

import com.barraiser.common.graphql.types.SkillScore;
import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.EventListener;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.entitychange.EntityChange;
import com.barraiser.onboarding.candidate.CandidateInformationManager;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.events.InterviewingConsumer;
import com.barraiser.onboarding.interview.EvaluationUtil;
import com.barraiser.onboarding.interview.InterviewUtil;
import com.barraiser.onboarding.interview.PartnerConfigManager;
import com.barraiser.onboarding.interview.evaluation.enums.InterviewProcessType;
import com.barraiser.onboarding.interview.evaluation.scores.BgsCalculator;
import com.barraiser.onboarding.interview.evaluation.search.dal.EvaluationSearchDAO;
import com.barraiser.onboarding.interview.evaluation.search.dal.EvaluationSearchRepository;
import com.barraiser.onboarding.interview.jobrole.JobRoleManager;
import com.barraiser.onboarding.interview.status.EvaluationStatusManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//Todo: refactor this class as EntityChangeEventHandler and create seprate classes for updateEvaluationSearchTable and sendEntityUpdateToZapier

@Component
@Log4j2
@RequiredArgsConstructor
public class UpdateEvaluationSearchHandler implements EventListener<InterviewingConsumer> {
	public static final String INTERVIEW = "interview";
	public static final String ERROR_MESSAGE = "could not parse interview raw entity state";

	private final ObjectMapper objectMapper;
	private final EvaluationRepository evaluationRepository;
	private final CandidateInformationManager candidateInformationManager;
	private final EvaluationSearchRepository evaluationSearchRepository;
	private final JobRoleManager jobRoleManager;
	private final EvaluationScoreRepository evaluationScoreRepository;
	private final EvaluationStatusManager evaluationStatusManager;
	private final EvaluationUtil evaluationUtil;
	private final InterviewUtil interviewUtil;
	private final BgsCalculator bgsCalculator;

	@Override
	public List<Class> eventsToListen() {
		return List.of(EntityChange.class);
	}

	@Override
	public void handleEvent(final Event event) {
		final EntityChange entityChange = this.objectMapper.convertValue(event.getPayload(), EntityChange.class);
		this.updateEvaluationSearchTable(entityChange);
		this.sendEntityUpdateToZapier(entityChange);
	}

	public JsonNode getChanges(JsonNode oldJson, JsonNode newJson) {
		ObjectNode changes = new ObjectNode(JsonNodeFactory.instance);
		Iterator<String> fieldNames = oldJson.fieldNames();

		while (fieldNames.hasNext()) {
			String fieldName = fieldNames.next();
			JsonNode oldValue = oldJson.get(fieldName);
			JsonNode newValue = newJson.get(fieldName);

			if (oldValue != null && newValue != null) {
				if (!oldValue.equals(newValue)) {
					ObjectNode changeDetails = new ObjectNode(JsonNodeFactory.instance);
					changeDetails.set("old", oldValue);
					changeDetails.set("new", newValue);
					changes.set(fieldName, changeDetails);
				}
			} else if (oldValue != null || newValue != null) { // the case where one is null but not both
				ObjectNode changeDetails = new ObjectNode(JsonNodeFactory.instance);
				changeDetails.set("old", oldValue);
				changeDetails.set("new", newValue);
				changes.set(fieldName, changeDetails);
			}
		}
		return changes;
	}

	private void sendEntityUpdateToZapier(final EntityChange entityChange) {
		if ("evaluation".equals(entityChange.getEntityName())) {
			try {
				ObjectMapper mapper = new ObjectMapper();
				String rawEntityStateString = entityChange.getRawEntityState();
				JsonNode rawEntityStateJson = mapper.readTree(rawEntityStateString);

				// If you have oldRawEntityStateString inside rawEntityStateJson
				JsonNode oldRawEntityStateJson = mapper.readTree(rawEntityStateJson.get("oldRawEntityState").asText());
				((ObjectNode) rawEntityStateJson).remove("oldRawEntityState");
				((ObjectNode) oldRawEntityStateJson).remove("oldRawEntityState");
				JsonNode updates = getChanges(oldRawEntityStateJson, rawEntityStateJson);
				((ObjectNode) rawEntityStateJson).set("updates", updates);
				((ObjectNode) rawEntityStateJson).put("entityName", entityChange.getEntityName());
				sendJsonToZapier(rawEntityStateJson, "https://hooks.zapier.com/hooks/catch/10879858/3sa5jdh/");
				// Now, you can use rawEntityStateJson and oldRawEntityStateJson as normal JSON
				// objects
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void sendJsonToZapier(JsonNode rawEntityStateJson, String urlString) {
		try {
			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json; utf-8");
			connection.setRequestProperty("Accept", "application/json");
			connection.setDoOutput(true);

			ObjectMapper mapper = new ObjectMapper();
			String jsonInputString = mapper.writeValueAsString(rawEntityStateJson);

			try (OutputStream os = connection.getOutputStream()) {
				byte[] input = jsonInputString.getBytes("utf-8");
				os.write(input, 0, input.length);
			}

			final int responseCode = connection.getResponseCode();
			if (responseCode < 200 && responseCode >= 400) {
				throw new RuntimeException("Failure in sending Zap");
			}
			connection.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void updateEvaluationSearchTable(final EntityChange entityChange) {
		final List<EvaluationDAO> evaluations = new ArrayList<>();
		if ("evaluation".equals(entityChange.getEntityName())) {
			final EvaluationDAO evaluation = this.evaluationRepository.findById(entityChange.getEntityId())
					.orElse(null);
			if (evaluation == null) {
				final Optional<EvaluationSearchDAO> evaluationSearchDAO = this.evaluationSearchRepository
						.findById(entityChange.getEntityId());
				if (evaluationSearchDAO.isPresent()) {
					this.evaluationSearchRepository.deleteById(entityChange.getEntityId());
				}
				return;
			}
			evaluations.add(evaluation);
		} else if ("candidate".equals(entityChange.getEntityName())) {
			try {

				final CandidateDAO candidate = this.objectMapper.readValue(
						entityChange.getRawEntityState(), CandidateDAO.class);
				evaluations.addAll(this.evaluationRepository.findAllByCandidateId(candidate.getId()));
			} catch (final Exception e) {
				log.error("could not parse user_details raw entity state", e);
			}
		} else if ("evaluation_score".equals(entityChange.getEntityName())) {
			try {
				final EvaluationScoreDAO evaluationScore = this.objectMapper.readValue(
						entityChange.getRawEntityState(), EvaluationScoreDAO.class);
				evaluations.add(
						this.evaluationRepository
								.findById(evaluationScore.getEvaluationId())
								.get());
			} catch (final Exception e) {
				log.error("could not parse evaluation_score raw entity state", e);
			}
		} else if (INTERVIEW.equals(entityChange.getEntityName())) {
			try {
				final InterviewDAO interviewDAO = this.objectMapper.readValue(
						entityChange.getRawEntityState(), InterviewDAO.class);
				evaluations.add(
						this.evaluationRepository
								.findById(interviewDAO.getEvaluationId())
								.get());
			} catch (final Exception e) {
				log.error(ERROR_MESSAGE, e);
			}
		}

		evaluations.forEach(
				e -> this.evaluationSearchRepository.save(this.getUpdatedEvaluationSearchData(e)));
	}

	private EvaluationSearchDAO getUpdatedEvaluationSearchData(final EvaluationDAO evaluation) {
		final CandidateDAO candidateDAO = this.candidateInformationManager.getCandidate(evaluation.getCandidateId());

		final Optional<JobRoleDAO> jobRoleDAOOptional = this.jobRoleManager.getJobRoleFromEvaluation(evaluation);
		JobRoleDAO jobRole = null;
		if (jobRoleDAOOptional.isPresent()) {
			jobRole = jobRoleDAOOptional.get();
		}
		final EvaluationDAO updatedEvaluationDAO = this.evaluationStatusManager.populateStatus(List.of((evaluation)))
				.get(0);

		// todo: jobrole
		if (jobRoleDAOOptional.isEmpty()) {
			return EvaluationSearchDAO.builder()
					.id(updatedEvaluationDAO.getId())
					.createdOn(updatedEvaluationDAO.getCreatedOn())
					.deletedOn(updatedEvaluationDAO.getDeletedOn())
					.pocEmail(updatedEvaluationDAO.getPocEmail())
					.companyId(updatedEvaluationDAO.getCompanyId())
					.partnerId(updatedEvaluationDAO.getPartnerId())
					.candidateName(
							String.format(
									"%s %s", candidateDAO.getFirstName(), candidateDAO.getLastName()))
					.displayStatus(updatedEvaluationDAO.getFinalStatus().getDisplayStatus())
					.bgs(this.getBgs(updatedEvaluationDAO.getId(),
							updatedEvaluationDAO.getDefaultScoringAlgoVersion(),
							updatedEvaluationDAO.getPartnerId()))
					.statusUpdatedOn(this.evaluationStatusManager.getStatusUpdatedOn(updatedEvaluationDAO))
					.isPendingApproval(this.evaluationUtil.checkIfEvaluationIsPendingApproval(updatedEvaluationDAO))
					.haveQueryForPartner(this.evaluationUtil.checkIfEvaluationHaveQueryForPartner(updatedEvaluationDAO))
					.containsInternalInterview(
							!this.interviewUtil.getAllInternalInterviewsForEvaluation(evaluation.getId()).isEmpty())
					.build();
		}

		return EvaluationSearchDAO.builder()
				.id(updatedEvaluationDAO.getId())
				.createdOn(updatedEvaluationDAO.getCreatedOn())
				.deletedOn(updatedEvaluationDAO.getDeletedOn())
				.pocEmail(updatedEvaluationDAO.getPocEmail())
				.jobRoleId(jobRole.getEntityId().getId())
				.jobRoleVersion(jobRole.getEntityId().getVersion())
				.jobRoleName(jobRole.getInternalDisplayName())
				.domainId(jobRole.getDomainId())
				.companyId(jobRole.getCompanyId())
				.partnerId(jobRole.getPartnerId())
				.candidateName(
						String.format(
								"%s %s", candidateDAO.getFirstName(), candidateDAO.getLastName()))
				.displayStatus(updatedEvaluationDAO.getFinalStatus().getDisplayStatus())
				.bgs(this.getBgs(updatedEvaluationDAO.getId(),
						updatedEvaluationDAO.getDefaultScoringAlgoVersion(),
						jobRole.getPartnerId()))
				.statusUpdatedOn(this.evaluationStatusManager.getStatusUpdatedOn(updatedEvaluationDAO))
				.isPendingApproval(this.evaluationUtil.checkIfEvaluationIsPendingApproval(updatedEvaluationDAO))
				.haveQueryForPartner(this.evaluationUtil.checkIfEvaluationHaveQueryForPartner(updatedEvaluationDAO))
				.containsInternalInterview(
						!this.interviewUtil.getAllInternalInterviewsForEvaluation(evaluation.getId()).isEmpty())
				.build();
	}

	// todo: TP-722 confirm that this is updated with scores calcualted according to
	// config
	private double getBgs(final String evaluationId, final String scoringAlgoVersion, String partnerId) {
		final List<EvaluationScoreDAO> scoreDAOs = this.evaluationScoreRepository
				.findAllByEvaluationIdIn(List.of(evaluationId))
				.stream()
				.filter(
						s -> InterviewProcessType.OVERALL.equals(s.getProcessType())
								&& s.getScoringAlgoVersion()
										.equals(scoringAlgoVersion))
				.collect(Collectors.toList());

		final List<SkillScore> scores = scoreDAOs.stream()
				.map(s -> this.objectMapper.convertValue(s, SkillScore.class))
				.collect(Collectors.toList());

		return BgsCalculator.calculateBgsNoScaleDouble(scores);
	}
}
