/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.validators;

import com.barraiser.common.graphql.input.SubmitQuestionInput;
import com.barraiser.common.graphql.types.QuestionValidationError;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.InterviewStatus;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@AllArgsConstructor
public class QuestionTaggingValidator {

	public ArrayList<QuestionValidationError> validate(final SubmitQuestionInput input,
			final InterviewDAO interviewDAO) {

		ArrayList<QuestionValidationError> errors = new ArrayList<QuestionValidationError>();

		if (!(InterviewStatus.PENDING_TAGGING.getValue().equals(interviewDAO.getStatus()) ||
				InterviewStatus.PENDING_FEEDBACK_SUBMISSION.getValue().equals(interviewDAO.getStatus()) ||
				InterviewStatus.PENDING_INTERVIEWING.getValue().equals(interviewDAO.getStatus()))) {
			errors.add(QuestionValidationError.builder()
					.fieldTag(null)
					.error("Submission not allowed at this stage.")
					.build());
		}

		if (input.getQuestions().size() < 1) {
			errors.add(QuestionValidationError.builder()
					.fieldTag(null)
					.error("No questions present")
					.build());
		}

		if (input.getInterviewStart() == null) {
			errors.add(QuestionValidationError.builder()
					.fieldTag("InterviewStart")
					.error("Interview Start can't be empty")
					.build());
		}

		if (input.getLastQuestionEnd() == null) {
			errors.add(QuestionValidationError.builder()
					.fieldTag("LastQuestionEnd")
					.error("Last Question End can't be empty")
					.build());
		}

		if (input.getInterviewEnd() == null) {
			errors.add(QuestionValidationError.builder()
					.fieldTag("InterviewEnd")
					.error("Interview End can't be empty")
					.build());
		}

		input.getQuestions().forEach(x -> {
			if (!Boolean.TRUE.equals(x.getIsDefault())) {

				if (StringUtils.isBlank(x.getQuestion())) {
					errors.add(QuestionValidationError.builder()
							.fieldTag(x.getId() + "question")
							.error("Question can't be empty")
							.build());
				}

				if (x.getStartTimeEpoch() == null) {
					errors.add(QuestionValidationError.builder()
							.fieldTag(x.getId() + "startTime")
							.error("Start Time can't be empty")
							.build());
				}

				if (x.getFollowUpQuestions() != null) {
					x.getFollowUpQuestions().forEach(y -> {
						if (StringUtils.isBlank(y.getQuestion())) {
							errors.add(QuestionValidationError.builder()
									.fieldTag(y.getId() + "question")
									.error("Question can't be empty")
									.build());
						}

						if (y.getStartTimeEpoch() == null) {
							errors.add(QuestionValidationError.builder()
									.fieldTag(y.getId() + "startTime")
									.error("Start Time can't be empty")
									.build());
						}
					});
				}
			}
		});

		return errors;
	}
}
