/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.user;

import com.barraiser.common.graphql.types.UserDetails;
import com.barraiser.onboarding.dal.UserDetailsDAO;
import com.barraiser.onboarding.dal.UserDetailsRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Log4j2
public class UserDetailsUtilService {
	private final UserDetailsRepository userDetailsRepository;

	public UserDetails getUserDetailsWithoutRoles(final String userId) {
		final UserDetailsDAO userDetailsDAO = this.userDetailsRepository.findById(userId).orElse(null);
		if (userDetailsDAO == null) {
			return null;
		}
		return this.mapUserDetails(userDetailsDAO);
	}

	public UserDetails mapUserDetails(final UserDetailsDAO userDetailsDAO) {
		return UserDetails.builder()
				.id(userDetailsDAO.getId())
				.almaMater(userDetailsDAO.getAlmaMater())
				.firstName(userDetailsDAO.getFirstName())
				.lastName(userDetailsDAO.getLastName())
				.phone(userDetailsDAO.getPhone())
				.email(userDetailsDAO.getEmail())
				.workExperienceInMonths(userDetailsDAO.getWorkExperienceInMonths())
				.currentCompanyName(userDetailsDAO.getCurrentCompanyName())
				.userName(userDetailsDAO.getId())
				.category(userDetailsDAO.getCategory())
				.whatsappNumber(userDetailsDAO.getWhatsappNumber())
				.build();
	}
}
