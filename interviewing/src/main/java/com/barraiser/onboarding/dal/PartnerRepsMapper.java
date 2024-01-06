/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import com.barraiser.common.graphql.types.PartnerRepDetails;
import com.barraiser.common.graphql.types.UserDetails;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@AllArgsConstructor
public class PartnerRepsMapper {

	UserDetailsRepository userDetailsRepository;
	PartnerRepsRepository partnerRepsRepository;

	public PartnerRepDetails toPartnerRepDetails(final PartnerRepsDAO partnerRepsDAO) {
		final Optional<UserDetailsDAO> userDetailsDAO = this.userDetailsRepository
				.findById(partnerRepsDAO.getPartnerRepId());

		final List<String> locations = partnerRepsDAO.getLocations() != null
				? Arrays.stream(partnerRepsDAO.getLocations().split(",")).collect(Collectors.toList())
				: List.of();

		final List<String> teams = partnerRepsDAO.getTeams() != null
				? Arrays.stream(partnerRepsDAO.getTeams().split(",")).collect(Collectors.toList())
				: List.of();

		UserDetails userDetails = UserDetails.builder().build();
		if (userDetailsDAO.isPresent()) {
			userDetails = userDetails.toBuilder()
					.id(userDetailsDAO.get().getId())
					.userName(userDetailsDAO.get().getFirstName())
					.email(userDetailsDAO.get().getEmail())
					.phone(userDetailsDAO.get().getPhone())
					.firstName(userDetailsDAO.get().getFirstName())
					.lastName(userDetailsDAO.get().getLastName())
					.build();
		}

		return PartnerRepDetails.builder()
				.partnerId(partnerRepsDAO.getPartnerId())
				.accessGrantedOn(
						partnerRepsDAO.getCreatedOn() == null ? null : partnerRepsDAO.getCreatedOn().getEpochSecond())
				.userDetails(userDetails)
				.locations(locations)
				.teams(teams)
				.roles(partnerRepsDAO.getPartnerRoles())
				.build();
	}

	public List<PartnerRepDetails> toPartnerRepDetailsList(final List<String> partnerRepsIds, final String partnerId) {

		List<PartnerRepDetails> partnerRepDetails = new ArrayList<>();
		if (partnerRepsIds != null && !partnerRepsIds.isEmpty()) {
			partnerRepDetails = partnerRepsIds.stream()
					.flatMap(id -> {
						Optional<PartnerRepsDAO> partnerRep = this.partnerRepsRepository
								.findByPartnerRepIdAndPartnerId(id, partnerId);
						if (partnerRep.isPresent()) {
							return Stream.of(this.toPartnerRepDetails(partnerRep.get()));
						} else {
							return Stream.of(PartnerRepDetails.builder().build());
						}
					})
					.collect(Collectors.toList());
		}

		return partnerRepDetails;
	}
}
