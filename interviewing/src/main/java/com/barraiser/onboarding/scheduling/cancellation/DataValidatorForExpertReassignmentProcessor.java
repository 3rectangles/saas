/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation;

import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.commons.auth.UserRole;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Log4j2
@Component
@AllArgsConstructor
public class DataValidatorForExpertReassignmentProcessor {
	private final InterViewRepository interViewRepository;

	public void validate(final String interviewId, final AuthenticatedUser reassignedBy) {
		final InterviewDAO interviewDAO = this.interViewRepository.findById(interviewId).get();
		if (interviewDAO.getStartDate() <= Instant.now().getEpochSecond()) {
			if (!this.isSuperUser(reassignedBy)) {
				throw new IllegalArgumentException("interview cannot be cancelled after start time");
			}
		}

		if (!this.isSuperUser(reassignedBy) && !(interviewDAO.getInterviewerId().equals(reassignedBy.getUserName()))) {
			throw new IllegalArgumentException("interview cannot be cancelled by the current user");
		}
	}

	private boolean isSuperUser(final AuthenticatedUser user) {
		return user.getRoles().contains(UserRole.OPS) || user.getRoles().contains(UserRole.ADMIN);
	}
}
