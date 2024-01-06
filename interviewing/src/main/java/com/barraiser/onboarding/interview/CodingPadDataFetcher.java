/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.types.CodingPad;
import com.barraiser.common.graphql.types.Interview;
import com.barraiser.onboarding.dal.InterviewPadDAO;
import com.barraiser.onboarding.dal.InterviewPadRepository;
import com.barraiser.onboarding.graphql.NamedDataFetcher;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.dataloader.DataLoader;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Component
@AllArgsConstructor
@Log4j2
public class CodingPadDataFetcher implements NamedDataFetcher {

	private final InterviewPadRepository interviewPadRepository;
	private final String CODING_PAD_LINK = "https://recruit.codejudge.io/candidate-interview-detail/%s?client_id=BarRaiser";
	public static final String CODINGPAD_DATA_LOADER = "CODINGPAD_DATA_LOADER";
	private final String INTERVIEW = "Interview";

	private static final Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	@Override
	public String name() {
		return "codingPad";
	}

	@Override
	public String type() {
		return "Interview";
	}

	public DataLoader<String, CodingPad> createCodingPadDataLoader() {
		return DataLoader.newMappedDataLoader(
				(Set<String> idsSet) -> CompletableFuture.supplyAsync(() -> getCodingPadDaoMap(idsSet), executor));
	}

	@Override
	public Object get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
		final GraphQLObjectType type = (GraphQLObjectType) dataFetchingEnvironment.getParentType();
		DataLoader<String, InterviewPadDAO> codingPadDataLoader = dataFetchingEnvironment
				.getDataLoader(CODINGPAD_DATA_LOADER);
		if (this.INTERVIEW.equals(type.getName())) {
			final Interview interview = dataFetchingEnvironment.getSource();
			return codingPadDataLoader.load(interview.getId());
		} else {
			throw new IllegalArgumentException(
					"Bad parent type while accessing CodingPad type, please fix your query");
		}
	}

	private Map<String, CodingPad> getCodingPadDaoMap(Set<String> idsSet) {

		final Map<String, CodingPad> codingPadDaoMap = new HashMap<>();
		final List<String> interviewIdsList = new ArrayList<>(idsSet);
		final List<Optional<InterviewPadDAO>> interviewPadDAOList = getInterviewPadDaoList(interviewIdsList);

		interviewPadDAOList.forEach(
				interviewPadDAO -> {
					final CodingPad codingPad = interviewPadDAO
							.map(
									padDAO -> CodingPad.builder()
											.link(
													getCodingPadLink(
															padDAO
																	.getIntervieweePad()))
											.build())
							.orElse(null);
					interviewPadDAO.map(
							padDAO -> codingPadDaoMap.put(padDAO.getInterviewId(), codingPad));
				});

		return codingPadDaoMap;
	}

	private String getCodingPadLink(String intervieweePad) {
		if (intervieweePad == null || intervieweePad.length() == 0)
			return null;
		final String[] splitArray = intervieweePad.split("[/ ?]+");
		return splitArray.length == 4 ? String.format(CODING_PAD_LINK, splitArray[2]) : null;
	}

	private List<Optional<InterviewPadDAO>> getInterviewPadDaoList(List<String> interviewIdsList) {
		final List<Optional<InterviewPadDAO>> interviewPadDAOList = new ArrayList<>();

		interviewIdsList.forEach(
				interviewId -> interviewPadDAOList.add(
						this.interviewPadRepository.findByInterviewId(interviewId)));
		return interviewPadDAOList;
	}
}
