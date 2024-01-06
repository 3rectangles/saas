/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.BaseModel;
import com.barraiser.onboarding.audit.AuditListener;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@EntityListeners(AuditListener.class)
@Entity
@Table(name = "candidate")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CandidateDAO extends BaseModel {
	@Id
	private String id;

	@Column(name = "user_id")
	private String userId;

	@Column(name = "alma_mater")
	private String almaMater;

	@Column(name = "birth_date")
	private String birthDate;

	@Column(name = "category")
	private String category;

	@Column(name = "country")
	private String country;

	@Column(name = "designation")
	private String designation;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "initials")
	private String initials;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "linked_in_profile")
	private String linkedInProfile;

	@Column(name = "work_experience_in_months")
	private Integer workExperienceInMonths;

	@Column(name = "current_ctc")
	private String currentCtc;

	@Column(name = "resume_url")
	private String resumeUrl;

	@Column(name = "redacted_resume_url")
	private String redactedResumeUrl;

	@Column(name = "current_company_id")
	private String currentCompanyId;

	@Column(name = "current_company_name")
	private String currentCompanyName;

	@Type(type = "list-array")
	@Column(columnDefinition = "text[]", name = "last_companies")
	@Builder.Default
	private final List<String> lastCompanies = new ArrayList<>();

	@Type(type = "list-array")
	@Column(columnDefinition = "text[]", name = "achievements")
	@Builder.Default
	private final List<String> achievements = new ArrayList<>();

	@Column(name = "timezone")
	private String timezone;

	@Column(name = "resume_id")
	private String resumeId;

}
