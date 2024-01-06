/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.partner.rest.controllers;

import com.barraiser.common.graphql.types.MeetingInterceptionConfiguration;
import com.barraiser.common.graphql.types.Partner;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.barraiser.common.constants.ServiceConfigurationConstants.INTERVIEWING_SERVICE_CONTEXT_PATH;
import static com.barraiser.onboarding.common.Constants.SAAS_TRIAL_PARTNERSHIP_MODEL_ID;

@RestController
@Log4j2
@AllArgsConstructor
public class PartnerInfoController {

	private final PartnerWhitelistedDomainsRepository whitelistedDomainsRepository;
	private final RelaxedMeetingInterceptionConfigRepository relaxedMeetingInterceptionConfigRepository;
	private final InterViewRepository interViewRepository;
	private final JobRoleRepository jobRoleRepository;
	private final JobRoleToInterviewStructureRepository jobRoleToInterviewStructureRepository;
	private final PartnerCompanyRepository partnerCompanyRepository;
	private final ObjectMapper objectMapper;

	@GetMapping(value = INTERVIEWING_SERVICE_CONTEXT_PATH + "/whitelisted-domains/{domain}/partner")
	public ResponseEntity<Partner> getPartner(@PathVariable("domain") final String domain) {

		final Optional<PartnerWhitelistedDomainDAO> partnerWhitelistedDomainDAOOptional = this.whitelistedDomainsRepository
				.findByEmailDomainIgnoreCase(domain);

		return partnerWhitelistedDomainDAOOptional.map(partnerWhitelistedDomainDAO -> ResponseEntity
				.ok(Partner.builder().id(partnerWhitelistedDomainDAO.getPartnerId()).build()))
				.orElseGet(() -> ResponseEntity.badRequest().build());
	}

	@GetMapping(value = INTERVIEWING_SERVICE_CONTEXT_PATH + "/partnerMeetingConfiguration")
	List<MeetingInterceptionConfiguration> getPartnerMeetingConfiguration() {
		return relaxedMeetingInterceptionConfigRepository.findAll().stream()
				.map(this::toMeetingInterceptionConfiguration)
				.filter(meetingInterceptionConfiguration -> this.partnerCompanyRepository
						.findById(meetingInterceptionConfiguration.getPartnerId()).get()
						.getPartnershipModelId()
						.equals(SAAS_TRIAL_PARTNERSHIP_MODEL_ID))
				.collect(Collectors.toList());
	}

	private MeetingInterceptionConfiguration toMeetingInterceptionConfiguration(
			RelaxedMeetingInterceptionConfigDAO relaxedMeetingInterceptionConfigDAO) {

		return MeetingInterceptionConfiguration.builder()
				.partnerId(relaxedMeetingInterceptionConfigDAO.getPartnerId())
				.keyword(relaxedMeetingInterceptionConfigDAO.getKeyword())
				.build();
	}

	@GetMapping(value = INTERVIEWING_SERVICE_CONTEXT_PATH + "/partner/{partner_id}/isInterviewLimitReached")
	Boolean isInterviewLimitReached(@PathVariable("partner_id") String partnerId) {
		// TODO: Modify Logic
		final PartnerCompanyDAO partnerCompanyDAO = this.partnerCompanyRepository.findById(partnerId).get();

		final List<InterviewDAO> interviewDAOList = interViewRepository.findAllyByPartnerId(partnerId);

		// Skipping Interviews with statuses in "Conduction Pending" as when Saas trial
		// interviews are added they are always scheduled
		final Integer scheduledInterviewCount = interviewDAOList.stream()
				.filter(interviewDAO -> !InterviewStatus.fromString(interviewDAO.getStatus())
						.isConductionPending())
				.collect(Collectors.toList()).size();

		if (scheduledInterviewCount >= partnerCompanyDAO.getMaxFreeTrialInterviews()) {
			log.info("Interview Limit Reached for Partner " + partnerId);
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	@GetMapping(value = INTERVIEWING_SERVICE_CONTEXT_PATH + "/partner/{partnerId}")
	Partner getPartnerById(@PathVariable("partnerId") String partnerId) {
		return this.objectMapper.convertValue(this.partnerCompanyRepository.findById(partnerId).get(), Partner.class);
	}

	public Boolean isSaasTrialPartner(final String partnerId) {
		return this.partnerCompanyRepository.findById(partnerId).get().getPartnershipModelId()
				.equals(SAAS_TRIAL_PARTNERSHIP_MODEL_ID);
	}

}
