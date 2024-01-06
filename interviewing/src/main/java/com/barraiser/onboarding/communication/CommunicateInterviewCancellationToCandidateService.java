/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.communication;

import com.barraiser.common.utilities.DateUtils;
import com.barraiser.onboarding.candidate.CandidateInformationManager;
import com.barraiser.onboarding.common.StaticAppConfigValues;
import com.barraiser.onboarding.communication.channels.email.EmailService;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.user.CompanyManager;

import com.barraiser.onboarding.user.TimezoneManager;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.barraiser.onboarding.common.Constants.CANCELLATION_REASON_ID_FOR_CANDIDATE_AND_EXPERT_DID_NOT_JOIN;

@Log4j2
@Component
@AllArgsConstructor
public class CommunicateInterviewCancellationToCandidateService {
	public static final String INTERVIEW_CANCELLATION_EMAIL_SUBJECT = "Interview Round Cancelled <> %s";
	public static final String INTERVIEW_RESCHEDULING_EMAIL_SUBJECT = "%s Interview Process - Request to reschedule";
	public static final String EMAIL_TEMPLATE_WHEN_CANCELLED_BY_CANDIDATE = "candidate_email_for_interview_cancellation_by_candidate";
	public static final String EMAIL_TEMPLATE_WHEN_CANCELLED_BY_OTHER_THAN_CANDIDATE = "candidate_email_for_interview_cancellation_by_others";
	public static final String CANDIDATE_EMAIL = "candidate_email";
	public static final String CANDIDATE_FIRST_NAME = "candidate_first_name";
	public static final String CANDIDATE_PHONE_NUMBER = "candidate_phone";
	public static final String FORMATTED_START_DATE_OF_INTERVIEW = "interview_date_time";
	public static final String POC_EMAILS = "poc_emails";
	public static final String CANCELLED_INTERVIEW_ID = "interview_id";
	public static final String IS_CANDIDATE_SCHEDULING_ALLOWED = "is_candidate_scheduling_allowed";
	public static final String CANCELLATION_REASON_OF_INTERVIEW = "cancellation_reason";
	public static final String IS_ROUND_INTERNAL = "is_round_internal";
	private static final String ROUND_TYPE_INTERNAL = "INTERNAL";
	private static final String CANCELLATION_REASON_FOR_CANDIDATE = "Candidate did not join the interview";

	private final CandidateInformationManager candidateInformationManager;
	private final DateUtils dateUtils;
	private final EvaluationRepository evaluationRepository;
	private final StaticAppConfigValues staticAppConfigValues;
	private final EmailService emailService;
	private final CompanyManager companyManager;
	private final TimezoneManager timezoneManager;

	private Map<String, String> prepareDataForEmail(
			final InterviewDAO interviewDAO, final boolean isCandidateSchedulingAllowed) {
		final Map<String, String> emailData = new HashMap<>();

		final CandidateDAO candidate = this.candidateInformationManager.getCandidate(interviewDAO.getIntervieweeId());
		final UserDetailsDAO user = this.candidateInformationManager
				.getUserForCandidate(interviewDAO.getIntervieweeId());
		final EvaluationDAO evaluationDAO = this.evaluationRepository.findById(interviewDAO.getEvaluationId()).get();
		final String pocEmails = evaluationDAO.getPocEmail();
		final String startTimeOfInterview = this.dateUtils.getFormattedDateString(
				interviewDAO.getStartDate(),
				this.timezoneManager.getTimezoneOfCandidate(interviewDAO.getId()),
				DateUtils.TIME_IN_12_HOUR_FORMAT);

		emailData.put(CANDIDATE_FIRST_NAME, candidate.getFirstName());
		emailData.put(CANDIDATE_EMAIL, user.getEmail());
		emailData.put(CANDIDATE_PHONE_NUMBER, user.getPhone());
		emailData.put(FORMATTED_START_DATE_OF_INTERVIEW, startTimeOfInterview);
		emailData.put(POC_EMAILS, pocEmails);
		emailData.put(CANCELLED_INTERVIEW_ID, interviewDAO.getId());
		emailData.put(
				IS_CANDIDATE_SCHEDULING_ALLOWED,
				Boolean.TRUE.equals(isCandidateSchedulingAllowed) ? "true" : null);
		emailData.put(
				IS_ROUND_INTERNAL, interviewDAO.getInterviewRound().equals(ROUND_TYPE_INTERNAL) ? "true" : null);

		return emailData;
	}

