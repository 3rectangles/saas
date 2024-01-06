/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.lever.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * TODO : Bad design , it feels like its generic. But its not , it has fields
 * specific
 * to lever event : Candidate stage change. Not reusable.
 */
@SuperBuilder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LeverWebhookRequestBody {
	private String id;

	private String event;

	private Long triggeredAt;

	private String token;

	private String signature;

	private EventPayload data;

	@Data
	public static class EventPayload {
		private String candidateId;

		private String fromStageId;

		private String toStageId;

		private String contactId;

		private String opportunityId;
	}
}
