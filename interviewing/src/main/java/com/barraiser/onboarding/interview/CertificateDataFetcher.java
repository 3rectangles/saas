/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.input.GetCertificateInput;
import com.barraiser.common.graphql.types.Certificate;
import com.barraiser.onboarding.candidate.CandidateInformationManager;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.graphql.DataFetcherType;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.onboarding.interview.certificate.CertificateDAO;
import com.barraiser.onboarding.interview.certificate.CertificateRepository;
import com.barraiser.onboarding.interview.jobrole.JobRoleManager;
import com.barraiser.onboarding.interview.status.EvaluationStatusManager;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class CertificateDataFetcher implements NamedDataFetcher {
	private final EvaluationRepository evaluationRepository;
	private final JobRoleManager jobRoleManager;
	private final DomainRepository domainRepository;
	private final GraphQLUtil graphQLUtil;
	private final CertificateImageGenerator certificateImageGenerator;
	private final CertificateRepository certificateRepository;
	private final EvaluationStatusManager evaluationStatusManager;
	private final CandidateInformationManager candidateInformationManager;

	@Override
	public Object get(final DataFetchingEnvironment dataFetchingEnvironment) throws Exception {

		final GetCertificateInput input = this.graphQLUtil.getInput(dataFetchingEnvironment, GetCertificateInput.class);

		if (input.getCertificateId() == null) {
			throw new IllegalArgumentException("CertificateId not found");
		}

		final EvaluationDAO evaluationDAO = this.evaluationRepository
				.findById(input.getCertificateId())
				.orElseThrow(
						() -> new IllegalArgumentException(
								"Certificate id is invalid. Does not exist"));
		final CandidateDAO candidateDetails = this.candidateInformationManager
				.getCandidate(evaluationDAO.getCandidateId());

		final JobRoleDAO jobRoleDAO = this.jobRoleManager
				.getJobRoleFromEvaluation(evaluationDAO)
				.orElseThrow(
						() -> new IllegalArgumentException(
								"No jobrole found for the given evaluation id."));
		final String domainId = jobRoleDAO.getDomainId();

		final DomainDAO domainDAO = this.domainRepository
				.findById(domainId)
				.orElseThrow(
						() -> new IllegalArgumentException(
								"No domain found for the evaluation"));

		final String domain = domainDAO.getName();
		final String name = candidateDetails.getFirstName()
				+ (candidateDetails.getLastName() != null
						? " " + candidateDetails.getLastName()
						: "");

		final Long issueDate = this.evaluationStatusManager.getBarRaiserStatusTransitionedTime(input.getCertificateId(),
				"Done");

		final String imageUrl = this.certificateImageGenerator.createAndUploadToS3(
				name, domain, issueDate, input.getCertificateId());

		final Certificate certificate = Certificate.builder()
				.candidateName(name)
				.domainName(domain)
				.issueDate(issueDate)
				.imageUrl(imageUrl)
				.build();

		this.certificateRepository.save(
				CertificateDAO.builder()
						.id(input.getCertificateId())
						.evaluationId(input.getCertificateId())
						.imageUrl(imageUrl)
						.build());

		return DataFetcherResult.newResult().data(certificate).build();
	}

	@Override
	public String name() {
		return "getCertificate";
	}

	@Override
	public String type() {
		return DataFetcherType.QUERY.getValue();
	}
}
