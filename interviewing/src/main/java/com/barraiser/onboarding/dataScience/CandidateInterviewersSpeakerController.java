/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dataScience;

import com.barraiser.onboarding.candidate.CandidateInformationManager;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.UserDetailsDAO;
import com.barraiser.onboarding.dal.UserDetailsRepository;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.JsonArray;

import java.io.IOException;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Log4j2
@AllArgsConstructor
@RestController
public class CandidateInterviewersSpeakerController {

	/**
	 * @param request
	 * @return
	 * @throws IOException
	 */
	private final InterViewRepository interViewRepository;
	private final UserDetailsRepository userDetailsRepository;
	private final SpeakerFeignClient speakerFeignClient;
	private final CandidateInformationManager candidateInformationManager;

	public JSONArray userDetailsToJSON(UserDetailsDAO userDetails) {

		JSONArray userJSON = new JSONArray();
		JSONObject detail = new JSONObject();

		detail.put("first_name", "" + userDetails.getFirstName());
		detail.put("last_name", "" + userDetails.getLastName());
		detail.put("email", "" + userDetails.getEmail());
		detail.put("company_name", "" + userDetails.getCurrentCompanyName());
		detail.put("designation", "" + userDetails.getDesignation());
		userJSON.put(detail);
		return userJSON;
	}

	@PostMapping(value = "/getCandidateInterviewers")
	public String getCandidateInterviewers(@RequestBody Map<String, Object> request) throws IOException {

		String interviewId = (String) request.get("interviewId");
		List<String> speakers = (List<String>) request.get("speakerList");
		InterviewDAO interviewDAO = this.interViewRepository.findById(interviewId).get();
		final UserDetailsDAO interviewer = this.userDetailsRepository.findById(interviewDAO.getInterviewerId()).get();
		final UserDetailsDAO candidate = this.candidateInformationManager
				.getUserForCandidate(interviewDAO.getIntervieweeId());
		JSONObject requestBody = new JSONObject();
		JSONArray interviewerJSON = userDetailsToJSON(interviewer);
		JSONArray candidateJSON = userDetailsToJSON(candidate);
		JSONArray jsonArraySpeakers = new JSONArray(speakers);
		requestBody.put("interviewer", interviewerJSON);
		requestBody.put("interviewee", candidateJSON);
		requestBody.put("speaker_list", jsonArraySpeakers);
		JSONArray barRaiserAI = new JSONArray();
		barRaiserAI.put("barraiser.ai");
		requestBody.put("barraiser_bot_name", barRaiserAI);
		String returnString = speakerFeignClient.detectSpeakers(requestBody.toString());
		// API call
		return returnString;
	}

}
