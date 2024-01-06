/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.BaseModel;
import com.barraiser.common.enums.Weightage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Table(name = "feedback")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackDAO extends BaseModel {
	@Id
	private String id;

	// Question id for a feedback , interview id for overall
	private String referenceId;

	// Feedback type. Example : Overall Strength, Overall Areas Of Inprovement , Per
	// Question etc
	private String type;

	private String categoryId;

	private Float rating;

	private Float weightage;

	private String difficulty;

	private Boolean handsOn;

	private String feedback;

	// --- Fields related to overall feedback ---

	private String strength;

	private String areasOfImprovement;

	private Boolean looksGood;

	// -------------------------------------------

	// @OneToMany(targetEntity = QcCommentDAO.class, mappedBy = "feedbackId")
	// private List<QcCommentDAO> qcComments;

	private Float normalisedRating;

	@Column(name = "reschedule_count")
	private Integer rescheduleCount;

	@Type(type = "jsonb")
	@Column(columnDefinition = "jsonb", name = "normalised_rating_mappings")
	private List<NormalisedRatingMapping> normalisedRatingMappings;

	@Enumerated(EnumType.STRING)
	@Column(name = "feedback_weightage")
	private Weightage feedbackWeightage;
}
