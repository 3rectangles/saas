package com.barraiser.onboarding.dal;

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
@Table(name = "event_logs")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class EventLogsDAO extends BaseModel {
    @Id
    private String id;

    @Column(name = "source")
    private String source;

    @Column(name = "timestamp")
    private Long timestamp;

    @Column(name = "version")
    private String version;

    @Column(name = "payload")
    private String payload;

    @Column(name = "type")
    private String type;
}
