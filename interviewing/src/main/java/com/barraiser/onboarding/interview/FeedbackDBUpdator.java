/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.input.SubmitFeedbackInput;
import com.barraiser.common.graphql.types.*;
import com.barraiser.common.utilities.DateUtils;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.common.Constants;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.evaluation.scores.EvaluationStrategy_V5Constants;
import com.barraiser.onboarding.interview.feeback.evaluation.FeedbackSummaryFetcher;
import com.barraiser.onboarding.interview.validators.SubmitFeedbackInputValidator;
import com.barraiser.onboarding.interviewing.notes.InterviewingDataSaver;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
@Component
public class FeedbackDBUpdator {
	private final ObjectMapper objectMapper;
	private final FeedbackRepository feedbackRepository;
	private final QcCommentRepository qcCommentRepository;
	private final RawFeedbackRepository rawFeedbackRepository;
	private final QuestionRepository questionRepository;
	private final NormalisedRatingPopulator normalisedRatingPopulator;
	private final InterviewerRecommendationRepository interviewerRecommendationRepository;
	private final InterviewService interviewService;
	private final DateUtils utilities;
	private final FeedbackSummaryFetcher feedbackSummaryFetcher;
	private final SubmitFeedbackInputValidator submitFeedbackInputValidator;
	private final InterviewingDataSaver interviewingDataSaver;
	private final InterviewUtil interviewUtil;

	@Transactional
	public SubmitFeedbackResult save(InterviewDAO interview, final SubmitFeedbackInput input,
			final AuthenticatedUser authenticatedUser) throws IOException {

		final Boolean bypassValidations = this.interviewUtil.isFastrackedInterview(interview.getInterviewRound());
		ArrayList<FeedbackValidationError> errors = new ArrayList<>();

		if (!bypassValidations) {
			errors = this.submitFeedbackInputValidator.validate(input, interview,
					authenticatedUser.getRoles());
		}

		if ((Boolean.TRUE.equals(input.getFinalSubmission())
				|| Boolean.TRUE.equals(input.getIncludeFeedbackImprovements()))
				&& errors.size() > 0) {
			return SubmitFeedbackResult.builder()
					.success(Boolean.FALSE)
					.errors(errors)
					.type("error")
					.build();
		}

		final Long feedbackSubmissionTime = this.utilities.convertDateTimeToEpoch(Instant.now());

		if (authenticatedUser.getUserName().equals(interview.getInterviewerId())) {
			interview = interview.toBuilder()
					.expertFeedbackSubmissionTime(feedbackSubmissionTime)
					.build();
		}

		/** Allowing change in start time and end time */
		interview = interview.toBuilder()
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
				.feedbackSubmissionTime(feedbackSubmissionTime)
				.build();
		interview = this.interviewService.save(interview);

		/** Saving raw request for having historical data, with no loss. */
		this.rawFeedbackRepository.save(
				RawFeedbackDAO.builder()
						.id(UUID.randomUUID().toString())
						.interviewId(interview.getId())
						.feedback(this.objectMapper.writeValueAsString(input))
						.createdBy(authenticatedUser.getUserName().toString())
						.build());

		/**
		 * DELETION => if needed Allowing questions deletion here also as admin role
		 * might need it
		 */
		this.deleteFeedbackFormComponentsIfNeeded(interview, input);

		/**
		 * Saving overall feedback.Updating incase already exists. Only one Overall
		 * Feedback allowed
		 * per interview.
		 */
		this.saveOverallFeedback(interview, input.getOverallFeedback());

		this.interviewingDataSaver.saveWasInterviewerVideoOn(interview.getId(), input.getWasInterviewerVideoOn());

		final int[] indices = new int[1];

		/** Saving questions and their follow up questions */
		final InterviewDAO interviewDAO = interview;
		if (input.getQuestions() != null) {

			final Map<String, QuestionDAO> savedQuestionsIdToQuestionDAO = this.questionRepository
					.findAllByInterviewId(interview.getId()).stream()
					.collect(Collectors.toMap(QuestionDAO::getId, Function.identity()));
			input.getQuestions().stream()
					.map(
							question -> this.saveQuestionData(
									question,
									authenticatedUser,
									interviewDAO,
									savedQuestionsIdToQuestionDAO,
									null,
									indices[0]++))
					.forEachOrdered(
							question -> {
								question = this.saveFeedbackDataForQuestion(question);
								final String masterQuestionId = question.getId();
								if (question.getFollowUpQuestions() != null) {
									question.getFollowUpQuestions().stream()
											.forEachOrdered(
													followUpQuestion -> {
														Question savedFollowupQuestion = this.saveQuestionData(
																followUpQuestion,
																authenticatedUser,
																interviewDAO,
																savedQuestionsIdToQuestionDAO,
																masterQuestionId,
																indices[0]++);

														this.saveFeedbackDataForQuestion(
																savedFollowupQuestion);

													});
								}
							});
			this.normalisedRatingPopulator.populate(interviewDAO);
		}

		List<String> feedbackImprovements = null;
		if (!this.interviewUtil.isFastrackedInterview(interview.getInterviewRound())
				&& Boolean.TRUE.equals(input.getIncludeFeedbackImprovements())) {
			feedbackImprovements = this.feedbackSummaryFetcher.getFeedbackSummary(input).stream()
					.filter(Objects::nonNull)
					.collect(Collectors.toList());
		}

		return SubmitFeedbackResult.builder()
				.success(Boolean.TRUE)
				.errors(errors)
				.feedbackImprovements(feedbackImprovements)
				.type("warning")
				.build();
	}

