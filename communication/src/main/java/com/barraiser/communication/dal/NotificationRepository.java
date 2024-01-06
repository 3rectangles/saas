package com.barraiser.communication.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationDAO, String> {
    List<NotificationDAO> findAllByEventTypeAndPartnerIdIsNotNullAndDisabledOnIsNull(String eventType);

    List<NotificationDAO> findAllByConfigIdAndDisabledOnIsNull(String configId);

    NotificationDAO findAllByEventTypeAndPartnerIdIsNull(String eventType);

    NotificationDAO findAllByEventTypeAndPartnerIdAndConfigId(String eventType,String partnerId,String configId);
}
