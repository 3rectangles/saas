/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.user;

import com.barraiser.common.graphql.types.Company;
import com.barraiser.common.graphql.types.Interviewee;
import com.barraiser.common.graphql.types.JobRole;
import com.barraiser.common.graphql.types.UserDetails;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.dal.CompanyDAO;
import com.barraiser.onboarding.dal.CompanyRepository;
import com.barraiser.onboarding.dal.TargetJobAttributesDAO;
import com.barraiser.onboarding.dal.UserDetailsDAO;
import com.barraiser.onboarding.graphql.MultiParentTypeDataFetcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class CompaniesDataFetcher implements MultiParentTypeDataFetcher {
	public static final String INTERVIEWER = "Interviewer";
	public static final String TARGET_JOB_ATTRIBUTES = "TargetJobAttributes";
	public static final String INTERVIEWEE = "Interviewee";
	public static final String JOB_ROLE = "JobRole";
	private final CompanyRepository companyRepository;
	public static final String USER_DETAILS = "UserDetails";
	private final UserDetailsRepository userDetailsRepository;
	private final ObjectMapper objectMapper;

	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {
		final GraphQLObjectType parentType = (GraphQLObjectType) environment.getParentType();

		final List<String> companies;
		if (TARGET_JOB_ATTRIBUTES.equals(parentType.getName())) {
			final TargetJobAttributesDAO targetJobAttributesDAO = environment.getSource();
			companies = targetJobAttributesDAO.getCompanies();
		} else if (INTERVIEWER.equals(parentType.getName())) {
			final UserDetailsDAO userDetailsDAO = environment.getLocalContext();
			companies = userDetailsDAO == null ? null : userDetailsDAO.getLastCompanies();
		} else if (INTERVIEWEE.equals(parentType.getName())) {
			final Interviewee interviewee = environment.getSource();
			companies = interviewee == null ? null : interviewee.getLastCompaniesId();
		} else if (JOB_ROLE.equals(parentType.getName())) {
			final JobRole jobRole = environment.getSource();
			if (jobRole.getCompanyId() == null) {
				// In case of ATS created job roles, we are not adding these.
				throw new RuntimeException("No Company Id present for JoboRole:" + jobRole.getId());
			}
			final CompanyDAO companyDAO = this.companyRepository
					.findById(jobRole.getCompanyId())
					.orElseThrow(
							() -> new RuntimeException(
									"Company does not exist: "
											+ jobRole.getCompanyId()));
			return DataFetcherResult.newResult().data(companyDAO).build();
		} else if (USER_DETAILS.equals(parentType.getName())) {
			UserDetails userDetails = environment.getSource();
			UserDetailsDAO userDetailsDAO = this.userDetailsRepository
					.findById(userDetails.getUserName())
					.orElseThrow(
							() -> new RuntimeException(
									"User does not exist: "
											+ userDetails.getUserName()));
			companies = userDetailsDAO.getLastCompanies();
		} else if (QUERY_TYPE.equals(parentType.getName())) {
			return this.getAllCompanies();
		} else {
			throw new IllegalArgumentException("Bad parent type: " + parentType.getName());
		}

		final List<CompanyDAO> companyDAOS = companies == null
				? Collections.emptyList()
				: companies.stream()
						// TODO: Not efficient, do a batch get
						.map(x -> this.companyRepository.findById(x).orElse(null))
						.filter(Objects::nonNull)
						.collect(Collectors.toList());

		return DataFetcherResult.newResult().data(companyDAOS).build();
	}

	private DataFetcherResult<Object> getAllCompanies() {
		final List<CompanyDAO> companyDAOs = this.companyRepository.findAll();
		final List<Company> companyList = companyDAOs.stream().sorted(Comparator.comparing(CompanyDAO::getName))
				.map(companyDAO -> this.objectMapper.convertValue(companyDAO, Company.class))
				.collect(Collectors.toList());
		return DataFetcherResult.newResult()
				.data(companyList)
				.build();
	}

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(
				List.of(TARGET_JOB_ATTRIBUTES, "companies"),
				List.of(INTERVIEWER, "lastCompanies"),
				List.of(INTERVIEWEE, "lastCompanies"),
				List.of(JOB_ROLE, "company"),
				List.of(USER_DETAILS, "lastCompanies"),
				List.of(QUERY_TYPE, "getAllCompanies"));
	}
}
