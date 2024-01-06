/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.expert;

import com.barraiser.onboarding.dal.ExpertDAO;
import com.barraiser.onboarding.dal.ExpertHistoryDAO;
import com.barraiser.onboarding.dal.ExpertHistoryRepository;
import com.barraiser.onboarding.dal.ExpertRepository;
import com.barraiser.onboarding.interview.jira.expert.ExpertElasticSearchManager;
import com.barraiser.onboarding.search.dao.ExpertSearchDAO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class ExpertService {
	private final ExpertHistoryRepository expertHistoryRepository;
	private final ExpertRepository expertRepository;
	private final ObjectMapper objectMapper;
	private final ExpertElasticSearchManager expertElasticSearchManager;

	@Transactional
	public void save(final ExpertDAO expertDAO, final String createdBy) throws IOException {
		this.expertRepository.save(expertDAO);
		final ExpertHistoryDAO expertHistoryDAO = this.objectMapper.convertValue(expertDAO, ExpertHistoryDAO.class)
				.toBuilder()
				.id(UUID.randomUUID().toString())
				.expertId(expertDAO.getId())
				.createdBy(createdBy)
				.build();
		this.expertHistoryRepository.save(expertHistoryDAO);
		this.saveOnElasticSearch(expertDAO);
	}

	public Optional<ExpertDAO> findById(final String expertId) {
		return this.expertRepository.findById(expertId);
	}

	public void save(final ExpertDAO expertDAO) throws IOException {
		this.save(expertDAO, null);
	}

	private void saveOnElasticSearch(final ExpertDAO expertDAO) throws IOException {
		ExpertSearchDAO expertSearchDAO = this.expertElasticSearchManager.findById(expertDAO.getId());
		if (expertSearchDAO != null) {
			expertSearchDAO = this.populate(expertDAO, expertSearchDAO);
			this.expertElasticSearchManager.save(expertSearchDAO);
		}
	}

	private ExpertSearchDAO populate(final ExpertDAO expertDAO, final ExpertSearchDAO expertSearchDAO) {
		return expertSearchDAO.toBuilder()
				.minCostPerHour(expertDAO.getMinPrice())
				.build();
	}
}
