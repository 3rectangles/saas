package com.barraiser.onboarding.interview;

import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.InterviewStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InterviewManagerTest {
    @Mock
    private InterViewRepository interViewRepository;
    @InjectMocks
    private InterviewManager interviewManager;

    @Test
    public void shouldReturnFalseIfInterviewIsNotCancelled() {
        final Boolean isInterviewCancelledByExpert = this.interviewManager.isInterviewCancelled(InterviewDAO.builder()
                .status("Done").build(), List.of("1","2"));
        assertEquals(false, isInterviewCancelledByExpert);
    }

    @Test
    public void shouldReturnFalseIfInterviewIsNotCancelledByExpert() {
        final Boolean isInterviewCancelledByExpert = this.interviewManager.isInterviewCancelled(InterviewDAO.builder()
                .status("cancellation_done").cancellationReasonId("3").build(), List.of("1","2"));
        assertEquals(false, isInterviewCancelledByExpert);
    }

    @Test
    public void shouldReturnTrueIfInterviewIsCancelledByExpert() {
        final Boolean isInterviewCancelledByExpert = this.interviewManager.isInterviewCancelled(InterviewDAO.builder()
                .status("cancellation_done").cancellationReasonId("1").build(), List.of("1","2"));
        assertEquals(true, isInterviewCancelledByExpert);
    }

    @Test
    public void shouldReturnFalseIfInterviewIsNotLastMinuteCancelled() {
        final Boolean isInterviewLastMinuteCancelled = this.interviewManager.isInterviewLastMinuteCancelled(InterviewDAO.builder()
            .cancellationTime("1628114400").startDate(1628118000L).build());
        assertEquals(false, isInterviewLastMinuteCancelled);
    }

    @Test
    public void shouldReturnTrueIfInterviewIsLastMinuteCancelled() {
        final Boolean isInterviewLastMinuteCancelled = this.interviewManager.isInterviewLastMinuteCancelled(InterviewDAO.builder()
            .cancellationTime("1628114400").startDate(1628116200L).build());
        assertEquals(true, isInterviewLastMinuteCancelled);
    }

    @Test
    public void shouldReturnZeroCancelledInterviews() {
        final Map<String, Integer> countPerInterviewStatus = this.interviewManager.getCountPerInterviewStatus(
            List.of(InterviewDAO.builder().status("pending_qc").build()), List.of("1", "2"));
        final Map<String, Integer> actualCountPerInterviewStatus = Map.of("Done", 0,
            "cancellation_done", 0, "last_minute_cancelled_interview", 0);
        countPerInterviewStatus.forEach((x, y) -> assertEquals(actualCountPerInterviewStatus.get(x), y));
    }

    @Test
    public void shouldReturnNonZeroCancelledInterviews() {
        final List<InterviewDAO> interviewDAOs = List.of(InterviewDAO.builder().status("Done").build(), InterviewDAO.builder().status("cancellation_done")
                .startDate(1628118000L).cancellationTime("1628114400").cancellationReasonId("1").build(),
            InterviewDAO.builder().status("cancellation_done").startDate(1628116200L).cancellationReasonId("1")
                .cancellationTime("1628114400").build());
        final Map<String, Integer> countPerInterviewStatus = this.interviewManager.getCountPerInterviewStatus(interviewDAOs, List.of("1", "2"));
        final Map<String, Integer> actualCountPerInterviewStatus = Map.of("Done", 1,
            "cancellation_done", 2, "last_minute_cancelled_interview", 1);
        countPerInterviewStatus.forEach((x, y) -> assertEquals(actualCountPerInterviewStatus.get(x), y));
    }

    @Test
    public void shouldReturnNonZeroCancelledInterviewsByExpert() {
        final Map<String, Integer> countPerInterviewStatus = this.interviewManager.getCountPerInterviewStatus(
            List.of(InterviewDAO.builder().status("Done").build(), InterviewDAO.builder().status("cancellation_done").startDate(1628118000L).cancellationTime("1628114400").cancellationReasonId("1").build(),
                InterviewDAO.builder().status("cancellation_done").startDate(1628116200L).cancellationReasonId("3").cancellationTime("1628114400").build()), List.of("1", "2"));
        final Map<String, Integer> actualCountPerInterviewStatus = Map.of("Done", 1,
            "cancellation_done", 1, "last_minute_cancelled_interview", 0);
        countPerInterviewStatus.forEach((x, y) -> assertEquals(actualCountPerInterviewStatus.get(x), y));
    }

    @Test
    public void shouldReturnInterviewsPerExpert() {
        final Specification<InterviewDAO> specification = null;
        when(this.interViewRepository.findAll(specification))
            .thenReturn(List.of(InterviewDAO.builder().id("1").interviewerId("1").build(),
                InterviewDAO.builder().id("2").interviewerId("1").build(),
                InterviewDAO.builder().id("3").interviewerId("2").build()));
        final Map<String, List<InterviewDAO>> interviewPerInterviewer = Map.of("1", List.of(InterviewDAO.builder().id("1").interviewerId("1").build(),
            InterviewDAO.builder().id("2").interviewerId("1").build()),
            "2", List.of(InterviewDAO.builder().interviewerId("2").id("3").build()));
        final Map<String, List<InterviewDAO>> actualInterviewPerInterviewer = this.interviewManager.getInterviewsPerExpert(specification);
        for (Map.Entry<String, List<InterviewDAO>> entry : actualInterviewPerInterviewer.entrySet()) {
            final List<InterviewDAO> interviews = entry.getValue();
            int index = 0;
            for (InterviewDAO interviewDAO : interviews) {
                assertEquals(interviewPerInterviewer.get(entry.getKey()).get(index).getInterviewerId(), interviewDAO.getInterviewerId());
                assertEquals(interviewPerInterviewer.get(entry.getKey()).get(index).getId(), interviewDAO.getId());
                index++;
            }
        }
    }

    @Test
    public void shouldReturnAllLinkedInterviews() {
        when(this.interViewRepository
            .findAllByEvaluationIdAndInterviewStructureId("e-1", "is-1"))
            .thenReturn(List.of(InterviewDAO.builder().id("1").build(), InterviewDAO.builder().id("2").build()));
        final InterviewDAO interviewDAO = InterviewDAO.builder().id("1").evaluationId("e-1").interviewStructureId("is-1").build();
        final List<InterviewDAO> actualInterviewDAOs = this.interviewManager.getLinkedInterviews(interviewDAO);
        final List<InterviewDAO> expected = List.of(InterviewDAO.builder().id("1").build(), InterviewDAO.builder().id("2").build());
        assertEquals(expected.size(), actualInterviewDAOs.size());
        expected.forEach(x -> assertEquals(x.getId(), actualInterviewDAOs.stream().filter(y -> y.getId()
            .equals(x.getId())).findFirst().get().getId()));
    }
}