	public void saveOverallFeedback(
			final InterviewDAO interviewDAO, final OverallFeedback overallFeedback) {
		if (overallFeedback != null) {
			if (overallFeedback.getStrength() != null) {

				final Feedback overallStrengthFeedback = overallFeedback.getStrength();
				final FeedbackDAO overallStrengthFeedbackInDb = this.feedbackRepository
						.findByReferenceIdAndTypeAndRescheduleCount(
								interviewDAO.getId(),
								Constants.OVERALL_FEEDBACK_TYPE_STRENGTH,
								interviewDAO.getRescheduleCount());

				final FeedbackDAO savedStrengthFeedback = this.feedbackRepository.save(
						this.objectMapper
								.convertValue(overallStrengthFeedback, FeedbackDAO.class)
								.toBuilder()
								.id(
										overallStrengthFeedbackInDb != null
												? overallStrengthFeedbackInDb.getId()
												: UUID.randomUUID().toString())
								.referenceId(interviewDAO.getId())
								.type(Constants.OVERALL_FEEDBACK_TYPE_STRENGTH)
								.rescheduleCount(interviewDAO.getRescheduleCount())
								.build());

				this.saveQcCommentData(
						overallStrengthFeedback.getQcComments(), savedStrengthFeedback.getId());
			}

			if (overallFeedback.getAreasOfImprovement() != null) {
				final Feedback overallAreasOfImprovementFeedback = overallFeedback.getAreasOfImprovement();
				final FeedbackDAO overallAreasOfImprovementFeedbackInDb = this.feedbackRepository
						.findByReferenceIdAndTypeAndRescheduleCount(
								interviewDAO.getId(),
								Constants.OVERALL_FEEDBACK_TYPE_AREAS_OF_IMPROVEMENT,
								interviewDAO.getRescheduleCount());

				final FeedbackDAO savedOverallAreasOfImprovementFeedback = this.feedbackRepository.save(
						this.objectMapper
								.convertValue(
										overallAreasOfImprovementFeedback,
										FeedbackDAO.class)
								.toBuilder()
								.id(
										overallAreasOfImprovementFeedbackInDb != null
												? overallAreasOfImprovementFeedbackInDb
														.getId()
												: UUID.randomUUID().toString())
								.referenceId(interviewDAO.getId())
								.type(Constants.OVERALL_FEEDBACK_TYPE_AREAS_OF_IMPROVEMENT)
								.rescheduleCount(interviewDAO.getRescheduleCount())
								.build());

				this.saveQcCommentData(
						overallAreasOfImprovementFeedback.getQcComments(),
						savedOverallAreasOfImprovementFeedback.getId());
			}

			if (overallFeedback.getSoftSkills() != null) {
				this.feedbackRepository.deleteByReferenceIdAndTypeAndRescheduleCount(
						interviewDAO.getId(),
						Constants.OVERALL_FEEDBACK_TYPE_SOFT_SKILLS,
						interviewDAO.getRescheduleCount());
				final List<FeedbackDAO> feedbackDAOS = overallFeedback.getSoftSkills().stream()
						.map(
								x -> this.objectMapper
										.convertValue(x, FeedbackDAO.class)
										.toBuilder()
										.id(UUID.randomUUID().toString())
										.type(
												Constants.OVERALL_FEEDBACK_TYPE_SOFT_SKILLS)
										.referenceId(interviewDAO.getId())
										.rescheduleCount(
												interviewDAO.getRescheduleCount())
										.build())
						.collect(Collectors.toList());
				this.feedbackRepository.saveAll(feedbackDAOS);
			}

			if (overallFeedback.getInterviewerRecommendation() != null) {
				this.saveInterviewerRecommendation(interviewDAO.getId(),
						overallFeedback.getInterviewerRecommendation());
			}
		}
	}

