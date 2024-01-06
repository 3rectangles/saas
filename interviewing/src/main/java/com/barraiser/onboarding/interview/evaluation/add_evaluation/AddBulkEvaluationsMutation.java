/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.add_evaluation;

import com.barraiser.common.graphql.input.AddBulkEvaluationsCandidateInput;
import com.barraiser.common.graphql.input.AddBulkEvaluationsInput;
import com.barraiser.common.graphql.types.AddBulkEvaluationsError;
import com.barraiser.common.graphql.types.AddBulkEvaluationsResult;
import com.barraiser.common.monitoring.Profiled;
import com.barraiser.common.utilities.EmailParser;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.commons.auth.UserRole;
import com.barraiser.onboarding.auth.AuthenticationException;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.onboarding.interview.evaluation.search.dal.EvaluationSearchRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@Log4j2
@Component
@AllArgsConstructor
public class AddBulkEvaluationsMutation implements NamedDataFetcher {
	private final GraphQLUtil graphQLUtil;
	private final ObjectMapper objectMapper;
	private final JobRoleRepository jobRoleRepository;
	private final PartnerRepsRepository partnerRepsRepository;
	private final PartnerCompanyRepository partnerCompanyRepository;
	private final AddEvaluation addEvaluation;
	private final ResumeUrlProcessor resumeUrlProcessor;

	private final EvaluationSearchRepository evaluationSearchRepository;
	final static String GENERIC_ERROR_RESPONSE = "There was some technical error. Please contact support.";
	final static String MORE_THAN_ALLOWED_ERROR_RESPONSE = "You don't have enough credits for this request. You have credits for $count additional candidates. Please contact support to get more credits";

	@Override
	public String name() {
		return "addBulkEvaluations";
	}

	@Override
	public String type() {
		return MUTATION_TYPE;
	}

	@Profiled(name = "addCandidate")
	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {
		final AddBulkEvaluationsInput input = this.graphQLUtil.getInput(environment, AddBulkEvaluationsInput.class);

		final AuthenticatedUser authenticatedUser = this.getAuthorizedUser(environment, input);
		final JobRoleDAO jobRoleDAO = validateInfo(input);
		final List<AddBulkEvaluationsResult> results = new ArrayList<>();

		input.getCandidates().forEach(candidateInput -> {
			final AddEvaluationProcessingData data = addCandidate(input, authenticatedUser, jobRoleDAO, candidateInput);
			results.add(data.getResult().toBuilder().serialId(candidateInput.getSerialId()).build());
		});

		return DataFetcherResult.newResult().data(results).build();
	}

	public AddEvaluationProcessingData addCandidate(final AddBulkEvaluationsInput input,
			final AuthenticatedUser authenticatedUser, final JobRoleDAO jobRoleDAO,
			final AddBulkEvaluationsCandidateInput candidateInput) {
		final AddEvaluationProcessingData data = this.objectMapper.convertValue(candidateInput,
				AddEvaluationProcessingData.class);
		String resumeUrl;
		data.setJobRoleDAO(jobRoleDAO);
		data.setPocEmail(input.getPocEmail());
		data.setResult(AddBulkEvaluationsResult.builder().success(true).build());
		data.setAuthenticatedUser(authenticatedUser);
		data.setIsAddedViaCalendarInterception(Boolean.FALSE);
		try {
			resumeUrl = this.resumeUrlProcessor.getResumeUrl(data.getUserId(), data.getDocumentId(),
					data.getDocumentLink(), null);
		} catch (Exception e) {
			log.error("Error while adding candidate : ", e);
			return getUnsuccessfulEvaluationProcessingData(data, e.getMessage());
		}

		data.setResumeUrl(resumeUrl);

		try {
			this.addEvaluation.add(data);
		} catch (final Exception e) {
			log.error("Error while adding candidate : ", e);
			return getUnsuccessfulEvaluationProcessingData(data, GENERIC_ERROR_RESPONSE);
		}
		return data;
	}

