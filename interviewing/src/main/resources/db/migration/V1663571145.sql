insert into event_to_entity (id, event_type, entity_type, entity_id_path)
values (uuid_generate_v4(), 'ExpertReminderForInterviewEvent', 'INTERVIEW', '{interviewId}');

insert into communication_template_config(id, event_type, channel, recipient_type,
template_rule, enabled, created_on, updated_on)
values (uuid_generate_v4(), 'ExpertReminderForInterviewEvent', 'WHATSAPP', 'EXPERT',
'ce8bdf1b-0f4a-4da7-92fc-7f3fca83eab3', true, now(), now());

insert into whatsapp_template(id, query, message_bird_template_name, message_bird_template_variables, created_on, updated_on)
values ('ce8bdf1b-0f4a-4da7-92fc-7f3fca83eab3', 'query getInterviews($input: GetInterviewsInput!) {
    getInterviews(input: $input) {
      interviewer {
        userDetails {
            firstName
        }
        timezone
      }
      scheduledStartDate
    }
  }', 'en_interview_reminder_expert',
  '{{getInterviews.0.interviewer.userDetails.firstName}}, {{formatEpochInSeconds getInterviews.0.scheduledStartDate getInterviews.0.interviewer.timezone}}, {{event.durationBeforeInterview}}',
  now(), now());
