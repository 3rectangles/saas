/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.communication;

import com.barraiser.common.utilities.DateUtils;
import com.barraiser.onboarding.candidate.CandidateInformationManager;
import com.barraiser.onboarding.common.StaticAppConfigValues;
import com.barraiser.onboarding.communication.channels.email.EmailService;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.featureToggle.FeatureToggleNames;
import com.barraiser.onboarding.featureToggle.InterviewLevelFeatureToggleManager;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.PartnerConfigManager;
import com.barraiser.onboarding.scheduling.lifecycle.InterviewToStepFunctionExecutionRepository;
import com.barraiser.onboarding.sfn.FlowType;
import com.barraiser.onboarding.user.TimezoneManager;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

import static com.barraiser.onboarding.common.Constants.CANCELLATION_TYPE_CANDIDATE_AND_EXPERT;

@Log4j2
@Component
@AllArgsConstructor
public class InterviewCancellationCommunicationService {

	public static final String CANCELLATION_REASON_TYPE_CANDIDATE = "CANDIDATE";
	public static final String DATEFORMAT_1 = "dd MMM uuuu, hh:mm a z";
	public static final String EXPERT_EMAIL_ID = "expert@barraiser.com";
	public static final String INTERVIEW_BARRAISER_EMAIL_ID = "interview@barraiser.com";
	public static final String JIRA_ISSUE_LINK_PREFIX = "https://barraiser.atlassian.net/browse/";
	public static final String TA_EMAIL_FOR_INTERVIEW_UPDATION = "ta_email_for_interview_updation";
	public static final String TA_EMAIL_FOR_INTERVIEW_CANCELLATION = "ta_email_for_interview_cancellation";

	private final InterViewRepository interviewRepository;
	private final EmailService emailService;
	private final UserDetailsRepository userDetailsRepository;
	private final CandidateInformationManager candidateInformationManager;
	private final CancellationReasonRepository cancellationReasonRepository;
	private final StaticAppConfigValues staticAppConfigValues;
	private final JiraUUIDRepository jiraUUIDRepository;
	private final DateUtils utilities;
	private final CommunicateInterviewCancellationToCandidateService communicateInterviewCancellationToCandidateService;
	private final PartnerConfigManager partnerConfigManager;
	private final TimezoneManager timezoneManager;
	private final InterviewToStepFunctionExecutionRepository interviewToStepFunctionExecutionRepository;

	public void communicateCancellationToExpert(final InterviewDAO cancelledInterview, final String expertId)
			throws Exception {
		this.sendEmailToExpertOnCancellation(cancelledInterview, expertId);
	}

	public void communicateCancellationToCandidate(final InterviewDAO cancelledInterview) throws Exception {
		// 1.Get cancellation reason for the interview
		final CancellationReasonDAO cancellationReasonDAO = this.cancellationReasonRepository
				.findById(cancelledInterview.getCancellationReasonId()).get();
		final String cancellationReasonType = cancellationReasonDAO.getCancellationType();
		final Boolean isCandidateSchedulingAllowed = this.partnerConfigManager
				.shouldSendSchedulingLinkToCandidate(cancelledInterview);

		// Check if cancelled by candidate
		if (CANCELLATION_REASON_TYPE_CANDIDATE.equalsIgnoreCase(cancellationReasonType) ||
				CANCELLATION_TYPE_CANDIDATE_AND_EXPERT.equalsIgnoreCase(cancellationReasonType)) {
			// mail to candidate only if it is not rescheduled
			if (!Boolean.TRUE.equals(cancelledInterview.getIsRescheduled())) {
				this.communicateInterviewCancellationToCandidateService.sendMailWhenCancelledByCandidate(
						cancelledInterview, isCandidateSchedulingAllowed, cancellationReasonDAO);
			}
			return;
		}

		if (!CANCELLATION_REASON_TYPE_CANDIDATE.equalsIgnoreCase(cancellationReasonType) &&
				(!CANCELLATION_TYPE_CANDIDATE_AND_EXPERT.equalsIgnoreCase(cancellationReasonType))) {
			// mail to candidate only if it is not rescheduled
			if (!Boolean.TRUE.equals(cancelledInterview.getIsRescheduled())) {
				this.communicateInterviewCancellationToCandidateService.sendMailWhenCancelledByOtherThanCandidate(
						cancelledInterview, isCandidateSchedulingAllowed);
			}
			return;
		}

	}