	public void deleteFeedbackFormComponentsIfNeeded(
			final InterviewDAO interviewDAO, final SubmitFeedbackInput input) {

		final List<QuestionDAO> questionsInDbForInterview = this.questionRepository
				.findAllByInterviewIdAndRescheduleCount(
						interviewDAO.getId(), interviewDAO.getRescheduleCount());
		// Excluding default questions here to prevent their deletion
		final Set<String> idsForAllQuestionsInDb = questionsInDbForInterview.stream()
				.filter(x -> x.getIsDefault() == null || x.getIsDefault() == false)
				.map(x -> x.getId())
				.collect(Collectors.toSet());

		final Set<String> idsForAllFeedbackInDb = this.feedbackRepository
				.findByReferenceIdIn(new ArrayList<String>(idsForAllQuestionsInDb))
				.stream()
				.map(x -> x.getId())
				.collect(Collectors.toSet());
		final Set<String> idsForQcCommentsInDb = this.qcCommentRepository
				.findByFeedbackIdIn(new ArrayList<String>(idsForAllFeedbackInDb))
				.stream()
				.map(x -> x.getId())
				.collect(Collectors.toSet());

		final Set<String> idsForAllQuestionsInRequest = new HashSet<String>(); // flat list of question ids from follow
		// up questions and
		// questions
		final Set<String> idsForAllMasterQuestionsInRequest = new HashSet<String>();
		final Set<String> idsForAllFollowUpQuestionsInRequest = new HashSet<String>();

		final Set<String> idsForAllFeedbackInRequest = new HashSet<String>();
		final Set<String> idsForAllQcCommentInRequest = new HashSet<String>();

		// TBD:Scope for refactoring .
		// Way too much branching. Code quality improvement needed
		if (input.getQuestions() != null) {
			input.getQuestions().stream()
					.filter(q -> q.getId() != null)
					.forEachOrdered(
							q -> {
								idsForAllMasterQuestionsInRequest.add(q.getId());

								if (q.getFeedbacks() != null) {
									q.getFeedbacks().stream()
											.filter(f -> f.getId() != null)
											.forEachOrdered(
													f -> {
														idsForAllFeedbackInRequest.add(f.getId());

														if (f.getQcComments() != null) {
															f.getQcComments().stream()
																	.filter(
																			qc -> qc.getId() != null)
																	.forEachOrdered(
																			qc -> {
																				idsForAllQcCommentInRequest
																						.add(
																								qc
																										.getId());
																			});
														}
													});
								}

								if (q.getFollowUpQuestions() != null) {

									q.getFollowUpQuestions().stream()
											.forEachOrdered(
													fuq -> {
														idsForAllFollowUpQuestionsInRequest.add(
																fuq.getId());
														if (fuq.getFeedbacks() != null) {
															fuq.getFeedbacks().stream()
																	.filter(f -> f.getId() != null)
																	.forEachOrdered(
																			f -> {
																				idsForAllFeedbackInRequest
																						.add(
																								f
																										.getId());

																				if (f
																						.getQcComments() != null) {
																					f
																							.getQcComments()
																							.stream()
																							.filter(
																									qc -> qc
																											.getId() != null)
																							.forEachOrdered(
																									qc -> {
																										idsForAllQcCommentInRequest
																												.add(
																														qc
																																.getId());
																									});
																				}
																			});
														}
													});
								}
							});
		}

		idsForAllQuestionsInRequest.addAll(idsForAllMasterQuestionsInRequest);
		idsForAllQuestionsInRequest.addAll(idsForAllFollowUpQuestionsInRequest);

		final List<String> idsForQuestionsToBeDeleted = new ArrayList<>(
				CollectionUtils.subtract(
						idsForAllQuestionsInDb, idsForAllQuestionsInRequest));
		final List<String> idsForFeedbacksToBeDeleted = new ArrayList<>(
				CollectionUtils.subtract(
						idsForAllFeedbackInDb, idsForAllFeedbackInRequest));
		final List<String> idsForQcCommentsToBeDeleted = new ArrayList<>(
				CollectionUtils.subtract(
						idsForQcCommentsInDb, idsForAllQcCommentInRequest));

		this.questionRepository.deleteByIdIn(idsForQuestionsToBeDeleted);
		this.feedbackRepository.deleteByIdIn(idsForFeedbacksToBeDeleted);
		this.qcCommentRepository.deleteByFeedbackIdIn(idsForQcCommentsToBeDeleted);
	}

