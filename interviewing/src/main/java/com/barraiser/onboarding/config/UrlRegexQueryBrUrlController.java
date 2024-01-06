/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.config;

import com.barraiser.onboarding.dal.PartnerCompanyDAO;
import com.barraiser.onboarding.dal.PartnerCompanyRepository;
import com.barraiser.onboarding.dal.QueryExecutor;
import com.barraiser.onboarding.dal.UrlRegexQueryBrUrlDAO;
import com.barraiser.onboarding.dal.UrlRegexQueryBrUrlRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.IOException;
import java.util.*;

import static com.barraiser.common.constants.ServiceConfigurationConstants.URL_REGEX_QUERY_URL_CONTROLLER;

@Log4j2
@AllArgsConstructor
@RestController
public class UrlRegexQueryBrUrlController {

	private final ObjectMapper objectMapper;
	private final UrlRegexQueryBrUrlRepository urlRegexQueryBrUrlRepository;
	private final QueryExecutor queryExecutor;
	private final PartnerCompanyRepository partnerCompanyRepository;

	@PostMapping(value = URL_REGEX_QUERY_URL_CONTROLLER)
	public ResponseEntity<JsonNode> getBrUrl(@RequestBody Map<String, Object> request) throws IOException {
		try {
			String url = (String) request.get("url");

			String finalUrl = getUrlRegexQueryBrUrl(url);

			JsonNode responseJson = objectMapper.createObjectNode().put("brUrl", finalUrl);
			return ResponseEntity.ok(responseJson);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping(value = "/partner/{partnerId}/getallowedhosts")
	public JsonNode getAllowedHosts(@PathVariable String partnerId) throws IOException {
		PartnerCompanyDAO partner = partnerCompanyRepository.findById(partnerId).get();
		JsonNode responseJson = objectMapper.createObjectNode().put("allowedHosts", partner.getHostAllowedRedirect());
		return responseJson;
	}

	@PostMapping(value = "getallowedhosts")
	public JsonNode getAllowedHosts() throws IOException {
		JsonNode responseJson = objectMapper.createObjectNode().put("allowedHosts", "www.google.com");
		return responseJson;
	}

	public static String extractHostFromUrl(String url) {
		final String HOST_REGEX = "^(?:\\w+://)?((?:[^./?#]+\\.)?([^/?#]+))";
		Pattern pattern = Pattern.compile(HOST_REGEX);
		Matcher matcher = pattern.matcher(url);

		if (matcher.find()) {
			return matcher.group(1);
		}

		return null; // or throw an exception if required
	}

	public String getUrlRegexQueryBrUrl(String url) {
		String host = extractHostFromUrl(url);
		List<UrlRegexQueryBrUrlDAO> matchUrlRegexQueryBrUrlDAOs = urlRegexQueryBrUrlRepository.findAllByHost(host);
		Map<String, String> regexMatchMap = new HashMap<>();
		for (UrlRegexQueryBrUrlDAO dao : matchUrlRegexQueryBrUrlDAOs) {
			String regex = dao.getRegex();
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(url);

			if (matcher.find()) {
				for (int i = 1; i <= matcher.groupCount(); i++) {
					String key = "param" + i;
					String value = matcher.group(i);
					regexMatchMap.put(key, value);
				}
				if (dao.getQuery() != null) {
					Map<String, String> queryResultParams = queryExecutor.executeParametrizedQuery(dao.getQuery(),
							regexMatchMap);
					if (queryResultParams != null) {
						regexMatchMap.putAll(queryResultParams);
					} else {
						return null;
					}
				}
				String finalUrl = dao.getUrlPattern();
				for (Map.Entry<String, String> entry : regexMatchMap.entrySet()) {
					String paramName = entry.getKey();
					String paramValue = entry.getValue();
					String placeholder = ":" + paramName;
					finalUrl = finalUrl.replace(placeholder, paramValue);
				}
				return finalUrl; // Stop after the first match
			}
		}

		return null;

	}

}
