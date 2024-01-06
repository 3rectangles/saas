/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.resume;

import com.barraiser.common.graphql.input.UserDetailsFromResumeInput;
import com.barraiser.common.graphql.types.UserDetails;
import com.barraiser.common.graphql.types.UserDetailsForResume;
import com.barraiser.onboarding.document.DocumentRepository;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.onboarding.resume.dto.ParsedResumeDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
@Log4j2
public class ParseResumesDataFetcher implements NamedDataFetcher {
	private final GraphQLUtil graphQLUtil;
	private final ResumeParser resumeParser;
	private final ParsedResumeStorage parsedResumeStorage;
	private final DocumentRepository documentRepository;
	private final ObjectMapper objectMapper;

	@Override
	public String name() {
		return "getUserDetailsFromResume";
	}

	@Override
	public String type() {
		return QUERY_TYPE;
	}

	@Transactional
	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {
		final List<UserDetailsFromResumeInput> input = List
				.of(this.graphQLUtil.getInput(environment, UserDetailsFromResumeInput[].class));

		final List<UserDetailsForResume> userDetailsForResumes = new ArrayList<>();

		input.forEach(resume -> {
			final String resumeUrl = this.getResumeUrl(resume.getDocumentId());
			final UserDetailsForResume userDetailsForResume = UserDetailsForResume.builder()
					.serialId(resume.getSerialId())
					.documentId(resume.getDocumentId())
					.userDetails(UserDetails.builder().build())
					.build();
			try {
				userDetailsForResumes.add(userDetailsForResume.toBuilder()
						.userDetails(parseResume(resumeUrl, resume.getDocumentId()))
						.build());

			} catch (Exception e) {
				userDetailsForResumes.add(userDetailsForResume);
			}
		});
		return DataFetcherResult.newResult().data(userDetailsForResumes).build();
	}

	public UserDetails parseResume(final String resumeUrl, final String documentId) {
		final String parsedResumeJsonString = this.resumeParser.parseResumeToJSONString(resumeUrl);
		final ParsedResumeDTO parsedResume = this.objectMapper.convertValue(this.resumeParser.parseResume(resumeUrl),
				ParsedResumeDTO.class);
		this.storeParsedResume(documentId, parsedResumeJsonString, parsedResume);
		final String email = parsedResume.getEmails() != null && parsedResume.getEmails().size() > 0
				? parsedResume.getEmails().get(0).getEmailAddress()
				: null;
		final String phone = parsedResume.getPhoneNumbers() != null && parsedResume.getPhoneNumbers().size() > 0
				? parsedResume.getPhoneNumbers().get(0).getFormattedNumber()
				: null;
		final String isdCode = parsedResume.getPhoneNumbers() != null && parsedResume.getPhoneNumbers().size() > 0
				? parsedResume.getPhoneNumbers().get(0).getIsdCode()
				: null;

		Integer workExperience = null;
		try {
			workExperience = Integer.parseInt(parsedResume.getWorkedPeriod().getTotalExperienceInMonths());
		} catch (final NumberFormatException e) {
			log.info("work experience could not be parsed");
		}
		return UserDetails.builder()
				.userName(parsedResume.getName() != null ? parsedResume.getName().getFullName() : null)
				.email(email)
				.phone(phone)
				.isdCode(isdCode)
				.workExperienceInMonths(workExperience)
				.resumeUrl(resumeUrl)
				.build();
	}

	public void storeParsedResume(final String documentId, final String parsedResumeJsonString,
			final ParsedResumeDTO parsedResume) {
		try {
			this.parsedResumeStorage.saveParsedResume(documentId, parsedResume, parsedResumeJsonString);
		} catch (Exception e) {
			log.warn(e, e);
		}
	}

	public String getResumeUrl(final String documentId) {
		try {
			return this.documentRepository.findById(documentId).get().getFileUrl();
		} catch (Exception e) {
			log.error(String.format("Resume does not exist for document id %s in documents table", documentId));
		}
		return null;
	}
}
