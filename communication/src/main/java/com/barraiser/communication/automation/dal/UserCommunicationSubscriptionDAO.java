package com.barraiser.communication.automation.dal;

import com.barraiser.common.dal.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_communication_subscription")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserCommunicationSubscriptionDAO extends BaseModel {
    @Id
    private String id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "event_type")
    private String eventType;

    @Column(name = "subscription_rule")
    private String subscriptionRule;
}
