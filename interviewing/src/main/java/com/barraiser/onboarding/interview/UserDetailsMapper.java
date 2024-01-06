/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.types.UserDetails;
import com.barraiser.onboarding.dal.UserDetailsDAO;
import com.barraiser.onboarding.dal.UserDetailsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class UserDetailsMapper {
	UserDetailsRepository userDetailsRepository;

	public UserDetails getUserDetails(final UserDetailsDAO userDetailsDAO) {
		return UserDetails.builder()
				.id(userDetailsDAO.getId())
				.userName(userDetailsDAO.getId())
				.firstName(userDetailsDAO.getFirstName())
				.lastName(userDetailsDAO.getLastName())
				.email(userDetailsDAO.getEmail())
				.currentCompanyName(userDetailsDAO.getCurrentCompanyName())
				.workExperienceInMonths(userDetailsDAO.getWorkExperienceInMonths())
				.category(userDetailsDAO.getCategory())
				.phone(userDetailsDAO.getPhone())
				.build();
	}

	public List<UserDetails> toUserDetailsList(final List<String> userIds) {
		if (userIds != null && !userIds.isEmpty()) {
			return this.userDetailsRepository.findAllByIdIn(userIds)
					.stream().map(this::getUserDetails)
					.collect(Collectors.toList());
		}

		return new ArrayList<>();
	}

}
