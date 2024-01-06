/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.communication;

import com.barraiser.common.utilities.DateUtils;
import com.barraiser.common.utilities.FormattingUtil;
import com.barraiser.onboarding.auth.magicLink.MagicLinkManager;
import com.barraiser.onboarding.candidate.CandidateInformationManager;
import com.barraiser.onboarding.communication.channels.email.EmailService;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.InterviewUtil;
import com.barraiser.onboarding.interview.jobrole.JobRoleManager;
import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.DTO.ExpertAllocatorData;
import com.barraiser.onboarding.scheduling.lifecycle.InterviewToStepFunctionExecutionRepository;
import com.barraiser.onboarding.scheduling.scheduling.ExpertSchedulingCommunicationData;
import com.barraiser.onboarding.scheduling.scheduling.SchedulingCommunicationData;
import com.barraiser.onboarding.sfn.FlowType;
import com.barraiser.onboarding.user.CompanyManager;
import com.barraiser.onboarding.user.SkillManager;
import com.barraiser.onboarding.user.TimezoneManager;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.barraiser.onboarding.common.Constants.ROUND_TYPE_INTERNAL;

@Log4j2
@Component
@AllArgsConstructor
public class InterviewSchedulingCommunicationService {
	public static final String SCHEDULED_INTERVIEW_SUBJECT_FOR_CANDIDATE = "%s Interview Round %sScheduled - %s";
	public static final String SCHEDULED_INTERVIEW_SUBJECT_FOR_EXPERT = "Interview has been scheduled <> %s <> %s";
	public static final String SCHEDULED_INTERVIEW_SUBJECT_FOR_TA = "Interview has been scheduled <> %s <> %s";
	public static final String INTERVIEW_SCHEDULED_TA_EMAIL_TEMPLATE = "interview_scheduled_ta_email_template";

	public static final String INTERVIEW_DATE_TIME_FORMAT = "dd MMM uuuu, hh:mm a z";

	public static final String COMPANY_NAME_KEY = "company_name";
	public static final String INTERVIEW_BARRAISER_EMAIL_ID = "interview@barraiser.com";

	private static final String CARS24_COMPANY_ID = "102";

	private final UserDetailsRepository userDetailsRepository;
	private final CandidateInformationManager candidateInformationManager;
	private final EmailService emailService;
	private final FormattingUtil formattingUtil;
	private final EvaluationRepository evaluationRepository;
	private final CompanyRepository companyRepository;
	private final JobRoleManager jobRoleManager;
	private final DateUtils utilities;
	private final JobRoleToInterviewStructureRepository jobRoleToInterviewStructureRepository;
	private final InterviewRoundTypeConfigurationRepository interviewRoundTypeConfigurationRepository;
	private final InterviewStructureRepository interviewStructureRepository;
	private final SkillManager skillManager;
	private final InterviewUtil interviewUtil;
	private final JiraUUIDRepository jiraUUIDRepository;
	private final MagicLinkManager magicLinkManager;
	private final CompanyManager companyManager;
	private final TimezoneManager timezoneManager;
	private final InterviewToStepFunctionExecutionRepository interviewToStepFunctionExecutionRepository;

