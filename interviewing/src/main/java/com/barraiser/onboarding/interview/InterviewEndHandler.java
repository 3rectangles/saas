/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.EventListener;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.interviewend.InterviewEnd;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.InterviewStatus;
import com.barraiser.onboarding.events.InterviewingConsumer;
import com.barraiser.onboarding.interview.feeback.firestore.v1.FirestoreFeedbackDocV1;
import com.barraiser.onboarding.interview.feeback.firestore.v1.FirestoreFeedbackV1Manager;
import com.barraiser.onboarding.interview.feeback.firestore.v1.InterviewFlow;
import com.barraiser.onboarding.interviewing.InterviewingFirestoreData;
import com.barraiser.onboarding.interviewing.InterviewingFirestoreDataFetcher;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Log4j2
@RequiredArgsConstructor
public class InterviewEndHandler implements EventListener<InterviewingConsumer> {
	private final ObjectMapper objectMapper;
	private final InterviewingFirestoreDataFetcher interviewingFirestoreDataFetcher;
	private final FirestoreFeedbackV1Manager firestoreFeedbackV1Manager;
	private final InterviewStatusManager interviewStatusManager;
	private final InterviewUtil interviewUtil;
	private final InterViewRepository interViewRepository;

	@Override
	public List<Class> eventsToListen() {
		return List.of(InterviewEnd.class);
	}

	@Override
	public void handleEvent(Event event) throws Exception {
		final InterviewEnd interviewEndEvent = this.objectMapper.convertValue(event.getPayload(),
				InterviewEnd.class);

		if (interviewEndEvent.getMaxSpeakers() != null && interviewEndEvent.getMaxSpeakers() < 2) {
			log.info("InterviewEnd event: Not enough speakers for interview Id: " + interviewEndEvent.getInterviewId());
			return;
		}

		this.transitionStatus(this.interViewRepository.findById(interviewEndEvent.getInterviewId()).get());

		final InterviewingFirestoreData interviewingFirestoreData = this.interviewingFirestoreDataFetcher
				.get(interviewEndEvent.getInterviewId());
		final InterviewFlow interviewFlow = this.objectMapper.convertValue(interviewingFirestoreData.getInterviewFlow(),
				InterviewFlow.class);
		if (interviewFlow != null && "1".equals(interviewFlow.getVersion())) {
			final FirestoreFeedbackDocV1 feedbackDoc = this.convertInterviewingToFirestore(interviewFlow);
			this.firestoreFeedbackV1Manager.setBaseDoc(interviewEndEvent.getInterviewId(), feedbackDoc);
			final List<String> questionIds = feedbackDoc.getQuestionIds();
			this.saveQuestionsAndFeedback(interviewEndEvent.getInterviewId(), interviewFlow, questionIds);

		}
	}

	private FirestoreFeedbackDocV1 convertInterviewingToFirestore(final InterviewFlow interviewFlow) {

		final Long interviewHiringRating = interviewFlow.getOverallFeedback() != null
				? interviewFlow.getOverallFeedback().getOverallRating() != null
						? interviewFlow.getOverallFeedback().getOverallRating().longValue()
						: null
				: null;

		return FirestoreFeedbackDocV1.builder()
				.version(Long.parseLong(interviewFlow.getVersion()))
				.questionIds(interviewFlow.getSections().stream()
						.filter(x -> x.getQuestions() != null)
						.map(x -> x.getQuestions().stream().map(y -> UUID.randomUUID().toString())
								.collect(Collectors.toList()))
						.flatMap(Collection::stream).collect(Collectors.toList()))
				.overallFeedback(FirestoreFeedbackDocV1.OverallFeedback.builder()
						.areasOfImprovement(FirestoreFeedbackDocV1.Feedback.builder()
								.feedback(FirestoreFeedbackDocV1.FeedbackText.builder()
										.sessionId(UUID.randomUUID().toString())
										.value(interviewFlow.getOverallFeedback() != null
												? interviewFlow.getOverallFeedback().getAreasOfImprovement()
												: null)
										.build())
								.build())
						.strength(FirestoreFeedbackDocV1.Feedback.builder()
								.feedback(FirestoreFeedbackDocV1.FeedbackText.builder()
										.sessionId(UUID.randomUUID().toString())
										.value(interviewFlow.getOverallFeedback() != null
												? interviewFlow.getOverallFeedback().getStrengths()
												: null)
										.build())
								.build())
						.softSkills(this.getSoftSkills(interviewFlow))
						.overallFeedback(FirestoreFeedbackDocV1.Feedback.builder()
								.feedback(FirestoreFeedbackDocV1.FeedbackText.builder()
										.sessionId(UUID.randomUUID().toString())
										.value(interviewFlow.getOverallFeedback() != null
												? interviewFlow.getOverallFeedback().getOverallFeedback()
												: null)
										.build())
								.build())
						.interviewerRecommendation(FirestoreFeedbackDocV1.InterviewerRecommendation.builder()
								.hiringRating(interviewHiringRating)
								.build())
						.build())
				.build();
	}

