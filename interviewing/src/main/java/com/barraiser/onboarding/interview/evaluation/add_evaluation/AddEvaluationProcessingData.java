/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.add_evaluation;

import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.JobRoleDAO;
import com.barraiser.common.graphql.types.AddBulkEvaluationsResult;
import com.barraiser.onboarding.dal.ParsedResumeDAO;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AddEvaluationProcessingData {
	private String candidateName;
	private Boolean isCandidateAnonymous;
	private String email;
	private String phone;
	private Integer workExperience;
	private String resumeUrl;
	private JobRoleDAO jobRoleDAO;
	private String pocEmail;
	private String userId;
	private String candidateId;
	private String evaluationJiraKey;
	private String evaluationId;
	private List<InterviewDAO> interviewDAOs;
	private AddBulkEvaluationsResult result;
	private AuthenticatedUser authenticatedUser;
	private String documentId;
	private String documentLink;
	private ParsedResumeDAO parsedResumeDAO;
	private Boolean isAtsEvaluation;
	private Boolean isAddedViaCalendarInterception;
	private String partnerId;
	private Boolean shouldNameSplit;
	private Boolean forcedAddFlag;
}
