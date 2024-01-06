/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.types.Question;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.QuestionDAO;
import com.barraiser.onboarding.dal.QuestionRepository;
import com.barraiser.onboarding.firebase.FirebaseManager;
import com.barraiser.onboarding.firebase.FirestoreCollection;
import com.barraiser.onboarding.interview.pojo.InterviewQuestionDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Log4j2
@AllArgsConstructor
public class InterviewQuestionDetailsFetcher {
	private final FirebaseManager firebaseManager;
	private final QuestionRepository questionRepository;
	private final InterViewRepository interViewRepository;
	private final InterviewCategoriesDataFetcher interviewCategoriesDataFetcher;
	private final ObjectMapper objectMapper;

	public InterviewQuestionDetails getInterviewQuestionDetails(final String interviewId)
			throws Exception {
		log.info(String.format(
				"Fetching InterviewQuestionDetails for interviewId:%s",
				interviewId));

		final InterviewQuestionDetails interviewQuestionDetails = new InterviewQuestionDetails();
		interviewQuestionDetails.setInterviewId(interviewId);

		this.addQuestionIdsFromFirestoreToInterviewQuestionDetails(interviewQuestionDetails);

		this.addQuestionsFromDatabaseToInterviewQuestionDetails(interviewQuestionDetails);

		this.addInterviewCategoriesFromDatabaseToInterviewQuestionDetails(interviewQuestionDetails);

		return interviewQuestionDetails;
	}

	private void addQuestionIdsFromFirestoreToInterviewQuestionDetails(
			final InterviewQuestionDetails interviewQuestionDetails)
			throws Exception {
		ApiFuture<DocumentSnapshot> interviewDocument = this.firebaseManager
				.getFirestoreDb()
				.collection(FirestoreCollection.INTERVIEW_FEEDBACK
						.getValue())
				.document(interviewQuestionDetails.getInterviewId())
				.get();

		interviewQuestionDetails.setQuestionIds(
				(List<String>) interviewDocument.get().get("questionIds"));
	}

	private void addQuestionsFromDatabaseToInterviewQuestionDetails(
			final InterviewQuestionDetails interviewQuestionDetails) {
		List<QuestionDAO> questions = interviewQuestionDetails
				.getQuestionIds()
				.stream()
				.map(questionId -> this.questionRepository.findById(questionId).get())
				.collect(Collectors.toList());

		interviewQuestionDetails.setQuestions(questions);
	}

	private void addInterviewCategoriesFromDatabaseToInterviewQuestionDetails(
			final InterviewQuestionDetails interviewQuestionDetails) {
		InterviewDAO interview = this.interViewRepository
				.findById(interviewQuestionDetails.getInterviewId())
				.get();

		interviewQuestionDetails.setInterviewCategories(
				this.interviewCategoriesDataFetcher
						.getParentInterviewCategoryOfSkills(
								interview.getInterviewStructureId()));
	}
}
