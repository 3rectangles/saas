-- event_to_entity

insert into event_to_entity (id, event_type, entity_type, entity_id_path)
values (uuid_generate_v4(), 'FollowUpForSchedulingEventTurn1', 'EVALUATION', '{evaluation,id}');

insert into event_to_entity (id, event_type, entity_type, entity_id_path)
values (uuid_generate_v4(), 'FollowUpForSchedulingEventTurn2', 'EVALUATION', '{evaluation,id}');

insert into event_to_entity (id, event_type, entity_type, entity_id_path)
values (uuid_generate_v4(), 'FollowUpForSchedulingEventTurn3', 'EVALUATION', '{evaluation,id}');


-- communication_template_config

insert into communication_template_config (id, event_type, channel, recipient_type, template_rule, enabled, created_on, updated_on)
values (uuid_generate_v4(), 'FollowUpForSchedulingEventTurn1', 'EMAIL', 'CANDIDATE', 10, true, now(), now());

insert into communication_template_config (id, event_type, channel, recipient_type, template_rule, enabled, created_on, updated_on)
values (uuid_generate_v4(), 'FollowUpForSchedulingEventTurn1', 'WHATSAPP', 'CANDIDATE', 2, true, now(), now());

insert into communication_template_config (id, event_type, channel, recipient_type, template_rule, enabled, created_on, updated_on)
values (uuid_generate_v4(), 'FollowUpForSchedulingEventTurn2', 'IVR', 'CANDIDATE', 2, true, now(), now());

insert into communication_template_config (id, event_type, channel, recipient_type, template_rule, enabled, created_on, updated_on)
values (uuid_generate_v4(), 'FollowUpForSchedulingEventTurn3', 'IVR', 'CANDIDATE', 2, true, now(), now());


-- email_template

insert into email_template (id, subject, body, query, branding, created_on, updated_on)
values (10, 'Welcome to {{getEvaluations.0.jobRole.company.name}} Interview Process', '{{#partial "content" }}
<p>Hi {{getEvaluations.0.candidate.firstName}} {{getEvaluations.0.candidate.lastName}},</p>

<p>We are waiting for you to schedule your interview round for {{getEvaluations.0.jobRole.company.name}} </p>

{{#if getEvaluations.0.jobRole.partner.isCandidateSchedulingEnabled}}

{{#getEvaluations.0.validInterviews}}

{{#if (stringEquals this.status "pending_scheduling")}}

 <div align="center" width="300" height="40" bgcolor="#0898A9" style="-webkit-border-radius: 5px;
   -moz-border-radius: 5px;
   border-radius: 5px;
   color: #ffffff;
   display: block;
   width:270px;
   height:40px;
   background-color:#0898A9;
   margin-left:auto;margin-right:auto;margin-bottom: 2em;">
   <a href="https://app.barraiser.com/candidate-scheduling/{{this.id}}"
      style="font-size:16px;
      font-weight: bold;
      font-family: Helvetica, Arial, sans-serif;
      text-decoration: none;
      line-height:40px;
      width:100%;
      display:inline-block">
   <span style="color: #FFFFFF"> Schedule round {{this.roundNumber}} interview </span></a>
</div>


{{/if}}

{{/getEvaluations.0.validInterviews}}
{{/if}}


<p>Thanks,<br>
Team BarRaiser on behalf of {{getEvaluations.0.jobRole.company.name}} </p>
{{/partial}}', 'query FetchEvaluation($input: GetEvaluationInput!) {
    getEvaluations(input: $input) {
      id
      candidate {
          firstName
          lastName
          phone
      }
      jobRole {
        candidateDisplayName
        domain {
          name
        }
        company {
          name
        }
        partner{
            isCandidateSchedulingEnabled
        }
      }
      validInterviews {
        id
        roundNumber
         status
      }
    }
  }', 'BARRAISER', now(), now());



-- whatsapp_template

insert into whatsapp_template (id, query, message_bird_template_name, message_bird_template_variables, created_on, updated_on)
values (2, 'query FetchEvaluation($input: GetEvaluationInput!) {
    getEvaluations(input: $input) {
      id
      candidate {
          firstName
          lastName
          phone
      }
      jobRole {
        candidateDisplayName
        domain {
          name
        }
        company {
          name
        }
        partner{
            isCandidateSchedulingEnabled
        }
      }
      validInterviews {
        id
        roundNumber
         status
      }
    }
  }', 'candidate_follow_up_for_scheduling_v2', '{{getEvaluations.0.candidate.firstName}},{{getEvaluations.0.candidate.lastName}},{{getEvaluations.0.jobRole.company.name}},{{setVariable "firstInterviewWithPendingScheduling" "false"}}{{#getEvaluations.0.validInterviews}}{{#if(stringEquals this.status "pending_scheduling")}}{{#if(stringEquals ../firstInterviewWithPendingScheduling "false")}}{{this.id}}{{setVariable "firstInterviewWithPendingScheduling" "true"}}{{/if}}{{/if}}{{/getEvaluations.0.validInterviews}}', now(), now());


-- ivr_template


insert into ivr_template (id, body, query, ivr_context_variables, created_on, updated_on)
values (2, 'Hi {{getEvaluations.0.candidate.firstName}},


We are waiting for you to schedule your interview for {{getEvaluations.0.jobRole.company.name}}. Please check your mail to find the scheduling link.


Thanks', 'query FetchEvaluation($input: GetEvaluationInput!) {
    getEvaluations(input: $input) {
      id
      candidate {
          firstName
          lastName
          phone
      }
      jobRole {
        candidateDisplayName
        domain {
          name
        }
        company {
          name
        }
        partner{
            isCandidateSchedulingEnabled
        }
      }
      validInterviews {
        id
        roundNumber
         status
      }
    }
  }', '{
"evaluation_id" : "{{getEvaluations.0.id}}",
"message_bird_flow_id": "c1b8086c-6ec1-402f-8a8d-50e5e92475c4",
"candidate_phone": "{{getEvaluations.0.candidate.phone}}"
}', now(), now());

-- ivr_response

create table if not exists ivr_response (
    id text primary key,
    message_bird_flow_id text,
    phone text,
    call_answered bool,
    ivr_response bool,
    created_on timestamp,
    updated_on timestamp
);