	public SchedulingCommunicationData prepareInterviewScheduledCommunicationData(final InterviewDAO interviewDAO)
			throws IOException {
		final UserDetailsDAO expert = this.userDetailsRepository.findById(interviewDAO.getInterviewerId()).get();
		final CandidateDAO candidate = this.candidateInformationManager.getCandidate(interviewDAO.getIntervieweeId());
		final UserDetailsDAO candidateAccessUser = this.candidateInformationManager
				.getUserForCandidate(interviewDAO.getIntervieweeId());
		final EvaluationDAO evaluation = this.evaluationRepository.findById(interviewDAO.getEvaluationId()).get();
		final JobRoleDAO jobRoleDAO = this.jobRoleManager.getJobRoleFromEvaluation(evaluation).get();
		final CompanyDAO companyDAO = this.companyRepository.findById(jobRoleDAO.getCompanyId()).get();
		final JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO = this.jobRoleToInterviewStructureRepository
				.findByJobRoleIdAndJobRoleVersionAndInterviewStructureId(jobRoleDAO.getEntityId().getId(),
						jobRoleDAO.getEntityId().getVersion(), interviewDAO.getInterviewStructureId())
				.get();
		final InterviewStructureDAO interviewStructureDAO = this.interviewStructureRepository.findById(
				interviewDAO.getInterviewStructureId()).get();
		final InterviewRoundTypeConfigurationDAO interviewRoundTypeConfigurationDAO = this
				.getInterviewRoundConfig(interviewDAO.getInterviewRound(), companyDAO.getId());

		final Map<String, String> expertData = new HashMap<>();
		expertData.put("expertEmailId", expert.getEmail());
		expertData.put("expertTimeZone", this.timezoneManager.getTimezoneOfExpert(expert.getId()));

		final Map<String, String> candidateData = new HashMap<>();
		candidateData.put("candidateEmailId", candidateAccessUser != null ? candidateAccessUser.getEmail() : null);
		candidateData.put("candidateTimeZone", this.timezoneManager.getTimezoneOfCandidate(interviewDAO.getId()));

		final SchedulingCommunicationData schedulingEmailData = new SchedulingCommunicationData();
		schedulingEmailData.setCommunicationData(this.prepareEmailData(interviewDAO, candidate, candidateAccessUser,
				expert, jobRoleDAO,
				companyDAO, interviewStructureDAO, interviewRoundTypeConfigurationDAO, jobRoleToInterviewStructureDAO));
		schedulingEmailData.setCandidateData(candidateData);
		schedulingEmailData.setExpertData(expertData);
		schedulingEmailData.setPocEmails(this.formattingUtil.convertStringToList(evaluation.getPocEmail(), ","));
		schedulingEmailData.setInterviewRoundTypeConfigurationDAO(interviewRoundTypeConfigurationDAO);
		schedulingEmailData.setInterviewStructureDAO(interviewStructureDAO);
		schedulingEmailData.setInterviewDAO(interviewDAO);
		schedulingEmailData.setIsCandidateAnonymous(this.isCandidateAnonymous(candidateAccessUser));
		return schedulingEmailData;
	}

	private Map<String, Object> prepareEmailData(final InterviewDAO interviewDAO, final CandidateDAO candidate,
			final UserDetailsDAO candidateAccessUser,
			final UserDetailsDAO expert,
			final JobRoleDAO jobRoleDAO, final CompanyDAO companyDAO, final InterviewStructureDAO interviewStructureDAO,
			final InterviewRoundTypeConfigurationDAO interviewRoundTypeConfigurationDAO,
			final JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO) {
		final Map<String, Object> emailData = new HashMap<>();
		final Boolean isCandidateAnonymous = this.isCandidateAnonymous(candidateAccessUser);
		emailData.put("candidate_firstname", candidate.getFirstName());
		emailData.put("candidate_name", String.format("%s %s", candidate.getFirstName(),
				candidate.getLastName() != null ? candidate.getLastName() : ""));
		emailData.put("expert_firstname", expert.getFirstName());
		emailData.put("expert_name", String.format("%s %s", expert.getFirstName(),
				expert.getLastName() != null ? expert.getLastName() : ""));
		emailData.put("job_role_name", jobRoleDAO.getCandidateDisplayName());
		emailData.put("company_name", companyDAO.getName());
		emailData.put("company_logo", companyDAO.getLogo());
		emailData.put("company_id", companyDAO.getId());
		emailData.put("interview_id", interviewDAO.getId());
		emailData.put("meeting_link", interviewDAO.getMeetingLink());
		emailData.put("common_zoom_link", interviewRoundTypeConfigurationDAO.getCommonZoomLink());
		emailData.put("resume_link", candidate.getRedactedResumeUrl());
		emailData.put("problem_statement_link", jobRoleToInterviewStructureDAO.getProblemStatementLink());
		emailData.put("is_round_internal", ROUND_TYPE_INTERNAL.equalsIgnoreCase(interviewDAO.getInterviewRound()));
		emailData.put("duration",
				this.utilities.getHumanReadableDuration(interviewStructureDAO.getDuration().longValue() * 60 * 1000));
		emailData.put("categories_list",
				this.skillManager.getCategoriesCoveredInInterviewStructure(interviewStructureDAO.getId()));
		emailData.put("specific_skills_list", String.join(", ", this.skillManager
				.getSpecificSkillsCoveredInInterviewStructure(interviewStructureDAO.getId()).stream()
				.map(SkillDAO::getName).collect(Collectors.toList())));
		final String baseCandidateLandingPageUrl = "https://app.barraiser.com/interview-landing/c/"
				+ interviewDAO.getId();
		final Long loginLinkExpiration = interviewDAO.getEndDate() - Instant.now().getEpochSecond();
		try {
			if (isCandidateAnonymous) {
				// TBD : Have to figure out a login mechanism for anonymous candidate. Not using
				// magic link for now.
				emailData.put("candidate_landing_page_url", baseCandidateLandingPageUrl);
			} else {
				emailData.put("candidate_landing_page_url", this.magicLinkManager
						.generateMagicUrl(baseCandidateLandingPageUrl, candidateAccessUser.getEmail(),
								loginLinkExpiration));
			}

		} catch (final URISyntaxException e) {
			throw new RuntimeException(e);
		}
		return emailData;
	}