	public void communicateInterviewCancellationToOps(final String interviewId, final String reason) {
		try {
			final InterviewDAO cancelledInterview = this.interviewRepository.findById(interviewId).get();

			this.sendEmailToOpsOnCandidateCancellation(cancelledInterview, reason);

		} catch (final Exception exception) {
			log.error("ERROR !!! There was an error while communicating interview cancellation to stakeholders");
			log.error(exception);
			// sendCancellationCommunicationFailureEmail(cancelledInterview,exception);
		}
	}

	private void sendEmailToExpertOnCancellation(final InterviewDAO cancelledInterview, final String expertId)
			throws IOException {
		// 1.get sender
		final String fromEmail = this.staticAppConfigValues.getInterviewLifecycleInformationEmail();

		// 2.construct recepient list
		final UserDetailsDAO expertDetail = this.userDetailsRepository.findById(expertId).get();
		final List<String> toEmail = new ArrayList();
		toEmail.add(expertDetail.getEmail());

		final List<String> ccEmail = new ArrayList<>();
		ccEmail.add(EXPERT_EMAIL_ID);

		final Map<String, String> expertEmailData = this.formEmailDataForExpert(cancelledInterview, expertId);

		final String subject = expertEmailData.get("candidate_first_name") + " Interview Cancelled <> "
				+ expertEmailData.get("interview_date_time");
		this.emailService.sendEmail(fromEmail, subject, "expert_email_for_interview_cancellation", toEmail, ccEmail,
				expertEmailData, null);
	}

	public void sendEmailToTaOnCancellation(final InterviewDAO cancelledInterview)
			throws IOException {

		final String fromEmail = this.staticAppConfigValues.getInterviewLifecycleInformationEmail();

		final UserDetailsDAO taDetail = this.userDetailsRepository.findById(cancelledInterview.getTaggingAgent()).get();

		final List<String> toEmail = new ArrayList();
		toEmail.add(taDetail.getEmail());

		final List<String> ccEmail = new ArrayList<>();
		ccEmail.add(INTERVIEW_BARRAISER_EMAIL_ID);

		final Map<String, String> taEmailData = this.formEmailDataForTa(cancelledInterview);

		final String subject = taEmailData.get("candidate_name") + " Interview Cancelled <> "
				+ taEmailData.get("ta_start_datetime");
		this.emailService.sendEmail(fromEmail, subject, TA_EMAIL_FOR_INTERVIEW_CANCELLATION, toEmail, ccEmail,
				taEmailData, null);
	}

	public void communicateInterviewUpdationToTa(final InterviewDAO interviewThatTaCanTake, final String taId)
			throws IOException {
		final String fromEmail = this.staticAppConfigValues.getInterviewLifecycleInformationEmail();

		final UserDetailsDAO taDetail = this.userDetailsRepository.findById(taId).get();
		final List<String> toEmail = new ArrayList();
		toEmail.add(taDetail.getEmail());
		final List<String> ccEmail = new ArrayList<>();
		ccEmail.add(INTERVIEW_BARRAISER_EMAIL_ID);

		final Map<String, String> taEmailData = this.formEmailDataForTa(interviewThatTaCanTake);

		final String subject = "BarRaiser Interview Rescheduled <>" + taEmailData.get("candidate_name") + " <> "
				+ taEmailData.get("ta_start_datetime");
		this.emailService.sendEmail(fromEmail, subject, TA_EMAIL_FOR_INTERVIEW_UPDATION, toEmail, ccEmail, taEmailData,
				null);
	}

	private void sendEmailToOpsOnCandidateCancellation(final InterviewDAO cancelledInterview,
			final String cancellationReason)
			throws IOException {

		final String jira = this.jiraUUIDRepository.findByUuid(cancelledInterview.getId()).get().getJira();

		// 1.get sender
		final String fromEmail = this.staticAppConfigValues.getInterviewLifecycleInformationEmail();

		// 2.construct recipient list TODO:send to assignee and cc to
		// interview@barraiser.com
		final List<String> toEmail = new ArrayList<>();
		toEmail.add(this.staticAppConfigValues.getInterviewLifecycleInformationEmail());

		// 3.construct cc list
		final List<String> ccEmail = new ArrayList<String>();

		// 4.construct email data
		final Map<String, String> candidateEmailData = this.formEmailDataForCandidate(cancelledInterview,
				cancellationReason);

		// 5.send
		final String subject = "Interview Round Cancelled <> " + " " + jira + " "
				+ candidateEmailData.get("interview_date_time");
		this.emailService.sendEmail(fromEmail, subject, "ops_email_for_interview_cancellation_by_candidate", toEmail,
				ccEmail, candidateEmailData, null);
	}

