/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.training;

import com.barraiser.common.graphql.input.training.TrainingSnippetInput;
import com.barraiser.common.graphql.input.training.TrainingTagInput;
import com.barraiser.common.graphql.types.training.JobRoleWithSnippetCount;
import com.barraiser.common.graphql.types.training.TrainingJobRole;
import com.barraiser.common.graphql.types.training.TrainingSnippet;
import com.barraiser.common.graphql.types.training.TrainingTag;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.*;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class TrainingSnippetManager {

	private final TrainingSnippetRepository snippetRepository;

	private final TrainingTagRepository trainingTagRepository;

	@Transactional(readOnly = true)
	public List<TrainingSnippet> getAllSnippetByRole(String jobRoleId) {
		List<TrainingSnippetDAO> snippetDAOList = snippetRepository.findByJobRoleId(jobRoleId);
		Set<String> tagIdSet = (snippetDAOList
				.stream()
				.filter(snippetDAO -> CollectionUtils.isNotEmpty(snippetDAO.getTagList()))
				.flatMap(snippetDAO -> snippetDAO.getTagList().stream())
				.collect(Collectors.toSet())).stream().map(TrainingTagMappingDAO::getTagId)
						.collect(Collectors.toSet());
		List<TrainingTagDAO> allTrainingTagsById = trainingTagRepository.findAllById(tagIdSet);
		Map<String, TrainingTagDAO> stringTrainingTagDAOMap = allTrainingTagsById.stream()
				.collect(Collectors.toMap(TrainingTagDAO::getId, Function.identity()));
		return snippetDAOList.stream().map(snippetDAO -> TrainingSnippet.builder()
				.id(snippetDAO.getId())
				.userId(snippetDAO.getUserId())
				.title(snippetDAO.getTitle())
				.description(snippetDAO.getDescription())
				.startTime(snippetDAO.getStartTime())
				.endTime(snippetDAO.getEndTime())
				.videoURL(snippetDAO.getVideoURL())
				.videoId(snippetDAO.getVideoId())
				.createdOn(snippetDAO.getUpdatedOn() == null ? snippetDAO.getCreatedOn() : snippetDAO.getUpdatedOn())
				.jobRoleList(snippetDAO.getJobRoleList().stream()
						.map(jobRole -> TrainingJobRole.builder().jobRoleId(jobRole.getJobRoleId()).build())
						.collect(Collectors.toList()))
				.tagList(CollectionUtils.isNotEmpty(snippetDAO.getTagList()) ? snippetDAO.getTagList().stream()
						.map(tag -> TrainingTag.builder()
								.id(stringTrainingTagDAOMap.get(tag.getTagId()).getId())
								.name(stringTrainingTagDAOMap.get(tag.getTagId()).getName())
								.build())
						.collect(Collectors.toList()) : Collections.emptyList())
				.build()).collect(Collectors.toList());
	}

	@Transactional
	public TrainingSnippetDAO saveSnippet(TrainingSnippetInput trainingSnippetInput, AuthenticatedUser user) {

		saveTrainingTags(trainingSnippetInput, user);

		TrainingSnippetDAO.TrainingSnippetDAOBuilder<?, ?> builder = TrainingSnippetDAO.builder()
				.userId(user.getUserName())
				.title(trainingSnippetInput.getTitle())
				.description(trainingSnippetInput.getDescription())
				.startTime(trainingSnippetInput.getStartTime())
				.endTime(trainingSnippetInput.getEndTime())
				.videoId(trainingSnippetInput.getVideoId())
				.videoURL(trainingSnippetInput.getVideoURL());

		if (StringUtils.isNotEmpty(trainingSnippetInput.getId())) {
			Optional<TrainingSnippetDAO> trainingSnippetDAO = snippetRepository.findById(trainingSnippetInput.getId());
			builder.id(trainingSnippetDAO.get().getId());
			builder.partnerId(trainingSnippetDAO.get().getPartnerId());

			builder.jobRoleList(trainingSnippetInput.getJobRoleList().stream()
					.map(trainingJobRoleInput -> TrainingJobRoleMappingDAO.builder()
							.trainingSnippetId(trainingSnippetDAO.get().getId())
							.id(UUID.randomUUID().toString())
							.jobRoleId(trainingJobRoleInput.getId())
							.build())
					.collect(Collectors.toList()));

			builder.tagList(trainingSnippetInput.getTagList().stream()
					.map(trainingTagDAO -> TrainingTagMappingDAO.builder()
							.id(UUID.randomUUID().toString())
							.trainingSnippetId(trainingSnippetDAO.get().getId())
							.tagId(trainingTagDAO.getId())
							.build())
					.collect(Collectors.toList()));
		} else {
			String snippetId = UUID.randomUUID().toString();
			builder.id(snippetId);
			builder.partnerId(trainingSnippetInput.getPartnerId());
			builder.jobRoleList(trainingSnippetInput.getJobRoleList().stream()
					.map(trainingJobRoleInput -> TrainingJobRoleMappingDAO.builder()
							.trainingSnippetId(snippetId)
							.id(UUID.randomUUID().toString())
							.jobRoleId(trainingJobRoleInput.getId())
							.build())
					.collect(Collectors.toList()));

			builder.tagList(trainingSnippetInput.getTagList().stream()
					.map(trainingTagDAO -> TrainingTagMappingDAO.builder()
							.id(UUID.randomUUID().toString())
							.trainingSnippetId(snippetId)
							.tagId(trainingTagDAO.getId())
							.build())
					.collect(Collectors.toList()));
		}

		return snippetRepository.save(builder.build());
	}

	private void saveTrainingTags(TrainingSnippetInput trainingSnippetInput, AuthenticatedUser user) {
		List<TrainingTagInput> filteredTrainingTagInputs = trainingSnippetInput.getTagList().stream()
				.filter(trainingTag -> StringUtils.isEmpty(trainingTag.getId()))
				.collect(Collectors.toList());

		filteredTrainingTagInputs.forEach(tag -> {
			tag.setId(UUID.randomUUID().toString());
		});

		trainingTagRepository.saveAll(filteredTrainingTagInputs.stream().map(trainingTag -> TrainingTagDAO.builder()
				.id(trainingTag.getId())
				.userId(user.getUserName())
				.partnerId(trainingSnippetInput.getPartnerId())
				.name(trainingTag.getName()).build()).collect(Collectors.toList()));
	}

	public Object getAllSnippetTagByName(TrainingTagInput trainingTagInput) {
		return trainingTagRepository.findByNameContainingIgnoreCaseAndPartnerId(trainingTagInput.getName(),
				trainingTagInput.getPartnerId());
	}

	public Boolean removeSnippet(String snippetId) {
		snippetRepository.deleteById(snippetId);
		return Boolean.TRUE;
	}

	public List<JobRoleWithSnippetCount> getAllJobRoleListWithCount(String partnerId) {
		return snippetRepository.findJobRoleCountByPartnerId(partnerId);
	}
}
