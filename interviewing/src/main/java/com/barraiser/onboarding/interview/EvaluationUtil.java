/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.onboarding.dal.EvaluationDAO;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.JobRoleToInterviewStructureDAO;
import com.barraiser.onboarding.dal.JobRoleToInterviewStructureRepository;
import com.barraiser.onboarding.dal.ReasonDAO;
import com.barraiser.onboarding.dal.ReasonRepository;
import com.barraiser.onboarding.dal.WaitingClientReason;
import com.barraiser.onboarding.dal.WaitingInformationDAO;
import com.barraiser.onboarding.dal.WaitingInformationRepository;
import com.barraiser.onboarding.interview.status.EvaluationStatusManager;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Log4j2
@Component
@AllArgsConstructor
public class EvaluationUtil {
	private static final String BR_EVALUATED = "BR Evaluated";
	private final WaitingInformationRepository waitingInformationRepository;
	private final ReasonRepository reasonRepository;
	private final InterViewRepository interViewRepository;
	private final InterviewUtil interviewUtil;
	private final JobRoleToInterviewStructureRepository jobRoleToInterviewStructureRepository;
	private final EvaluationStatusManager evaluationStatusManager;

	public Boolean checkIfEvaluationHaveQueryForPartner(EvaluationDAO evaluation) {
		Optional<WaitingInformationDAO> waitingInformationDAO = this.waitingInformationRepository
				.findById(evaluation.getId());
		if (waitingInformationDAO.isPresent()) {
			Optional<ReasonDAO> reasonDAO = this.reasonRepository
					.findById(waitingInformationDAO.get().getWaitingReasonId());
			return reasonDAO.isPresent()
					&& WaitingClientReason.ASKED_TO_KEEP_THE_CANDIDATE_ON_HOLD.getValue()
							.equalsIgnoreCase(reasonDAO.get().getReason());
		}
		return false;
	}

	public Boolean checkIfEvaluationIsPendingApproval(EvaluationDAO evaluationDAO) {
		final EvaluationDAO updatedEvaluationDAO = this.evaluationStatusManager.populateStatus(List.of(evaluationDAO))
				.get(0);
		if (evaluationDAO.getFinalStatus().getDisplayStatus().equalsIgnoreCase(BR_EVALUATED)) {
			return true;
		}
		List<InterviewDAO> interviewDAOList = this.interViewRepository
				.findAllByEvaluationId(updatedEvaluationDAO.getId());

		if (evaluationDAO.getJobRoleId() == null) {
			return false;
		}

		return interviewDAOList.stream()
				.anyMatch(
						interviewDAO -> {
							JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO = this.jobRoleToInterviewStructureRepository
									.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId(
											updatedEvaluationDAO.getJobRoleId(),
											updatedEvaluationDAO.getJobRoleVersion(),
											interviewDAO.getInterviewStructureId())
									.get();
							return this.interviewUtil.doesInterviewRequireApproval(
									interviewDAO, jobRoleToInterviewStructureDAO);
						});
	}
}
