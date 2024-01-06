/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.lever.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
	private String id;

	private String name;

	private String username;

	private String email;

	private Long createdAt;

	private Long deactivatedAt;

	private String accessRole;

	private String photo;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String externalDirectoryId;

	@JsonInclude(JsonInclude.Include.NON_NULL)
	private List<String> linkedContactIds;
}
