/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.BaseModel;
import com.vladmihalcea.hibernate.type.array.ListArrayType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.util.List;

@Entity
@Table(name = "partner_company")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@TypeDef(name = "list-array", typeClass = ListArrayType.class)
public class PartnerCompanyDAO extends BaseModel {

	@Id
	private String id;
	private String companyId;
	private String hrEmail;
	private String hrPhoneNumber;
	private String escalationEmail;

	@Type(type = "list-array")
	@Column(columnDefinition = "text[]", name = "email_domains")
	private List<String> emailDomains;

	private Boolean isCandidateSchedulingEnabled;

	@Column(name = "is24hour_scheduling_allowed")
	private Boolean is24HourSchedulingAllowed;

	@Column(name = "host_allowed_redirect")
	private String hostAllowedRedirect;

	@Column(name = "total_upload_allowed")
	private Integer totalUploadAllowed;

	private String partnershipModelId;
	@Column(name = "scale_bgs")
	private Integer scaleBGS;

	@Column(name = "scale_scoring")
	private Integer scaleScoring;

	private Integer overallTextFormat;

	@Column(name = "is_normalisation_enabled")
	private Boolean isNormalisationEnabled;

	@Column(name = "is_linear_score")
	private Boolean isLinearScore;

	@Column(name = "default_interview_structure")
	private String defaultInterviewStructure;

	/**
	 * Wether the company has opted in for using BR Feedback mechanism
	 * or fill the feedback on ATS Itself.
	 */
	@Column(name = "use_ats_feedback")
	private Boolean useATSFeedback;

	@Column(name = "max_free_trial_interviews")
	private Integer maxFreeTrialInterviews;
}
