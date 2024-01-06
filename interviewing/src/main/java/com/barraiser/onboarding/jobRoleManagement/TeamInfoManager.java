/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.jobRoleManagement;

import com.barraiser.onboarding.interview.jobrole.dal.TeamRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class TeamInfoManager {

	private final TeamRepository teamRepository;

}
