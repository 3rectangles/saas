/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.lifecycle;

import com.barraiser.onboarding.sfn.FlowType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InterviewToStepFunctionExecutionRepository
		extends JpaRepository<InterviewToStepFunctionExecutionDAO, String> {

	List<InterviewToStepFunctionExecutionDAO> findAllByInterviewIdIn(List<String> interviewIds);

	Optional<InterviewToStepFunctionExecutionDAO> findTopByInterviewIdAndFlowTypeOrderByCreatedOnDesc(
			String interviewId, FlowType flowType);

	Optional<InterviewToStepFunctionExecutionDAO> findTopByInterviewIdAndFlowTypeAndRescheduleCountOrderByCreatedOnDesc(
			String id, FlowType interviewCancellation, Integer rescheduleCount);

	List<InterviewToStepFunctionExecutionDAO> findAllByInterviewIdAndRescheduleCountAndFlowTypeIn(String interviewId,
			Integer rescheduleCount, List<FlowType> flowTypes);
}
