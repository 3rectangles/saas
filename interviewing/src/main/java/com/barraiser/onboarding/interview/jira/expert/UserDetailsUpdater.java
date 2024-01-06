/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.jira.expert;

import com.barraiser.common.dal.Money;
import com.barraiser.common.utilities.PhoneParser;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.jira.JiraUtil;
import com.barraiser.onboarding.interview.jira.dto.ExpertIssue;
import com.barraiser.onboarding.interview.jira.dto.IdValueField;
import com.barraiser.onboarding.user.CompanyManager;
import com.barraiser.onboarding.user.UserInformationManagementHelper;
import com.barraiser.commons.auth.UserRole;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class UserDetailsUpdater {
	private static final List<UserRole> POSSIBLE_EP_ROLES = List.of(UserRole.EXPERT, UserRole.QC);

	private final UserDetailsRepository userDetailsRepository;
	private final PhoneParser phoneParser;
	private final JiraUtil jiraUtil;
	private final UserInformationManagementHelper userManagement;
	private final CompanyManager companyManager;
	private final TargetJobAttributesRepository targetJobAttributesRepository;

	public UserDetailsDAO createOrUpdateUserDetails(
			final String userId, final ExpertIssue.Fields fields) {
		final List<UserRole> userRoles = this.getUpdatedUserRoles(fields, userId);
		final UserDetailsDAO userDetails = this.createOrUpdateUserInDB(userId, fields, userRoles);
		this.saveUserRoleAndAttributes(
				userDetails,
				fields,
				this.phoneParser.getFormattedPhone(fields.getPhone()),
				userRoles);
		this.createOrUpdateTargetJobAttributes(userId, fields);
		return userDetails;
	}

	private void saveUserRoleAndAttributes(
			final UserDetailsDAO userDetails,
			final ExpertIssue.Fields fields,
			final String formattedPhone,
			final List<UserRole> userRoles) {
		final String userId = userDetails.getId();
		this.userManagement.updateUserAttributes(
				userId,
				Map.of(
						"given_name",
						String.format("%s %s", fields.getFirstName(), fields.getLastName()),
						"email",
						fields.getEmail(),
						"custom:phone_number",
						formattedPhone != null ? formattedPhone : "",
						"email_verified",
						"true",
						"custom:whatsapp_number",
						userDetails.getWhatsappNumber() == null
								? ""
								: userDetails.getWhatsappNumber()));
		this.userManagement.updateUserRoles(userId, userRoles);
	}

	private UserDetailsDAO createOrUpdateUserInDB(
			final String userId, final ExpertIssue.Fields fields, final List<UserRole> userRoles) {
		final UserDetailsDAO userDetailsDAO = this.userDetailsRepository
				.findById(userId)
				.orElse(UserDetailsDAO.builder().id(userId).build());
		final String opsRep = fields.getAssignee() == null ? null : fields.getAssignee().getDisplayName();
		final String category = fields.getCategory() == null ? null : fields.getCategory().getValue();
		final Boolean isActive = fields.getActive() != null && "true".equals(fields.getActive().getValue());
		final String formattedPhone = this.phoneParser.getFormattedPhone(fields.getPhone());
		final String formattedWhatsappNumber = this.phoneParser.getFormattedPhone(fields.getWhatsappNumber());
		final List<String> domains = fields.getDomain() == null
				? userDetailsDAO.getDomains()
				: this.jiraUtil.extractIdFromList(Arrays.asList(fields.getDomain()));
		final String currentCompanyId = this.companyManager.getOrCreateCompany(fields.getCurrentCompany().trim())
				.getId();
		Money costPerHour = Optional.ofNullable(userDetailsDAO.getCost()).orElse(Money.builder().build());
		if (fields.getCostPerHour() != null) {
			final String currency = fields.getCurrency() == null ? null : fields.getCurrency().getValue();
			costPerHour = costPerHour.toBuilder()
					.value(fields.getCostPerHour())
					.currency(currency)
					.build();
		}

		final UserDetailsDAO userDetails = userDetailsDAO.toBuilder()
				.firstName(fields.getFirstName())
				.lastName(fields.getLastName())
				.phone(formattedPhone)
				.email(fields.getEmail())
				.resumeUrl(fields.getResume())
				.category(category)
				.currentCompanyId(currentCompanyId)
				.currentCompanyName(fields.getCurrentCompany())
				.designation(fields.getDesignation())
				.almaMater(fields.getAlmaMater())
				.currentCompanyName(fields.getCurrentCompany())
				.linkedInProfile(fields.getLinkedInProfile())
				.workExperienceInMonths(fields.getWorkExperience())
				.previousWorkExperienceInMonths(
						userDetailsDAO.getPreviousWorkExperienceInMonths() == null
								? fields.getWorkExperience()
								: userDetailsDAO.getPreviousWorkExperienceInMonths())
				.initials(
						""
								+ fields.getFirstName().charAt(0)
								+ (fields.getLastName() != null ? fields.getLastName().charAt(0) : ""))
				.opsRep(opsRep)
				.isExpertPartner(userRoles.contains(UserRole.EXPERT))
				.isActive(isActive)
				.lastCompanies(this.jiraUtil.extractIdFromList(fields.getLastCompanies()))
				.cost(costPerHour)
				.peerDomains(this.jiraUtil.extractIdFromList(fields.getPeerDomains()))
				.expertDomains(this.jiraUtil.extractIdFromList(fields.getExpertDomains()))
				.domains(domains)
				.whatsappNumber(formattedWhatsappNumber)
				.countryCode(
						fields.getCountryThatExpertBelongsTo() == null
								? null
								: fields.getCountryThatExpertBelongsTo().getValue())
				.timezone(fields.getTimezone() == null ? null : fields.getTimezone().getValue())
				.build();
		this.userDetailsRepository.save(userDetails);
		return userDetails;
	}

	private void createOrUpdateTargetJobAttributes(
			final String userId, final ExpertIssue.Fields fields) {
		final String targetCompany = fields.getTargetCompany() == null
				? null
				: this.jiraUtil.getIdFromString(fields.getTargetCompany().getValue());
		this.targetJobAttributesRepository.save(
				this.targetJobAttributesRepository
						.findById(userId)
						.orElse(TargetJobAttributesDAO.builder().userId(userId).build())
						.toBuilder()
						.companies(Collections.singletonList(targetCompany))
						.build());
	}

	private List<UserRole> getUserRolesFromJiraEvent(final ExpertIssue.Fields fields) {
		if (fields.getRoles() != null) {
			return fields.getRoles().stream()
					.map(IdValueField::getValue)
					.map(UserRole::fromString)
					.collect(Collectors.toList());
		}
		return List.of(UserRole.EXPERT);
	}

	private List<UserRole> getUpdatedUserRoles(
			final ExpertIssue.Fields fields, final String userId) {
		final List<UserRole> originalRoles = this.userManagement.getRolesOfUser(userId).stream()
				.map(UserRole::fromString)
				.collect(Collectors.toList());
		final List<UserRole> userRolesFromJira = this.getUserRolesFromJiraEvent(fields);
		final List<UserRole> updatedUserRoles = originalRoles;
		updatedUserRoles.removeAll(POSSIBLE_EP_ROLES);
		updatedUserRoles.addAll(userRolesFromJira);
		return updatedUserRoles;
	}
}
