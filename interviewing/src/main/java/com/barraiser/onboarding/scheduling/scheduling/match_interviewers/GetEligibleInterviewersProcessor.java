/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.match_interviewers;

import com.barraiser.onboarding.dal.ExpertDAO;
import com.barraiser.onboarding.dal.ExpertRepository;
import com.barraiser.onboarding.dal.InterviewStructureDAO;
import com.barraiser.onboarding.dal.InterviewStructureRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class GetEligibleInterviewersProcessor implements MatchInterviewersProcessor {
	private final FeedbackTimeBasedFilteringProcessor feedbackTimeBasedFilteringProcessor;
	private final GetAllUnusedInterviewersProcessor getAllUnusedInterviewersProcessor;
	private final BlacklistedInterviewersFilteringProcessor blacklistedInterviewersFilteringProcessor;
	private final SpecificSkillsMatchingProcessor specificSkillsMatchingProcessor;
	private final ExpertRepository expertRepository;
	private final ObjectMapper objectMapper;
	private final ExpertElasticSearchMatchingProcessor expertSearchProcessor;
	private final ExpertFixedQuestionMatchingProcessor expertFixedQuestionMatchingProcessor;
	private final MaxCostBasedFilteringProcessor maxCostBasedFilteringProcessor;
	private final InterviewStructureRepository interviewStructureRepository;

	@Override
	public void process(final MatchInterviewersData data) throws IOException {
		this.populateEligibleInterviewers(data);
		final List<InterviewerData> duplicateExperts = getDuplicateExpertsForActualExperts(data.getInterviewers());
		data.setDuplicateExperts(duplicateExperts);
	}

	public void populateEligibleInterviewers(final MatchInterviewersData data) throws IOException {

		Map<String, String[]> fixedQuestionMap = getFixedQuestionMapFromStructure(data);

		// 1. Get Interviewers that match a filter from ElasticSearch
		if (fixedQuestionMap.keySet().size() == 0) {
			this.expertSearchProcessor.process(data);
		} else {
			data.setFixedQuestionMap(fixedQuestionMap);
			this.expertFixedQuestionMatchingProcessor.process(data);
		}

		log.info("eligible experts after elastic search for interview_id : {} are : {}", data.getInterviewId(),
				data.getInterviewers());

		// 2. filter interviewers by cost
		this.maxCostBasedFilteringProcessor.process(data);

		log.info("eligible experts after cost filter for interview_id : {} are : {}", data.getInterviewId(),
				data.getInterviewers());

		// 3. Filter by Mandatory skills
		this.specificSkillsMatchingProcessor.process(data);

		log.info("eligible experts after skills filter for interview_id : {} are : {}", data.getInterviewId(),
				data.getInterviewers());

		// 4. Filter out Blacklisted Interviewers
		this.blacklistedInterviewersFilteringProcessor.process(data);

		log.info("eligible experts after blacklist filter for interview_id : {} are : {}", data.getInterviewId(),
				data.getInterviewers());

		// 5. Get All Unused Interviewers
		this.getAllUnusedInterviewersProcessor.process(data);

		log.info("eligible experts after unused filter for interview_id : {} are : {}", data.getInterviewId(),
				data.getInterviewers());

		// 6. Remove interviewers that have pending feedback > 72 hrs
		this.feedbackTimeBasedFilteringProcessor.process(data);
		log.info("final eligible experts for interview_id : {} are : {}", data.getInterviewId(),
				data.getInterviewers());
	}

	private List<InterviewerData> getDuplicateExpertsForActualExperts(
			final List<InterviewerData> actualInterviewers) {
		final List<ExpertDAO> expertDAOs = this.expertRepository.findAllByDuplicatedFromIn(
				actualInterviewers.stream()
						.map(InterviewerData::getId)
						.collect(Collectors.toList()));
		return expertDAOs.stream()
				.map(x -> this.objectMapper.convertValue(x, InterviewerData.class))
				.collect(Collectors.toList());
	}

	private Map<String, String[]> getFixedQuestionMapFromStructure(final MatchInterviewersData data) {
		// Initialize the map
		Map<String, String[]> questionMap = new HashMap<>();

		String interviewStructureId = data.getInterviewStructureId();
		if (interviewStructureId == null)
			return questionMap;

		InterviewStructureDAO interviewStructureDAO = interviewStructureRepository.findById(interviewStructureId)
				.orElse(null);
		if (interviewStructureDAO == null)
			return questionMap;

		String inputString = interviewStructureDAO.getInterviewFlow(); // "This is a <FSQ question1, question5> tag.
		// Here's another <FSQ question2, question8>
		// tag.";
		if (inputString == null)
			return questionMap;

		// Extract all the tags from the input string
		Pattern pattern = Pattern.compile("<FSQ[^>]*>");
		Matcher matcher = pattern.matcher(inputString);

		while (matcher.find()) {
			// Extract the tag string
			String tagString = matcher.group();

			// Extract the string inside the angle brackets
			String questionsString = tagString.replaceAll("<FSQ|>", "");

			// Split the string into an array using the comma separator
			String[] questionArray = questionsString.split(",");

			// Trim whitespace from each question code
			for (int i = 0; i < questionArray.length; i++) {
				questionArray[i] = questionArray[i].trim();
			}

			// Add the question array to the map with the tag string as the key
			questionMap.put(tagString, questionArray);
		}

		return questionMap;
	}
}
