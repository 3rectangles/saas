/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.jobRoleManagement.JobRole.search;

import com.barraiser.elasticsearch.config.client.BarraiserElasticsearchClient;
import com.barraiser.onboarding.dal.JobRoleDAO;
import com.barraiser.onboarding.interview.jobrole.JobRoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;

import org.springframework.stereotype.Service;

import java.io.IOException;

@Deprecated
@Log4j2
@RequiredArgsConstructor
@Service
public class JobRoleElasticsearchManager {

	private final String JOB_ROLE_INDEX_NAME = "job-role-filter";
	private final BarraiserElasticsearchClient esClient;
	private final JobRoleMapper jobRoleMapper;

	public void updateJobRole(final String jobRoleDocumentId, final JobRoleDAO jobRoleDAO) throws IOException {

		final IndexRequest indexRequest = this.esClient.getIndexRequest(this.JOB_ROLE_INDEX_NAME, jobRoleDocumentId,
				this.jobRoleMapper.toJobRoleSearchDAO(jobRoleDAO));
		this.esClient.index(indexRequest, RequestOptions.DEFAULT);
	}

}
