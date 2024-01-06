/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.input;

import lombok.*;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class UpdateCandidateInput {
	private String candidateId;
	private String email;
	private String phoneNumber;
	private String firstName;
	private String lastName;
	private String resumeLink;
	private String atsSource;
}
