/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.lever.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@SuperBuilder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LeverWebhookInterviewCreatedRequestBody {
	private String id;

	private String event;

	private Long triggeredAt;

	private String token;

	private String signature;

	private EventPayload data;

	@Data
	public static class EventPayload {
		private String interviewId;

		private String panelId;

		private String opportunityId;

		// TODO: Not caring about this or now.
		private Instant createdAt;

	}
}
