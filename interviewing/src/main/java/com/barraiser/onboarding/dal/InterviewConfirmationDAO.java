/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "interview_confirmation")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class InterviewConfirmationDAO extends BaseModel {
	@Id
	private String id;

	@Column(name = "interview_id")
	private String interviewId;

	@Column(name = "candidate_confirmation")
	private Boolean candidateConfirmation;

	@Column(name = "interviewer_confirmation")
	private Boolean interviewerConfirmation;

	private Instant candidateConfirmationTime;

	private Instant interviewerConfirmationTime;

	private String communicationChannel;

	@Column(name = "reschedule_count")
	private Integer rescheduleCount;

	@Column(name = "candidate_confirmation_given_by")
	private String candidateConfirmationGivenBy;
}
