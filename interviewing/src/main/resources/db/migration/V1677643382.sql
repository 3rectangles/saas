CREATE TABLE IF NOT EXISTS interviewer_feedback (
	id text NOT NULL,
	interview_id text NULL,
	feedback text NULL,
	tagged_users_list _text NULL,
	feedback_provider_user_id text NULL,
	interviewer_id text NULL,
	reschedule_count int NULL,
	offset_time int NULL,
	created_on timestamp NULL,
	updated_on timestamp NULL
);

insert into event_to_entity (id, event_type, entity_type, entity_id_path)
values (uuid_generate_v4(), 'SendInterviewerFeedback', 'INTERVIEW', '{interview,id}');

INSERT INTO  email_template (id,subject,body,query,"header",branding,created_on,updated_on)
	VALUES ('f35e99bb-7042-40f8-a82b-cb005e2c5bbb','Feedback on Interview - {{getInterviews.0.interviewee.firstName}} ','<p>Hi,</p><br>
	<p>You have feedback on an interview conducted by you:</p>
<br>
	<p>Feedback - {{event.feedback}}</p>
	<p>Report link - https://app.barraiser.com/interview-feedback/{{event.interview.id}}</p>
	<p>Candidate Name - {{getInterviews.0.interviewee.firstName}} {{getInterviews.0.interviewee.lastName}}</p>
	<p>Interview Date - {{formatEpochInSeconds getInterviews.0.scheduledStartDate getInterviews.0.interviewee.timezone}}</p>
<br>
<p>Sent by - {{event.sender.firstName}} {{event.sender.lastName}} </p>
<br>
<p>Team BarRaiser</p>','query GetInterviews($input: GetInterviewsInput!) {
    getInterviews(input: $input){
        id
       scheduledStartDate
       intervieweeTimezone
       interviewee {
            firstName
            lastName
        }
    }
}','BarRaiser','BARRAISER',now(), now());




INSERT INTO communication_template_config (id,event_type,channel,recipient_type,template_rule,enabled,created_on,updated_on)
	VALUES (uuid_generate_v4(),'SendInterviewerFeedback','EMAIL','EXPERT','f35e99bb-7042-40f8-a82b-cb005e2c5bbb',true,now(),now());


