/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.jobRoleManagement.JobRoleConfiguration.dal;

import com.barraiser.onboarding.audit.AuditListener;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@EntityListeners(AuditListener.class)
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table(name = "interview_structure_to_skill_interviewing_configuration")
public class InterviewStructureToSkillInterviewingConfigurationDAO {

	@Id
	private String id;

	@Column(name = "interview_structure_id")
	private String interviewStructureId;

	@Column(name = "skill_interviewing_configuration_id")
	private String skillInterviewingConfigurationId;

	@Column(name = "skill_interviewing_configuration_version")
	private Integer skillInterviewingConfigurationVersion;
}
