/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "expert_normalised_rating_history")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ExpertNormalisedRatingHistoryDAO extends BaseModel {

	@Id
	private String id;

	private String interviewerId;

	private Double rating;

	private Double normalisedRating;

	private Double average;

	private Double standardDeviation;

	private Double cappedNormalisedRating;

	private String normalisationVersion;
}
