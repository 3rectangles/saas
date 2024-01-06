/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.calendar_interception.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class AtsInterview {
	String id;

	String interviewStructureId;

	String evaluationId;

	String jobRoleId;

	String remoteData;
}
