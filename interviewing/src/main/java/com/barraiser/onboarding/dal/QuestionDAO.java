/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import com.barraiser.onboarding.audit.AuditBaseModel;
import com.barraiser.onboarding.audit.AuditListener;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;

@EntityListeners(AuditListener.class)
@Entity
@Getter
@Table(name = "question")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDAO extends AuditBaseModel {
	@Id
	private String id;

	private String interviewId;

	@Column(name = "start_time_epoch")
	private Long startTimeEpoch;

	@Column(name = "end_time")
	private Long endTime;

	@Column(name = "question")
	private String question;

	@Column(name = "type")
	private String type;

	@Column(name = "difficulty")
	private String difficulty;

	@Column(name = "hands_on")
	private Boolean handsOn;

	@Column(name = "irrelevant")
	private Boolean irrelevant;

	@Column(name = "serial_number")
	private Integer serialNumber;

	@Column(name = "master_question_id")
	private String masterQuestionId;

	@OneToMany(fetch = FetchType.EAGER, targetEntity = FeedbackDAO.class, mappedBy = "referenceId")
	// @JoinTable(name = "feedback", joinColumns = {@JoinColumn(name =
	// "questionId")})
	private List<FeedbackDAO> feedbacks;

	@Column(name = "is_default")
	private Boolean isDefault;

	@Type(type = "list-array")
	@Column(columnDefinition = "text[]", name = "question_tags")
	private List<String> tags;

	@Column(name = "start_time_predicted")
	private Long startTimePredicted;

	@Column(name = "question_to_transcript_match_score")
	private Float questionToTranscriptMatchScore;

	@Column(name = "transcript_text")
	private String transcriptText;

	@Column(name = "reschedule_count")
	private Integer rescheduleCount;

	@Column(name = "question_category")
	private String questionCategory;
}
