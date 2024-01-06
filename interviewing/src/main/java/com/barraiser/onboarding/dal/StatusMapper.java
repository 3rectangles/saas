/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import com.barraiser.common.graphql.types.StatusType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Log4j2
@AllArgsConstructor
public class StatusMapper {
	ObjectMapper objectMapper;
	StatusRepository statusRepository;

	public StatusType toStatusType(final StatusDAO statusDAO) {
		return StatusType.builder()
				.id(statusDAO.getId())
				.displayStatus(statusDAO.getDisplayStatus())
				.internalStatus(statusDAO.getInternalStatus())
				.build();
	}

	public StatusType toStatusTypeFromId(final String id) {
		if (id != null) {
			Optional<StatusDAO> statusDAO = this.statusRepository.findById(id);
			if (statusDAO.isPresent()) {
				return this.toStatusType(statusDAO.get());
			}
			return StatusType.builder()
					.id(id)
					.displayStatus(id)
					.internalStatus(id)
					.build();
		}
		return StatusType.builder()
				.build();
	}

	public List<StatusType> toStatusTypes(final List<String> statusIds) {
		if (statusIds != null && !statusIds.isEmpty()) {
			return this.statusRepository.findAllByIdIn(statusIds)
					.stream().map(this::toStatusType)
					.collect(Collectors.toList());
		}

		return new ArrayList<>();
	}

	@SneakyThrows
	public String ExtractModelFromContext(final String context) {
		final Map<String, String> map = objectMapper.readValue(context, Map.class);
		return map.get("model");
	}

	@SneakyThrows
	public String ExtractTypeFromContext(final String context) {
		final Map<String, String> map = objectMapper.readValue(context, Map.class);
		return map.get("type");
	}

	public Boolean matchContext(final StatusDAO statusDAO, final String model, final String type) {
		String contextModel = this.ExtractModelFromContext(statusDAO.getContext());
		String contextType = this.ExtractTypeFromContext(statusDAO.getContext());
		if (contextModel != null && contextType != null) {
			return model.equals(contextModel) && type.equals(contextType);
		}
		return false;
	}
}
