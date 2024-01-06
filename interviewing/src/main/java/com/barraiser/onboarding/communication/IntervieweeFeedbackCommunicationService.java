/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.communication;

import com.barraiser.common.graphql.types.SkillScore;
import com.barraiser.common.utilities.DateUtils;
import com.barraiser.onboarding.candidate.CandidateInformationManager;
import com.barraiser.onboarding.communication.channels.email.EmailService;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.evaluation.scores.BgsCalculator;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class IntervieweeFeedbackCommunicationService {
	private final IntervieweeFeedbackRepository intervieweeFeedbackRepository;
	private final EvaluationScoreRepository evaluationScoreRepository;
	private final CandidateInformationManager candidateInformationManager;
	private final UserDetailsRepository userDetailsRepository;
	private final InterViewRepository interViewRepository;
	private final DateUtils dateUtils;
	private final EmailService emailService;
	private final ObjectMapper objectMapper;
	private static final String SENDER_EMAIL = "expert-feedback@barraiser.com";

	private void sendFeedbackToExpert(
			final IntervieweeFeedbackDAO feedback, final InterviewDAO interview) throws Exception {

		final UserDetailsDAO expert = this.userDetailsRepository
				.findById(interview.getInterviewerId())
				.orElseThrow(
						() -> new IllegalArgumentException(
								"Expert not found for interview : "
										+ interview.getId()));

		final CandidateDAO candidate = this.candidateInformationManager.getCandidate(interview.getIntervieweeId());

		if (candidate == null) {
			throw new IllegalArgumentException(
					"Candidate not found for interview : "
							+ interview.getId());
		}

		final String candidateName = String.format(
				"%s %s",
				candidate.getFirstName() != null ? candidate.getFirstName() : "",
				candidate.getLastName() != null ? candidate.getLastName() : "");

		final List<String> toEmail = new ArrayList();
		toEmail.add(expert.getEmail());

		final List<String> ccEmail = new ArrayList<>();
		ccEmail.add(SENDER_EMAIL);

		final Map<String, String> emailData = new HashedMap();

		emailData.put("interviewee_name", candidateName);
		emailData.put(
				"date",
				this.dateUtils.getFormattedDateString(
						interview.getVideoStartTime(), null, DateUtils.DATE_IN_YYYY_MM_DD_FORMAT));
		emailData.put("average_rating", feedback.getAverageRating().toString());
		emailData.put(
				"clarity_of_questions", feedback.getInterviewerClarityOfQuestions().toString());
		emailData.put("quality_of_questions", feedback.getQualityOfQuestions().toString());
		emailData.put("interviewer_knowledge", feedback.getInterviewerKnowledge().toString());
		emailData.put("feedback", feedback.getFeedbackFromInterviewer().toString());
		emailData.put("interview_structure", feedback.getStructureOfInterview().toString());
		emailData.put("other_remarks", feedback.getAnyOtherFeedback());

		final String subject = String.format("Candidate feedback - %s", candidateName);

		this.emailService.sendEmail(
				SENDER_EMAIL, subject, "interviewee_feedback", toEmail, ccEmail, emailData, null);
	}

	// tp-722 confirm if this is right.. should it be eval id or interview id
	private boolean canFeedbackBeSentToExpert(
			final IntervieweeFeedbackDAO feedback, final EvaluationDAO evaluation) {
		if (feedback.getAverageRating() >= 3) {
			return true;
		}
		final List<SkillScore> scores = this.evaluationScoreRepository
				.findAllByEvaluationIdAndScoringAlgoVersion(
						evaluation.getId(), evaluation.getDefaultScoringAlgoVersion())
				.stream()
				.map(x -> this.objectMapper.convertValue(x, SkillScore.class))
				.collect(Collectors.toList());
		return BgsCalculator.calculateBgsNoScale(scores) >= 500;
	}

	private IntervieweeFeedbackDAO getFeedbackForInterview(final String interviewId) {
		return this.intervieweeFeedbackRepository.findByInterviewId(interviewId).orElse(null);
	}

	public void sendIntervieweeFeedbacksForEvaluation(final EvaluationDAO evaluation) {
		final List<InterviewDAO> interviews = this.interViewRepository.findAllByEvaluationId(evaluation.getId());
		interviews.forEach(
				interview -> {
					final IntervieweeFeedbackDAO feedback = this.getFeedbackForInterview(interview.getId());
					if (feedback != null && this.canFeedbackBeSentToExpert(feedback, evaluation)) {
						try {
							this.sendFeedbackToExpert(feedback, interview);
						} catch (final Exception e) {
							log.error(e);
						}
					}
				});
	}
}