	public Question saveFeedbackDataForQuestion(final Question question) {
		final String questionId = question.getId();
		if (question.getFeedbacks() != null) {
			question.getFeedbacks().stream()
					.forEachOrdered(
							y -> {
								final FeedbackDAO feedbackDAO = this.objectMapper
										.convertValue(y, FeedbackDAO.class)
										.toBuilder()
										.id(
												y.getId() != null
														? y.getId()
														: UUID.randomUUID().toString())
										.referenceId(questionId)
										.type("PER_QUESTION")
										.build();
								this.feedbackRepository.save(feedbackDAO);
								/** Persisting QC comments for each feedback */
								this.saveQcCommentData(y.getQcComments(), y.getId());
							});
		}
		return question;
	}

	public void saveQcCommentData(final List<QcComment> qcComments, final String feedbackId) {
		if (qcComments != null) {
			qcComments.stream()
					.map(x -> this.objectMapper.convertValue(x, QcCommentDAO.class))
					.map(
							x -> x.toBuilder()
									.id(
											x.getId() != null
													? x.getId()
													: UUID.randomUUID().toString())
									.feedbackId(feedbackId)
									.build())
					.forEachOrdered(
							x -> {
								final QcCommentDAO savedQcComment = this.qcCommentRepository.save(
										x.toBuilder()
												.commenterId(
														x.getCommentedBy() != null
																? x.getCommentedBy().getId()
																: null)
												.build());
							});
		}
	}

	// TBD: Common between two mutations. Can be moved to a utility
	public Question saveQuestionData(
			final Question question,
			final AuthenticatedUser authenticatedUser,
			final InterviewDAO interview,
			final Map<String, QuestionDAO> savedQuestionsIdToQuestionDAO,
			final String masterQuestionId,
			final int serialNumber) {
		QuestionDAO savedQuestionDAO = QuestionDAO.builder().build();

		if (question.getId() != null
				&& savedQuestionsIdToQuestionDAO.containsKey(question.getId())) {
			savedQuestionDAO = savedQuestionsIdToQuestionDAO.get(question.getId());
		}

		final String questionType = question.getType() == null ? savedQuestionDAO.getType() : question.getType();
		final QuestionDAO questionDAO = savedQuestionDAO.toBuilder()
				.id(
						question.getId() != null
								? question.getId()
								: UUID.randomUUID().toString())
				.interviewId(interview.getId())
				.question(question.getQuestion())
				// .tags(this.getQuestionTags(question.getQuestion()))
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
				.rescheduleCount(interview.getRescheduleCount())
				.questionCategory(question.getQuestionCategory())
				.build();

		final QuestionDAO savedQuestion = this.questionRepository.save(questionDAO);
		return question.toBuilder().id(savedQuestion.getId()).build();
	}

	public void saveInterviewerRecommendation(final String interviewId,
			final InterviewerRecommendation recommendation) {
		final InterviewerRecommendationDAO interviewerRecommendationDAO = this.interviewerRecommendationRepository
				.findByInterviewId(interviewId)
				.orElse(InterviewerRecommendationDAO.builder().id(UUID.randomUUID().toString()).interviewId(interviewId)
						.build());

		this.interviewerRecommendationRepository.save(
				interviewerRecommendationDAO.toBuilder()
						.hiringRating(recommendation.getHiringRating())
						.cheatingSuspectedRemarks(recommendation.getCheatingSuspectedRemarks())
						.interviewIncompleteRemarks(recommendation.getInterviewIncompleteRemarks())
						.remarks(recommendation.getRemarks())
						.build());
	}

}
