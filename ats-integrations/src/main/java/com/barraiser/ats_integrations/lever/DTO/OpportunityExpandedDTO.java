/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.lever.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OpportunityExpandedDTO {
	@JsonProperty("id")
	private String id;

	@JsonProperty("name")
	private String name;

	@JsonProperty("tags")
	private List<String> tags;

	@JsonProperty("urls")
	private Urls urls;

	@JsonProperty("links")
	private List<String> links;

	@JsonProperty("owner")
	private String owner;

	@JsonProperty("stage")
	private Stage stage;

	@JsonProperty("emails")
	private List<String> emails;

	@JsonProperty("origin")
	private String origin;

	@JsonProperty("phones")
	private List<Phone> phones;

	@JsonProperty("contact")
	private String contact;

	@JsonProperty("sources")
	private List<String> sources;

	@JsonProperty("archived")
	private String archived;

	@JsonProperty("headline")
	private String headline;

	@JsonProperty("location")
	private String location;

	@JsonProperty("createdAt")
	private long createdAt;

	@JsonProperty("followers")
	private List<String> followers;

	@JsonProperty("sourcedBy")
	private String sourcedBy;

	@JsonProperty("updatedAt")
	private long updatedAt;

	@JsonProperty("applications")
	private List<Application> applications;

	@JsonProperty("isAnonymized")
	private boolean isAnonymized;

	@JsonProperty("snoozedUntil")
	private String snoozedUntil;

	@JsonProperty("stageChanges")
	private List<StageChange> stageChanges;

	@JsonProperty("dataProtection")
	private String dataProtection;

	@JsonProperty("lastAdvancedAt")
	private long lastAdvancedAt;

	@JsonProperty("confidentiality")
	private String confidentiality;

	@JsonProperty("lastInteractionAt")
	private long lastInteractionAt;

	@SuperBuilder(toBuilder = true)
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Urls {
		@JsonProperty("list")
		private String list;

		@JsonProperty("show")
		private String show;
	}

	@SuperBuilder(toBuilder = true)
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Stage {
		@JsonProperty("id")
		private String id;

		@JsonProperty("text")
		private String text;
	}

	@SuperBuilder(toBuilder = true)
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Phone {
		@JsonProperty("type")
		private String type;

		@JsonProperty("value")
		private String value;
	}

	@SuperBuilder(toBuilder = true)
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Application {
		@JsonProperty("id")
		private String id;

		@JsonProperty("type")
		private String type;

		@JsonProperty("user")
		private String user;

		@JsonProperty("posting")
		private String posting;

		@JsonProperty("candidateId")
		private String candidateId;

		@JsonProperty("opportunityId")
		private String opportunityId;
	}

	@SuperBuilder(toBuilder = true)
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class StageChange {
		@JsonProperty("userId")
		private String userId;

		@JsonProperty("toStageId")
		private String toStageId;

		@JsonProperty("updatedAt")
		private long updatedAt;

		@JsonProperty("toStageIndex")
		private int toStageIndex;
	}
}
