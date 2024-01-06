/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.smartRecruiters.POJO;

import com.barraiser.ats_integrations.smartRecruiters.DTO.CandidateDTO;
import com.barraiser.ats_integrations.smartRecruiters.DTO.UserDTO;
import com.barraiser.common.graphql.types.Document;
import lombok.Data;

@Data
public class SmartRecruitersAddEvaluationCreationData {
	private String jobId;
	private CandidateDTO candidateDTO;
	private Document resume;
	private UserDTO hiringManager;
}
