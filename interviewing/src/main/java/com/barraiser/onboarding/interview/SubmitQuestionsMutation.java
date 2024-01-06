/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.input.SubmitQuestionInput;
import com.barraiser.common.graphql.types.Question;
import com.barraiser.common.graphql.types.QuestionValidationError;
import com.barraiser.common.graphql.types.SubmitQuestionResult;
import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.question_tagging_submission_event.QuestionTaggingSubmissionEvent;
import com.barraiser.commons.eventing.schema.commons.InterviewEvent;
import com.barraiser.onboarding.auth.AuthenticationException;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.communication.channels.email.EmailService;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.events.InterviewingEventProducer;
import com.barraiser.onboarding.graphql.Constants;
import com.barraiser.onboarding.graphql.DataFetcherType;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.onboarding.interview.evaluation.scores.EvaluationStrategy_V5Constants;
import com.barraiser.onboarding.interview.validators.QuestionTaggingValidator;
import com.fasterxml.jackson.databind.ObjectMapper;

import graphql.GraphQLContext;
import graphql.schema.DataFetchingEnvironment;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

@AllArgsConstructor
@Component
@Log4j2
public class SubmitQuestionsMutation implements NamedDataFetcher {
	private final GraphQLUtil graphQLUtil;
	private final QuestionRepository questionRepository;
	private final FeedbackRepository feedbackRepository;
	private final QcCommentRepository qcCommentRepository;
	private final ObjectMapper objectMapper;
	private final RawFeedbackRepository rawFeedbackRepository;
	private final InterViewRepository interViewRepository;
	private final QuestionTaggingValidator questionTaggingValidator;
	private final EmailService emailService;
	private final JiraUUIDRepository jiraUUIDRepository;
	private final InterviewingEventProducer eventProducer;
	private final InterviewService interviewService;
	private final InterviewStatusManager interviewStatusManager;

	@Override
	public String name() {
		return "submitQuestion";
	}

	@Override
	public String type() {
		return DataFetcherType.MUTATION.getValue();
	}

