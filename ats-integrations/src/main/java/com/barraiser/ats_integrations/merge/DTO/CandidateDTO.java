/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.merge.DTO;

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
public class CandidateDTO {
	@JsonProperty("id")
	private String id;

	@JsonProperty("remote_id")
	private String remoteId;

	@JsonProperty("first_name")
	private String firstName;

	@JsonProperty("last_name")
	private String lastName;

	@JsonProperty("company")
	private String company;

	@JsonProperty("title")
	private String title;

	@JsonProperty("remote_created_at")
	private String remoteCreatedAt;

	@JsonProperty("remote_updated_at")
	private String remoteUpdatedAt;

	@JsonProperty("last_interaction_at")
	private String lastInteractionAt;

	@JsonProperty("is_private")
	private Boolean isPrivate;

	@JsonProperty("can_email")
	private Boolean canEmail;

	@JsonProperty("locations")
	private List<String> locations;

	@JsonProperty("phone_numbers")
	private List<PhoneNumberDTO> phoneNumbers;

	@JsonProperty("email_addresses")
	private List<EmailAddressDTO> emailAddresses;

	@JsonProperty("urls")
	private List<UrlDTO> urls;

	@JsonProperty("tags")
	private List<String> tags;

	@JsonProperty("applications")
	private List<String> applications;

	@JsonProperty("attachments")
	private List<String> attachments;

}
