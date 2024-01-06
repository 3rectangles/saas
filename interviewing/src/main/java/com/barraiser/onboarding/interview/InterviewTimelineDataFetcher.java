/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.types.Interview;
import com.barraiser.common.graphql.types.InterviewChangeHistory;
import com.barraiser.common.graphql.types.UserDetails;
import com.barraiser.commons.auth.UserRole;
import com.barraiser.onboarding.candidate.CandidateInformationManager;
import com.barraiser.onboarding.dal.InterviewHistoryDAO;
import com.barraiser.onboarding.dal.InterviewStatus;
import com.barraiser.onboarding.dal.UserDetailsDAO;
import com.barraiser.onboarding.dal.UserDetailsRepository;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.onboarding.scheduling.confirmation.InterviewConfirmationManager;
import com.barraiser.onboarding.scheduling.lifecycle.DTO.InterviewConfirmationStatus;
import com.barraiser.onboarding.user.UserInformationManagementHelper;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class InterviewTimelineDataFetcher implements NamedDataFetcher {
	public static final String CREATED_BY_BARRAISER = "BarRaiser";
	public static final String CREATED_BY_ZAPIER = "Zapier";
	public static final String DISPLAY_STATUS_RESCHEDULED = "Rescheduled";
	public static final String DISPLAY_STATUS_CANCELLED = "Cancelled";
	public static final String DISPLAY_STATUS_SCHEDULED = "Scheduled";
	public static final String DISPLAY_STATUS_CREATED = "Created";
	public static final String DISPLAY_STATUS_PENDING_DECISION = "Incomplete Interview";
	public static final String DISPLAY_STATUS_NO_SHOW = "No Show";
	public static final String DISPLAY_NAME_CANDIDATE = "Candidate";

	private Map<String, String> statusToDisplayStatusMap;

	private final UserDetailsRepository userDetailsRepository;
	private final CancellationReasonManager cancellationReasonManager;
	private final InterviewHistoryManager interviewHistoryManager;
	private final InterviewManager interviewManager;
	private final InterviewStatusManager interviewStatusManager;
	private final UserInformationManagementHelper userInformationManagementHelper;
	private final InterviewConfirmationManager interviewConfirmationManager;
	private final CandidateInformationManager candidateInformationManager;

	@Override
	public String name() {
		return "interviewTimeline";
	}

	@Override
	public String type() {
		return "Interview";
	}

	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {
		final Interview interview = environment.getSource();
		return this.getInterviewTimeline(interview);
	}

	private List<InterviewChangeHistory> getInterviewTimeline(final Interview interview) {
		final List<InterviewHistoryDAO> interviewHistoryDAOs = this.interviewHistoryManager
				.getEarliestInterviewChangeHistoriesByField(interview.getId(), "status").stream()
				.filter(x -> !(InterviewStatus.CANCELLATION_DONE.getValue().equals(x.getStatus())
						&& x.getInterviewerId() == null))
				.collect(Collectors.toList());
		final List<UserDetailsDAO> createdByUsers = this.userDetailsRepository
				.findAllByIdIn(interviewHistoryDAOs.stream().map(InterviewHistoryDAO::getCreatedBy)
						.filter(x -> x != null && !x.equals(CREATED_BY_BARRAISER) && !x.equals(CREATED_BY_ZAPIER))
						.collect(Collectors.toList()));
		final List<InterviewChangeHistory> interviewChangeHistories = interviewHistoryDAOs.stream()
				.map(x -> this.getInterviewChangeHistory(x, createdByUsers)).collect(Collectors.toList());
		return this.getFormattedList(interviewChangeHistories);
	}

	private List<InterviewChangeHistory> getFormattedList(final List<InterviewChangeHistory> interviewChangeHistories) {
		String previousDisplayStatus = interviewChangeHistories.get(0).getDisplayValue();

		for (int i = 2; i <= interviewChangeHistories.size(); i++) {
			final String currentDisplayStatus = interviewChangeHistories.get(i - 1).getDisplayValue();

			// Previous cancelled will be marked as rescheduled
			if (previousDisplayStatus.equals(DISPLAY_STATUS_CANCELLED)) {
				final UserDetails createdBy = i < interviewChangeHistories.size()
						? interviewChangeHistories.get(i).getCreatedByUser()
						: interviewChangeHistories.get(i - 2).getCreatedByUser();
				interviewChangeHistories.set(i - 2, interviewChangeHistories.get(i - 2).toBuilder()
						.displayValue(DISPLAY_STATUS_RESCHEDULED)
						.createdByUser(createdBy).build());
				previousDisplayStatus = DISPLAY_STATUS_RESCHEDULED;
			}
			if (this.shouldConsiderDisplayStatus(previousDisplayStatus, currentDisplayStatus)) {
				interviewChangeHistories.remove(i - 1);
				i--;
			}
			previousDisplayStatus = interviewChangeHistories.get(i - 1).getDisplayValue();
		}
		return interviewChangeHistories;
	}

	private InterviewChangeHistory getInterviewChangeHistory(final InterviewHistoryDAO interviewHistoryDAO,
			final List<UserDetailsDAO> createdByUsers) {
		final String displayValueOfStatus = this.getDisplayValueOfStatus(interviewHistoryDAO);
		return InterviewChangeHistory.builder()
				.interviewId(interviewHistoryDAO.getInterviewId())
				.fieldName(interviewHistoryDAO.getStatus())
				.createdOn(interviewHistoryDAO.getCreatedOn().getEpochSecond())
				.createdByUser(this.getCreatedByUser(interviewHistoryDAO, createdByUsers))
				.displayValue(displayValueOfStatus)
				.scheduledTime(this.getScheduledTime(displayValueOfStatus, interviewHistoryDAO.getStartDate()))
				.rescheduledTime(this.getRescheduledTime(displayValueOfStatus, interviewHistoryDAO))
				.displayReason(this.getCancellationDisplayReason(displayValueOfStatus,
						interviewHistoryDAO.getCancellationReasonId()))
				.build();
	}

	private UserDetails getCreatedByUser(final InterviewHistoryDAO interviewHistoryDAO,
			final List<UserDetailsDAO> createdByUsers) {
		final String createdBy = interviewHistoryDAO.getCreatedBy();
		final List<String> userRoles = createdBy == null || CREATED_BY_BARRAISER.equalsIgnoreCase(createdBy)
				|| CREATED_BY_ZAPIER.equalsIgnoreCase(createdBy)
						? List.of()
						: this.userInformationManagementHelper.getRolesOfUser(createdBy);
		UserDetails createdByUser = UserDetails.builder().firstName(CREATED_BY_BARRAISER).build();

		if (this.isCandidate(createdBy, interviewHistoryDAO.getIntervieweeId())) {
			createdByUser = UserDetails.builder().firstName(DISPLAY_NAME_CANDIDATE).build();
		} else if (this.isSuperUser(createdBy, userRoles)) {
			createdByUser = UserDetails.builder().firstName(CREATED_BY_BARRAISER).build();
		} else if (this.isPartner(userRoles)) {
			final UserDetailsDAO user = createdByUsers.stream()
					.filter(y -> y.getId().equals(createdBy)).findFirst().get();
			createdByUser = UserDetails.builder()
					.firstName(user.getFirstName())
					.lastName(user.getLastName())
					.email(user.getEmail())
					.phone(user.getPhone()).build();
		}
		return createdByUser;
	}

	private String getDisplayValueOfStatus(final InterviewHistoryDAO interviewHistoryDAO) {
		final String displayValueOfStatus;
		if (InterviewStatus.CANCELLATION_DONE.getValue().equals(interviewHistoryDAO.getStatus())) {
			final String candidateConfirmationStatus = this.interviewConfirmationManager.getInterviewConfirmationStatus(
					interviewHistoryDAO.getInterviewId(), interviewHistoryDAO.getRescheduleCount());
			if (this.shouldDisplayStatusBeNoShow(interviewHistoryDAO, candidateConfirmationStatus)) {
				displayValueOfStatus = DISPLAY_STATUS_NO_SHOW;
			} else {
				displayValueOfStatus = DISPLAY_STATUS_CANCELLED;
			}
		} else {
			displayValueOfStatus = this.statusToDisplayStatusMap.get(interviewHistoryDAO.getStatus());
		}
		return displayValueOfStatus;
	}

	private Long getScheduledTime(final String displayValue, final Long interviewStartDate) {
		return (DISPLAY_STATUS_SCHEDULED.equals(displayValue) ? interviewStartDate : null);
	}

	private Long getRescheduledTime(final String displayValue, final InterviewHistoryDAO interviewHistoryDAO) {
		return (DISPLAY_STATUS_RESCHEDULED.equals(displayValue) && interviewHistoryDAO.getIsRescheduled()
				? this.interviewManager.getRescheduledTimeOfInterview(interviewHistoryDAO.getId())
				: null);
	}

	private String getCancellationDisplayReason(final String displayValue, final String cancellationReasonId) {
		return ((DISPLAY_STATUS_RESCHEDULED.equals(displayValue) || DISPLAY_STATUS_CANCELLED.equals(displayValue))
				? this.cancellationReasonManager.getDisplayReason(cancellationReasonId)
				: null);
	}

	private boolean shouldConsiderDisplayStatus(final String previousDisplayStatus, final String currentDisplayStatus) {
		return currentDisplayStatus.equals(DISPLAY_STATUS_CREATED) ||
				(previousDisplayStatus.equals(DISPLAY_STATUS_RESCHEDULED)
						&& currentDisplayStatus.equals(DISPLAY_STATUS_SCHEDULED))
				||
				previousDisplayStatus.equals(currentDisplayStatus) ||
				previousDisplayStatus.equals(DISPLAY_STATUS_NO_SHOW)
						&& currentDisplayStatus.equals(DISPLAY_STATUS_CANCELLED)
				||
				DISPLAY_STATUS_PENDING_DECISION.equals(currentDisplayStatus);
	}

	private boolean isCandidate(final String createdBy, final String intervieweeId) {
		final UserDetailsDAO candidateUser = this.candidateInformationManager.getUserForCandidate(intervieweeId);
		return createdBy != null && createdBy.equals(candidateUser.getId());
	}

	private boolean isSuperUser(final String userId, final List<String> userRoles) {
		return CREATED_BY_BARRAISER.equalsIgnoreCase(userId) || userRoles.contains(UserRole.ADMIN.getRole()) ||
				userRoles.contains(UserRole.OPS.getRole()) || userRoles.contains(UserRole.QC.getRole())
				|| CREATED_BY_ZAPIER.equalsIgnoreCase(userId);
	}

	private boolean isPartner(final List<String> userRoles) {
		return userRoles.contains(UserRole.PARTNER.getRole())
				|| userRoles.contains(UserRole.PARTNER_EMPLOYEE.getRole());
	}

	private boolean shouldDisplayStatusBeNoShow(final InterviewHistoryDAO interviewHistoryDAO,
			final String candidateConfirmationStatus) {
		return (candidateConfirmationStatus.equals(InterviewConfirmationStatus.CONFIRMED.name()) ||
				candidateConfirmationStatus.equals(InterviewConfirmationStatus.NOACTION.name()))
				&& interviewHistoryDAO.getStartDate() != null &&
				interviewHistoryDAO.getCancellationTime() != null &&
				(Long.parseLong(interviewHistoryDAO.getCancellationTime()) > interviewHistoryDAO.getStartDate());
	}

	@PostConstruct
	private void fetchStatusToDisplayStatusMapping() {
		this.statusToDisplayStatusMap = this.interviewStatusManager.getMapOfStatusToDisplayStatus();
	}
}
