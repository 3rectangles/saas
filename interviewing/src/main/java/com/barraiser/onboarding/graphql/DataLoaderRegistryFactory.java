/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.graphql;

import com.barraiser.onboarding.interview.CodingPadDataFetcher;
import com.barraiser.onboarding.interview.CommentsDataFetcher;
import com.barraiser.onboarding.interview.DomainsDataFetcher;
import com.barraiser.onboarding.interview.InterviewDataFetcher;
import com.barraiser.onboarding.interview.IntervieweeDataFetcher;
import com.barraiser.onboarding.interview.scoring.ScoreDataFetcher;
import lombok.AllArgsConstructor;
import org.dataloader.DataLoaderRegistry;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class DataLoaderRegistryFactory {
	private final IntervieweeDataFetcher intervieweeDataFetcher;
	private final InterviewDataFetcher interviewDataFetcher;
	private final DomainsDataFetcher domainsDataFetcher;
	private final ScoreDataFetcher skillScoreDataFetcher;
	private final CodingPadDataFetcher codingPadDataFetcher;
	private final CommentsDataFetcher commentsDataFetcher;

	private final List<DataLoaderFactory> dataLoaderFactories;

	public DataLoaderRegistry newDataLoaderRegistry() {

		final var registry = new DataLoaderRegistry();
		registry.register(
				IntervieweeDataFetcher.INTERVIEWEE_DATA_LOADER,
				this.intervieweeDataFetcher.createIntervieweeDataLoader());
		registry.register(
				DomainsDataFetcher.DOMAIN_DATA_LOADER,
				this.domainsDataFetcher.createDomainDataLoader());
		registry.register(
				InterviewDataFetcher.INTERVIEW_FOR_EVALUATIONS_DATA_LOADER,
				this.interviewDataFetcher.createInterviewForEvaluationsDataLoader());
		registry.register(
				InterviewDataFetcher.INTERVIEWS_FOR_INTERVIEW_IDS_DATA_LOADER,
				this.interviewDataFetcher.getInterviewsForInterviewIdsDataLoader());
		registry.register(
				ScoreDataFetcher.INTERVIEW_LEVEL_SCORE_DATA_LOADER,
				this.skillScoreDataFetcher.createInterviewScoreDataLoader());
		registry.register(
				CodingPadDataFetcher.CODINGPAD_DATA_LOADER,
				this.codingPadDataFetcher.createCodingPadDataLoader());
		registry.register(
				ScoreDataFetcher.INTERVIEW_LEVEL_SCORE_DATA_LOADER,
				this.skillScoreDataFetcher.createInterviewScoreDataLoader());
		registry.register(
				CommentsDataFetcher.COMMENTS_FOR_ENTITY_DATA_LOADER,
				this.commentsDataFetcher.getCommentsForEntityDataLoader());

		this.dataLoaderFactories.forEach(
				x -> registry.register(x.dataLoaderName(), x.getDataLoader()));
		return registry;
	}

}
