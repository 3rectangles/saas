/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "training_snippet")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class TrainingSnippetDAO extends BaseModel {
	@Id
	private String id;
	@Column(name = "user_id")
	private String userId;
	@Column(name = "partner_id")
	private String partnerId;
	private String title;
	private String description;
	@Column(name = "start_time")
	private long startTime;
	@Column(name = "end_time")
	private long endTime;
	@Column(name = "video_id")
	private String videoId;
	@Column(name = "video_url")
	private String videoURL;
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "training_snippet_id")
	private List<TrainingTagMappingDAO> tagList;
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	@JoinColumn(name = "training_snippet_id")
	private List<TrainingJobRoleMappingDAO> jobRoleList;
}
