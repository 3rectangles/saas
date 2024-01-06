/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation.expertReassignment;

import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.InterviewStatus;
import com.barraiser.onboarding.graphql.GraphQLMutation;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.DTO.ExpertAllocatorData;
import com.barraiser.onboarding.interview.graphql.input.AllocateExpertInput;
import com.barraiser.onboarding.scheduling.scheduling.SchedulingSessionManager;
import com.barraiser.onboarding.user.expert.ExpertUtil;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class AllocateExpertMutation implements GraphQLMutation<Boolean> {
	private final GraphQLUtil graphQLUtil;
	private final ExpertAllocator expertAllocator;
	private final SchedulingSessionManager schedulingSessionManager;
	private final InterViewRepository interViewRepository;
	private final ExpertUtil expertUtil;

	@Override
	public String name() {
		return "allocateExpert";
	}

	@Override
	public Boolean get(final DataFetchingEnvironment environment) throws Exception {
		final AllocateExpertInput input = this.graphQLUtil.getInput(environment, AllocateExpertInput.class);
		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);
		this.validateData(input.getInterviewId());
		final ExpertAllocatorData data = ExpertAllocatorData.builder()
				.interviewId(input.getInterviewId())
				.interviewerId(input.getExpertId())
				.isOnlyCandidateChanged(false)
				.allocatedBy(authenticatedUser.getUserName())
				.source(ExpertAllocationSource.MANUAL_EXPERT_REASSIGNMENT.getValue())
				.build();
		this.expertAllocator.allocateExpertToInterview(data);
		return true;
	}

	private void validateData(final String interviewId) {
		final InterviewDAO interviewDAO = this.interViewRepository.findById(interviewId).get();
		this.schedulingSessionManager.checkIfSchedulingSessionDataIsStale(interviewId,
				interviewDAO.getRescheduleCount());
		if (InterviewStatus.PENDING_SCHEDULING.getValue().equals(interviewDAO.getStatus())) {
			throw new IllegalArgumentException("expert cannot be allocated at this stage");
		}

		if (interviewDAO.getInterviewerId() != null
				&& !this.expertUtil.isExpertDuplicate(interviewDAO.getInterviewerId())) {
			throw new IllegalArgumentException("expert already allocated");
		}
	}
}
