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
public class AttachmentDTO {
	@JsonProperty("id")
	private String id;

	@JsonProperty("remote_is")
	private String remoteId;

	@JsonProperty("file_name")
	private String fileName;

	@JsonProperty("file_url")
	private String fileUrl;

	@JsonProperty("candidate")
	private String candidate;

	@JsonProperty("attachment_type")
	private String attachmentType; // RESUME, COVER_LETTER, OFFER_LETTER, OTHER
}
