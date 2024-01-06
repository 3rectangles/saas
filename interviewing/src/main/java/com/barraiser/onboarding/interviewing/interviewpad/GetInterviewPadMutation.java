package com.barraiser.onboarding.interviewing.interviewpad;

import com.barraiser.onboarding.dal.InterviewPadDAO;
import com.barraiser.onboarding.graphql.GraphQLMutation;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class GetInterviewPadMutation implements GraphQLMutation<InterviewPad> {
  private final InterviewPadGenerationService interviewPadGenerationService;
  private final GraphQLUtil graphQLUtil;

  @Override
  public String name() {
    return "getInterviewPad";
  }

  @Override
  public InterviewPad get(final DataFetchingEnvironment environment) throws Exception {

    final GetInterviewPadInput input =
        this.graphQLUtil.getInput(environment, GetInterviewPadInput.class);
    final InterviewPadDAO codingPads =
        this.interviewPadGenerationService.getInterviewPad(input.getInterviewId());
    return InterviewPad.builder()
        .intervieweePad(codingPads.getIntervieweePad())
        .interviewerPad(codingPads.getInterviewerPad())
        .build();
  }
}
