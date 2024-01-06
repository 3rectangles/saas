/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.jira.expert;

import com.barraiser.onboarding.dal.ExpertDAO;
import com.barraiser.onboarding.dal.UserDetailsDAO;
import com.barraiser.onboarding.expert.ExpertService;
import com.barraiser.onboarding.interview.jira.JiraUtil;
import com.barraiser.onboarding.interview.jira.dto.ExpertIssue;
import com.barraiser.onboarding.interview.jira.dto.IdValueField;
import com.barraiser.onboarding.user.expert.ExpertUtil;

import com.barraiser.onboarding.user.expert.dto.ExpertDetails;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class ExpertDBUpdater {
	private final ExpertService expertService;
	private final JiraUtil jiraUtil;
	private final ExpertUtil expertUtil;

	public ExpertDAO createOrUpdateExpertInDB(
			final UserDetailsDAO userDetails, final ExpertIssue.Fields fields, final ExpertDetails expertDetails)
			throws IOException {
		ExpertDAO expert = this.expertService
				.findById(userDetails.getId())
				.orElse(ExpertDAO.builder().id(userDetails.getId()).build());
		final Long gapBetweenInterviews = fields.getGapBetweenInterviews() == null
				? null
				: fields.getGapBetweenInterviews() * 60;
		final String interviewerReferrer = this.jiraUtil.getIdFromString(
				this.getValueFromField(fields.getInterviewerReferrer()));
		final Instant resumeReceivedDate = fields.getResumeReceivedDate() != null
				? Instant.parse(fields.getResumeReceivedDate() + "T00:00:00Z")
				: null;
		final Boolean isDemoEligible = fields.getIsDemoEligible() != null
				&& Boolean.TRUE.toString().equals(fields.getIsDemoEligible().getValue());
		final Boolean willingToSwitchVideoOn = fields.getWillingToSwitchVideoOn() != null
				? Boolean.valueOf(fields.getWillingToSwitchVideoOn().getValue())
				: null;

		expert = expert.toBuilder()
				.id(userDetails.getId())
				.multiplier(fields.getMultiplier())
				.isActive(userDetails.getIsActive())
				.isDemoEligible(isDemoEligible)
				.costLogic(this.getValueFromField(fields.getCalculationLogic()))
				.cancellationLogic(this.getValueFromField(fields.getCancellationLogic()))
				.bankAccount(fields.getAccountNumber())
				.expertDomains(userDetails.getExpertDomains())
				.peerDomains(userDetails.getPeerDomains())
				.baseCost(userDetails.getCost().getValue())
				.currency(userDetails.getCost().getCurrency())
				.minPrice(expertDetails.getPayoutDetails().getMinCostPerHour())
				.interviewerReferrer(interviewerReferrer)
				.consultancyReferrer(
						this.getValueFromField(fields.getConsultancyReferrer()))
				.reachoutChannel(this.getValueFromField(fields.getReachoutChannel()))
				.IFSC(fields.getIFSC())
				.opsRep(userDetails.getOpsRep())
				.resumeReceivedDate(resumeReceivedDate)
				.isUnderTraining(
						this.expertUtil.isExpertUnderTraining(fields.getIsUnderTraining()))
				.companiesForWhichExpertCanTakeInterview(
						this.companiesForWhichExpertCanTakeInterviews(
								fields.getCompaniesForWhichExpertCanTakeInterview()))
				.gapBetweenInterviews(gapBetweenInterviews)
				.countriesForWhichExpertCanTakeInterviews(
						this.countriesForWhichExpertCanTakeInterviews(
								fields.getCountryThatExpertBelongsTo(),
								fields.getCountriesForWhichExpertCanTakeInterviews()))
				.totalInterviewsCompleted(
						expert.getTotalInterviewsCompleted() == null ? 0 : expert.getTotalInterviewsCompleted())
				.willingToSwitchVideoOn(willingToSwitchVideoOn)
				.build();
		this.expertService.save(expert);
		return expert;
	}

	private String getValueFromField(final IdValueField idValueField) {
		return idValueField == null ? null : idValueField.getValue();
	}

	public List<String> companiesForWhichExpertCanTakeInterviews(
			final List<IdValueField> companiesForWhichExpertCanTakeInterview) {
		return companiesForWhichExpertCanTakeInterview == null
				? List.of()
				: companiesForWhichExpertCanTakeInterview.stream()
						.map(c -> this.jiraUtil.getIdFromString(c.getValue()))
						.collect(Collectors.toList());
	}

	public List<String> countriesForWhichExpertCanTakeInterviews(
			final IdValueField countryCode,
			final List<IdValueField> countriesForWhichExpertCanTakeInterviews) {
		final List<String> countries = this.extractIdFromList(countriesForWhichExpertCanTakeInterviews);
		return countries != null ? countries : Arrays.asList(this.getValueFromField(countryCode));
	}

	private List<String> extractIdFromList(final List<IdValueField> fields) {
		if (fields == null) {
			return null;
		}
		return fields.stream().map(IdValueField::getValue).collect(Collectors.toList());
	}
}
