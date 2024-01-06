/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.lever.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LeverApplicationDTO {
	private String id;

	private String candidateId;

	private String type;

	private String opportunityId;

	private Long createdAt;

	private String user;

	private String posting;

	private String postingOwner;

	private String postingHiringManager;

	private String name;

	private String email;

	private Phone phone;

	private String company;

	private List<String> links;

	private String comments;

	private List<Object> customQuestions;

	private Archived archived;

	private RequisitionForHire requisitionForHire;

	@Data
	public static class Phone {
		private String type;

		private String value;
	}

	@Data
	public static class Archived {
		private Long archivedAt;

		private String reason;
	}

	@Data
	public static class RequisitionForHire {
		private String id;

		private String requisitionCode;

		private String hiringManagerOnHire;
	}
}
