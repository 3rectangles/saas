/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.interviewer;

import com.barraiser.common.graphql.types.Interviewer;
import com.barraiser.onboarding.dal.ExpertDAO;
import com.barraiser.onboarding.dal.UserDetailsDAO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class InterviewerMapper {

	public Interviewer toInterviewer(final UserDetailsDAO userDetailsDAO, final ExpertDAO expertDAO) {
		return Interviewer.builder()
				.id(userDetailsDAO.getId())
				.designation(userDetailsDAO.getDesignation())
				.currentCompanyName(userDetailsDAO.getCurrentCompanyName())
				.initials(userDetailsDAO.getInitials())
				.almaMater(userDetailsDAO.getAlmaMater())
				.designation(userDetailsDAO.getDesignation())
				.workExperienceInMonths(userDetailsDAO.getWorkExperienceInMonths())
				.expertDomains(userDetailsDAO.getExpertDomains())
				.peerDomains(userDetailsDAO.getPeerDomains())
				.tenantId(expertDAO.getTenantId())
				.build();
	}

}
