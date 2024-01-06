--event_to_entity

insert into event_to_entity (id, event_type, entity_type, entity_id_path)
values (3, 'InterviewConfirmationEventTurn1', 'INTERVIEW', '{interview,id}');

insert into event_to_entity (id, event_type, entity_type, entity_id_path)
values (4, 'InterviewConfirmationEventTurn2', 'INTERVIEW', '{interview,id}');

insert into event_to_entity (id, event_type, entity_type, entity_id_path)
values (5, 'InterviewConfirmationEventTurn3', 'INTERVIEW', '{interview,id}');

insert into event_to_entity (id, event_type, entity_type, entity_id_path)
values (6, 'InterviewConfirmationEventTurn4.1', 'INTERVIEW', '{interview,id}');

insert into event_to_entity (id, event_type, entity_type, entity_id_path)
values (7, 'InterviewConfirmationEventTurn4.2', 'INTERVIEW', '{interview,id}');

insert into event_to_entity (id, event_type, entity_type, entity_id_path)
values (8, 'InterviewConfirmationEventTurn5', 'INTERVIEW', '{interview,id}');

insert into event_to_entity (id, event_type, entity_type, entity_id_path)
values (9, 'InterviewConfirmedEvent', 'INTERVIEW', '{interview,id}');

--communication_template_config

insert into communication_template_config (id, event_type, channel, recipient_type, template_rule, enabled)
values (5, 'InterviewConfirmationEventTurn1', 'EMAIL', 'CANDIDATE', 5, true);

insert into communication_template_config (id, event_type, channel, recipient_type, template_rule, enabled)
values (6, 'InterviewConfirmationEventTurn1', 'SMS', 'CANDIDATE', 1, true);

insert into communication_template_config (id, event_type, channel, recipient_type, template_rule, enabled)
values (7, 'InterviewConfirmationEventTurn1', 'WHATSAPP', 'CANDIDATE', 1, true);

insert into communication_template_config (id, event_type, channel, recipient_type, template_rule, enabled)
values (8, 'InterviewConfirmationEventTurn2', 'IVR', 'CANDIDATE', 1, true);

insert into communication_template_config (id, event_type, channel, recipient_type, template_rule, enabled)
values (9, 'InterviewConfirmationEventTurn3', 'EMAIL', 'CANDIDATE', 5, true);

insert into communication_template_config (id, event_type, channel, recipient_type, template_rule, enabled)
values (10, 'InterviewConfirmationEventTurn4.1', 'WHATSAPP', 'CANDIDATE', 1, true);

insert into communication_template_config (id, event_type, channel, recipient_type, template_rule, enabled)
values (11, 'InterviewConfirmationEventTurn4.2', 'EMAIL', 'CANDIDATE', 5, true);

insert into communication_template_config (id, event_type, channel, recipient_type, template_rule, enabled)
values (12, 'InterviewConfirmationEventTurn4.2', 'SMS', 'CANDIDATE', 1, true);

insert into communication_template_config (id, event_type, channel, recipient_type, template_rule, enabled)
values (13, 'InterviewConfirmationEventTurn5', 'IVR', 'CANDIDATE', 1, true);

insert into communication_template_config (id, event_type, channel, recipient_type, template_rule, enabled)
values (14, 'InterviewConfirmedEvent', 'EMAIL', 'CANDIDATE', 6, true);

insert into communication_template_config (id, event_type, channel, recipient_type, template_rule, enabled)
values (15, 'InterviewConfirmedEvent', 'EMAIL', 'OPERATIONS', 7, true);

--email_template

insert into email_template (id, subject, body, query, branding)
values (5, '[IMPORTANT] - {{getInterviews.0.jobRole.company.name}} Interview Confirmation', '{{#partial "content" }}
<span style="opacity: 0"> {{ randomNumberToPreventEmailTrimming }} </span>
<p>Dear {{getInterviews.0.interviewee.firstName}} {{getInterviews.0.interviewee.lastName}},</p>

<p>Please confirm your presence for the interview scheduled at <strong>{{formatEpochInSeconds getInterviews.0.scheduledStartDate getInterviews.0.interviewee.timezone}}</strong> for <strong>{{getInterviews.0.jobRole.company.name}}</strong></p>
<p></p>

<div style="text-align:center;">
   <a class="button approval" id="accept" style="background-color:#54BE65;" href = "https://www.barraiser.com/interview-confirmation/candidate/{{getInterviews.0.id}}?channel=email&accept=true">I will attend</a>
   <a class="button approval" id="decline" style="background-color:#F44336;" href = "https://www.barraiser.com/interview-confirmation/candidate/{{getInterviews.0.id}}?channel=email&accept=false" >Cancel</a>
</div>

<p>Thanks,<br>
Team BarRaiser
</p>
<span style="opacity: 0"> {{ randomNumberToPreventEmailTrimming }} </span>
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
        }
        jobRole {
            company {
                name
            }
        }
    }
}', 'BARRAISER');


