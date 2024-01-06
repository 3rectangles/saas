/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.lever;

import com.barraiser.ats_integrations.lever.DTO.LeverApplicationDTO;
import com.barraiser.ats_integrations.lever.DTO.OpportunityDTO;
import com.barraiser.ats_integrations.lever.DTO.ResumeDTO;
import com.barraiser.ats_integrations.lever.DTO.UserDTO;
import lombok.Data;

import java.util.List;

@Data
public class LeverAddEvaluationEventCreationData {
	private OpportunityDTO opportunityDTO;
	private LeverApplicationDTO leverApplicationDTO;
	private List<ResumeDTO> resumeDTOList;
	private UserDTO userDTO;
}
