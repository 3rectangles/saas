/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.auth;

import com.barraiser.common.graphql.input.GetInterviewsInput;
import com.barraiser.onboarding.auth.AuthorizationException;
import com.barraiser.onboarding.auth.ResourceAuthorizer;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.candidate.CandidateInformationManager;
import com.barraiser.onboarding.dal.EvaluationDAO;
import com.barraiser.onboarding.dal.UserDetailsDAO;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.InterviewUtil;

import com.barraiser.onboarding.partner.EvaluationManager;
import com.barraiser.onboarding.partner.auth.PartnerPortalAuthorizer;
import com.barraiser.commons.auth.UserRole;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Log4j2
public class InterviewAuthorizer implements ResourceAuthorizer {
	public final static String RESOURCE_TYPE = "interview";
	public final static String ACTION_SCHEDULE = "schedule_interview";
	public final static String ACTION_READ_AND_WRITE_PREFERRED_SLOTS = "read_and_write_preferred_slots";
	public final static String ACTION_READ = "read";
	public final static String ACTION_REOPEN = "reopen";

	private final PartnerPortalAuthorizer partnerPortalAuthorizer;
	private final CandidateInformationManager candidateInformationManager;
	private final EvaluationManager evaluationManager;
	private final InterviewUtil interviewUtil;
	private final InterViewRepository interViewRepository;

	@Override
	public String type() {
		return RESOURCE_TYPE;
	}

	@Override
	public void can(final AuthenticatedUser user, final String action, final Object resource)
			throws AuthorizationException {
		final String interviewId = this.getInterviewIdFromResourceObject(resource);
		if (action.equals(ACTION_READ)) {
			this.canRead(user, (GetInterviewsInput) resource);
			return;
		}
		switch (action) {
			case ACTION_SCHEDULE:
				this.canSchedule(user, interviewId);
				break;
			case ACTION_READ_AND_WRITE_PREFERRED_SLOTS:
				this.canReadAndWritePreferredSlotsForInterview(user, interviewId);
				break;
			case ACTION_REOPEN:
				this.canReopen(user);
				break;
			default:
				throw new IllegalArgumentException(
						"no valid action found " + action + " on resource type " + RESOURCE_TYPE);
		}
	}

	private String getInterviewIdFromResourceObject(final Object resource) {
		return resource.toString();
	}

	private void canRead(final AuthenticatedUser user, final GetInterviewsInput interviewInput) {
		if (this.isSuperUser(user)) {
			return;
		}
		if (interviewInput.getInterviewerId() != null
				&& !user.getUserName().equals(interviewInput.getInterviewerId())) {
			throw new AuthorizationException();
		}
	}

	private void canSchedule(final AuthenticatedUser user, final String interviewId)
			throws AuthorizationException {
		this.canPerformActionOnSlots(user, interviewId);
	}

	private void canReadAndWritePreferredSlotsForInterview(final AuthenticatedUser user, final String interviewId)
			throws AuthorizationException {
		this.canPerformActionOnSlots(user, interviewId);
	}

	public String getPartnerCompanyForInterview(final String interviewId) {
		final EvaluationDAO evaluationDAO = this.interviewUtil.getEvaluationForInterview(interviewId);
		return this.evaluationManager.getPartnerCompanyForEvaluation(evaluationDAO.getId());
	}

	private boolean isCandidateForInterview(final String userId, final String interviewId) {
		final String candidateId = this.interViewRepository.findById(interviewId).get().getIntervieweeId();
		final UserDetailsDAO candidateUser = this.candidateInformationManager.getUserForCandidate(candidateId);
		return userId.equals(candidateUser.getId());
	}

	private void canPerformActionOnSlots(final AuthenticatedUser user, final String interviewId)
			throws AuthorizationException {

		if (this.isSuperUser(user)) {
			return;
		}
		final String partnerCompanyId = this.getPartnerCompanyForInterview(interviewId);

		if (!this.partnerPortalAuthorizer.isPartnerRep(user, partnerCompanyId)
				&& !this.isCandidateForInterview(user.getUserName(), interviewId)) {
			throw new AuthorizationException();
		}
	}

	private void canReopen(final AuthenticatedUser user) {
		if (!this.isSuperUser(user)) {
			throw new AuthorizationException();
		}
	}
}