	@Transactional
	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {
		final GraphQLContext context = environment.getContext();
		final AuthenticatedUser authenticatedUser = context.get(Constants.CONTEXT_KEY_USER);

		if (authenticatedUser == null) {
			throw new AuthenticationException("No authenticated user found");
		}

		final SubmitQuestionInput input = this.graphQLUtil.getInput(environment, SubmitQuestionInput.class);

		final String interviewId = input.getInterviewId();
		InterviewDAO interview = this.interViewRepository
				.findById(interviewId)
				.orElseThrow(
						() -> new IllegalArgumentException(
								"No interview found with the id: " + interviewId));
		final ArrayList<QuestionValidationError> errors = this.questionTaggingValidator.validate(input, interview);

		if (Boolean.TRUE.equals(input.getFinalSubmission()) && errors.size() > 0) {
			return SubmitQuestionResult.builder()
					.success(Boolean.FALSE)
					.errors(errors)
					.type("error")
					.build();
		}

		log.info("submitting questions for interview: {}", interview.getId());

		interview = interview.toBuilder()
				.videoStartTime(
						input.getVideoStartTime() != null ? input.getVideoStartTime() : interview.getVideoStartTime())
				.interviewStartTime(input.getInterviewStart())
				.actualEndDate(
						input.getInterviewEnd() == null
								? interview.getActualEndDate()
								: input.getInterviewEnd())
				.lastQuestionEnd(
						input.getLastQuestionEnd() == null
								? interview.getLastQuestionEnd()
								: input.getLastQuestionEnd())
				.operatedBy(authenticatedUser.getUserName())
				.build();
		interview = this.interviewService.save(interview);

		/** Saving raw request for having historical data, with no loss. */
		this.rawFeedbackRepository.save(
				RawFeedbackDAO.builder()
						.id(UUID.randomUUID().toString())
						.interviewId(interviewId)
						.feedback(this.objectMapper.writeValueAsString(input))
						.build());

		final int[] indices = new int[1];

		/**
		 * Delete questions their feedbacks and their qcComments if question is being
		 * deleted.
		 */
		final List<QuestionDAO> questionsInDbForInterview = this.questionRepository.findAllByInterviewId(interviewId);

		// Excluding default questions here to prevent their deletion

		final Set<String> idsForAllQuestionsInDb = questionsInDbForInterview.stream()
				.map(QuestionDAO::getId)
				.collect(Collectors.toSet());

		final Set<String> idsForAllQuestionsInRequest = new HashSet<String>(); // flat list of question ids from follow
		// up questions and
		// questions
		final Set<String> idsForAllMasterQuestionsInRequest = new HashSet<String>();
		final Set<String> idsForAllFollowUpQuestionsInRequest = new HashSet<String>();

		if (input.getQuestions() != null) {
			// TBD : Think in terms of flat map | Also can be optimised
			input.getQuestions().stream()
					.filter(x -> x.getId() != null)
					.map(x -> x.getId())
					.collect(Collectors.toCollection(() -> idsForAllMasterQuestionsInRequest));

			input.getQuestions().stream()
					.filter(x -> x.getId() != null)
					.forEachOrdered(
							x -> {
								if (x.getFollowUpQuestions() != null) {
									x.getFollowUpQuestions().stream()
											.filter(y -> y.getId() != null)
											.map(y -> y.getId())
											.collect(
													Collectors.toCollection(
															() -> idsForAllFollowUpQuestionsInRequest));
								}
							});
		}

		idsForAllQuestionsInRequest.addAll(idsForAllMasterQuestionsInRequest);
		idsForAllQuestionsInRequest.addAll(idsForAllFollowUpQuestionsInRequest);

		final List<String> idsForQuestionsToBeDeleted = new ArrayList<>(
				CollectionUtils.subtract(
						idsForAllQuestionsInDb, idsForAllQuestionsInRequest));
		if (!Boolean.TRUE.equals(input.getOnlyTagTime())) {
			this.questionRepository.deleteByIdIn(idsForQuestionsToBeDeleted);
			final List<String> idsOfFeedbackToBeDeleted = this.feedbackRepository
					.findByReferenceIdIn(idsForQuestionsToBeDeleted).stream()
					.map(x -> x.getId())
					.collect(Collectors.toList());
			this.feedbackRepository.deleteByReferenceIdIn(idsForQuestionsToBeDeleted);

			// Delete qc comments
			this.qcCommentRepository.deleteByFeedbackIdIn(idsOfFeedbackToBeDeleted);
		}

		/** Saving questions and their follow up questions */
		final InterviewDAO interviewDAO = interview;
		if (input.getQuestions() != null) {

			final Map<String, QuestionDAO> savedQuestionsIdToQuestionDAO = this.questionRepository
					.findAllByInterviewId(interviewId).stream()
					.collect(Collectors.toMap(QuestionDAO::getId, Function.identity()));

			input.getQuestions().stream()
					.map(
							question -> this.saveQuestionData(
									question,
									authenticatedUser,
									interviewDAO,
									savedQuestionsIdToQuestionDAO,
									null,
									indices[0]++,
									input.getOnlyTagTime()))
					.forEachOrdered(
							question -> {
								final String masterQuestionId = question.getId();
								if (question.getFollowUpQuestions() != null) {
									question.getFollowUpQuestions().stream()
											.forEachOrdered(
													followUpQuestion -> this.saveQuestionData(
															followUpQuestion,
															authenticatedUser,
															interviewDAO,
															savedQuestionsIdToQuestionDAO,
															masterQuestionId,
															indices[0]++,
															input.getOnlyTagTime()));
								}
							});
		}

		if (Boolean.TRUE.equals(input.getFinalSubmission())) {
			interview = this.interviewService.save(
					interview.toBuilder().questionTaggingStatus("SUBMITTED").build());
		}

		if (Boolean.TRUE.equals(input.getOnlyTagTime())) {
			this.informOpsRegardingTimeTaggingCompletion(interviewId);
		}

		if (!Boolean.TRUE.equals(input.getOnlyTagTime())
				&& Boolean.TRUE.equals(input.getFinalSubmission())) {
			this.transitionStatus(interview, authenticatedUser.getUserName());
			this.pushQuestionTaggingSubmittedEvent(input);
		}

		return SubmitQuestionResult.builder()
				.success(Boolean.TRUE)
				.errors(errors)
				.type("warning")
				.build();
	}

