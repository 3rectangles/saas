insert into communication_template_config (id, event_type, channel, recipient_type, template_rule, enabled, created_on, updated_on)
values (uuid_generate_v4(), 'InterviewScheduledEvent', 'WHATSAPP', 'EXPERT', '3cdd9898-ab88-4246-a08b-0a06994785f4', true, now(), now());

insert into whatsapp_template(id, query, message_bird_template_name, message_bird_template_variables, created_on, updated_on)
values ('3cdd9898-ab88-4246-a08b-0a06994785f4', ' query getInterviews($input: GetInterviewsInput!) {
    getInterviews(input: $input) {
      interviewer {
          userDetails {
            firstName
          }
        timezone
      }
      scheduledStartDate
    }
  }', 'en_expert_interview_scheduled', '{{getInterviews.0.interviewer.userDetails.firstName}}, {{formatEpochInSeconds getInterviews.0.scheduledStartDate getInterviews.0.interviewer.timezone}}', now(), now());
