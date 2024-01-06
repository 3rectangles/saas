/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.config;

import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.commons.auth.feign.AuthorizationServiceFeignClient;
import com.barraiser.onboarding.dal.PartnerCompanyDAO;
import com.barraiser.onboarding.dal.PartnerCompanyRepository;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class PartnerConfigContextConstructor {

	private final PartnerCompanyRepository partnerCompanyRepository;
	private final AuthorizationServiceFeignClient authorizationServiceFeignClient;

	private final List<String> userRolePriorityLowToHigh = List.of("INTERVIEWER", "COLLABORATOR", "PARTNER_SUPER_ADMIN",
			"OPS", "ADMIN");

	/**
	 * @param fetchConfigInput
	 * @return
	 */
	public List<String> getContextTags(final String partnerId, final Map<String, Object> fetchConfigInput,
			final AuthenticatedUser authenticatedUser) {

		final List<String> resultantContextTags = new ArrayList<>();
		final List<String> userInputContextTags = (List<String>) fetchConfigInput.getOrDefault("context",
				Arrays.asList());
		final List<String> mandatoryContextTags = this.getMandatoryContextTags(partnerId, fetchConfigInput,
				authenticatedUser);

		resultantContextTags.addAll(mandatoryContextTags);
		resultantContextTags.addAll(userInputContextTags);

		// NOTE: User role tags are added at the end as they are of the highest priorty.
		// Their keys should
		// win incase of collission to ensure a user is never authorized for anything
		// more.
		resultantContextTags.addAll(this.getUserRoleContextTag(partnerId, authenticatedUser));

		return resultantContextTags;
	}

	/**
	 * NOTE : Order of tags in the list determines their precedence.
	 * The first one being the lowest precedence
	 * <p>
	 * So if there is a colliding key against two tags t1 , t2 and the contexts tags
	 * list looks like [t1,t2]
	 * The valoe of t2 will override that of t1.
	 */
	public List<String> getMandatoryContextTags(final String partnerId, final Map<String, Object> fetchConfigInput,
			final AuthenticatedUser authenticatedUser) {
		final List<String> mandatoryContextTags = new ArrayList<>();
		final String partnershipTypeTag = this.getPartnershipModelContextTag(partnerId);

		// 1: Get partnership model tag
		if (partnershipTypeTag != null) {
			mandatoryContextTags.add(this.getPartnershipModelContextTag(partnerId));
		}

		// 2. Get partner tag
		mandatoryContextTags.add(this.getPartnerContextTag(partnerId));

		// TODO: Add environment tag. Not tying up environment to profile of backend
		// server right now. Maybe front end can pass the environment tag by itself.
		return mandatoryContextTags;
	}

	private String getPartnerContextTag(final String partnerId) {
		return "partner_id." + partnerId;
	}

	private String getPartnershipModelContextTag(final String partnerId) {
		final PartnerCompanyDAO partnerCompanyDAO = this.partnerCompanyRepository.findById(partnerId).get();
		return partnerCompanyDAO.getPartnershipModelId() != null
				? "partnership_model." + partnerCompanyDAO.getPartnershipModelId()
				: null;
	}

	private List<String> getUserRoleContextTag(final String partnerId, final AuthenticatedUser authenticatedUser) {

		// Get the ids of all the roles assumed by the user
		List<String> userRoleIds = this.authorizationServiceFeignClient
				.getActiveUserRoles(partnerId, authenticatedUser.getUserName())
				.stream().map(r -> r.getName()).collect(Collectors.toList());

		// Sorting the user assumed roles by priority
		userRoleIds.sort(Comparator.comparingInt(this.userRolePriorityLowToHigh::indexOf));

		return userRoleIds.stream().map(r -> "user_role." + r).collect(Collectors.toList());
	}

}
