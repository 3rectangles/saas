/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.followup;

import com.barraiser.communication.common.CommunicationStaticAppConfig;
import com.barraiser.onboarding.candidate.CandidateInformationManager;
import com.barraiser.onboarding.dal.UserDetailsDAO;
import com.barraiser.onboarding.dal.UserDetailsRepository;
import com.barraiser.onboarding.endpoint.IvrResponseDAO;
import com.barraiser.onboarding.endpoint.IvrResponseRepository;
import com.barraiser.onboarding.sfn.StepFunctionProcessor;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class GetIvrResponseStatus implements StepFunctionProcessor<FollowUpForSchedulingStepFunctionDTO> {

	private final IvrResponseRepository ivrResponseRepository;
	private final CommunicationStaticAppConfig staticAppConfig;
	private final CandidateInformationManager candidateInformationManager;

	@Override
	public String getFlowIdentifier(FollowUpForSchedulingStepFunctionDTO data) {
		return data.getEvaluationId();
	}

	@Override
	public void process(FollowUpForSchedulingStepFunctionDTO data) throws Exception {
		final UserDetailsDAO userForCandidate = this.candidateInformationManager
				.getUserForCandidate(data.getEvaluation().getCandidateId());

		String phone = userForCandidate != null ? userForCandidate.getPhone() : null;
		String messageBirdFlowId = this.staticAppConfig.getCandidateFollowUpForSchedulingMessagebirdIvrFlowId();
		IvrResponseDAO ivrResponseDAO = this.ivrResponseRepository
				.findTopByPhoneAndMessageBirdFlowIdAndCreatedOnIsNotNullOrderByCreatedOnDesc(phone, messageBirdFlowId)
				.get();
		data.setIvrCallPicked(ivrResponseDAO.getCallAnswered());
	}
}
