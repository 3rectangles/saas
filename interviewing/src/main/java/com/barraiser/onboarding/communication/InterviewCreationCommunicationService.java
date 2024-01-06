/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.communication;

import com.barraiser.common.utilities.FormattingUtil;
import com.barraiser.onboarding.candidate.CandidateInformationManager;
import com.barraiser.onboarding.common.StaticAppConfigValues;
import com.barraiser.onboarding.communication.channels.email.EmailService;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.jobrole.JobRoleManager;
import com.barraiser.onboarding.partner.PartnerConfigurationManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
@Component
public class InterviewCreationCommunicationService {
	public static final String INTERVIEW_CREATION_MAIL_SUBJECT = "Congratulations from %s";

	private final StaticAppConfigValues staticAppConfig;
	private final EmailService emailService;
	private final CompanyRepository companyRepository;
	private final DomainRepository domainRepository;
	private final FormattingUtil formattingUtil;
	private final PartnerCompanyRepository partnerCompanyRepository;
	private final JobRoleManager jobRoleManager;
	private final PartnerConfigurationManager partnerConfigurationManager;
	private final CandidateInformationManager candidateInformationManager;

	public void sendEmailRegardingInterviewCreation(final EvaluationDAO evaluationDAO,
			final List<InterviewDAO> interviews) throws IOException {
		final Map<String, Object> emailData = this.prepareDataForInterviewCreationMail(evaluationDAO, interviews);
		final List<String> pocEmails = this.formattingUtil.convertStringToList(evaluationDAO.getPocEmail(), ",");
		final String fromEmail = emailData.get("company_name")
				+ "<" + this.staticAppConfig.getInterviewLifecycleInformationEmail() + ">";
		this.emailService.sendEmailForObjectData(fromEmail,
				String.format(INTERVIEW_CREATION_MAIL_SUBJECT, emailData.get("company_name")),
				"candidate_interview_creation_mail", List.of(emailData.get("email").toString()), pocEmails,
				emailData, emailData.get("company_name").toString());
	}

	private Map<String, Object> prepareDataForInterviewCreationMail(final EvaluationDAO evaluationDAO,
			final List<InterviewDAO> interviews) {
		final CandidateDAO candidate = this.candidateInformationManager.getCandidate(evaluationDAO.getCandidateId());
		final UserDetailsDAO user = this.candidateInformationManager
				.getUserForCandidate(evaluationDAO.getCandidateId());
		final Optional<JobRoleDAO> jobRoleDAO = this.jobRoleManager.getJobRoleFromEvaluation(evaluationDAO);
		final CompanyDAO companyDAO = this.companyRepository.findById(jobRoleDAO.get().getCompanyId()).get();
		final String partnerId = this.partnerCompanyRepository.findByCompanyId(companyDAO.getId()).get().getId();
		final Boolean isCandidateAllowedToSchedule = this.partnerConfigurationManager
				.isCandidateSchedulingEnabled(partnerId);
		final String companyName = companyDAO.getName();
		final String domain = this.domainRepository.findById(jobRoleDAO.get().getDomainId()).get().getName();

		final Map<String, Object> emailData = new HashMap<>();
		emailData.put("candidate_name", candidate.getFirstName());
		emailData.put("company_name", companyName);
		emailData.put("company_logo", companyDAO.getLogo());
		emailData.put("job_role", jobRoleDAO.get().getCandidateDisplayName());
		emailData.put("domain", domain);
		emailData.put("interviews", interviews);
		emailData.put("email", user != null ? user.getEmail() : null);
		emailData.put("candidate_phone", user != null ? user.getPhone() : null);
		emailData.put("is_candidate_scheduling_on", isCandidateAllowedToSchedule);
		return emailData;
	}
}
