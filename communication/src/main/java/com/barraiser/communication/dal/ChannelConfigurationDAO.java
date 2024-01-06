package com.barraiser.communication.dal;

import com.barraiser.common.dal.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;


@Entity
@Table(name = "channel_configuration")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChannelConfigurationDAO extends BaseModel {
    @Id
    private String id;
    @Column(name = "target_entity_id")
    private String targetEntityId;

    @Column(name = "target_entity_type")
    private String targetEntityType;

    @Column(name = "recipient")
    private String recipient;

    @Column(name = "recipient_id")
    private String recipientId;

    @Column(name = "channel_type")
    private String channelType;

    @Column(name = "secrets")
    private String secrets;

    @Column(name = "disabled_on")
    private Instant disabledOn;

}
