/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.input.LinkedinShareInput;
import com.barraiser.common.graphql.types.LinkedinShareResult;
import com.barraiser.onboarding.graphql.DataFetcherType;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.onboarding.interview.certificate.CertificateDAO;
import com.barraiser.onboarding.interview.certificate.CertificateRepository;
import com.barraiser.onboarding.linkedInService.LinkedinProcessor;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class ShareCertificateOnLInkedIn implements NamedDataFetcher {
	private final LinkedinProcessor linkedinProcessor;
	private final GraphQLUtil graphQLUtil;
	private final CertificateRepository certificateRepository;

	private final String REDIRECT_URL = "https://www.linkedin.com/feed/update/";

	@Override
	public String name() {
		return "linkedinShare";
	}

	@Override
	public String type() {
		return DataFetcherType.MUTATION.getValue();
	}

	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {

		final LinkedinShareInput input = this.graphQLUtil.getInput(environment, LinkedinShareInput.class);

		final String certificateId = input.getCertificateId();
		final CertificateDAO certificateDAO = this.certificateRepository
				.findById(certificateId)
				.orElseThrow(
						() -> new IllegalArgumentException(
								"Certificate id is invalid. Does not exist"));
		final String imageUrl = certificateDAO.getImageUrl();
		if (input.getCode() == null) {
			throw new IllegalArgumentException("No code found");
		}

		final String accessToken = this.linkedinProcessor.getAccessToken(input.getCode());

		final String candidateUrn = this.linkedinProcessor.getUrn(accessToken);

		final String id = this.linkedinProcessor.shareCandidatePost(
				accessToken, candidateUrn, imageUrl, certificateId);

		final String redirectUrl = this.REDIRECT_URL + id;

		log.info("Certificate shared on linkedin {}", redirectUrl);
		final LinkedinShareResult linkedinShareResult = LinkedinShareResult.builder().redirectUrl(redirectUrl).build();

		return DataFetcherResult.newResult().data(linkedinShareResult).build();
	}
}
