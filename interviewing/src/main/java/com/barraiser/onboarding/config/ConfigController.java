/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.config;

import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.auth.AuthenticationException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

import static com.barraiser.common.constants.ServiceConfigurationConstants.INTERVIEWING_SERVICE_CONTEXT_PATH;

@Log4j2
@AllArgsConstructor
@RestController
public class ConfigController {

	private final ConfigComposer configComposer;
	private final PartnerConfigContextConstructor partnerConfigContextConstructor;

	@PostMapping(value = INTERVIEWING_SERVICE_CONTEXT_PATH + "/partner/{partnerId}/config")
	public JsonNode getConfig(@PathVariable String partnerId,
			@RequestBody Map<String, Object> request,
			@RequestAttribute(name = "loggedInUser") AuthenticatedUser user) throws IOException {
		String tag = (String) request.get("tag");

		if (user == null) {
			throw new AuthenticationException("No authenticated user found");
		}

		List<String> contextTags = this.partnerConfigContextConstructor.getContextTags(partnerId, request, user);
		return this.configComposer.compose(tag, contextTags);
	}

}
