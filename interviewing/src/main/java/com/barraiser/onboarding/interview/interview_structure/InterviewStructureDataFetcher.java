/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.interview_structure;

import com.barraiser.common.graphql.types.Interview;
import com.barraiser.common.graphql.types.InterviewStructure;
import com.barraiser.common.graphql.types.RoundLevelInterviewStructure;
import com.barraiser.common.utilities.ObjectFieldsFilter;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.graphql.AuthorizationResult;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLQuery;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLQuery_deprecated;
import com.barraiser.onboarding.interview.InterviewUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;
import org.springframework.stereotype.Component;
import com.barraiser.onboarding.config.ConfigComposer;

import java.util.ArrayList;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class InterviewStructureDataFetcher extends AuthorizedGraphQLQuery_deprecated<InterviewStructure> {
	private final InterviewStructureRepository interviewStructureRepository;
	private final JobRoleToInterviewStructureRepository jobRoleToInterviewStructureRepository;
	private final DefaultQuestionsRepository defaultQuestionsRepository;
	private final ObjectMapper objectMapper;
	private final PartnerCompanyRepository partnerCompanyRepository;
	private final ConfigComposer configComposer;

	public InterviewStructureDataFetcher(final InterviewStructureRepository interviewStructureRepository,
			final JobRoleToInterviewStructureRepository jobRoleToInterviewStructureRepository,
			final DefaultQuestionsRepository defaultQuestionsRepository,
			final InterviewStructureAuthorizer interviewStructureAuthorizer,
			final ObjectFieldsFilter<InterviewStructure> interviewStructureObjectFieldsFilter,
			final ObjectMapper objectMapper,
			final PartnerCompanyRepository partnerCompanyRepository,
			final ConfigComposer configComposer) {
		super(interviewStructureAuthorizer, interviewStructureObjectFieldsFilter);

		this.interviewStructureRepository = interviewStructureRepository;
		this.jobRoleToInterviewStructureRepository = jobRoleToInterviewStructureRepository;
		this.defaultQuestionsRepository = defaultQuestionsRepository;
		this.objectMapper = objectMapper;
		this.partnerCompanyRepository = partnerCompanyRepository;
		this.configComposer = configComposer;
	}

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(
				List.of("Interview", "interviewStructure"),
				List.of("RoundLevelInterviewStructure", "interviewStructure"));
	}

	@Override
	public InterviewStructure fetch(final DataFetchingEnvironment environment,
			final AuthorizationResult authorizationResult) {
		final GraphQLObjectType type = (GraphQLObjectType) environment.getParentType();

		if (type.getName().equals("Interview")) {
			return this.getInterviewStructureForInterview(environment);

		} else if (type.getName().equals("RoundLevelInterviewStructure")) {
			return this.getRoundLevelInterviewStructure(environment);

		} else {
			throw new IllegalArgumentException(
					"Bad parent type while accessing interview structure type, please fix your query");
		}
	}

	private InterviewStructure getRoundLevelInterviewStructure(DataFetchingEnvironment environment) {
		final RoundLevelInterviewStructure roundLevelInterviewStructure = environment.getSource();

		final JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO = this.jobRoleToInterviewStructureRepository
				.findById(roundLevelInterviewStructure.getId()).get();
		final String interviewStructureId = roundLevelInterviewStructure.getInterviewStructure().getId();

		final InterviewStructureDAO interviewStructureDAO = this.interviewStructureRepository
				.findById(interviewStructureId)
				.orElseThrow(() -> new IllegalArgumentException(
						"Interview Structure does not exist : " + interviewStructureId));

		return this.objectMapper
				.convertValue(
						interviewStructureDAO,
						InterviewStructure.class)
				.toBuilder()
				.defaultQuestions(this.getDefaultQuestions(interviewStructureId))
				.link(jobRoleToInterviewStructureDAO.getInterviewStructureLink())
				.build();

	}

	private InterviewStructure getInterviewStructureForInterview(DataFetchingEnvironment environment) {
		final Interview interview = environment.getSource();
		final String interviewStructureId = interview.getInterviewStructureId();

		if (interviewStructureId == null) {
			return null;
		}

		InterviewStructureDAO interviewStructureDAO = null;

		if (interviewStructureId != null) {
			interviewStructureDAO = this.interviewStructureRepository.findById(interviewStructureId).orElse(null);
		}
		InterviewStructure interviewStructure = this.objectMapper.convertValue(interviewStructureDAO,
				InterviewStructure.class);
		Boolean isInternal = InterviewUtil.isInternalInterview(interview.getInterviewRound());
		interviewStructure.setFeedbackTextType(this.getFeedbackTextType(interview.getPartnerId(), isInternal));

		String inputType = this.getQuestionInputType(interview.getPartnerId(), isInternal);
		interviewStructure.setInputType(inputType);

		return interviewStructure;

	}

	private List<String> getDefaultQuestions(final String interviewStructureId) {
		return this.defaultQuestionsRepository.findAllByInterviewStructureId(interviewStructureId)
				.stream()
				.map(DefaultQuestionsDAO::getQuestion)
				.collect(Collectors.toList());
	}

	public String getQuestionInputType(String partnerId, final Boolean isInternal) {
		/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
		PartnerCompanyDAO partnerCompanyDAO = this.partnerCompanyRepository.findById(partnerId).get();
		Integer scaleScoring = partnerCompanyDAO.getScaleScoring();
		if (scaleScoring == null) {
			List<String> tags = new ArrayList<>();
			tags.add("partnership_model." + partnerCompanyDAO.getPartnershipModelId());
			if (!isInternal)
				tags.add("interview_type.br");
			else
				tags.add("interview_type.internal");
			try {
				JsonNode config = this.configComposer.compose("scoring_rating", tags);
				JsonNode configFeedback = config != null ? config.get("feedback_config") : null;
				scaleScoring = (configFeedback != null) ? configFeedback.get("question.inputScale").asInt() : null;
			} catch (Exception e) {
				scaleScoring = 5;
			}
		}
		if (scaleScoring == null)
			return "RATING_TILL_5";
		if (scaleScoring == 4)
			return "RATING_TILL_4";
		if (scaleScoring == 10)
			return "RATING_TILL_10";
		return "RATING_TILL_5";
	}

	private String getFeedbackTextType(final String partnerId, final Boolean isInternal) {
		PartnerCompanyDAO partnerCompanyDAO = this.partnerCompanyRepository.findById(partnerId).get();
		Integer overAllTextFormat = partnerCompanyDAO.getOverallTextFormat();

		if (overAllTextFormat == null) {
			List<String> tags = new ArrayList<>();
			tags.add("partnership_model." + partnerCompanyDAO.getPartnershipModelId());
			if (!isInternal)
				tags.add("interview_type.br");
			else
				tags.add("interview_type.internal");
			try {
				JsonNode config = this.configComposer.compose("scoring_rating", tags);
				JsonNode configFeedback = config != null ? config.get("feedback_config") : null;
				overAllTextFormat = (configFeedback != null)
						? configFeedback.get("overallFeedback.feedBackTextType").asInt()
						: null;
			} catch (Exception e) {
				overAllTextFormat = 1;
			}
		}
		if (overAllTextFormat == null)
			return "ONE_TEXT_TYPE";
		if (overAllTextFormat == 2)
			return "TWO_TEXT_TYPE";
		return "ONE_TEXT_TYPE";
	}
}