	private Boolean isCandidateAnonymous(final UserDetailsDAO user) {
		return user == null;
	}

	private InterviewRoundTypeConfigurationDAO getInterviewRoundConfig(final String interviewRound,
			final String companyId) {
		Optional<InterviewRoundTypeConfigurationDAO> roundTypeConfigurationDAOOptional = this.interviewRoundTypeConfigurationRepository
				.findByRoundTypeAndCompanyId(interviewRound, companyId);

		if (!roundTypeConfigurationDAOOptional.isPresent()) {
			roundTypeConfigurationDAOOptional = this.interviewRoundTypeConfigurationRepository
					.findByRoundType(interviewRound);
		}
		return roundTypeConfigurationDAOOptional.get();
	}

	public void sendEmailToExpert(final SchedulingCommunicationData schedulingEmailData) throws IOException {

		final String expertEmailId = schedulingEmailData.getExpertData().get("expertEmailId");
		final String expertTimeZone = schedulingEmailData.getExpertData().get("expertTimeZone");

		// TBD:email data formation can be refactored to another method
		final Long interviewStartTime = schedulingEmailData.getInterviewDAO().getStartDate();
		final Long expertStartTimeEpoch = interviewStartTime
				+ schedulingEmailData.getInterviewStructureDAO().getExpertJoiningTime() * 60;
		final Long expertEndTimeEpoch = interviewStartTime
				+ schedulingEmailData.getInterviewStructureDAO().getDuration() * 60;

		// check if we have expert timezone
		final String expertStartDatetime = this.utilities.getFormattedDateString(expertStartTimeEpoch, expertTimeZone,
				INTERVIEW_DATE_TIME_FORMAT);
		final String expertEndDatetime = this.utilities.getFormattedDateString(expertEndTimeEpoch, expertTimeZone,
				INTERVIEW_DATE_TIME_FORMAT);

		final HashMap<String, Object> emailDataMap = new HashMap<String, Object>();
		emailDataMap.putAll(schedulingEmailData.getCommunicationData());
		emailDataMap.put("expert_start_datetime", expertStartDatetime);
		emailDataMap.put("expert_end_datetime", expertEndDatetime);

		final String emailTemplate = schedulingEmailData.getInterviewRoundTypeConfigurationDAO()
				.getInterviewScheduledExpertEmailTemplate();

		// 1.get sender
		final String fromEmail = INTERVIEW_BARRAISER_EMAIL_ID;

		// 2.construct recepient list
		final List<String> toEmail = new ArrayList<String>();
		toEmail.add(expertEmailId);

		// 3.construct cc list
		final List<String> ccEmail = new ArrayList<>();
		ccEmail.add(INTERVIEW_BARRAISER_EMAIL_ID);

		// 4.send
		final String candidateName = emailDataMap.get("candidate_name").toString();
		final String subject = String.format(SCHEDULED_INTERVIEW_SUBJECT_FOR_EXPERT, candidateName,
				expertStartDatetime);
		this.emailService.sendEmailForObjectData(fromEmail, subject, emailTemplate, toEmail, ccEmail, emailDataMap,
				null);

	}

