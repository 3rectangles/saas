/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.lever.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
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
public class OpportunityDTO {
	private String id;

	private String name;

	private String headline;

	private String contact;

	private String stage;

	private List<StageChange> stageChanges;

	private String confidentiality;

	private String location;

	private List<Phone> phones;

	private List<String> emails;

	private List<String> links;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Archived archived;

	private List<String> tags;

	private List<String> sources;

	private String sourcedBy;

	private String origin;

	private String owner;

	private List<String> followers;

	private List<String> applications;

	private Long createdAt;

	private Long updatedAt;

	private Long lastInteractionAt;

	private Long lastAdvancedAt;

	private Long snoozedUntil;

	private Urls urls;

	private DataProtection dataProtection;

	private Boolean isAnonymized;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String deletedBy;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Long deletedAt;

	@Data
	public static class StageChange {
		private String toStageId;

		private Integer toStageIndex;

		private Long updatedAt;

		private String userId;
	}

	@Data
	public static class Phone {
		private String value;

		private String type;
	}

	@Data
	public static class Archived {
		private Long archivedAt;

		private String reason;
	}

	@Data
	public static class Urls {
		private String list;

		private String show;
	}

	@Data
	public static class DataProtection {
		private AllowedAndExpiresAt store;

		private AllowedAndExpiresAt contact;
	}

	@Data
	public static class AllowedAndExpiresAt {
		private Boolean allowed;

		private Long expiresAt;
	}
}
