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
@Getter
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@Table(name = "interview_round_type_configuration")
public class InterviewRoundTypeConfigurationDAO extends BaseModel {
    @Id
    private String id;

    //peer or expert or machine etc
    @Column(name = "round_type")
    private String roundType;

    @Column(name = "company_id")
    private String companyId;

    @Column(name = "candidate_start_time_offset_mintues")
    private Long candidateStartTimeOffsetMinutes;

    @Column(name = "candidate_end_time_offset_minutes")
    private Long candidateEndTimeOffsetMinutes;

    @Column(name = "expert_end_time_offset_minutes")
    private Long expertEndTimeOffsetMinutes;

    @Column(name = "interview_scheduled_candidate_email_template")
    private String interviewScheduledCandidateEmailTemplate;

    @Column(name = "interview_scheduled_expert_email_template")
    private String interviewScheduledExpertEmailTemplate;

    @Column(name = "interview_scheduled_candidate_calendar_invite_template")
    private String interviewScheduledCandidateCalendarInviteTemplate;

    @Column(name = "interview_scheduled_expert_calendar_invite_template")
    private String interviewScheduledExpertCalendarInviteTemplate;

    //This is a zoom link that can be used as a common gethering
    //area for candidates for rounds like machine round
    @Column(name = "common_zoom_link")
    private String commonZoomLink;

    //This can be used when we want to show different
    //descriptions on pre interview page. This can be the
    //text of the template
    @Column(name = "description_template")
    private String descriptionTemplate;

}