	public void sendEmailToTa(final SchedulingCommunicationData schedulingEmailData) throws IOException {

		final String taEmailId = schedulingEmailData.getTaData().get("taEmailId");
		final String taTimeZone = schedulingEmailData.getTaData().get("taTimeZone");
		final InterviewDAO interviewDAO = schedulingEmailData.getInterviewDAO();
		final UserDetailsDAO taggingAgent = this.userDetailsRepository
				.findById(schedulingEmailData.getInterviewDAO().getTaggingAgent()).get();
		final Long interviewStartTime = schedulingEmailData.getInterviewDAO().getStartDate();
		final Long taStartTime = interviewStartTime;
		final Long taEndEndTimeEpoch = interviewStartTime
				+ schedulingEmailData.getInterviewStructureDAO().getDuration() * 60;

		final String taStartDatetime = this.utilities.getFormattedDateString(taStartTime, taTimeZone,
				INTERVIEW_DATE_TIME_FORMAT);
		final String taEndDatetime = this.utilities.getFormattedDateString(taEndEndTimeEpoch, taTimeZone,
				INTERVIEW_DATE_TIME_FORMAT);
		final String jira = this.jiraUUIDRepository.findByUuid(interviewDAO.getId()).get().getJira();

		final HashMap<String, Object> emailDataMap = this.getEmailMailDataMapForTa(schedulingEmailData, interviewDAO,
				taggingAgent, taStartDatetime, taEndDatetime, jira);

		final String emailTemplate = INTERVIEW_SCHEDULED_TA_EMAIL_TEMPLATE;
		final String fromEmail = INTERVIEW_BARRAISER_EMAIL_ID;

		final List<String> toEmail = new ArrayList<String>();
		toEmail.add(taEmailId);

		final List<String> ccEmail = new ArrayList<>();
		ccEmail.add(INTERVIEW_BARRAISER_EMAIL_ID);
		final String candidateName = emailDataMap.get("candidate_name").toString();
		final String subject = String.format(SCHEDULED_INTERVIEW_SUBJECT_FOR_TA, candidateName, taStartDatetime);
		this.emailService.sendEmailForObjectData(fromEmail, subject, emailTemplate, toEmail, ccEmail, emailDataMap,
				null);

	}

	private HashMap<String, Object> getEmailMailDataMapForTa(final SchedulingCommunicationData schedulingEmailData,
			final InterviewDAO interviewDAO, final UserDetailsDAO ta, final String taStartDatetime,
			final String taEndDatetime, final String jira) {
		final Boolean shouldTaJoinMeeting = !this.willBotEnter(interviewDAO);
		final HashMap<String, Object> emailDataMap = new HashMap<>();
		emailDataMap.putAll(schedulingEmailData.getCommunicationData());
		emailDataMap.put("ta_start_datetime", taStartDatetime);
		emailDataMap.put("ta_end_datetime", taEndDatetime);
		emailDataMap.put("ta_name", ta.getFirstName());
		emailDataMap.put("interview_id", interviewDAO.getId());
		emailDataMap.put("jira", jira);
		emailDataMap.put("fallback_meeting_link", interviewDAO.getMeetingLink());
		if (shouldTaJoinMeeting) {
			emailDataMap.put("meet_link", interviewDAO.getMeetingLink());
		}
		return emailDataMap;
	}

	private Boolean willBotEnter(final InterviewDAO interviewDAO) {
		return !this.interviewToStepFunctionExecutionRepository.findAllByInterviewIdAndRescheduleCountAndFlowTypeIn(
				interviewDAO.getId(), interviewDAO.getRescheduleCount(), List.of(FlowType.INTERVIEWING_LIFECYCLE))
				.isEmpty();
	}

	public void sendEmailToCandidate(final SchedulingCommunicationData schedulingEmailData) throws IOException {
		final List<SkillDAO> skillsToBeTested = new ArrayList<SkillDAO>();
		final String candidateEmailId = schedulingEmailData.getCandidateData().get("candidateEmailId");
		final String candidateTimeZone = schedulingEmailData.getCandidateData().get("candidateTimeZone");
		// TBD:email data formation can be refactored to another method
		final InterviewDAO interviewDAO = schedulingEmailData.getInterviewDAO();
		final Long interviewStartTime = interviewDAO.getStartDate();
		//
		final Long candidateStartTimeEpoch = interviewStartTime;
		final Long candidateEndTimeEpoch = interviewStartTime
				+ schedulingEmailData.getInterviewStructureDAO().getDuration() * 60;

		final String candidateStartDatetime = this.utilities.getFormattedDateString(candidateStartTimeEpoch,
				candidateTimeZone, INTERVIEW_DATE_TIME_FORMAT);
		final String candidateEndDatetime = this.utilities.getFormattedDateString(candidateEndTimeEpoch,
				candidateTimeZone, INTERVIEW_DATE_TIME_FORMAT);
		final HashMap<String, Object> emailDataMap = new HashMap<String, Object>();
		emailDataMap.putAll(schedulingEmailData.getCommunicationData());
		emailDataMap.put("candidate_start_datetime", candidateStartDatetime);
		emailDataMap.put("candidate_end_datetime", candidateEndDatetime);

		if (interviewDAO.getRescheduledFrom() != null && !interviewDAO.getRescheduledFrom().isEmpty()) {
			emailDataMap.put("rescheduled_from", interviewDAO.getRescheduledFrom());
		}

		final String emailTemplate = schedulingEmailData.getInterviewRoundTypeConfigurationDAO()
				.getInterviewScheduledCandidateEmailTemplate();

		// 1.get sender
		final String fromEmail = INTERVIEW_BARRAISER_EMAIL_ID;

		// 2.construct recepient list
		final List<String> toEmail = new ArrayList<String>();
		toEmail.add(candidateEmailId);

		// 3.construct cc list

		final List<String> ccEmail = new ArrayList<>();
		ccEmail.add(INTERVIEW_BARRAISER_EMAIL_ID);
		ccEmail.addAll(schedulingEmailData.getPocEmails());

		final String subject = String.format(
				SCHEDULED_INTERVIEW_SUBJECT_FOR_CANDIDATE,
				emailDataMap.get(COMPANY_NAME_KEY),
				emailDataMap.get("company_id").equals(CARS24_COMPANY_ID)
						? this.interviewUtil.getRoundNumberOfInterview(schedulingEmailData.getInterviewDAO()) + " "
						: "",
				candidateStartDatetime);

		// 4.send
		this.emailService.sendEmailForObjectData(fromEmail,
				subject,
				emailTemplate,
				toEmail,
				ccEmail,
				emailDataMap, schedulingEmailData.getCommunicationData().get("company_name").toString());

	}

