/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.jira.expert;

import com.barraiser.common.dal.Money;
import com.barraiser.common.utilities.PhoneParser;
import com.barraiser.onboarding.config.DynamicAppConfigProperties;
import com.barraiser.onboarding.dal.ExpertDAO;
import com.barraiser.onboarding.dal.ExpertRepository;
import com.barraiser.onboarding.dal.UserDetailsDAO;
import com.barraiser.onboarding.dal.UserDetailsRepository;
import com.barraiser.onboarding.interview.jira.JiraUtil;
import com.barraiser.onboarding.interview.jira.JiraWorkflowManager;
import com.barraiser.onboarding.interview.jira.dto.ExpertIssue;

import com.barraiser.onboarding.interview.jira.dto.JiraCommentDTO;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.ExpertCostForSchedulingFeatureToggleManager;
import com.barraiser.onboarding.user.CompanyManager;
import com.barraiser.onboarding.user.expert.ExpertUtil;
import com.barraiser.onboarding.user.expert.dto.ExpertDetails;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@Component
@AllArgsConstructor
public class ExpertSpecificDetailsUpdater {

	public static final String DYNAMO_NUMBER_OF_DUPLICATE_EXPERTS = "number-of-duplicate-experts";
	private final static String STATUS_APPROVED = "Approved";

	private final static String MIDNIGHT_UTC_TIME_SUFFIX = "T00:00:00Z";
	private final static String BOOLEAN_TRUE = "true";
	private final static String EXPERT_INDEX = "expert";

	private final ExpertRepository expertRepository;
	private final ExpertDBUpdater expertDBUpdater;
	private final DynamicAppConfigProperties appConfigProperties;
	private final ExpertUtil expertUtil;
	private final UserDetailsRepository userDetailsRepository;
	private final ExpertElasticSearchManager expertElasticSearchManager;
	private final JiraUtil jiraUtil;
	private final PhoneParser phoneParser;
	private final CompanyManager companyManager;
	private final JiraWorkflowManager jiraWorkflowManager;
	private final ExpertCostForSchedulingFeatureToggleManager expertCostForSchedulingFeatureToggleManager;

	public void createOrUpdateExpertDetails(final ExpertIssue.Fields fields, final UserDetailsDAO userDetails)
			throws IOException {
		final ExpertDAO expertDAO = this.expertRepository.findById(userDetails.getId()).orElse(null);
		if (userDetails.getIsExpertPartner() && fields.getStatus().getName().equals(STATUS_APPROVED)
				&& this.areMandatoryFieldsFilled(fields)) {
			final ExpertDetails expertDetails = this.constructExpertDetails(fields, userDetails);
			this.expertDBUpdater.createOrUpdateExpertInDB(userDetails, fields, expertDetails);
			this.expertElasticSearchManager.updateExpertDetails(expertDetails);
			this.createDuplicateExperts(userDetails);
			this.notifyOnEpIfExpertActivated(fields, userDetails, expertDAO);
		}
		this.removeFromStorageIfNotExpert(userDetails.getId(), userDetails.getIsExpertPartner());
		this.notifyOnEpIfExpertDeactivated(fields, userDetails, expertDAO);
	}

	private void removeFromStorageIfNotExpert(final String userId, final boolean isExpert) throws IOException {
		if (!isExpert) {
			if (this.expertRepository.findById(userId).isPresent()) {
				this.expertRepository.deleteById(userId);
				final List<ExpertDAO> duplicateExperts = this.expertUtil.getDuplicateExpertsForGivenExpert(userId);
				this.expertRepository.deleteAll(duplicateExperts);
				this.expertElasticSearchManager.deleteExpertDetails(EXPERT_INDEX, userId);
			}
		}
	}

	private void createDuplicateExperts(final UserDetailsDAO originalExpert) {
		final int numberOfDuplicateExpertsRequired = this.appConfigProperties
				.getInt(DYNAMO_NUMBER_OF_DUPLICATE_EXPERTS);
		final List<ExpertDAO> duplicateExperts = this.expertUtil
				.getDuplicateExpertsForGivenExpert(originalExpert.getId());
		final int remainingDuplicateExperts = numberOfDuplicateExpertsRequired - duplicateExperts.size();
		for (int i = 1; i <= remainingDuplicateExperts; i++) {
			this.createDuplicateExpert(originalExpert, i + duplicateExperts.size());
		}
	}

	private void createDuplicateExpert(final UserDetailsDAO originalExpert, final int count) {
		final String email = "dup_" + count + "_" + originalExpert.getEmail();
		final UserDetailsDAO user = UserDetailsDAO.builder()
				.id(UUID.randomUUID().toString())
				.firstName(originalExpert.getFirstName())
				.lastName(originalExpert.getLastName())
				.email(email)
				.build();
		final ExpertDAO duplicateExpert = ExpertDAO.builder()
				.id(user.getId())
				.duplicatedFrom(originalExpert.getId()).build();
		this.userDetailsRepository.save(user);
		this.expertRepository.save(duplicateExpert);
	}

