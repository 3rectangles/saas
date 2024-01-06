package com.barraiser.onboarding.interviewing.interviewpad;

import com.barraiser.onboarding.dal.InterviewPadDAO;
import com.barraiser.onboarding.dal.InterviewPadRepository;
import com.barraiser.onboarding.interviewing.interviewpad.codejudge.CodeJudgeBulkCreateResponse;
import com.barraiser.onboarding.interviewing.interviewpad.codejudge.CodeJudgeService;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class InterviewPadGenerationService {
  private final InterviewPadRepository interviewPadRepository;
  private final CodeJudgeService codeJudgeService;

  public InterviewPadDAO getInterviewPad(final String interviewId) {
    return this.interviewPadRepository
        .findByInterviewId(interviewId)
        .orElseGet(() -> getNewAndSaveInterviewPad(interviewId));
  }

  private InterviewPadDAO getNewAndSaveInterviewPad(final String interviewId) {
    final CodeJudgeBulkCreateResponse newPad = this.codeJudgeService.generateNewPad();
    final InterviewPadDAO interviewPadDAO =
        InterviewPadDAO.builder()
            .id(UUID.randomUUID().toString())
            .interviewId(interviewId)
            .intervieweePad(newPad.getResponse().get(0).getCandidate())
            .interviewerPad(newPad.getResponse().get(0).getInterviewer_1())
            .build();

    this.interviewPadRepository.save(interviewPadDAO);
    return interviewPadDAO;
  }
}
