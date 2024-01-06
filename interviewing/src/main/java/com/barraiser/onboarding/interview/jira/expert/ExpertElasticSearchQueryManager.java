/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.jira.expert;

import com.barraiser.onboarding.common.Constants;
import com.barraiser.common.enums.RoundType;
import com.barraiser.onboarding.interview.jira.expert.util.ExpertMatchingUtil;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.MatchInterviewersData;
import com.barraiser.onboarding.search.BarraiserEsQueryHelper;
import lombok.AllArgsConstructor;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
class ExpertElasticSearchQueryManager {

	private final BarraiserEsQueryHelper esQueryHelper;
	private final ExpertMatchingUtil expertMatchingUtil;

	public QueryBuilder getBaseQuery(final MatchInterviewersData data) {
		final Boolean isInternalInterview = Constants.ROUND_TYPE_INTERNAL.equals(data.getInterviewRound());
		final String companyForWhichExpertCanTakeInterview = isInternalInterview
				? data.getHiringCompanyId()
				: ExpertProps.COMPANY_BARRAISER_EXPERT_ID;
		final boolean shouldExpertBeUnderTraining = ExpertProps.COMPANY_BARRAISER_EXPERT_ID
				.equals(data.getHiringCompanyId());
		QueryBuilder query = QueryBuilders.boolQuery();
		query = this.esQueryHelper.eq(query, ExpertProps.IS_ACTIVE, true);
		query = this.esQueryHelper.notEq(query, ExpertProps.ID, data.getCandidateId());
		query = this.esQueryHelper.notEq(query, ExpertProps.CURRENT_COMPANY_ID, data.getHiringCompanyId());
		query = this.esQueryHelper.notEq(query, ExpertProps.LAST_COMPANIES, data.getHiringCompanyId());
		query = this.esQueryHelper.eq(query, ExpertProps.IS_UNDER_TRAINING, shouldExpertBeUnderTraining);
		query = this.esQueryHelper.eq(query, ExpertProps.COMPANIES_FOR_WHICH_EXPERT_CAN_TAKE_INTERVIEW,
				companyForWhichExpertCanTakeInterview);
		query = this.esQueryHelper.in(query, ExpertProps.COUNTRIES_FOR_WHICH_EXPERT_CAN_TAKE_INTERVIEWS,
				(List) data.getEligibleCountriesForExperts());

		return query;
	}

	public QueryBuilder createQueryForFixedQuestions(final MatchInterviewersData data) {
		QueryBuilder query = getBaseQuery(data);
		query = this.esQueryHelper.in(query, ExpertProps.ID, (List) data.getFixedQuestionExperts());
		return query;
	}

	public QueryBuilder createQuery(final MatchInterviewersData data) {
		QueryBuilder query = getBaseQuery(data);

		final List<String> categories = this.expertMatchingUtil.getRequiredCategoryOfExpert(data.getCategory(),
				data.getInterviewRound());
		query = this.esQueryHelper.in(query, ExpertProps.CATEGORY, (List) categories);

		final Integer workExperienceOfInterviewer = this.expertMatchingUtil.getRequiredWorkExperienceOfExpert(
				data.getCategory(),
				data.getWorkExperienceOfIntervieweeInMonths(),
				data.getInterviewRound(), data.getPartnerCompanyId());

		if (workExperienceOfInterviewer != null) {
			query = this.esQueryHelper.gte(query, ExpertProps.WORK_EXPERIENCE_IN_MONTHS,
					workExperienceOfInterviewer);

		}
		if (data.getInterviewRound().equals(RoundType.EXPERT.getValue())) {
			query = this.esQueryHelper.eq(query, ExpertProps.EXPERT_DOMAINS, data.getDomainId());
		} else {
			query = this.esQueryHelper.eq(query, ExpertProps.PEER_DOMAINS, data.getDomainId());

		}
		return query;
	}
}
