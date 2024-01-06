insert into event_to_entity values (uuid_generate_v4(), 'InterviewCancelledEvent', 'INTERVIEW', '{interviewId}');

insert into email_template values ('11', '{{#if getInterviews.0.jobRole.partner.isCandidateSchedulingEnabled}} {{getInterviews.0.jobRole.company.name}} Interview Process - Request to reschedule{{else}}Interview Round Cancelled <> {{formatEpochInSeconds event.startDate getInterviews.0.intervieweeTimezone}}{{/if}}',
 '{{#partial "content" }}
    {{#if (stringEquals event.cancellationReasonId "80")}}
        <title>Your interview has been cancelled</title>
            <p>Hi {{getInterviews.0.interviewee.firstName}},</p>
            <p>The interview which is scheduled at {{formatEpochInSeconds event.startDate getInterviews.0.intervieweeTimezone}} has been cancelled by you. </p>
            <p>Reason: {{event.cancellationReason}}</p>
            <p>Kindly reach out to us in case you want to reschedule the interview or if you have any queries. Our team will also try to reach out for next steps.</p>
        <p>Regards,</p>
        <p>
            Team BarRaiser
        </p>
            <br/>
    {{else}}
        {{#if (stringEquals event.cancellationType "CANDIDATE")}}
            <title>Your interview has been cancelled</title>
            <p>Hi {{getInterviews.0.interviewee.firstName}},</p>
            <p>The interview which is scheduled at {{formatEpochInSeconds event.startDate getInterviews.0.intervieweeTimezone}} has been cancelled by you. </p>
            <p>Reason: {{event.cancellationReason}}</p>
            <p>Kindly reach out to us in case you want to reschedule the interview or if you have any queries. Our team will also try to reach out for next steps.</p>
            <p>Regards,</p>
            <p>
                Team BarRaiser
            </p>
                <br/>
        {{else}}
        <title>Your interview has been cancelled</title>
        <p>Hi {{getInterviews.0.interviewee.firstName}},</p>
        <p>We regret to inform you that the interview which is scheduled at {{interview_date_time}} is being rescheduled because of an unavoidable emergency. </p>
        {{#if getInterviews.0.jobRole.partner.isCandidateSchedulingEnabled}}
            {{#if (stringEquals getInterviews.0.interviewRound "INTERNAL")}}
            <p>Please reschedule your interview round using the link below.<br>
            <div align="center" width="300" height="40" bgcolor="#0898A9" style="-webkit-border-radius: 5px;
                -moz-border-radius: 5px;
                border-radius: 5px;
                color: #ffffff;
                display: block;
                width:270px;
                height:40px;
                background-color:#0898A9;
                margin-left:auto;margin-right:auto">
                <a href="https://www.barraiser.com/candidate-scheduling/{{event.interviewId}}"
                style="font-size:16px;
                font-weight: bold;
                font-family: Helvetica, Arial, sans-serif;
                text-decoration: none;
                line-height:40px;
                width:100%;
                display:inline-block">
                <span style="color: #FFFFFF">Schedule Now</span></a>
            </div>
            </p>
            {{/if}}
        {{else}}
                <p>Our team will reach out to you for rescheduling this interview round. We sincerely apologise for the inconvenience caused.</p>
            {{/if}}
        <p>Regards,</p>
      <p>
         Team BarRaiser
      </p>
        <br/>
        {{/if}}
    {{/if}}

{{/partial}}', 'query getInterviews($input: GetInterviewsInput!) {
    getInterviews(input: $input) {
        id
        interviewRound
        interviewee {
            firstName
        }
        intervieweeTimezone
        jobRole {
            company {
                name
            }
            partner {
                isCandidateSchedulingEnabled
            }
        }
    }
}', now(), now(), 'BarRaiser', 'BARRAISER');

--communication_template_config

insert into communication_template_config (id, event_type, channel, recipient_type, template_rule, enabled, created_on, updated_on)
values (uuid_generate_v4(), 'InterviewCancelledEvent', 'EMAIL', 'CANDIDATE', '11', true, now(), now());

