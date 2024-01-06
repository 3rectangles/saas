package com.barraiser.communication.dal;

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
@Table(name = "event_template")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class TemplateDAO extends BaseModel{
    @Id
    private String id;

    @Column(name = "event_type")
    private String eventType;

    @Column(name="event_desc")
    private String eventDesc;

    @Column(name = "template")
    private String template;


}
