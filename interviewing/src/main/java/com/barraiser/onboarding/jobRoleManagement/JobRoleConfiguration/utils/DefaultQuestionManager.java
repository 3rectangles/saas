/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.jobRoleManagement.JobRoleConfiguration.utils;

import com.barraiser.common.graphql.input.InterviewStructureInput;
import com.barraiser.onboarding.dal.DefaultQuestionsDAO;
import com.barraiser.onboarding.dal.DefaultQuestionsRepository;
import com.barraiser.onboarding.dal.EvaluationDAO;
import com.barraiser.onboarding.dal.EvaluationRepository;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.QuestionDAO;
import com.barraiser.onboarding.dal.QuestionRepository;
import com.barraiser.onboarding.interview.feeback.InterviewFeedbackVersionFetcher;
import com.barraiser.onboarding.interview.feeback.firestore.v1.FirestoreFeedbackDocV1;
import com.barraiser.onboarding.interview.feeback.firestore.v1.FirestoreFeedbackV1Manager;
import com.barraiser.onboarding.jobRoleManagement.SkillInterviewingConfiguration.dal.EntityToDocumentMappingDAO;
import com.barraiser.onboarding.jobRoleManagement.SkillInterviewingConfiguration.repository.EntityToDocumentMappingRepository;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Log4j2
@AllArgsConstructor
@Component
public class DefaultQuestionManager {

	private final DefaultQuestionsRepository defaultQuestionsRepository;
	private final EntityToDocumentMappingRepository entityToDocumentMappingRepository;
	private final QuestionRepository questionRepository;
	private final FirestoreFeedbackV1Manager firestoreFeedbackV1Manager;
	private final EvaluationRepository evaluationRepository;

	private static final String ENTITY_TYPE_INTERVIEW_STRUCTURE = "INTERVIEW_STRUCTURE";
	private static final String DOCUMENT_CONTEXT_DEFAULT_QUESTION = "DEFAULT_QUESTION";

	public void save(final List<InterviewStructureInput> interviewStructureInputs) {

		this.defaultQuestionsRepository.saveAll(
				interviewStructureInputs.stream()
						.map(
								interviewStructureInput -> getDefaulatQuestionsDAOsFromInterviewStructureInput(
										interviewStructureInput))
						.flatMap(Collection::stream)
						.collect(Collectors.toList()));

		this.entityToDocumentMappingRepository.saveAll(
				interviewStructureInputs.stream()
						.map(
								is -> getEntityToDocumentDAOsFromInterviewStrutureInput(is))
						.flatMap(Collection::stream)
						.collect(Collectors.toList()));
	}

	private List<EntityToDocumentMappingDAO> getEntityToDocumentDAOsFromInterviewStrutureInput(
			InterviewStructureInput is) {
		return is.getDefaultQuestionsDocuments().stream()
				.map(
						d -> EntityToDocumentMappingDAO.builder()
								.id(
										UUID.randomUUID()
												.toString())
								.context(
										DOCUMENT_CONTEXT_DEFAULT_QUESTION)
								.entityType(
										ENTITY_TYPE_INTERVIEW_STRUCTURE)
								.entityId(is.getId())
								.documentId(d.getId())
								.build())
				.collect(Collectors.toList());
	}

	private List<DefaultQuestionsDAO> getDefaulatQuestionsDAOsFromInterviewStructureInput(
			InterviewStructureInput interviewStructureInput) {
		return interviewStructureInput
				.getDefaultQuestionsWithCategories()
				.stream()
				.map(categoricalQuestionInput -> categoricalQuestionInput.getCategoryIds().stream()
						.map(category -> DefaultQuestionsDAO.builder()
								.id(UUID.randomUUID().toString())
								.interviewStructureId(interviewStructureInput.getId())
								.question(categoricalQuestionInput.getQuestion())
								.questionType(categoricalQuestionInput.getQuestionType().getValue())
								.categoryId(category)
								.isPreInterviewQuestion(
										categoricalQuestionInput.getIsPreInterviewQuestion())
								.build())
						.collect(Collectors.toList()))
				.flatMap(Collection::stream)
				.collect(Collectors.toList());
	}

	public void addDefaultQuestionsToInterview(final InterviewDAO interviewDAO) {
		final List<DefaultQuestionsDAO> defaultQuestionsDAOS = this.defaultQuestionsRepository
				.findByInterviewStructureIdOrderByCreatedOnAsc(interviewDAO.getInterviewStructureId());
		if (this.shouldAddInFirestore(interviewDAO)) {
			this.addDefaultQuestionsToInterviewInFirestore(interviewDAO, defaultQuestionsDAOS);
		} else {
			this.addDefaultQuestionsToInterviewInDb(interviewDAO, defaultQuestionsDAOS);
		}
	}

	private void addDefaultQuestionsToInterviewInFirestore(final InterviewDAO interview,
			final List<DefaultQuestionsDAO> defaultQuestionsDAOS) {
		try {
			if (this.firestoreFeedbackV1Manager.exists(interview.getId())) {
				return;
			}
		} catch (final ExecutionException | InterruptedException e) {
			throw new RuntimeException(e);
		}
		final List<String> questionIds = new ArrayList<>();
		for (DefaultQuestionsDAO defaultQuestion : defaultQuestionsDAOS) {
			final String questionId = UUID.randomUUID().toString();
			questionIds.add(questionId);
			this.firestoreFeedbackV1Manager.saveQuestion(interview.getId(), FirestoreFeedbackDocV1.Question.builder()
					.id(questionId)
					.question(defaultQuestion.getQuestion())
					.questionTagged(defaultQuestion.getQuestion())
					.feedbackIds(List.of())
					.build());
		}
		this.firestoreFeedbackV1Manager.setBaseDoc(interview.getId(), FirestoreFeedbackDocV1.builder()
				.version(1L)
				.questionIds(questionIds)
				.build());
	}

	private void addDefaultQuestionsToInterviewInDb(final InterviewDAO interview,
			final List<DefaultQuestionsDAO> defaultQuestionsDAOS) {
		this.questionRepository.deleteAllByInterviewIdAndIsDefaultTrue(interview.getId());
		final List<QuestionDAO> questionDAOS = defaultQuestionsDAOS.stream()
				.map(
						defaultQuestion -> QuestionDAO.builder()
								.id(UUID.randomUUID().toString())
								.interviewId(interview.getId())
								.question(defaultQuestion.getQuestion())
								.rescheduleCount(interview.getRescheduleCount())
								.isDefault(Boolean.TRUE)
								.build())
				.collect(Collectors.toList());

		this.questionRepository.saveAll(questionDAOS);
	}

	private boolean shouldAddInFirestore(final InterviewDAO interviewDAO) {
		final EvaluationDAO evaluationDAO = this.evaluationRepository.findById(interviewDAO.getEvaluationId()).get();
		return Integer.parseInt(evaluationDAO.getDefaultScoringAlgoVersion()) >= 12;
	}

}
