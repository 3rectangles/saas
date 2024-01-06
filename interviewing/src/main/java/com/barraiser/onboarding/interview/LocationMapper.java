/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.types.Location;
import com.barraiser.onboarding.interview.jobrole.dal.LocationDAO;
import com.barraiser.onboarding.interview.jobrole.dal.LocationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Log4j2
@AllArgsConstructor
public class LocationMapper {
	LocationRepository locationRepository;

	public Location toLocation(final LocationDAO locationDAO) {
		return Location.builder()
				.id(locationDAO.getId())
				.atsId(locationDAO.getAtsId())
				.creationSource(locationDAO.getCreationSource())
				.description(locationDAO.getDescription())
				.name(locationDAO.getName())
				.sourceMeta(locationDAO.getCreationSourceMeta())
				.partnerId(locationDAO.getPartnerId())
				.build();
	}

	public List<Location> toLocations(final List<String> locationIds) {
		if (locationIds != null && !locationIds.isEmpty()) {
			return this.locationRepository.findAllByIdIn(locationIds)
					.stream().map(this::toLocation)
					.collect(Collectors.toList());
		}

		return new ArrayList<>();
	}
}