	private Map<String, String> formEmailDataForCandidate(final InterviewDAO interviewDAO,
			final String cancellationReason) {
		final Map<String, String> emailData = new HashMap<>();
		final CandidateDAO candidate = this.candidateInformationManager.getCandidate(interviewDAO.getIntervieweeId());

		final String jira = this.jiraUUIDRepository.findByUuid(interviewDAO.getId()).get().getJira();
		final String jiraIssueLink = JIRA_ISSUE_LINK_PREFIX + jira;
		final String candidateFirstName = candidate.getFirstName();
		final String candidateTimezone = candidate.getTimezone();
		final String interviewDateTime = this.utilities.getFormattedDateString(interviewDAO.getStartDate(),
				candidateTimezone, DATEFORMAT_1);

		emailData.put("candidate_first_name", candidateFirstName);
		emailData.put("interview_date_time", interviewDateTime);
		emailData.put("cancellation_reason", cancellationReason);
		emailData.put("jira_link", jiraIssueLink);

		return emailData;
	}

	// can be combined with above method if needed
	private final Map<String, String> formEmailDataForExpert(final InterviewDAO interviewDAO, final String expertId) {
		final Map<String, String> emailData = new HashMap<>();
		final CandidateDAO candidateDetail = this.candidateInformationManager
				.getCandidate(interviewDAO.getIntervieweeId());
		final UserDetailsDAO expertDetail = this.userDetailsRepository.findById(expertId)
				.get();

		final String expertFirstName = expertDetail.getFirstName();
		final String expertTimeZone = this.timezoneManager.getTimezoneOfExpert(expertId);

		final String candidateFirstName = candidateDetail.getFirstName();
		final String interviewDateTime = this.utilities.getFormattedDateString(interviewDAO.getStartDate(),
				expertTimeZone, DATEFORMAT_1);

		emailData.put("expert_first_name", expertFirstName);
		emailData.put("interview_date_time", interviewDateTime);
		emailData.put("candidate_first_name", candidateFirstName);

		return emailData;
	}

	private Map<String, String> formEmailDataForTa(final InterviewDAO interviewDAO) {
		final Map<String, String> emailData = new HashMap<>();
		final UserDetailsDAO taDetail = this.userDetailsRepository.findById(interviewDAO.getTaggingAgent())
				.get();

		final CandidateDAO candidateDetail = this.candidateInformationManager
				.getCandidate(interviewDAO.getIntervieweeId());

		final String taFirstName = taDetail.getFirstName();
		final String taTimeZone = taDetail.getTimezone();
		final String interviewDateTime = this.utilities.getFormattedDateString(interviewDAO.getStartDate(), taTimeZone,
				DATEFORMAT_1);
		final String interviewEndDateTime = this.utilities.getFormattedDateString(interviewDAO.getEndDate(), taTimeZone,
				DATEFORMAT_1);
		final String jira = this.jiraUUIDRepository.findByUuid(interviewDAO.getId()).get().getJira();
		final Boolean shouldTaJoinMeeting = !this.willBotEnter(interviewDAO);

		emailData.put("ta_name", taFirstName);
		emailData.put("candidate_name", candidateDetail.getFirstName());
		emailData.put("ta_start_datetime", interviewDateTime);
		emailData.put("ta_end_datetime", interviewEndDateTime);
		emailData.put("jira", jira);
		emailData.put("interview_id", interviewDAO.getId());
		if (shouldTaJoinMeeting) {
			emailData.put("meet_link", interviewDAO.getMeetingLink());
		}

		return emailData;
	}

	private Boolean willBotEnter(final InterviewDAO interviewDAO) {
		return !this.interviewToStepFunctionExecutionRepository.findAllByInterviewIdAndRescheduleCountAndFlowTypeIn(
				interviewDAO.getId(), interviewDAO.getRescheduleCount(), List.of(FlowType.INTERVIEWING_LIFECYCLE))
				.isEmpty();
	}
}
