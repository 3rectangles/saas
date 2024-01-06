/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interviewing.notes.dal;

import com.barraiser.common.dal.BaseModel;
import com.barraiser.onboarding.interviewing.InterviewingFirestoreData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Table(name = "interviewing_note")
public class InterviewingDataDAO extends BaseModel {
	@Id
	private String id;

	@Column(name = "interview_id")
	private String interviewId;

	@Type(type = "jsonb")
	@Column(name = "notes", columnDefinition = "jsonb")
	private List<InterviewingFirestoreData.Note> notes;

	@Type(type = "jsonb")
	@Column(name = "interview_flow", columnDefinition = "jsonb")
	private Object interviewFlow;

	@Column(name = "was_interviewer_video_on")
	private Boolean wasInterviewerVideoOn;
}