	private List<FirestoreFeedbackDocV1.Feedback> getSoftSkills(final InterviewFlow interviewFlow) {
		final List<FirestoreFeedbackDocV1.Feedback> softSkills = new ArrayList<>();

		if (interviewFlow.getOverallFeedback() != null && interviewFlow.getOverallFeedback().getSoftSkills() != null) {
			interviewFlow.getOverallFeedback().getSoftSkills().forEach(
					x -> softSkills.add(FirestoreFeedbackDocV1.Feedback.builder()
							.categoryId(x.getId())
							.categoryName(x.getName())
							.feedbackWeightage(x.getWeightage())
							.rating(x.getRating() != null ? x.getRating().longValue() : null)
							.build()));
		}

		return softSkills;
	}

	private void saveQuestionsAndFeedback(final String interviewId, final InterviewFlow interviewFlow,
			final List<String> questionIds) {
		int count = 0;
		final List<FirestoreFeedbackDocV1.Feedback> feedbacks = new ArrayList<>();
		final List<FirestoreFeedbackDocV1.Question> firestoreQuestions = new ArrayList<>();
		for (final InterviewFlow.Section section : interviewFlow.getSections()) {
			if (section.getQuestions() != null) {
				for (final InterviewFlow.Question question : section.getQuestions()) {
					final String questionId = questionIds.get(count);
					final FirestoreFeedbackDocV1.Feedback feedback = FirestoreFeedbackDocV1.Feedback.builder()
							.id(UUID.randomUUID().toString())
							.feedback(FirestoreFeedbackDocV1.FeedbackText.builder()
									.sessionId(UUID.randomUUID().toString())
									.value(question.getFeedback() == null ? "" : question.getFeedback())
									.build())
							.questionId(questionId)
							.rating(question.getRating() != null ? question.getRating().longValue() : null)
							.feedbackWeightage(question.getWeightage())
							.categoryId(section.getSkill().getId())
							.build();
					final FirestoreFeedbackDocV1.Question firestoreQuestion = FirestoreFeedbackDocV1.Question.builder()
							.id(questionId)
							.question(question.getName())
							.feedbackIds(Arrays.asList(feedback.getId()))
							.build();
					feedbacks.add(feedback);
					firestoreQuestions.add(firestoreQuestion);
					count++;
				}
			}
		}
		for (FirestoreFeedbackDocV1.Question question : firestoreQuestions) {
			this.firestoreFeedbackV1Manager.saveQuestion(interviewId, question);
		}
		for (FirestoreFeedbackDocV1.Feedback feedback : feedbacks) {
			this.firestoreFeedbackV1Manager.saveFeedback(interviewId, feedback);
		}
	}

	private InterviewDAO transitionStatus(final InterviewDAO interviewDAO) {
		try {
			final InterviewStatus toStatus = this.getDestinationInterviewStatus(interviewDAO);
			if (InterviewStatus.PENDING_INTERVIEWING.getValue().equals(interviewDAO.getStatus())) {
				return this.interviewStatusManager.updateInterviewStatus(interviewDAO, toStatus, null,
						null);
			}
		} catch (final Exception e) {
			log.error(e, e);
		}
		return interviewDAO;
	}

	private InterviewStatus getDestinationInterviewStatus(final InterviewDAO interviewDAO) {

		if (this.interviewUtil.isFastrackedInterview(interviewDAO.getInterviewRound())
				|| Boolean.FALSE.equals(interviewDAO.getIsTaggingAgentNeeded())) {
			return InterviewStatus.PENDING_FEEDBACK_SUBMISSION;
		}

		return InterviewStatus.PENDING_TAGGING;
	}

}
