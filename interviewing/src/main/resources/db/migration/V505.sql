update whatsapp_template
set message_bird_template_name = 'candidate_interview_reminder_v8',
message_bird_template_variables = '{{getInterviews.0.jobRole.company.name}},{{getInterviews.0.interviewee.firstName}},{{getInterviews.0.interviewee.lastName}},{{formatEpochInSeconds getInterviews.0.scheduledStartDate getInterviews.0.interviewee.timezone}},{{getInterviews.0.id}}?magic_token={{getInterviews.0.interviewee.magicToken}}'
where id = '3';
