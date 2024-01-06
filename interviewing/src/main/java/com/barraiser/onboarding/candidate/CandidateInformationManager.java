/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.candidate;

import com.barraiser.onboarding.dal.CandidateDAO;
import com.barraiser.onboarding.dal.CandidateRepository;
import com.barraiser.onboarding.dal.UserDetailsDAO;
import com.barraiser.onboarding.dal.UserDetailsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Component
public class CandidateInformationManager {

	private final CandidateRepository candidateRepository;
	private final UserDetailsRepository userDetailsRepository;

	public CandidateDAO getCandidate(final String candidateId) {
		return this.candidateRepository.findById(candidateId).orElse(null);
	}

	public List<CandidateDAO> getCandidatesByUserId(final String userId) {
		return this.candidateRepository.findByUserId(userId);
	}

	public List<CandidateDAO> getCandidates(final Iterable<String> candidateIds) {
		return this.candidateRepository.findAllById(candidateIds);
	}

	public UserDetailsDAO getUserForCandidate(final String candidateId) {
		final String userId = this.candidateRepository.findById(candidateId).get().getUserId();

		if (userId != null) {
			return this.userDetailsRepository.findById(userId).get();
		}

		return null;
	}

	public void updateCandidate(final CandidateDAO candidateDAO) {
		this.candidateRepository.save(candidateDAO);
	}

	public Boolean isCandidateAnonymous(final String candidateId) {
		final CandidateDAO candidateDAO = this.candidateRepository.findById(candidateId).get();
		return (candidateDAO.getUserId() == null);
	}
}