	public ExpertSchedulingCommunicationData prepareInterviewScheduledCommunicationDataForExpert(
			final ExpertAllocatorData expertAllocatorData)
			throws IOException {
		final InterviewDAO interviewDAO = expertAllocatorData.getInterview();
		final UserDetailsDAO expert = this.userDetailsRepository.findById(expertAllocatorData.getInterviewerId()).get();
		final CandidateDAO candidate = this.candidateInformationManager.getCandidate(interviewDAO.getIntervieweeId());
		final InterviewStructureDAO interviewStructureDAO = this.interviewStructureRepository.findById(
				interviewDAO.getInterviewStructureId()).get();
		final CompanyDAO companyDAO = this.companyManager.getTargetCompanyOfInterview(interviewDAO);
		final InterviewRoundTypeConfigurationDAO interviewRoundTypeConfigurationDAO = this
				.getInterviewRoundConfig(interviewDAO.getInterviewRound(), companyDAO.getId());

		final Map<String, String> expertData = new HashMap<>();
		expertData.put("expertEmailId", expert.getEmail());
		expertData.put("expertTimeZone", expert.getTimezone());

		final ExpertSchedulingCommunicationData schedulingEmailData = new ExpertSchedulingCommunicationData();
		schedulingEmailData.setCommunicationData(
				this.prepareEmailDataForExpert(interviewDAO, expert, candidate, interviewStructureDAO));
		schedulingEmailData.setExpertData(expertData);
		schedulingEmailData.setInterviewRoundTypeConfigurationDAO(interviewRoundTypeConfigurationDAO);
		schedulingEmailData.setInterviewStructureDAO(interviewStructureDAO);
		schedulingEmailData.setInterviewDAO(interviewDAO);
		schedulingEmailData.setExpertId(expert.getId());
		schedulingEmailData.setStartDate(expertAllocatorData.getStartDate());
		return schedulingEmailData;
	}

	private Map<String, Object> prepareEmailDataForExpert(final InterviewDAO interviewDAO, final UserDetailsDAO expert,
			final CandidateDAO candidate, final InterviewStructureDAO interviewStructureDAO) {
		final Map<String, Object> emailData = new HashMap<>();

		emailData.put("candidate_name", String.format("%s %s", candidate.getFirstName(),
				candidate.getLastName() != null ? candidate.getLastName() : ""));
		emailData.put("expert_name", expert.getFirstName());
		emailData.put("interview_id", interviewDAO.getId());
		emailData.put("is_round_internal", ROUND_TYPE_INTERNAL.equalsIgnoreCase(interviewDAO.getInterviewRound()));
		emailData.put("duration",
				this.utilities.getHumanReadableDuration(interviewStructureDAO.getDuration().longValue() * 60 * 1000));
		emailData.put("categories_list",
				this.skillManager.getCategoriesCoveredInInterviewStructure(interviewStructureDAO.getId()));
		emailData.put("specific_skills_list", String.join(", ", this.skillManager
				.getSpecificSkillsCoveredInInterviewStructure(interviewStructureDAO.getId()).stream()
				.map(SkillDAO::getName).collect(Collectors.toList())));
		return emailData;
	}

}
