/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.user.candidate.mapper;

import com.barraiser.common.graphql.types.Interviewee;
import com.barraiser.onboarding.dal.CandidateDAO;
import com.barraiser.onboarding.dal.UserDetailsDAO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class IntervieweeMapper {

	public Interviewee toInterviewee(final UserDetailsDAO userDetailsDAO) {
		return Interviewee.builder()
				.id(userDetailsDAO.getId())
				.firstName(userDetailsDAO.getFirstName())
				.lastName(userDetailsDAO.getLastName())
				.email(userDetailsDAO.getEmail())
				.designation(userDetailsDAO.getDesignation())
				.linkedInProfile(userDetailsDAO.getLinkedInProfile())
				.lastCompaniesId(userDetailsDAO.getLastCompanies())
				.almaMater(userDetailsDAO.getAlmaMater())
				.workExperienceInMonths(userDetailsDAO.getWorkExperienceInMonths())
				.currentCompanyName(userDetailsDAO.getCurrentCompanyName())
				.redactedResumeUrl(userDetailsDAO.getRedactedResumeUrl())
				.timezone(userDetailsDAO.getTimezone())
				.phone(userDetailsDAO.getPhone())
				.whatsappNumber(userDetailsDAO.getWhatsappNumber())
				.resumeUrl(userDetailsDAO.getResumeUrl())
				.build();
	}

	public Interviewee toInterviewee(final CandidateDAO candidateDAO, final UserDetailsDAO userDetailsDAO) {
		return Interviewee.builder()
				.email(userDetailsDAO.getEmail())
				.phone(userDetailsDAO.getPhone())
				.whatsappNumber(userDetailsDAO.getWhatsappNumber())
				.id(candidateDAO.getId())
				.firstName(candidateDAO.getFirstName())
				.lastName(candidateDAO.getLastName())
				.designation(candidateDAO.getDesignation())
				.linkedInProfile(candidateDAO.getLinkedInProfile())
				.lastCompaniesId(candidateDAO.getLastCompanies())
				.almaMater(candidateDAO.getAlmaMater())
				.workExperienceInMonths(candidateDAO.getWorkExperienceInMonths())
				.currentCompanyName(candidateDAO.getCurrentCompanyName())
				.redactedResumeUrl(candidateDAO.getRedactedResumeUrl())
				.timezone(candidateDAO.getTimezone())
				.resumeUrl(candidateDAO.getResumeUrl())
				.build();
	}

}