	public void sendMailWhenCancelledByOtherThanCandidate(
			final InterviewDAO interviewDAO, final Boolean isCandidateSchedulingEnabled)
			throws IOException {

		final Map<String, String> emailData = this.prepareDataForEmail(interviewDAO, isCandidateSchedulingEnabled);
		final String fromEmail = this.staticAppConfigValues.getInterviewLifecycleInformationEmail();
		final List<String> ccEmails = this.getListOfEmailsToCC(emailData.get(POC_EMAILS));

		final String subject = this.getSubjectOfEmail(
				interviewDAO,
				isCandidateSchedulingEnabled,
				emailData.get(FORMATTED_START_DATE_OF_INTERVIEW));
		this.emailService.sendEmail(
				fromEmail,
				subject,
				EMAIL_TEMPLATE_WHEN_CANCELLED_BY_OTHER_THAN_CANDIDATE,
				List.of(emailData.get(CANDIDATE_EMAIL)),
				ccEmails,
				emailData,
				null);
	}

	private List<String> getListOfEmailsToCC(final String pocEmails) {
		final List<String> pocEmailList = new ArrayList<>();
		if (pocEmails != null) {
			pocEmailList.addAll(
					Arrays.stream(pocEmails.split(","))
							.map(String::strip)
							.collect(Collectors.toList()));
		}
		pocEmailList.add(this.staticAppConfigValues.getInterviewLifecycleInformationEmail());
		return pocEmailList;
	}

	private String getSubjectOfEmail(
			final InterviewDAO interviewDAO,
			final Boolean isCandidateSchedulingAllowed,
			final String startTimeOfInterview) {
		if (isCandidateSchedulingAllowed) {
			final CompanyDAO companyDAO = this.companyManager.getTargetCompanyOfInterview(interviewDAO);
			return String.format(INTERVIEW_RESCHEDULING_EMAIL_SUBJECT, companyDAO.getName());
		} else {
			return String.format(INTERVIEW_CANCELLATION_EMAIL_SUBJECT, startTimeOfInterview);
		}
	}

	public void sendMailWhenCancelledByCandidate(
			final InterviewDAO interviewDAO,
			final Boolean isCandidateSchedulingEnabled,
			final CancellationReasonDAO cancellationReasonDAO)
			throws IOException {
		final Map<String, String> emailData = this.prepareDataForEmail(interviewDAO, isCandidateSchedulingEnabled);
		emailData.put(
				CANCELLATION_REASON_OF_INTERVIEW,
				Objects.equals(cancellationReasonDAO.getId(),
						CANCELLATION_REASON_ID_FOR_CANDIDATE_AND_EXPERT_DID_NOT_JOIN)
								? CANCELLATION_REASON_FOR_CANDIDATE
								: cancellationReasonDAO.getCancellationReason());

		final String fromEmail = this.staticAppConfigValues.getInterviewLifecycleInformationEmail();
		final List<String> ccEmails = this.getListOfEmailsToCC(emailData.get(POC_EMAILS));

		final String subject = String.format(
				INTERVIEW_CANCELLATION_EMAIL_SUBJECT,
				emailData.get(FORMATTED_START_DATE_OF_INTERVIEW));
		this.emailService.sendEmail(
				fromEmail,
				subject,
				EMAIL_TEMPLATE_WHEN_CANCELLED_BY_CANDIDATE,
				List.of(emailData.get(CANDIDATE_EMAIL)),
				ccEmails,
				emailData,
				null);
	}
}
