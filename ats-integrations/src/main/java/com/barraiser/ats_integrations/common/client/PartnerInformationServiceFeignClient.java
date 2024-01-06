/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.common.client;

import com.barraiser.common.graphql.types.MeetingInterceptionConfiguration;
import com.barraiser.common.graphql.types.Partner;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

import static com.barraiser.common.constants.ServiceConfigurationConstants.INTERVIEWING_SERVICE_CONTEXT_PATH;

@FeignClient(name = "partner-information-service-feign-client", url = "http://localhost:5000")
public interface PartnerInformationServiceFeignClient {

	@GetMapping(value = INTERVIEWING_SERVICE_CONTEXT_PATH + "/whitelisted-domains/{domain}/partner")
	Partner getPartner(@PathVariable("domain") String domain);

	@GetMapping(value = INTERVIEWING_SERVICE_CONTEXT_PATH + "/partnerMeetingConfiguration")
	List<MeetingInterceptionConfiguration> getPartnerMeetingConfiguration();

	@GetMapping(value = INTERVIEWING_SERVICE_CONTEXT_PATH + "/partner/{partner_id}/isInterviewLimitReached")
	Boolean isInterviewLimitReached(@PathVariable("partner_id") String partnerId);

	@GetMapping(value = INTERVIEWING_SERVICE_CONTEXT_PATH + "/partner/{partnerId}")
	Partner getPartnerById(@PathVariable("partnerId") String partnerId);

}