insert into email_template (id, subject, body, query, branding)
values (6, 'Your upcoming interview is confirmed!', '{{#partial "content" }}
<span style="opacity: 0"> {{ randomNumberToPreventEmailTrimming }} </span>
<p>Hi {{getInterviews.0.interviewee.firstName}} {{getInterviews.0.interviewee.lastName}},</p>

<p>Your upcoming interview scheduled for <strong>{{formatEpochInSeconds getInterviews.0.scheduledStartDate getInterviews.0.interviewee.timezone}}</strong>, with <strong>{{getInterviews.0.jobRole.company.name}}</strong> has been confirmed.</p>
<p></p>

<p>Wishing you all the luck!</p>
<p>Regards,<br>
BarRaiser Team
</p>
<span style="opacity: 0"> {{ randomNumberToPreventEmailTrimming }} </span>
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
        }
        jobRole {
            company {
                name
            }
        }
    }
}', 'BARRAISER');


insert into email_template (id, subject, body, query, branding)
values (7, 'Candidate has confirmed Interview! <>  {{getInterviews.0.evaluation.jira}}', '{{#partial "content" }}
<span style="opacity: 0"> {{ randomNumberToPreventEmailTrimming }} </span>
<p>Hi Team,</p>

<p>This is regarding the interview of <strong>{{getInterviews.0.interviewee.firstName}} {{getInterviews.0.interviewee.lastName}}</strong> scheduled on <strong>{{formatEpochInSeconds getInterviews.0.scheduledStartDate getInterviews.0.interviewee.timezone}}</strong>. The candidate has confirmed that they will attend the interview.</p>

<p>Wishing you all the luck!</p>
<p>Regards,<br>
BarRaiser Team
</p>
<span style="opacity: 0"> {{ randomNumberToPreventEmailTrimming }} </span>
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
        }
        jobRole {
            company {
                name
            }
        }
    }
}', 'BARRAISER');


--sms_template

create table if not exists sms_template (
    id text primary key,
    body text,
    query text,
    created_on timestamp,
    updated_on timestamp
);


insert into sms_template (id, body, query)
values (1, '{{getInterviews.0.jobRole.company.name}} Interview Confirmation

Dear {{getInterviews.0.interviewee.firstName}} {{getInterviews.0.interviewee.lastName}}, please confirm your presence for {{getInterviews.0.jobRole.company.name}} interview scheduled at {{formatEpochInSeconds getInterviews.0.scheduledStartDate getInterviews.0.interviewee.timezone}} using the below link.

https://www.barraiser.com/interview-confirmation/candidate/{{getInterviews.0.id}}?channel=sms

Thanks', 'query getInterviews($input: GetInterviewsInput!) {
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
        }
        jobRole {
            company {
                name
            }
        }
    }
}');


--ivr_template

create table if not exists ivr_template (
    id text primary key,
    body text,
    query text,
    ivr_context_variables text,
    created_on timestamp,
    updated_on timestamp
);


insert into ivr_template (id, body, query, ivr_context_variables)
values (1, 'Hi

Your interview for {{getInterviews.0.jobRole.company.name}} is scheduled at {{formatEpochInSeconds getInterviews.0.scheduledStartDate }} . If you will be joining the interview, press 1 and if you want to reschedule the interview press 2.

Thanks', 'query getInterviews($input: GetInterviewsInput!) {
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
            resumeUrl
            timezone
        }
        interviewer {
            firstName
            lastName
            email
            timezone
        }
        interviewStructure {
            categories {
                name
            }
            specificSkills {
                name
            }
        }
        jobRole {
            company {
                name
            }
        }
        roundTypeConfiguration {
            commonZoomLink
        }
        roundLevelInterviewStructure {
            problemStatementLink
        }
    }
}', '{
"interview_id" : "{{getInterviews.0.id}}",
"message_bird_flow_id": "c94411ac-4d2d-4f29-9c60-9f2c056cf590"
}');


--whatsapp_template

create table if not exists whatsapp_template (
    id text primary key,
    query text,
    message_bird_template_name text,
    message_bird_template_variables text,
    created_on timestamp,
    updated_on timestamp
);


insert into whatsapp_template (id, query, message_bird_template_name, message_bird_template_variables)
values (1, 'query getInterviews($input: GetInterviewsInput!) {
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
        }
        jobRole {
            company {
                name
            }
        }
    }
}', 'interview_confirmation_v3_2', '{{getInterviews.0.jobRole.company.name}},{{getInterviews.0.interviewee.firstName}},{{getInterviews.0.interviewee.lastName}},{{getInterviews.0.jobRole.company.name}},{{formatEpochInSeconds getInterviews.0.scheduledStartDate getInterviews.0.interviewee.timezone}},{{getInterviews.0.id}}');
