/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.jobrole;

import com.amazonaws.services.iot.model.Job;
import com.barraiser.common.dal.VersionedEntityId;
import com.barraiser.common.graphql.types.JobRole;
import com.barraiser.onboarding.dal.JobRoleRepository;
import com.barraiser.onboarding.graphql.DataLoaderFactory;
import lombok.AllArgsConstructor;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class JobRoleDataLoaderFactory implements DataLoaderFactory<JobRoleCriteria, JobRole> {
	public static final String JOB_ROLE_DATA_LOADER = "JOB_ROLE_DATA_LOADER";
	private static final Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	private final JobRoleRepository jobRoleRepository;
	private final JobRoleMapper jobRoleMapper;

	@Override
	public String dataLoaderName() {
		return JOB_ROLE_DATA_LOADER;
	}

	@Override
	public DataLoader<JobRoleCriteria, JobRole> getDataLoader() {
		return DataLoader.newMappedDataLoader(
				(Set<JobRoleCriteria> criterias) -> CompletableFuture.supplyAsync(
						() -> getData(criterias),
						executor));
	}

	@Override
	public Map<JobRoleCriteria, JobRole> getData(final Set<JobRoleCriteria> entities) {
		final List<VersionedEntityId> versionedEntityIds = entities
				.stream()
				.map(x -> new VersionedEntityId(x.getJobRoleId(), x.getVersion()))
				.collect(Collectors.toList());

		final List<JobRole> jobRoles = this.jobRoleRepository.findAllByEntityIdIn(
				versionedEntityIds)
				.stream().map(this.jobRoleMapper::toJobRole)
				.collect(Collectors.toList());

		final Map<JobRoleCriteria, JobRole> jobRoleMap = new HashMap<>();
		entities.forEach(x -> jobRoleMap.put(x,
				jobRoles.stream()
						.filter(y -> y.getId().equals(x.getJobRoleId()) && y.getVersion().equals(x.getVersion()))
						.findFirst().get()));
		return jobRoleMap;
	}
}
