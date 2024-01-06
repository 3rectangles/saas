/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.user.expert;

import com.barraiser.onboarding.dal.ExpertDAO;
import com.barraiser.onboarding.dal.ExpertRepository;
import com.barraiser.onboarding.user.expert.dto.ExpertDetails;
import com.barraiser.onboarding.user.expert.mapper.ExpertMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class ExpertDBManager {

	private final ExpertRepository expertRepository;
	private final ExpertMapper expertMapper;

	public ExpertDAO getExpert(final String expertId) {
		return this.expertRepository.findById(expertId).orElse(null);
	}

	public ExpertDAO getOrCreateExpertById(final String expertId) {
		final ExpertDAO expertDAO = this.expertRepository.findById(expertId)
				.orElse(ExpertDAO.builder().id(expertId).build());
		this.expertRepository.save(expertDAO);

		return expertDAO;
	}

	public void updateExpertDetails(final ExpertDetails details) {
		this.expertRepository.save(this.expertMapper.toExpertDAO(details));
	}

}
