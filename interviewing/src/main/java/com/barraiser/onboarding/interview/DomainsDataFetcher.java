/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.types.*;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.graphql.MultiParentTypeDataFetcher;
import com.barraiser.common.graphql.types.SkillInterviewingConfiguration.SkillInterviewingConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
@Log4j2
public class DomainsDataFetcher implements MultiParentTypeDataFetcher {
	private static final String USER_DETAILS = "UserDetails";
	private final DomainRepository domainRepository;
	private final ObjectMapper objectMapper;
	private final String JOB_ROLE = "JobRole";
	private final String INTERVIEWER = "Interviewer";
	private final ExpertRepository expertRepository;
	private final SkillRepository skillRepository;

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(
				List.of("SkillInterviewingConfiguration", "domain"),
				List.of("Query", "getDomains"),
				List.of(this.JOB_ROLE, "domain"),
				List.of(USER_DETAILS, "expertDomains"),
				List.of(USER_DETAILS, "peerDomains"),
				List.of("InterviewStructure", "domain"),
				List.of("Skill", "domain"));
	}

	public static final String DOMAIN_DATA_LOADER = "DOMAIN_DATA_LOADER";

	private static final Executor executor = Executors
			.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	public DataLoader<String, DomainDAO> createDomainDataLoader() {
		return DataLoader.newMappedDataLoader((Set<String> idsSet) -> CompletableFuture.supplyAsync(() -> {
			final Map<String, DomainDAO> domainDAOMap = new HashMap<>();
			final List<DomainDAO> domainDAOs = this.domainRepository.findAllByIdIn(new ArrayList<>(idsSet));
			domainDAOs.forEach(domainDAO -> {
				domainDAOMap.put(domainDAO.getId(), domainDAO);
			});
			idsSet.forEach(id -> {
				if (!domainDAOMap.containsKey(id)) {
					log.error("Domain does not exist: " + id);
				}
			});
			return domainDAOMap;
		}, executor));
	}

	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {
		final GraphQLObjectType type = (GraphQLObjectType) environment.getParentType();
		DataLoader<String, DomainDAO> domainDataLoader = environment.getDataLoader(DOMAIN_DATA_LOADER);

		if (this.JOB_ROLE.equals(type.getName())) {
			final JobRole jobRole = environment.getSource();
			return jobRole.getDomainId() != null ? domainDataLoader.load(jobRole.getDomainId()) : null;

		} else if (USER_DETAILS.equals(type.getName())) {
			UserDetails userDetails = environment.getSource();
			ExpertDAO expertDAO = this.expertRepository.findById(userDetails.getUserName())
					.orElseThrow(() -> new RuntimeException("Expert does not exist: " + userDetails.getUserName()));

			final List<String> domains;

			if (environment.getFieldDefinition().getName().equals("expertDomains")) {
				domains = expertDAO.getExpertDomains();
			} else {
				domains = expertDAO.getPeerDomains();
			}

			final List<DomainDAO> domainDAOS = domains == null ? Collections.emptyList()
					: domains.stream()
							.map(x -> this.domainRepository.findById(x).orElse(null))
							.filter(Objects::nonNull)
							.collect(Collectors.toList());

			return DataFetcherResult.newResult()
					.data(domainDAOS)
					.build();

		} else if (type.getName().equals("Query")) {
			final List<DomainDAO> domainDAOs;
			domainDAOs = this.domainRepository.findAll();
			final List<Domain> domains = domainDAOs.stream().map(domainDAO -> {
				final Domain domain = this.objectMapper.convertValue(domainDAO, Domain.class);
				return domain;
			}).collect(Collectors.toList());

			return DataFetcherResult.newResult()
					.data(domains)
					.build();
		} else if (type.getName().equals(this.INTERVIEWER)) {
			return DataFetcherResult.newResult()
					.data(this.domainRepository.findAll())
					.build();
		} else if (type.getName().equals("InterviewStructure")) {
			final InterviewStructure interviewStructure = environment.getSource();
			return DataFetcherResult.newResult()
					.data(interviewStructure.getDomainId() == null ? null
							: this.domainRepository.findById(interviewStructure.getDomainId()))
					.build();
		} else if (type.getName().equals("Skill")) {
			final Skill skill = environment.getSource();
			final String domainId = this.skillRepository.findById(skill.getId()).get().getDomain();
			return DataFetcherResult.newResult()
					.data(this.domainRepository.findById(domainId))
					.build();
		} else {
			throw new IllegalArgumentException("Bad parent type while accessing domain type, please fix your query");
		}
	}
}
