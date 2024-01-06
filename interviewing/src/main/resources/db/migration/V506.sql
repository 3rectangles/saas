--event_to_entity

insert into event_to_entity (id, event_type, entity_type, entity_id_path)
values (uuid_generate_v4(), 'SendInterviewScheduledInfoEvent', 'INTERVIEW', '{interviewId}');

-- communication_template_config
insert into communication_template_config (id, event_type, channel, recipient_type, template_rule, enabled, created_on, updated_on)
values (uuid_generate_v4(), 'SendInterviewScheduledInfoEvent', 'WHATSAPP', 'CANDIDATE', '661206c8-8b72-4626-b2cb-6b0a72dfb880', true, now(), now());

-- whatsapp_template
insert into whatsapp_template (id, query, message_bird_template_name, message_bird_template_variables, created_on, updated_on)
values ('661206c8-8b72-4626-b2cb-6b0a72dfb880', 'query getInterviews($input: GetInterviewsInput!) {
    getInterviews(input: $input) {
        id
        scheduledStartDate
        scheduledEndDate
        durationInMinutes
        expertScheduledStartDate
        isRescheduled
        interviewRound
        zoomLink
        interviewee {
            firstName
            lastName
            timezone
           magicToken
        }
        jobRole {
            company {
                name
            }
        }
    }
}', 'candidate_interview_scheduled_information', '{{getInterviews.0.jobRole.company.name}},{{getInterviews.0.interviewee.firstName}},{{getInterviews.0.interviewee.lastName}},{{getInterviews.0.durationInMinutes}},{{formatEpochInSeconds getInterviews.0.scheduledStartDate getInterviews.0.interviewee.timezone}},{{getInterviews.0.id}}?magic_token={{getInterviews.0.interviewee.magicToken}}', now(), now());
