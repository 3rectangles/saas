/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.common.search.graphql.typeResolvers;

import com.barraiser.common.graphql.types.CandidateInterviewFeedback;
import com.barraiser.common.graphql.types.Interview;
import com.barraiser.common.graphql.types.JobRole;
import com.barraiser.onboarding.graphql.typeResolvers.TypeResolver;
import com.barraiser.common.graphql.types.Evaluation;
import com.barraiser.common.graphql.types.SkillInterviewingConfiguration.SkillInterviewingConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class SearchRecordTypeResolver implements TypeResolver {

	@Override
	public String type() {
		return "SearchRecord";
	}

	@Override
	public graphql.schema.TypeResolver getTypeResolver() {
		return env -> {
			final Object javaObject = env.getObject();
			if (javaObject instanceof Evaluation) {
				return env.getSchema().getObjectType("Evaluation");
			}
			if (javaObject instanceof SkillInterviewingConfiguration) {
				return env.getSchema().getObjectType("SkillInterviewingConfiguration");
			}
			if (javaObject instanceof JobRole) {
				return env.getSchema().getObjectType("JobRole");
			}
			if (javaObject instanceof CandidateInterviewFeedback) {
				return env.getSchema().getObjectType("CandidateInterviewFeedback");
			}
			if (javaObject instanceof Interview) {
				return env.getSchema().getObjectType("Interview");
			}

			return null;
		};
	}
}
