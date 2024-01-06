package com.barraiser.communication.automation.dal;

import com.barraiser.common.dal.BaseModel;
import com.barraiser.common.entity.EntityType;
import com.barraiser.communication.automation.constants.Channel;
import com.barraiser.communication.automation.constants.RecipientType;
import com.barraiser.communication.automation.constants.Status;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "communication_log")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CommunicationLogDAO extends BaseModel {
    @Id
    private String id;

    @Column(name = "event_type")
    private String eventType;

    @Enumerated(EnumType.STRING)
    @Column(name = "recipient_type")
    private RecipientType recipientType;

    @Column(name = "entity_id")
    private String entityId;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type")
    private EntityType entityType;

    @Column(name = "partner_id")
    private String partnerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel")
    private Channel channel;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Type(type = "jsonb")
    @Column(name = "payload", columnDefinition = "jsonb")
    private JsonNode payload;
}