	// TBD: Common between two mutations. Can be moved to a utility
	public Question saveQuestionData(
			final Question question,
			final AuthenticatedUser authenticatedUser,
			final InterviewDAO interview,
			final Map<String, QuestionDAO> savedQuestionsIdToQuestionDAO,
			final String masterQuestionId,
			final int serialNumber,
			final Boolean onlyTagTime) {

		QuestionDAO savedQuestionDAO = QuestionDAO.builder().build();

		if (question.getId() != null
				&& savedQuestionsIdToQuestionDAO.containsKey(question.getId())) {
			savedQuestionDAO = savedQuestionsIdToQuestionDAO.get(question.getId());
		} else if (Boolean.TRUE.equals(onlyTagTime)) {
			return question;
		}

		final String questionType = question.getType() == null ? savedQuestionDAO.getType() : question.getType();
		final QuestionDAO questionDAO;

		if (Boolean.TRUE.equals(onlyTagTime)) {
			questionDAO = savedQuestionDAO.toBuilder()
					.endTime(
							question.getEndTime() == null
									? savedQuestionDAO.getEndTime()
									: question.getEndTime())
					.startTimeEpoch(question.getStartTimeEpoch())
					.build();
		} else {
			questionDAO = savedQuestionDAO.toBuilder()
					.id(
							question.getId() != null
									? question.getId()
									: UUID.randomUUID().toString())
					.interviewId(interview.getId())
					.question(question.getQuestion())
					.endTime(
							question.getEndTime() == null
									? savedQuestionDAO.getEndTime()
									: question.getEndTime())
					.startTimeEpoch(question.getStartTimeEpoch())
					.masterQuestionId(masterQuestionId)
					.type(questionType)
					.irrelevant(
							EvaluationStrategy_V5Constants.QuestionType.DELETED
									.getValue()
									.equals(questionType))
					.feedbacks(Collections.emptyList())
					.serialNumber(serialNumber)
					.operatedBy(authenticatedUser.getUserName())
					.build();
		}

		final QuestionDAO savedQuestion = this.questionRepository.save(
				questionDAO.toBuilder()
						.rescheduleCount(interview.getRescheduleCount())
						.build());
		return question.toBuilder().id(savedQuestion.getId()).build();
	}

	private void informOpsRegardingTimeTaggingCompletion(final String interviewId)
			throws Exception {
		final String jira = this.jiraUUIDRepository.findByUuid(interviewId).get().getJira();
		final Map<String, String> emailData = new HashMap<>();
		emailData.put("jira", jira);
		final String subject = String.format("Time correction completed %s", jira);

		final String fromEmail = "interview@barraiser.com";
		final String toEmail = "ops-qc@barraiser.com";

		this.emailService.sendEmail(
				fromEmail, subject, "time_tagging_completed", List.of(toEmail), emailData, null);
	}

	private void transitionStatus(final InterviewDAO interviewDAO, final String userId) {
		try {
			this.interviewStatusManager.updateInterviewStatus(interviewDAO, InterviewStatus.PENDING_FEEDBACK_SUBMISSION,
					userId, null);
		} catch (final Exception e) {
			log.error(e, e);
		}
	}

	private void pushQuestionTaggingSubmittedEvent(final SubmitQuestionInput input)
			throws Exception {
		final Event<QuestionTaggingSubmissionEvent> event = new Event<>();
		event.setPayload(
				new QuestionTaggingSubmissionEvent()
						.interview(new InterviewEvent().id(input.getInterviewId())));
		this.eventProducer.pushEvent(event);
	}
}
