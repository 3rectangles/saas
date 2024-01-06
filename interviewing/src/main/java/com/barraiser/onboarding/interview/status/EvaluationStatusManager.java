/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.status;

import com.barraiser.onboarding.auth.AuthorizationException;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.PartnerConfigManager;
import com.barraiser.onboarding.interview.evaluation.EvaluationChangeHistoryManager;
import com.barraiser.onboarding.interview.evaluation.EvaluationPartnerStatus;
import com.barraiser.onboarding.interview.jira.dto.JiraChangeLogsResponse;
import com.barraiser.onboarding.interview.jira.evaluation.EvaluationEventGenerator;

import com.barraiser.onboarding.jobRoleManagement.JobRole.JobRoleEvaluationStatisticsHandler;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class EvaluationStatusManager {
	private static final String ENTITY_TYPE = "evaluation";
	public static final String BARRAISER_PARTNER_ID = "BarRaiser";
	private static final String BARRAISER_STATUS_FIELD_NAME = "barraiser_status";
	private static final String PARTNER_STATUS_FIELD_NAME = "partner_status";

	private final StatusRepository statusRepository;
	private final StatusOrderRepository statusOrderRepository;
	private final EvaluationRepository evaluationRepository;
	private final EvaluationEventGenerator evaluationEventGenerator;
	private final EvaluationChangeHistoryRepository evaluationChangeHistoryRepository;
	private final EvaluationChangeHistoryManager evaluationChangeHistoryManager;
	private final PartnerConfigManager partnerConfigManager;
	private final JobRoleEvaluationStatisticsHandler jobRoleEvaluationStatisticsHandler;

	public List<StatusDAO> getAllStatusForPartner(final String partnerId) {
		final List<StatusDAO> statusDAOs;
		if (this.isStatusCustomizedForPartner(partnerId)) {
			statusDAOs = this.statusRepository.findAllByPartnerIdInAndEntityType(
					List.of(partnerId, BARRAISER_PARTNER_ID), ENTITY_TYPE);
		} else {
			statusDAOs = this.statusRepository.findAllDefaultEvaluationStatus();
		}
		final List<StatusOrderDAO> statusOrderDAOs = this.statusOrderRepository.findAllByStatusIdIn(
				statusDAOs.stream().map(StatusDAO::getId).collect(Collectors.toList()));
		statusDAOs.sort(
				(s1, s2) -> {
					final Integer o1 = statusOrderDAOs.stream()
							.filter(s -> s.getStatusId().equals(s1.getId()))
							.findFirst()
							.get()
							.getOrderIndex();
					final Integer o2 = statusOrderDAOs.stream()
							.filter(s -> s.getStatusId().equals(s2.getId()))
							.findFirst()
							.get()
							.getOrderIndex();
					return o1 - o2;
				});
		return statusDAOs;
	}

	public List<String> getAllDisplayStatusForPartner(final String partnerId) {
		final List<StatusDAO> statusDAOs = this.getAllStatusForPartner(partnerId);
		final List<String> displayStatus = new ArrayList<>();
		for (final StatusDAO statusDAO : statusDAOs) {
			if (!displayStatus.contains(statusDAO.getDisplayStatus())) {
				displayStatus.add(statusDAO.getDisplayStatus());
			}
		}
		return displayStatus;
	}

	public Boolean isStatusCustomizedForPartner(final String partnerId) {
		final Optional<StatusDAO> statusDAO = this.statusRepository.findTopByPartnerIdAndEntityType(partnerId,
				ENTITY_TYPE);
		return statusDAO.isPresent();
	}

	public Long getPartnerStatusTransitionedTime(final String evaluationId, final String statusId) {
		final Instant createdOnInstant = this.evaluationChangeHistoryRepository
				.findTopByEvaluationIdAndFieldNameAndFieldValueOrderByCreatedOnDesc(
						evaluationId, PARTNER_STATUS_FIELD_NAME, statusId)
				.orElse(EvaluationChangeHistoryDAO.builder().build())
				.getCreatedOn();
		return createdOnInstant == null ? null : createdOnInstant.getEpochSecond();
	}

	public Long getBarRaiserStatusTransitionedTime(final String evaluationId, final String status) {
		final StatusDAO statusDAO = this.getBarRaiserStatusByInternalStatus(status);
		final Instant createdOnInstant = this.evaluationChangeHistoryRepository
				.findTopByEvaluationIdAndFieldNameAndFieldValueOrderByCreatedOnDesc(
						evaluationId, BARRAISER_STATUS_FIELD_NAME, statusDAO.getId())
				.orElse(EvaluationChangeHistoryDAO.builder().build())
				.getCreatedOn();
		return createdOnInstant == null ? null : createdOnInstant.getEpochSecond();
	}

	public Instant getStatusUpdatedOn(final EvaluationDAO evaluation) {
		final Long updatedOnEpoch;
		if (evaluation.getPartnerStatus() != null) {
			updatedOnEpoch = this.getPartnerStatusTransitionedTime(
					evaluation.getId(), evaluation.getPartnerStatus().getId());
		} else {
			updatedOnEpoch = this.getBarRaiserStatusTransitionedTime(
					evaluation.getId(), evaluation.getStatus());
		}
		return updatedOnEpoch == null ? null : Instant.ofEpochSecond(updatedOnEpoch);
	}

	public void transitionPartnerStatus(
			final String evaluationId, final String statusId, final String transitionedBy) {
		final StatusDAO toPartnerStatus = this.getStatusById(statusId);
		final EvaluationDAO evaluationDAO = this.getEvaluation(evaluationId);
		if (!(EvaluationStatus.DONE.getValue().equals(evaluationDAO.getStatus())
				|| EvaluationStatus.CANCELLED.getValue().equals(evaluationDAO.getStatus()))) {
			throw new IllegalArgumentException(
					"You are not allowed to transition to " + toPartnerStatus.getDisplayStatus());
		}

		this.savePartnerStatus(evaluationDAO, toPartnerStatus, transitionedBy);
	}

	public EvaluationDAO transitionBarRaiserStatus(
			final String evaluationId,
			final String status,
			final String transitionedBy) {
		final EvaluationDAO originalEvaluation = this.evaluationRepository.findById(evaluationId).get();
		final String originalStatus = originalEvaluation.getStatus();
		final StatusDAO statusDAO = this.getBarRaiserStatusByInternalStatus(status);
		EvaluationDAO updatedEvaluation = this.saveBarRaiserStatus(originalEvaluation, statusDAO, transitionedBy);
		if (this.hasTransitionedToDone(status, originalStatus)) {
			final long bgsCreatedTimeEpoch = Instant.now().getEpochSecond();
			updatedEvaluation = updatedEvaluation.toBuilder().bgsCreatedTimeEpoch(bgsCreatedTimeEpoch).build();
			updatedEvaluation = this.evaluationRepository.save(updatedEvaluation);
			this.jobRoleEvaluationStatisticsHandler.removeActiveCandidateCount(updatedEvaluation.getJobRoleId(),
					updatedEvaluation.getJobRoleVersion());
			this.evaluationEventGenerator.sendEvaluationCompletedEvent(updatedEvaluation);
		}
		if (this.hasTransitionedToCancelled(status, originalStatus)) {
			this.jobRoleEvaluationStatisticsHandler.removeActiveCandidateCount(updatedEvaluation.getJobRoleId(),
					updatedEvaluation.getJobRoleVersion());
			this.evaluationEventGenerator.sendEvaluationCancelledEvent(updatedEvaluation);
		}
		return updatedEvaluation;
	}

	public EvaluationDAO transitionBarRaiserStatus(
			final String evaluationId,
			final String status,
			final String transitionedBy, final List<JiraChangeLogsResponse.ChangeLog> changeLogs) {
		final EvaluationDAO originalEvaluation = this.evaluationRepository.findById(evaluationId).get();
		final String originalStatus = originalEvaluation.getStatus();
		final StatusDAO statusDAO = this.getBarRaiserStatusByInternalStatus(status);
		EvaluationDAO updatedEvaluation = this.saveBarRaiserStatus(originalEvaluation, statusDAO, transitionedBy,
				changeLogs);
		if (this.hasTransitionedToDone(status, originalStatus)) {
			final long bgsCreatedTimeEpoch = Instant.now().getEpochSecond();
			updatedEvaluation = updatedEvaluation.toBuilder().bgsCreatedTimeEpoch(bgsCreatedTimeEpoch).build();
			updatedEvaluation = this.evaluationRepository.save(updatedEvaluation);
			this.jobRoleEvaluationStatisticsHandler.removeActiveCandidateCount(updatedEvaluation.getJobRoleId(),
					updatedEvaluation.getJobRoleVersion());
			this.evaluationEventGenerator.sendEvaluationCompletedEvent(updatedEvaluation);
		}
		if (this.hasTransitionedToCancelled(status, originalStatus)) {
			this.jobRoleEvaluationStatisticsHandler.removeActiveCandidateCount(updatedEvaluation.getJobRoleId(),
					updatedEvaluation.getJobRoleVersion());
			this.evaluationEventGenerator.sendEvaluationCancelledEvent(updatedEvaluation);
		}
		return updatedEvaluation;
	}

	private EvaluationDAO saveBarRaiserStatus(
			final EvaluationDAO evaluationDAO,
			final StatusDAO status,
			final String transitionedBy) {
		if (!status.getInternalStatus().equals(evaluationDAO.getStatus())) {
			this.evaluationChangeHistoryManager.saveHistory(
					evaluationDAO.getId(),
					BARRAISER_STATUS_FIELD_NAME,
					status.getId(),
					transitionedBy,
					null);
		}
		final EvaluationDAO evaluationToSave = evaluationDAO.toBuilder().status(status.getInternalStatus()).build();
		return this.evaluationRepository.save(evaluationToSave);
	}

	private EvaluationDAO saveBarRaiserStatus(
			final EvaluationDAO evaluationDAO,
			final StatusDAO status,
			final String transitionedBy, final List<JiraChangeLogsResponse.ChangeLog> changeLogs) {
		if (!status.getInternalStatus().equals(evaluationDAO.getStatus())) {
			this.evaluationChangeHistoryManager.saveHistory(
					evaluationDAO.getId(),
					BARRAISER_STATUS_FIELD_NAME,
					status.getId(),
					transitionedBy,
					this.getToStatusTransitionTime(status, changeLogs));
		}
		final EvaluationDAO evaluationToSave = evaluationDAO.toBuilder().status(status.getInternalStatus()).build();
		return this.evaluationRepository.save(evaluationToSave);
	}

	private void savePartnerStatus(
			final EvaluationDAO evaluationDAO,
			final StatusDAO toPartnerStatus,
			final String transitionedBy) {
		final StatusDAO fromPartnerStatus = evaluationDAO.getPartnerStatus();
		if (fromPartnerStatus == null
				|| !fromPartnerStatus.getId().equals(toPartnerStatus.getId())) {
			this.evaluationChangeHistoryManager.saveHistory(
					evaluationDAO.getId(),
					PARTNER_STATUS_FIELD_NAME,
					toPartnerStatus.getId(),
					transitionedBy,
					null);
		}
		final EvaluationDAO evaluationToSave = evaluationDAO.toBuilder().partnerStatus(toPartnerStatus).build();
		this.evaluationRepository.save(evaluationToSave);
	}

	private StatusDAO getStatusById(final String statusId) {
		final StatusDAO statusDAO = this.statusRepository
				.findById(statusId)
				.orElseThrow(() -> new IllegalArgumentException("status not found"));
		if (!statusDAO.getEntityType().equals(ENTITY_TYPE)) {
			throw new IllegalArgumentException("status not found");
		}
		return statusDAO;
	}

	private StatusDAO getBarRaiserStatusByInternalStatus(final String internalStatus) {
		return this.statusRepository
				.findByInternalStatusAndPartnerIdAndEntityType(
						internalStatus, BARRAISER_PARTNER_ID, ENTITY_TYPE)
				.orElseThrow(() -> new IllegalArgumentException("status not found"));
	}

	private boolean hasTransitionedToDone(final String status, final String originalStatus) {
		return status.equals(EvaluationStatus.DONE.getValue())
				&& !EvaluationStatus.DONE.getValue().equals(originalStatus);
	}

	private boolean hasTransitionedToCancelled(
			final String status, final String originalStatus) {
		return status.equals(EvaluationStatus.CANCELLED.getValue())
				&& !EvaluationStatus.CANCELLED.getValue().equals(originalStatus);
	}

	public EvaluationDAO getEvaluation(final String evaluationId) {
		return this.evaluationRepository
				.findById(evaluationId)
				.orElseThrow(() -> new IllegalArgumentException("evaluation not found"));
	}

	// TODO: DO NOT USE THIS ANYMORE. made for making barraiserStatus and
	// finalStatus transient. Ideally, these two fields shouldn't be present
	public List<EvaluationDAO> populateStatus(final List<EvaluationDAO> evaluations) {
		final List<StatusDAO> barraiserStatuses = this.statusRepository
				.findAllByPartnerIdInAndEntityType(List.of(BARRAISER_PARTNER_ID), ENTITY_TYPE);
		final List<EvaluationDAO> updatedEvaluations = evaluations.stream().map(
				e -> e.toBuilder()
						.barraiserStatus(barraiserStatuses.stream()
								.filter(s -> e.getStatus().equals(s.getInternalStatus())).findFirst().get())
						.build())
				.collect(Collectors.toList());
		return updatedEvaluations.stream().map(
				e -> e.toBuilder()
						.finalStatus(e.getPartnerStatus() != null ? e.getPartnerStatus() : e.getBarraiserStatus())
						.build())
				.collect(Collectors.toList());
	}

	private Instant getToStatusTransitionTime(final StatusDAO status,
			final List<JiraChangeLogsResponse.ChangeLog> changeLogs) {
		Instant historyTimestamp = Instant.now();
		for (final JiraChangeLogsResponse.ChangeLog changeLog : changeLogs) {
			for (final JiraChangeLogsResponse.ChangeLog.Item item : changeLog.getItems()) {
				if (item.getToString()
						.equalsIgnoreCase(EvaluationStatus.fromString(status.getInternalStatus()).getValue())) {
					historyTimestamp = changeLog.getCreated().toInstant();
				}
			}
		}
		return historyTimestamp;
	}

	public Long getBgsCreatedTime(final String evaluationId) {
		final StatusDAO statusDAO = this.getBarRaiserStatusByInternalStatus(EvaluationStatus.DONE.getValue());
		final Instant evaluationDoneInstant = this.evaluationChangeHistoryRepository
				.findTopByEvaluationIdAndFieldNameAndFieldValueOrderByCreatedOnDesc(
						evaluationId, BARRAISER_STATUS_FIELD_NAME, statusDAO.getId())
				.orElse(EvaluationChangeHistoryDAO.builder().build())
				.getFieldChangedOn();
		return evaluationDoneInstant == null ? null : evaluationDoneInstant.getEpochSecond();
	}

	public Optional<StatusDAO> getPartnerStatusByInternalStatus(final String partnerId,
			final EvaluationPartnerStatus evaluationPartnerStatus) {
		if (this.isStatusCustomizedForPartner(partnerId)) {
			return this.statusRepository.findByInternalStatusAndPartnerIdAndEntityType(
					evaluationPartnerStatus.getValue(), partnerId, ENTITY_TYPE);
		} else {
			return this.statusRepository
					.findDefaultEvaluationStatusForInternalStatus(evaluationPartnerStatus.getValue());
		}
	}

	public void transitionPartnerStatus(
			final EvaluationDAO evaluationDAO, final EvaluationPartnerStatus evaluationPartnerStatus,
			final String transitionedBy) {
		final String partnerId = this.partnerConfigManager.getPartnerIdFromCompanyId(evaluationDAO.getCompanyId());
		final StatusDAO partnerStatus = this.getPartnerStatusByInternalStatus(partnerId,
				evaluationPartnerStatus).get();
		this.transitionPartnerStatus(evaluationDAO.getId(), partnerStatus.getId(), transitionedBy);
	}
}
