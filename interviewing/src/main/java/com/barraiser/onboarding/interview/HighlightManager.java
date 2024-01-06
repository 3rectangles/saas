/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.Speaker;
import com.barraiser.common.graphql.types.*;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.evaluation.dal.HighlightDAO;
import com.barraiser.onboarding.interview.evaluation.dal.HighlightQuestionDAO;
import com.barraiser.onboarding.interview.evaluation.dal.HighlightQuestionRepository;
import com.barraiser.onboarding.interview.evaluation.dal.HighlightRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Log4j2
@AllArgsConstructor
public class HighlightManager {
	private final HighlightRepository highlightRepository;
	private final HighlightQuestionRepository highlightQuestionRepository;
	private final InterViewRepository interViewRepository;
	private final SkillRepository skillRepository;

	public List<Highlight> saveHighlights(final CreateHighlightsInput createHighlightsInput) {
		final List<Highlight> highlightList = new ArrayList<>();

		if (interviewHasHighlights(createHighlightsInput.getInterviewId())) {
			log.info(String.format("Removing Highlights for interviewId: %s", createHighlightsInput.getInterviewId()));
			this.removeHighlightsFromInterview(createHighlightsInput.getInterviewId());
		}

		log.info(String.format("SaveHighlights interviewId: %s", createHighlightsInput.getInterviewId()));
		final InterviewDAO interviewDAO = this.interViewRepository.findById(createHighlightsInput.getInterviewId())
				.get();

		createHighlightsInput.getHighlights().forEach(h -> highlightList.add(this.handleHighlightAddition(h,
				createHighlightsInput.getInterviewId())));

		if (createHighlightsInput.getAreHighlightsComplete()) {
			this.interViewRepository.save(
					interviewDAO.toBuilder()
							.areHighlightsComplete(true)
							.build());
		} else { // In case of rescheduled interview as highlights don't have reschedule count
			this.interViewRepository.save(
					interviewDAO.toBuilder()
							.areHighlightsComplete(false)
							.build());
		}

		return highlightList;
	}

	private Highlight handleHighlightAddition(final HighlightInput highlightInput, final String interviewId) {

		final List<String> skillIds = new ArrayList<>();

		highlightInput.getSkills().forEach(s -> {
			Optional<SkillDAO> skill = skillRepository.findById(s.getId());
			if (skill.isEmpty()) {
				this.skillRepository.save(
						SkillDAO.builder()
								.id(s.getId())
								.name(s.getName())
								.build());
			}
			skillIds.add(s.getId());
		});

		final HighlightDAO savedHighlightDAO = this.highlightRepository.save(HighlightDAO.builder()
				.id(UUID.randomUUID().toString())
				.interviewId(interviewId)
				.startTime(highlightInput.getStartTime())
				.endTime(highlightInput.getEndTime())
				.description(highlightInput.getDescription())
				.skillIds(skillIds)
				.build());

		return Highlight.builder()
				.id(savedHighlightDAO.getId())
				.startTime(savedHighlightDAO.getStartTime())
				.endTime(savedHighlightDAO.getEndTime())
				.questions(this.handleHighlightQuestionAddition(highlightInput, savedHighlightDAO.getId()))
				.skills(highlightInput.getSkills().stream().map(
						s -> Skill.builder()
								.id(s.getId())
								.name(s.getName())
								.build())
						.collect(Collectors.toList()))
				.description(savedHighlightDAO.getDescription())
				.build();
	}

	public Boolean interviewHasHighlights(final String interviewId) {
		final List<HighlightDAO> savedHighlights = this.highlightRepository
				.findByInterviewId(interviewId);
		return !savedHighlights.isEmpty();
	}

	private void removeHighlightsFromInterview(final String interviewId) {
		final List<HighlightDAO> highlightDAOS = this.highlightRepository.findByInterviewId(interviewId);

		highlightDAOS.forEach(h -> {
			this.removeHighlightQuestions(h.getId());
			this.highlightRepository.delete(h);
		});
	}

	private void removeHighlightQuestions(final String highlightId) {
		final List<HighlightQuestionDAO> highlightQuestionDAOS = this.highlightQuestionRepository
				.findByHighlightId(highlightId);

		for (HighlightQuestionDAO hq : highlightQuestionDAOS) {
			this.highlightQuestionRepository.delete(hq);
		}
	}

	private List<HighlightQuestion> handleHighlightQuestionAddition(final HighlightInput highlightInput,
			final String highlightId) {

		final HighlightQuestionDAO question = this.highlightQuestionRepository
				.save(HighlightQuestionDAO.builder()
						.id(UUID.randomUUID().toString())
						.highlightId(highlightId)
						.description(highlightInput.getQuestion())
						.offsetTime(1) // Storing offset time as 1 for questions for ordering
						.speaker(highlightInput.getQuestionSpeakerName())
						.build());

		final HighlightQuestionDAO answer = this.highlightQuestionRepository
				.save(HighlightQuestionDAO.builder()
						.id(UUID.randomUUID().toString())
						.highlightId(highlightId)
						.description(highlightInput.getAnswer())
						.offsetTime(2) // Storing offset time as 2 for answers for ordering
						.speaker(highlightInput.getAnswerSpeakerName())
						.build());

		return List.of(toHighlightQuestion(question), toHighlightQuestion(answer));
	}

	private HighlightQuestion toHighlightQuestion(final HighlightQuestionDAO highlightQuestionDAO) {
		return HighlightQuestion.builder()
				.id(highlightQuestionDAO.getId())
				.offsetTime(highlightQuestionDAO.getOffsetTime())
				.description(highlightQuestionDAO.getDescription())
				.speaker(Speaker.builder()
						.name(highlightQuestionDAO.getSpeaker())
						.build())
				.build();
	}
}
