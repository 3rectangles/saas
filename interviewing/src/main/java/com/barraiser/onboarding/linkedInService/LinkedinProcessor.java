/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.linkedInService;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@AllArgsConstructor
@Component
public class LinkedinProcessor {
	private LinkedInService linkedInService;

	public String getAccessToken(final String code) {
		return this.linkedInService.accessToken(code).getAccessToken();
	}

	public String getUrn(final String accessToken) {
		return this.linkedInService.getUrn(accessToken).getId();
	}

	public String shareCandidatePost(
			final String accessToken,
			final String candidateUrn,
			final String imageUrl,
			final String certificateId) {
		return this.linkedInService
				.sharePost(accessToken, candidateUrn, imageUrl, certificateId)
				.getActivity();
	}
}
