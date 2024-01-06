/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.BaseModel;
import com.barraiser.common.dal.Money;
import com.barraiser.onboarding.audit.AuditListener;
import com.vladmihalcea.hibernate.type.array.ListArrayType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@EntityListeners(AuditListener.class)
@Entity
@Table(name = "user_details")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@TypeDef(name = "list-array", typeClass = ListArrayType.class)
public class UserDetailsDAO extends BaseModel {
	@Id
	private String id;

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

	@Column(name = "email")
	private String email;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "initials")
	private String initials;

	@Type(type = "jsonb")
	@Column(columnDefinition = "jsonb")
	private Money cost;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "linked_in_profile")
	private String linkedInProfile;

	@Column(name = "phone")
	private String phone;

	@Column(name = "role")
	private String role;

	@Column(name = "work_experience")
	private String workExperience;

	@Column(name = "work_experience_in_months")
	private Integer workExperienceInMonths;

	@Column(name = "previous_work_experience_in_months")
	private Integer previousWorkExperienceInMonths;

	@Column(name = "current_ctc")
	private String currentCtc;

	@Column(name = "is_expert_partner")
	private Boolean isExpertPartner;

	@Column(name = "resume_document_id")
	private String resumeDocumentId;

	@Column(name = "resume_url")
	private String resumeUrl;

	@Column(name = "redacted_resume_url")
	private String redactedResumeUrl;

	@Column(name = "current_company_id")
	private String currentCompanyId;

	@Column(name = "current_company_name")
	private String currentCompanyName;

	@Column(name = "ops_rep")
	private String opsRep;

	@Column(name = "is_active")
	private Boolean isActive;

	@Type(type = "list-array")
	@Column(columnDefinition = "text[]", name = "expert_domains")
	private List<String> expertDomains;

	@Type(type = "list-array")
	@Column(columnDefinition = "text[]", name = "peer_domains")
	private List<String> peerDomains;

	@Type(type = "list-array")
	@Column(columnDefinition = "text[]", name = "domains")
	@Builder.Default
	private final List<String> domains = new ArrayList<>();

	@Type(type = "list-array")
	@Column(columnDefinition = "text[]", name = "skill_ids")
	@Builder.Default
	private final List<String> skillIds = new ArrayList<>();

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

	@Column(name = "whatsapp_number")
	private String whatsappNumber;

	@Column(name = "country_code")
	private String countryCode;
}
