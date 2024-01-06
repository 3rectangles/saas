update whatsapp_template set message_bird_template_name = 'candidate_interview_confirmation_v3'
where id = '1';

update email_template set body = '{{#partial "content" }}
<span style="opacity: 0"> {{ randomNumberToPreventEmailTrimming }} </span>
<p>Dear {{getInterviews.0.interviewee.firstName}} {{getInterviews.0.interviewee.lastName}},</p>
<p>Please confirm your presence for the interview scheduled at <strong>{{formatEpochInSeconds getInterviews.0.scheduledStartDate getInterviews.0.interviewee.timezone}}</strong> for <strong>{{getInterviews.0.jobRole.company.name}}</strong></p>
<p></p>
<div style="text-align:center;">
   <a class="button approval" id="accept" style="background-color:#54BE65;" href = "https://app.barraiser.com/interview-confirmation/candidate/{{getInterviews.0.id}}?channel=email&accept=true">I will attend</a>
   <a class="button approval" id="decline" style="background-color:#F44336;" href = "https://app.barraiser.com/interview-confirmation/candidate/{{getInterviews.0.id}}?channel=email&accept=false" >Cancel</a>
</div>
<p>
    NOTE: <b>Please confirm your presence at least 2.5 hour before the interview, or we will cancel it.</b>
</p>
<p>Thanks,<br>
Team BarRaiser
</p>
<span style="opacity: 0"> {{ randomNumberToPreventEmailTrimming }} </span>
{{/partial}}' where id = '5';

update whatsapp_template set message_bird_template_name = 'candidate_interview_scheduled_information_v2'
where id = '661206c8-8b72-4626-b2cb-6b0a72dfb880';

