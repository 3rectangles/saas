/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.resume;

import com.barraiser.onboarding.dal.ParsedResumeDAO;
import com.barraiser.onboarding.resume.dto.ParsedResumeDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ParsedResumeStorage {
	private final ParsedResumeRepository parsedResumeRepository;

	public void saveParsedResume(final String documentId, final ParsedResumeDTO parsedResume,
			final String parsedResumeJsonString) {
		this.parsedResumeRepository
				.save(this.constructParsedResumeDAO(parsedResume, parsedResumeJsonString, documentId));
	}

	private ParsedResumeDAO constructParsedResumeDAO(final ParsedResumeDTO parsedResume,
			final String parsedResumeJsonString, final String documentId) {

		final String email = parsedResume.getEmails() != null && parsedResume.getEmails().size() > 0
				? parsedResume.getEmails().get(0).getEmailAddress()
				: null;
		final String phone = parsedResume.getPhoneNumbers() != null && parsedResume.getPhoneNumbers().size() > 0
				? parsedResume.getPhoneNumbers().get(0).getFormattedNumber()
				: null;
		final String name = parsedResume.getName() != null ? parsedResume.getName().getFullName() : null;
		final String currentEmployer = parsedResume.getCurrentEmployer();
		final List<String> pastEmployers = this.getPastEmployers(parsedResume);
		final List<String> skills = parsedResume.getSkills() != null ? parsedResume.getSkills().stream()
				.map(ParsedResumeDTO.Skill::getSkill).collect(Collectors.toList()) : null;
		final String address = parsedResume.getAddress() != null && parsedResume.getAddress().size() > 0
				? parsedResume.getAddress().get(0).getFullAddress()
				: null;
		String experienceInMonths = parsedResume.getWorkedPeriod() != null
				? parsedResume.getWorkedPeriod().getTotalExperienceInMonths()
				: null;
		experienceInMonths = experienceInMonths.isEmpty() ? null : experienceInMonths;
		String almaMater = parsedResume.getInstitution();
		if (almaMater == null && !parsedResume.getSegregatedQualifications().isEmpty()) {
			almaMater = parsedResume.getSegregatedQualifications().get(0).getInstitution() != null
					? parsedResume.getSegregatedQualifications().get(0).getInstitution().getName()
					: null;
		}

		return ParsedResumeDAO.builder()
				.documentId(documentId)
				.name(name)
				.address(address)
				.experience(parsedResume.getExperience())
				.qualification(parsedResume.getQualification())
				.certification(parsedResume.getCertification())
				.experienceInMonths(experienceInMonths == null ? null : Integer.parseInt(experienceInMonths))
				.currentEmployer(currentEmployer)
				.pastEmployers(pastEmployers)
				.email(email)
				.phone(phone)
				.achievements(parsedResume.getAchievements())
				.hobbies(parsedResume.getHobbies())
				.skills(skills)
				.currentDesignation(parsedResume.getJobProfile())
				.almaMater(almaMater)
				.rawData(parsedResumeJsonString)
				.build();
	}

	private List<String> getPastEmployers(final ParsedResumeDTO parsedResume) {
		if (parsedResume.getEmployers() != null) {
			return parsedResume.getEmployers().stream()
					.filter(x -> (x.getIsCurrentEmployer().equals("false") && x.getEmployer() != null))
					.map(x -> x.getEmployer().getName())
					.collect(Collectors.toList());
		}
		return null;
	}
}
