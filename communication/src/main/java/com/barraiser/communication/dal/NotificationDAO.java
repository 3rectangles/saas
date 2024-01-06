package com.barraiser.communication.dal;

import com.barraiser.common.dal.BaseModel;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

import lombok.Data;
import org.hibernate.annotations.TypeDef;


@Entity
@Table(name = "slack_event_info")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class NotificationDAO extends BaseModel {
    @Id
    private String id;

    @Column(name="config_id")
    private String configId;

    @Column(name="partner_id")
    private String partnerId;

    @Column(name = "event_type")
    private String eventType;

    @Type(type = "jsonb")
    @Column(name = "template")
    private SlackMessageTemplate template;

    @Column(name = "disabled_on")
    private Instant disabledOn;
}
