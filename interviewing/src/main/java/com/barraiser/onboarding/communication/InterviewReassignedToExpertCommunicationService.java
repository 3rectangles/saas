/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.communication;

import com.barraiser.common.utilities.DateUtils;
import com.barraiser.onboarding.candidate.CandidateInformationManager;
import com.barraiser.onboarding.common.StaticAppConfigValues;
import com.barraiser.onboarding.communication.channels.email.EmailService;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.InterviewUtil;
import com.barraiser.onboarding.user.SkillManager;
import com.barraiser.onboarding.user.TimezoneManager;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class InterviewReassignedToExpertCommunicationService {
	private final StaticAppConfigValues staticAppConfigValues;
	private final UserDetailsRepository userDetailsRepository;
	private final CandidateInformationManager candidateInformationManager;
	private final EmailService emailService;
	private final DateUtils dateUtils;
	private final SkillManager skillManager;
	private final InterViewRepository interViewRepository;
	private final QuestionRepository questionRepository;
	private final InterviewUtil interviewUtil;
	private final TimezoneManager timezoneManager;

	public void communicateInterviewUpdationToExpert(final InterviewDAO interviewThatExpertCanTake,
			final String expertId)
			throws IOException {
		// 1.get sender
		final String fromEmail = this.staticAppConfigValues.getInterviewLifecycleInformationEmail();

		// 2.construct recipient list
		final UserDetailsDAO expertDetail = this.userDetailsRepository.findById(expertId).get();
		final List<String> toEmail = new ArrayList();
		toEmail.add(expertDetail.getEmail());
		final List<String> ccEmails = List.of(this.staticAppConfigValues.getInterviewLifecycleInformationEmail());

		final Map<String, Object> emailData = this.prepareDataForExpertUpdatedInterviewMail(interviewThatExpertCanTake,
				expertId);
		emailData.put("expert_first_name", expertDetail.getFirstName());

		final String subject = "BarRaiser Interview Rescheduled <>" + emailData.get("candidate_first_name") + " <> "
				+ emailData.get("interview_date_and_time");
		this.emailService.sendEmailForObjectData(fromEmail, subject, "expert_email_for_interview_updation", toEmail,
				ccEmails, emailData, null);
	}

	private Map<String, Object> prepareDataForExpertUpdatedInterviewMail(final InterviewDAO interviewDAO,
			final String expertId) {
		final Long startTimeOfInterviewForExpert = this.interviewUtil.getExpertStartTimeForInterview(interviewDAO);
		final CandidateDAO candidate = this.candidateInformationManager.getCandidate(interviewDAO.getIntervieweeId());
		final Map<String, Object> emailData = new HashMap<>();
		emailData.put("interview_id", interviewDAO.getId());
		emailData.put("interview_date_and_time", this.dateUtils.getFormattedDateString(startTimeOfInterviewForExpert,
				this.timezoneManager.getTimezoneOfExpert(expertId),
				InterviewSchedulingCommunicationService.INTERVIEW_DATE_TIME_FORMAT));
		emailData.put("candidate_first_name", candidate.getFirstName());
		emailData.put("categories_list",
				this.skillManager.getCategoriesCoveredInInterviewStructure(interviewDAO.getInterviewStructureId()));
		emailData.put("specific_skills_list", String.join(", ", this.skillManager
				.getSpecificSkillsCoveredInInterviewStructure(interviewDAO.getInterviewStructureId()).stream()
				.map(SkillDAO::getName).collect(Collectors.toList())));
		List<QuestionDAO> questions = this.getQuestions(interviewDAO.getEvaluationId());
		if (questions.size() > 0) {
			emailData.put("were_questions_asked_in_previous_rounds", true);
			emailData.put("questions_asked_in_previous_rounds", questions);
		}
		return emailData;
	}

	private List<QuestionDAO> getQuestions(final String evaluationId) {
		final List<String> interviewIds = this.interViewRepository.findAllByEvaluationId(evaluationId).stream()
				.map(InterviewDAO::getId).collect(Collectors.toList());
		final List<QuestionDAO> questionDAOs = this.questionRepository
				.findAllByInterviewIdInAndMasterQuestionIdIsNull(interviewIds);
		return questionDAOs;
	}

}
