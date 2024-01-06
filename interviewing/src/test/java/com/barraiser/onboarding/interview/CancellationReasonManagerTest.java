package com.barraiser.onboarding.interview;

import com.barraiser.onboarding.dal.CancellationReasonDAO;
import com.barraiser.onboarding.dal.CancellationReasonRepository;
import com.barraiser.onboarding.dal.InterviewDAO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CancellationReasonManagerTest {
    @Mock
    private CancellationReasonRepository cancellationReasonRepository;
    @InjectMocks
    private CancellationReasonManager cancellationReasonManager;

    @Test
    public void shouldReturnCancellationReasonsForInterviewsCancelledByExpertTest() {
        when(this.cancellationReasonRepository.findAllByCancellationType("EXPERT"))
            .thenReturn(List.of(CancellationReasonDAO.builder().id("1").build(), CancellationReasonDAO.builder().id("2").build()));
        final List<String> cancellationReasonIds = List.of("1", "2");
        final List<CancellationReasonDAO> cancellationReasonDAOs = this.cancellationReasonManager.getCancellationReasonsForInterviewsCancelledByExpert();
        AtomicInteger index = new AtomicInteger();
        cancellationReasonDAOs.forEach(x -> {
            assertEquals(x.getId(), cancellationReasonIds.get(index.get()));
            index.getAndIncrement();
        });
    }

    @Test
    public void shouldReturnCountPerCancellationReason() {
        final Map<String, Integer> expectedCountPerCancellationReason = Map.of("Health", 1, "Electricity", 2);
        final List<InterviewDAO> interviews = List.of(InterviewDAO.builder().cancellationReasonId("1").status("cancellation_done").build(),
            InterviewDAO.builder().cancellationReasonId("2").status("cancellation_done").build(),
            InterviewDAO.builder().cancellationReasonId("2").status("cancellation_done").build(),
            InterviewDAO.builder().cancellationReasonId("3").status("cancellation_done").build(),
            InterviewDAO.builder().status("pending_qc").build());
        final List<CancellationReasonDAO> cancellationReasonDAOs = List.of(CancellationReasonDAO.builder().id("1").cancellationReason("Health").build(),
            CancellationReasonDAO.builder().id("2").cancellationReason("Electricity").build());
        final Map<String, Integer> actualCountPerCancellationReason = this.cancellationReasonManager.getCountPerCancellationReason(interviews, cancellationReasonDAOs);
        actualCountPerCancellationReason.forEach((x, y) -> assertEquals(actualCountPerCancellationReason.get(x), expectedCountPerCancellationReason.get(x)));
    }

    @Test
    public void shouldReturnTrueIfCancelledByExpert() {
        when(this.cancellationReasonRepository.findById("1"))
            .thenReturn(Optional.ofNullable(CancellationReasonDAO.builder().cancellationType("EXPERT").build()));
        final Boolean actual = this.cancellationReasonManager.isCancelledByExpert("1");
        assertTrue(actual);
    }

    @Test
    public void shouldReturnFalseIfNotCancelledByExpert() {
        when(this.cancellationReasonRepository.findById("1"))
            .thenReturn(Optional.ofNullable(CancellationReasonDAO.builder().cancellationType("CANDIDATE").build()));
        final Boolean actual = this.cancellationReasonManager.isCancelledByExpert("1");
        assertFalse(actual);
    }

    @Test
    public void shouldReturnDisplayStatus() {
        when(this.cancellationReasonRepository.findById("1"))
            .thenReturn(Optional.of(CancellationReasonDAO.builder().customerDisplayableReason("REASON").build()));
        final String actualReason = this.cancellationReasonManager.getDisplayReason("1");
        assertEquals("REASON", actualReason);
    }
}