	private JobRoleDAO validateInfo(final AddBulkEvaluationsInput input) throws Exception {
		final JobRoleDAO jobRoleDAO = this.jobRoleRepository
				.findTopByEntityIdIdOrderByEntityIdVersionDesc(input.getJobRoleId()).orElseThrow(
						() -> new IllegalArgumentException(GENERIC_ERROR_RESPONSE));

		if (jobRoleDAO.getDeprecatedOn() != null || Boolean.TRUE.equals(jobRoleDAO.getIsDraft())) {
			throw new IllegalArgumentException("The Job Role is inactive. Kindly Contact Barraiser Team for Support");
		}
		final PartnerCompanyDAO partnerCompany = this.partnerCompanyRepository.findById(input.getPartnerId())
				.orElseThrow(
						() -> new IllegalArgumentException(GENERIC_ERROR_RESPONSE));

		if (!partnerCompany.getCompanyId().equals(jobRoleDAO.getCompanyId())) {
			throw new IllegalArgumentException(GENERIC_ERROR_RESPONSE);
		}
		// checking if company has credit quota left
		if (partnerCompany.getTotalUploadAllowed() != null) {
			int countRequired = input.getCandidates().size();

			long evalutionCount = evaluationSearchRepository
					.countAllNotCancelledByCompanyId(partnerCompany.getCompanyId());
			int totalUploadAllowed = (int) (partnerCompany.getTotalUploadAllowed() * 1.2);
			if (evalutionCount + countRequired > totalUploadAllowed) {
				final Map<String, String> emailData = new HashMap<>();
				final String emailBody = "Poc: " + input.getPocEmail()
						+ " of customerId: "
						+ partnerCompany.getCompanyId()
						+ " was not able to upload candidates, "
						+ "as they dont have enough "
						+ "credits: " + partnerCompany.getTotalUploadAllowed();
				emailData.put("body", emailBody);
				final List<String> toEmail = new ArrayList<>();
				toEmail.add("sales@barraiser.com");

				// Create a JSON object with the required fields
				final JSONObject payload = new JSONObject();
				payload.put("subject", "More Credits required: " + partnerCompany.getCompanyId());
				payload.put("body", emailBody);

				// Send the email to sales@barraiser.com through the Zapier hook
				final String zapierHookURL = "https://hooks.zapier.com/hooks/catch/10879858/34ghcjb/";
				HttpURLConnection connection = (HttpURLConnection) new URL(zapierHookURL).openConnection();
				connection.setRequestMethod("POST");
				connection.setDoOutput(true);
				connection.setRequestProperty("Content-Type", "application/json");
				OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
				writer.write(payload.toString());
				writer.flush();

				int responseCode = connection.getResponseCode();
				InputStream inputStream;
				if (responseCode >= 200 && responseCode < 400) {
					inputStream = connection.getInputStream();
				} else {
					inputStream = connection.getErrorStream();
				}

				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
				StringBuilder response = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					response.append(line);
				}
				reader.close();

				throw new IllegalArgumentException(MORE_THAN_ALLOWED_ERROR_RESPONSE.replace("$count",
						String.valueOf(Math.max(totalUploadAllowed - evalutionCount, 0))));
			}
		}

		try {
			this.validatePocEmails(input.getPocEmail());
		} catch (final Exception e) {
			throw new IllegalArgumentException("POC Email not following proper format: " + input.getPocEmail());
		}
		return jobRoleDAO;
	}

	private AuthenticatedUser getAuthorizedUser(final DataFetchingEnvironment environment,
			final AddBulkEvaluationsInput input) {
		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);
		final Boolean isAuthorized = this.isAuthorized(authenticatedUser, input.getPartnerId());

		if (!isAuthorized) {
			throw new AuthenticationException(GENERIC_ERROR_RESPONSE);
		}
		return authenticatedUser;
	}

	private Boolean isAuthorized(final AuthenticatedUser authenticatedUser, final String partnerId) {
		boolean isAuthorized = false;

		if (authenticatedUser.getRoles().contains(UserRole.ADMIN)) {
			isAuthorized = true;
		} else {
			if (authenticatedUser.getRoles().contains(UserRole.PARTNER)) {
				final Optional<PartnerRepsDAO> partnerRep = this.partnerRepsRepository
						.findByPartnerRepIdAndPartnerId(authenticatedUser.getUserName(), partnerId);

				if (partnerRep.isPresent()) {
					isAuthorized = true;
				}
			} else if (authenticatedUser.getRoles().contains(UserRole.OPS)) {
				isAuthorized = true;
			}
		}
		return isAuthorized;
	}

	private void validatePocEmails(final String pocEmails) {
		Arrays.stream(pocEmails.split(","))
				.map(String::trim)
				.forEach(EmailParser::validateEmail);
	}

	private AddEvaluationProcessingData getUnsuccessfulEvaluationProcessingData(
			final AddEvaluationProcessingData data, final String errorResponse) {
		data.setResult(AddBulkEvaluationsResult.builder()
				.success(false)
				.errors(List.of(AddBulkEvaluationsError.builder().error(errorResponse).build()))
				.build());
		return data;
	}

}
