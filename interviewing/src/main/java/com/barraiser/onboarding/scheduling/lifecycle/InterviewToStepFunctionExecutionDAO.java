/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.lifecycle;

import com.barraiser.onboarding.sfn.FlowType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "interview_to_step_function_execution")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class InterviewToStepFunctionExecutionDAO {
	@Id
	private String id;

	private String interviewId;

	private String ExecutionArn;

	@Enumerated(EnumType.STRING)
	private FlowType flowType;

	@Column(name = "created_on", updatable = false)
	@CreationTimestamp
	private Instant createdOn;

	@Column(name = "reschedule_count")
	private Integer rescheduleCount;
}
