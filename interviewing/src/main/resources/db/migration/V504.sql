
-- event_to_entity

insert into event_to_entity (id, event_type, entity_type, entity_id_path)
values (uuid_generate_v4(), 'InterviewReminderEvent', 'INTERVIEW', '{interview,id}');


-- communication_template_config

insert into communication_template_config (id, event_type, channel, recipient_type, template_rule, enabled)
values (uuid_generate_v4(), 'InterviewReminderEvent', 'EMAIL', 'CANDIDATE', 13, true);

insert into communication_template_config (id, event_type, channel, recipient_type, template_rule, enabled)
values (uuid_generate_v4(), 'InterviewReminderEvent', 'WHATSAPP', 'CANDIDATE', 3, true);


-- email_template

insert into email_template (id, subject, body, query, branding)
values (13, '{{getInterviews.0.jobRole.company.name}} Interview in 15 mins', '{{#partial "content" }}
<p>Hi {{getInterviews.0.interviewee.firstName}} {{getInterviews.0.interviewee.lastName}},</p>
<p>You have an interview starting in next 15 mins at <strong>{{formatEpochInSeconds getInterviews.0.scheduledStartDate getInterviews.0.timezone}}</strong></p>

<div align="center" width="300" height="40" bgcolor="#0898A9" style="-webkit-border-radius: 5px;
   -moz-border-radius: 5px;
   border-radius: 5px;
   color: #ffffff;
   display: block;
   width:270px;
   height:40px;
   background-color:#0898A9;
   margin-left:auto;margin-right:auto">
   <a href="https://app.barraiser.com/interview-landing/c/{{getInterviews.0.id}}?magic_token={{getInterviews.0.interviewee.magicToken}}"
      style="font-size:16px;
      font-weight: bold;
      font-family: Helvetica, Arial, sans-serif;
      text-decoration: none;
      line-height:40px;
      width:100%;
      display:inline-block">
   <span style="color: #FFFFFF">Join Interview</span></a>
</div>

<p>Zoom passcode: 123456</p>

<p>Wishing you all the luck!</p>

<p>Regards,<br>
BarRaiser Team
</p>
{{/partial}}', 'query getInterviews($input: GetInterviewsInput!) {
    getInterviews(input: $input) {
        id
        scheduledStartDate
        scheduledEndDate
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
}', 'BARRAISER');



-- whatsapp_template

insert into whatsapp_template (id, query, message_bird_template_name, message_bird_template_variables)
values (3, 'query getInterviews($input: GetInterviewsInput!) {
    getInterviews(input: $input) {
        id
        scheduledStartDate
        scheduledEndDate
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
}', 'candidate_interview_reminder_v4', '{{getInterviews.0.jobRole.company.name}},{{getInterviews.0.interviewee.firstName}},{{getInterviews.0.interviewee.lastName}},{{formatEpochInSeconds getInterviews.0.scheduledStartDate getInterviews.0.interviewee.timezone}},{{getInterviews.0.id}},{{getInterviews.0.interviewee.magicToken}}');