	public ExpertDetails constructExpertDetails(final ExpertIssue.Fields fields, final UserDetailsDAO userDetails) {

		final String category = fields.getCategory() == null ? null : fields.getCategory().getValue();
		final Boolean isActive = fields.getActive() != null && BOOLEAN_TRUE.equals(fields.getActive().getValue());

		final Boolean isDemoEligible = fields.getIsDemoEligible() != null
				&& Boolean.TRUE.toString().equals(fields.getIsDemoEligible().getValue());
		final String costLogic = fields.getCalculationLogic() != null ? fields.getCalculationLogic().getValue() : null;
		final String cancellationLogic = fields.getCancellationLogic() != null
				? fields.getCancellationLogic().getValue()
				: null;
		final String interviewerReferrer = this.jiraUtil
				.getIdFromString(this.jiraUtil.getValueFromField(fields.getInterviewerReferrer()));
		final Instant resumeReceivedDate = fields.getResumeReceivedDate() != null
				? Instant.parse(fields.getResumeReceivedDate() + MIDNIGHT_UTC_TIME_SUFFIX)

				: null;
		final Long gapBetweenInterviews = fields.getGapBetweenInterviews() == null
				? null
				: fields.getGapBetweenInterviews() * 60;

		final String formattedPhone = this.phoneParser.getFormattedPhone(fields.getPhone());
		final String formattedWhatsappNumber = this.phoneParser.getFormattedPhone(fields.getWhatsappNumber());
		final String currentCompanyId = this.companyManager.getOrCreateCompany(fields.getCurrentCompany().trim())
				.getId();

		Money costPerHour = Money.builder().build();
		if (fields.getCostPerHour() != null) {
			final String currency = fields.getCurrency() == null ? null : fields.getCurrency().getValue();

			costPerHour = costPerHour.toBuilder()
					.value(fields.getCostPerHour())
					.currency(currency)
					.build();
		}
		final Optional<ExpertDAO> expertDAO = this.expertRepository.findById(userDetails.getId());
		final boolean shouldDefaultMinCost = !(this.expertCostForSchedulingFeatureToggleManager
				.isFeatureToggleForMinCostOn(userDetails.getId()) && expertDAO.isPresent()
				&& expertDAO.get().getMinPrice() != null);
		return ExpertDetails.builder()
				.id(userDetails.getId())
				.isActive(isActive)
				.isDemoEligible(isDemoEligible)
				.category(category)
				.email(fields.getEmail())
				.designation(fields.getDesignation())
				.expertDomains(this.jiraUtil.extractIdFromList(fields.getExpertDomains()))
				.peerDomains(this.jiraUtil.extractIdFromList(fields.getPeerDomains()))
				.phone(formattedPhone)
				.whatsappNumber(formattedWhatsappNumber)
				.currentCompanyId(currentCompanyId)
				.workExperienceInMonths(fields.getWorkExperience())
				.interviewerReferrer(interviewerReferrer)
				.consultancyReferrer(this.jiraUtil.getValueFromField(fields.getConsultancyReferrer()))
				.reachoutChannel(this.jiraUtil.getValueFromField(fields.getReachoutChannel()))
				.resumeReceivedDate(resumeReceivedDate)
				.isUnderTraining(
						this.expertUtil.isExpertUnderTraining(fields.getIsUnderTraining()))
				.companiesForWhichExpertCanTakeInterview(
						this.expertDBUpdater.companiesForWhichExpertCanTakeInterviews(
								fields.getCompaniesForWhichExpertCanTakeInterview()))
				.countriesForWhichExpertCanTakeInterview(
						this.expertDBUpdater.countriesForWhichExpertCanTakeInterviews(
								fields.getCountryThatExpertBelongsTo(),
								fields.getCountriesForWhichExpertCanTakeInterviews()))
				.lastCompaniesId(this.jiraUtil.extractIdFromList(fields.getLastCompanies()))
				.gapBetweenInterviews(gapBetweenInterviews)
				.payoutDetails(ExpertDetails.PayoutDetails.builder()
						.baseCost(costPerHour.getValue())
						.currency(costPerHour.getCurrency())
						.multiplier(fields.getMultiplier())
						.costLogic(costLogic)
						.cancellationLogic(cancellationLogic)
						.bankAccount(fields.getAccountNumber())
						.IFSC(fields.getIFSC())
						.minCostPerHour(shouldDefaultMinCost
								? costPerHour.getValue() * fields.getMultiplier()
								: expertDAO.get().getMinPrice())
						.build())
				.build();
	}

	public Boolean areMandatoryFieldsFilled(final ExpertIssue.Fields fields) {
		return fields.getCalculationLogic() != null
				&& fields.getCostPerHour() != null
				&& fields.getMultiplier() != null
				&& fields.getCurrency() != null
				&& fields.getCancellationLogic() != null
				&& fields.getCountryThatExpertBelongsTo() != null
				&& fields.getTimezone() != null;
	}

	private void notifyOnEpIfExpertActivated(final ExpertIssue.Fields fields, final UserDetailsDAO userDetails,
			final ExpertDAO expertDAO) {
		final Boolean isActive = fields.getActive() != null && BOOLEAN_TRUE.equals(fields.getActive().getValue());
		if (isActive && this.isFirstTimeActivation(expertDAO)) {
			this.jiraWorkflowManager.addCommentInJira(userDetails.getId(),
					JiraCommentDTO.builder().body("ACTIVATED").build());
		}
	}

	private void notifyOnEpIfExpertDeactivated(final ExpertIssue.Fields fields, final UserDetailsDAO userDetails,
			final ExpertDAO expertDAO) {
		final Boolean isActive = fields.getActive() != null && BOOLEAN_TRUE.equals(fields.getActive().getValue());
		if (!isActive && this.isFirstTimeDeactivation(expertDAO)) {
			this.jiraWorkflowManager.addCommentInJira(userDetails.getId(),
					JiraCommentDTO.builder().body("DEACTIVATED").build());
		}
	}

	private Boolean isFirstTimeActivation(final ExpertDAO expertDAO) {
		if (expertDAO != null && Boolean.TRUE.equals(expertDAO.getIsActive())) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	private Boolean isFirstTimeDeactivation(final ExpertDAO expertDAO) {
		if (expertDAO != null && Boolean.TRUE.equals(expertDAO.getIsActive())) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
}
