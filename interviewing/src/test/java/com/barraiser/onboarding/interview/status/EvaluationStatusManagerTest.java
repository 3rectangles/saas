package com.barraiser.onboarding.interview.status;

import com.barraiser.onboarding.dal.EvaluationChangeHistoryRepository;
import com.barraiser.onboarding.dal.EvaluationDAO;
import com.barraiser.onboarding.dal.EvaluationRepository;
import com.barraiser.onboarding.dal.StatusDAO;
import com.barraiser.onboarding.dal.StatusOrderDAO;
import com.barraiser.onboarding.dal.StatusOrderRepository;
import com.barraiser.onboarding.dal.StatusRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class EvaluationStatusManagerTest {
    @InjectMocks
    private EvaluationStatusManager evaluationStatusManager;

    @Mock
    private StatusRepository statusRepository;
    @Mock
    private StatusOrderRepository statusOrderRepository;
    @Mock
    private EvaluationRepository evaluationRepository;
    @Mock
    private EvaluationChangeHistoryRepository evaluationChangeHistoryRepository;

    private final String TEST_PARTNER_ID = "test_p_c";

    @Test
    public void shouldReturnTrueForIsStatusCustomizedForPartnerIfAStatusExistsForPartnerId() {
        //GIVEN
        when(this.statusRepository.findTopByPartnerIdAndEntityType(any(), any()))
            .thenReturn(Optional.of(StatusDAO.builder().build()));

        //WHEN
        final Boolean statusIsCustomized = this.evaluationStatusManager.isStatusCustomizedForPartner(TEST_PARTNER_ID);

        //THEN
        verify(this.statusRepository).findTopByPartnerIdAndEntityType(TEST_PARTNER_ID, "evaluation");
        assertTrue(statusIsCustomized);
    }

    @Test
    public void shouldReturnFalseForIsStatusCustomizedForPartnerIfAStatusExistsForPartnerId() {
        //GIVEN
        when(this.statusRepository.findTopByPartnerIdAndEntityType(any(), any()))
            .thenReturn(Optional.empty());

        //WHEN
        final Boolean statusIsCustomized = this.evaluationStatusManager.isStatusCustomizedForPartner(TEST_PARTNER_ID);

        //THEN
        verify(this.statusRepository).findTopByPartnerIdAndEntityType(TEST_PARTNER_ID, "evaluation");
        assertFalse(statusIsCustomized);
    }

    @Test
    public void shouldReturnAllStatusForPartnerInOrderWhenStatusIsCustomized() {
        //GIVEN
        when(this.statusRepository.findTopByPartnerIdAndEntityType(any(), any()))
            .thenReturn(Optional.of(StatusDAO.builder().build()));

        //WHEN
        final List<StatusDAO> statusDAOs = this.evaluationStatusManager.getAllStatusForPartner(TEST_PARTNER_ID);

        //THEN
        verify(this.statusRepository).findAllByPartnerIdInAndEntityType(argThat(
            arg -> arg.contains("BarRaiser") && arg.contains(TEST_PARTNER_ID)
        ), eq("evaluation"));

        verify(this.statusOrderRepository).findAllByStatusIdIn(
            argThat(arg -> arg.containsAll(statusDAOs.stream().map(StatusDAO::getId).collect(Collectors.toList()))));

        final List<String> expectedStatusIds = List.of("1", "7", "6", "2", "4", "3", "5");
        assertEquals(expectedStatusIds, statusDAOs.stream().map(StatusDAO::getId).collect(Collectors.toList()));
    }

    @Test
    public void shouldReturnAllStatusForPartnerInOrderWhenStatusIsNotCustomized() {
        //GIVEN
        when(this.statusRepository.findTopByPartnerIdAndEntityType(any(), any()))
            .thenReturn(Optional.empty());

        //WHEN
        final List<StatusDAO> statusDAOs = this.evaluationStatusManager.getAllStatusForPartner(TEST_PARTNER_ID);

        //THEN
        verify(this.statusRepository).findAllDefaultEvaluationStatus();

        verify(this.statusOrderRepository).findAllByStatusIdIn(
            argThat(arg -> arg.containsAll(statusDAOs.stream().map(StatusDAO::getId).collect(Collectors.toList()))));

        final List<String> expectedStatusIds = List.of("1", "7", "6", "2", "4", "3", "5");
        assertEquals(expectedStatusIds, statusDAOs.stream().map(StatusDAO::getId).collect(Collectors.toList()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailTransitionPartnerStatusIfStatusIdIsNotFound() {
        //GIVEN
        when(this.statusRepository.findById(any())).thenReturn(Optional.empty());

        //WHEN
        this.evaluationStatusManager.transitionPartnerStatus("eid", "1", TEST_PARTNER_ID);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailTransitionPartnerStatusIfStatusEntityIsIncorrect() {
        //GIVEN
        when(this.statusRepository.findById(any())).thenReturn(
            Optional.of(StatusDAO.builder().id("1").entityType("interview").build())
        );

        //WHEN
        this.evaluationStatusManager.transitionPartnerStatus("eid", "1", "userId");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailTransitionPartnerStatusIfEvaluationIsNotDone() {
        //GIVEN
        when(this.statusRepository.findById(any())).thenReturn(
            Optional.of(StatusDAO.builder().id("1").entityType("evaluation").build())
        );
        when(this.evaluationRepository.findById(any())).thenReturn(Optional.of(
            EvaluationDAO.builder().id("eid").barraiserStatus(
                StatusDAO.builder().id("2").internalStatus("Cancelled").build()
            ).build()
        ));

        //WHEN
        this.evaluationStatusManager.transitionPartnerStatus("eid", "1", "userId");
    }

    @Test
    public void shouldSavePartnerStatusOnTransitionPartnerStatus() {
        //GIVEN
        when(this.statusRepository.findById(any())).thenReturn(
            Optional.of(StatusDAO.builder().id("1").entityType("evaluation").build())
        );
        when(this.evaluationRepository.findById(any())).thenReturn(Optional.of(
            EvaluationDAO.builder().id("eid").barraiserStatus(
                StatusDAO.builder().id("2").internalStatus("Done").build()
            ).build()
        ));

        //WHEN
        this.evaluationStatusManager.transitionPartnerStatus("eid", "1", "userId");

        verify(this.evaluationRepository).save(argThat(arg -> arg.getPartnerStatus().getId().equals("1")));
    }

    @Test
    public void shouldRecordStatusChangeOnTransitionPartnerStatusIfFromPartnerStatusIsNull() {
        //GIVEN
        when(this.statusRepository.findById(any())).thenReturn(
            Optional.of(StatusDAO.builder().id("1").entityType("evaluation").build())
        );
        when(this.evaluationRepository.findById(any())).thenReturn(Optional.of(
            EvaluationDAO.builder().id("eid").barraiserStatus(
                StatusDAO.builder().id("2").internalStatus("Done").build()
            ).build()
        ));

        //WHEN
        this.evaluationStatusManager.transitionPartnerStatus("eid", "1", "userId");

        //GIVEN
        verify(this.evaluationChangeHistoryRepository).save(argThat(arg ->
                arg.getEvaluationId().equals("eid") &&
                arg.getFieldName().equals("partner_status") &&
                arg.getFieldValue().equals("1") &&
                arg.getCreatedBy().equals("userId")
            ));
    }

    @Test
    public void shouldRecordStatusChangeOnTransitionPartnerStatusIfFromPartnerStatusIsDifferent() {
        //GIVEN
        when(this.statusRepository.findById(any())).thenReturn(
            Optional.of(StatusDAO.builder().id("1").entityType("evaluation").build())
        );
        when(this.evaluationRepository.findById(any())).thenReturn(Optional.of(
            EvaluationDAO.builder().id("eid").barraiserStatus(
                StatusDAO.builder().id("2").internalStatus("Done").build()
            )
                .partnerStatus(StatusDAO.builder().id("2").build())
                .build()
        ));

        //WHEN
        this.evaluationStatusManager.transitionPartnerStatus("eid", "1", "userId");

        //GIVEN
        verify(this.evaluationChangeHistoryRepository).save(argThat(arg ->
            arg.getEvaluationId().equals("eid") &&
                arg.getFieldName().equals("partner_status") &&
                arg.getFieldValue().equals("1") &&
                arg.getCreatedBy().equals("userId")
        ));
    }

    @Test
    public void shouldNotRecordStatusChangeOnTransitionPartnerStatusIfFromPartnerStatusIsEqual() {
        //GIVEN
        when(this.statusRepository.findById(any())).thenReturn(
            Optional.of(StatusDAO.builder().id("1").entityType("evaluation").build())
        );
        when(this.evaluationRepository.findById(any())).thenReturn(Optional.of(
            EvaluationDAO.builder().id("eid").barraiserStatus(
                StatusDAO.builder().id("2").internalStatus("Done").build()
            )
                .partnerStatus(StatusDAO.builder().id("1").build())
                .build()
        ));

        //WHEN
        this.evaluationStatusManager.transitionPartnerStatus("eid", "1", "userId");

        //GIVEN
        verify(this.evaluationChangeHistoryRepository, never()).save(any());
    }

    @Test
    public void shouldReturnDisplayStatusForPartnerInOrder() {
        //GIVEN
        when(this.statusRepository.findTopByPartnerIdAndEntityType(any(), any()))
            .thenReturn(Optional.of(StatusDAO.builder().build()));

        // WHEN
        final List<String> displayStatus = this.evaluationStatusManager.getAllDisplayStatusForPartner(TEST_PARTNER_ID);

        final List<String> expectedDisplayStatus = List.of("A", "C", "B", "D");

        //THEN
        assertEquals(expectedDisplayStatus, displayStatus);
    }

    @Before
    public void setup() {
        when(this.statusRepository.findAllByPartnerIdInAndEntityType(any(), any()))
            .thenReturn(Arrays.asList(
                StatusDAO.builder().id("1").partnerId(TEST_PARTNER_ID).displayStatus("A").build(),
                StatusDAO.builder().id("2").partnerId(TEST_PARTNER_ID).displayStatus("B").build(),
                StatusDAO.builder().id("3").partnerId(TEST_PARTNER_ID).displayStatus("C").build(),
                StatusDAO.builder().id("4").partnerId(TEST_PARTNER_ID).displayStatus("D").build(),
                StatusDAO.builder().id("5").partnerId("BarRaiser").displayStatus("A").build(),
                StatusDAO.builder().id("6").partnerId("BarRaiser").displayStatus("B").build(),
                StatusDAO.builder().id("7").partnerId("BarRaiser").displayStatus("C").build()
            ));
        when(this.statusOrderRepository.findAllByStatusIdIn(any()))
            .thenReturn(Arrays.asList(
                StatusOrderDAO.builder().id("1").statusId("1").partnerId(TEST_PARTNER_ID).orderIndex(0).build(),
                StatusOrderDAO.builder().id("2").statusId("2").partnerId(TEST_PARTNER_ID).orderIndex(3).build(),
                StatusOrderDAO.builder().id("3").statusId("3").partnerId(TEST_PARTNER_ID).orderIndex(5).build(),
                StatusOrderDAO.builder().id("4").statusId("4").partnerId(TEST_PARTNER_ID).orderIndex(4).build(),
                StatusOrderDAO.builder().id("5").statusId("5").partnerId(TEST_PARTNER_ID).orderIndex(6).build(),
                StatusOrderDAO.builder().id("6").statusId("6").partnerId(TEST_PARTNER_ID).orderIndex(2).build(),
                StatusOrderDAO.builder().id("7").statusId("7").partnerId(TEST_PARTNER_ID).orderIndex(1).build()
            ));
        when(this.statusRepository.findAllDefaultEvaluationStatus()).thenReturn(
            Arrays.asList(
                StatusDAO.builder().id("1").partnerId(null).displayStatus("A").build(),
                StatusDAO.builder().id("2").partnerId(null).displayStatus("B").build(),
                StatusDAO.builder().id("3").partnerId(null).displayStatus("C").build(),
                StatusDAO.builder().id("4").partnerId(null).displayStatus("D").build(),
                StatusDAO.builder().id("5").partnerId("BarRaiser").displayStatus("A").build(),
                StatusDAO.builder().id("6").partnerId("BarRaiser").displayStatus("B").build(),
                StatusDAO.builder().id("7").partnerId("BarRaiser").displayStatus("C").build()
            )
        );
    }
}
