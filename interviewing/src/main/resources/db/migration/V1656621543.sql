--event_to_entity

insert into event_to_entity (id, event_type, entity_type, entity_id_path)
values (uuid_generate_v4(), 'RedoInterviewEvent', 'INTERVIEW', '{interviewId}');

--communication_template_config

insert into communication_template_config (id, event_type, channel, recipient_type, template_rule, enabled, created_on, updated_on)
values (uuid_generate_v4(), 'RedoInterviewEvent', 'EMAIL', 'PARTNER', '4da46d28-d439-4c4f-9dfe-daed060578bf', true, now(), now());

insert into communication_template_config (id, event_type, channel, recipient_type, template_rule, enabled, created_on, updated_on)
values (uuid_generate_v4(), 'RedoInterviewEvent', 'EMAIL', 'CANDIDATE', '2396eb15-2cb8-439d-80fc-02493893d484', true, now(), now());


--email_template

insert into email_template (id, subject, body, query, branding, created_on, updated_on)
values ('4da46d28-d439-4c4f-9dfe-daed060578bf', '[BarRaiser]: {{getInterviews.0.interviewee.firstName}} {{getInterviews.0.interviewee.lastName}} - {{getInterviews.0.jobRole.internalDisplayName}}  -  Interview Reopened', '{{#partial "content" }}

{{#if (stringEquals getInterviews.0.redoReasonId "7")}}

<p>As requested by you, round {{getInterviews.0.roundNumber}} of {{getInterviews.0.interviewee.firstName}} {{getInterviews.0.interviewee.lastName}} for {{getInterviews.0.jobRole.internalDisplayName}} ({{getInterviews.0.jobRole.domain.name}}) has been reopened.</p>

{{else}}

<p>Due to operational error, round {{getInterviews.0.roundNumber}} of {{getInterviews.0.interviewee.firstName}} {{getInterviews.0.interviewee.lastName}} for {{getInterviews.0.jobRole.internalDisplayName}} ({{getInterviews.0.jobRole.domain.name}}) was incorrectly completed. We have reopened the interview and have taken the  necessary actions to ensure smooth interview experience.</p>

{{/if}}

<div align="center" width="300" height="40" bgcolor="#0898A9" style="-webkit-border-radius: 5px;
   -moz-border-radius: 5px;
   border-radius: 5px;
   color: #ffffff;
   display: block;
   width:270px;
   height:40px;
   background-color:#0898A9;
   margin-left:auto;margin-right:auto;margin-bottom: 2em;">
   <a href="https://app.barraiser.com/customer/{{getInterviews.0.partnerId}}/evaluations/{{getInterviews.0.evaluation.id}}"
      style="font-size:16px;
      font-weight: bold;
      font-family: Helvetica, Arial, sans-serif;
      text-decoration: none;
      line-height:40px;
      width:100%;
      display:inline-block">
   <span style="color: #FFFFFF">View on Portal</span></a>
</div>



<p>Regards,<br>
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
        roundNumber
        redoReasonId
        partnerId
        interviewee {
            firstName
            lastName
            timezone
        }
		evaluation {
           id
        }
        jobRole {
		    internalDisplayName
            company {
                name
            }
			domain {
                name
            }
        }
    }
}', 'BARRAISER', now(), now());



insert into email_template (id, subject, body, query, branding, created_on, updated_on)
values ('2396eb15-2cb8-439d-80fc-02493893d484', '{{getInterviews.0.jobRole.company.name}} interview process: Request to schedule', '{{#partial "content" }}
<p>Hi {{getInterviews.0.interviewee.firstName}} {{getInterviews.0.interviewee.lastName}},</p>

<p>We request you to schedule your interview for the job: {{getInterviews.0.jobRole.candidateDisplayName}}, round {{getInterviews.0.roundNumber}} at the earliest.</p>


<div align="center" width="300" height="40" bgcolor="#0898A9" style="-webkit-border-radius: 5px;
   -moz-border-radius: 5px;
   border-radius: 5px;
   color: #ffffff;
   display: block;
   width:270px;
   height:40px;
   background-color:#0898A9;
   margin-left:auto;margin-right:auto;margin-bottom: 2em;">
   <a href="https://app.barraiser.com/candidate-scheduling/{{getInterviews.0.id}}"
      style="font-size:16px;
      font-weight: bold;
      font-family: Helvetica, Arial, sans-serif;
      text-decoration: none;
      line-height:40px;
      width:100%;
      display:inline-block">
   <span style="color: #FFFFFF">Schedule Interview</span></a>
</div>



<p>In case of any issue or query regarding scheduling the interview please revert to the same email.</p>

<p>Regards,<br>
Team BarRaiser</p>
{{/partial}}', 'query getInterviews($input: GetInterviewsInput!) {
    getInterviews(input: $input) {
        id
        scheduledStartDate
        scheduledEndDate
        expertScheduledStartDate
        isRescheduled
        roundNumber
        redoReasonId
        partnerId
        interviewee {
            firstName
            lastName
            timezone
        }
		evaluation {
           id
        }
        jobRole {
		    candidateDisplayName
            company {
                name
            }
			domain {
                name
            }
        }
    }
}', 'BARRAISER', now(), now());
