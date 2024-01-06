package com.barraiser.onboarding.interview;

import com.barraiser.onboarding.dal.InterviewHistoryDAO;
import com.barraiser.onboarding.dal.InterviewHistoryRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InterviewHistoryManagerTest {
    @Mock
    private InterviewHistoryRepository interviewHistoryRepository;
    @InjectMocks
    private InterviewHistoryManager interviewHistoryManager;

    @Test
    public void shouldReturnInterviewChangeHistory() {
        when(this.interviewHistoryRepository
            .findAllByInterviewIdInAndCreatedOnIsNotNullOrderByCreatedOnAsc(List.of("i-1", "i-2")))
            .thenReturn(List.of(
                InterviewHistoryDAO.builder().id("1").interviewId("i-1").status("cancellation_done").build(),
                InterviewHistoryDAO.builder().id("2").interviewId("i-2").status("pending_scheduling").build()));
        final List<InterviewHistoryDAO> actual = this.interviewHistoryManager.getEarliestInterviewChangeHistoriesByField("i-1", "status");
        final List<InterviewHistoryDAO> expected = List.of(
            InterviewHistoryDAO.builder().id("1").interviewId("i-1").status("cancellation_done").build(),
            InterviewHistoryDAO.builder().id("2").interviewId("i-2").status("pending_scheduling").build());
        expected.forEach(x -> {
            final InterviewHistoryDAO ic = actual.stream().filter(y -> y.getId().equals(x.getId())).findFirst().get();
            assertEquals(x.getInterviewId(), ic.getInterviewId());
            assertEquals(x.getStatus(), ic.getStatus());
        });
    }
}
